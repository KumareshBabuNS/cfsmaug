package pivotal.smaug;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.cloudfoundry.client.CloudFoundryClient;
import org.cloudfoundry.client.v2.OrderDirection;
import org.cloudfoundry.client.v2.applications.ApplicationEntity;
import org.cloudfoundry.client.v2.applications.ApplicationResource;
import org.cloudfoundry.client.v2.applications.ListApplicationsRequest;
import org.cloudfoundry.client.v2.applicationusageevents.ApplicationUsageEventResource;
import org.cloudfoundry.client.v2.applicationusageevents.ListApplicationUsageEventsRequest;
import org.cloudfoundry.client.v2.events.EventResource;
import org.cloudfoundry.client.v2.events.ListEventsRequest;
import org.cloudfoundry.client.v2.serviceinstances.ListServiceInstancesRequest;
import org.cloudfoundry.client.v2.serviceinstances.ServiceInstanceResource;
import org.cloudfoundry.client.v2.spaces.ListSpacesRequest;
import org.cloudfoundry.client.v2.spaces.SpaceApplicationSummary;
import org.cloudfoundry.client.v2.spaces.SpaceResource;
import org.cloudfoundry.client.v2.users.ListUsersRequest;
import org.cloudfoundry.client.v2.users.UserResource;
import org.cloudfoundry.operations.CloudFoundryOperations;
import org.cloudfoundry.operations.DefaultCloudFoundryOperations;
import org.cloudfoundry.operations.applications.ApplicationSummary;
import org.cloudfoundry.operations.organizations.OrganizationSummary;
import org.cloudfoundry.operations.spaces.SpaceSummary;
import org.cloudfoundry.reactor.DefaultConnectionContext;
import org.cloudfoundry.reactor.client.ReactorCloudFoundryClient;
import org.cloudfoundry.reactor.tokenprovider.PasswordGrantTokenProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import pivotal.smaug.json.AppAggregate;
import pivotal.smaug.json.OrgSpaceApp;
import pivotal.smaug.json.UserAggregate;
import pivotal.smaug.json.OrgSpaceApp.App;
import reactor.core.publisher.Flux;

@RestController
public class SmaugController {
	
	private final static Logger LOG = LoggerFactory.getLogger(SmaugController.class);
	
	private static OrgSpaceApp H = new OrgSpaceApp();

	@Bean
	CloudFoundryClient cloudFoundryClient(@Value("${cf.host}") String host,
										  @Value("${cf.username}") String username,
										 @Value("${cf.password}") String password
											 ) {

		
		CloudFoundryClient cfc = ReactorCloudFoundryClient.builder()
				.connectionContext(
						DefaultConnectionContext.builder()
					    .apiHost(host)
					    .skipSslValidation(true)
					    .build()
				)
				.tokenProvider(
						PasswordGrantTokenProvider.builder()
					    .password(password)
					    .username(username)
					    .build()
				).build();
				
				//.password("bb913e0143c121504957")
				
		return cfc;
	}

	@Autowired
	CloudFoundryClient cfc;

	@Bean
	CloudFoundryOperations cloudFoundryOperations(/*
													 * CloudFoundryClient
													 * cloudFoundryClient,
													 * 
													 * @Value(
													 * "${cf.organization}")
													 * String organization,
													 * 
													 * @Value("${cf.space}")
													 * String space
													 */) {
		CloudFoundryOperations cfo = DefaultCloudFoundryOperations.builder().cloudFoundryClient(cfc).organization("system").space("system").build();
		
		// cache orgs
		LOG.info("Caching Orgs");
		for (OrganizationSummary o : cfo.organizations().list().collectList().block(Duration.ofMinutes(1)))
			H.orgs.put(o.getId(), o.getName());
		// cache spaces
		LOG.info("Caching Spaces");
		int p = 1;
		while (true) {
			ListSpacesRequest r = ListSpacesRequest.builder().page(p++).build();
			List<SpaceResource> l = cfc.spaces().list(r)
					.flatMap(response -> Flux.fromIterable(response.getResources()))
					.collectList().block(Duration.ofMinutes(1));
			if (l.size() <= 0) break;
			for (SpaceResource sr : l) {
				H.spaces.put(sr.getMetadata().getId(), sr.getEntity().getName());
				List<String> spacesInOrg = H.spacesByOrg.get(sr.getEntity().getOrganizationId());
				if (spacesInOrg == null) spacesInOrg = new ArrayList<String>();
				spacesInOrg.add(sr.getMetadata().getId());
				H.spacesByOrg.put(sr.getEntity().getOrganizationId(), spacesInOrg);
			}
		}
		// cache si
		LOG.info("Caching Services Instances");
		p = 1;
		while (true) {
			ListServiceInstancesRequest r = ListServiceInstancesRequest.builder().page(p++).build();
			List<ServiceInstanceResource> l = cfc.serviceInstances().list(r)
					.flatMap(response -> Flux.fromIterable(response.getResources()))
					.collectList().block(Duration.ofMinutes(1));
			if (l.size() <= 0) break;
			for (ServiceInstanceResource si : l) {
				List<String> servicesInSpace = H.servicesBySpace.get(si.getEntity().getSpaceId());
				if (servicesInSpace == null) servicesInSpace = new ArrayList<String>();
				servicesInSpace.add(si.getMetadata().getId());
				H.servicesBySpace.put(si.getEntity().getSpaceId(), servicesInSpace);
			}
		}

		
		return cfo;
	}

	@Autowired
	CloudFoundryOperations cfo;

	@RequestMapping(value = "/PCF/**", method = RequestMethod.GET)
	public ModelAndView PCF(ModelMap model, HttpServletRequest rq) throws Throwable {
		return new ModelAndView("redirect:https://apps."+"run.haas-35.pez.pivotal.io/"+rq.getRequestURI().substring(5), model);
	}
	
	
	@RequestMapping(value = "/v2/info/events", method = RequestMethod.GET)
	public @ResponseBody List<EventResource> events() throws Throwable {
		ListEventsRequest r = ListEventsRequest.builder()
				.orderDirection(OrderDirection.DESCENDING)
				.resultsPerPage(100).page(1).build();
		List<EventResource> l = cfc.events().list(r)
				.flatMap(response -> Flux.fromIterable(response.getResources()))
				.collectList().block();
		return l;
	}

	@RequestMapping(value = "/v2/info/appevents", method = RequestMethod.GET)
	public @ResponseBody List<ApplicationUsageEventResource> appevents() throws Throwable {
		ListApplicationUsageEventsRequest r = ListApplicationUsageEventsRequest.builder()
				.orderDirection(OrderDirection.DESCENDING)
				.resultsPerPage(100).page(1).build();
		List<ApplicationUsageEventResource> l = cfc.applicationUsageEvents().list(r)
				.flatMap(response -> Flux.fromIterable(response.getResources()))
				.collectList().block();
		return l.stream().filter(u -> !"BUILDPACK_SET".equals(u.getEntity().getState())).collect(Collectors.toList());
	}

	@RequestMapping(value = "/v2/info/users", method = RequestMethod.GET)
	public @ResponseBody UserAggregate users() throws Throwable {
		UserAggregateWorker usersw = new UserAggregateWorker(); 
		int p = 1;
		while (true) {
			ListUsersRequest r = ListUsersRequest.builder().page(p++).build();
			List<UserResource> l = cfc.users().list(r)
					.flatMap(response -> Flux.fromIterable(response.getResources()))
					.collectList().block();
			if (l.size() <= 0) break;
			for (UserResource ur : l) usersw.take(ur);
		}
		return usersw.users;
	}

	@RequestMapping(value = "/v2/info/orgspaceapps", method = RequestMethod.GET)
	public @ResponseBody OrgSpaceApp orgspaceapps() throws Throwable {
		// Note - apps details will be missing if you call this before /v2/info/apps
		return H;
	}
	
	@RequestMapping(value = "/v2/info/failedapps", method = RequestMethod.GET)
	public @ResponseBody List<ApplicationResource> failedapps() throws Throwable {
		List<ApplicationResource> failedApps = new ArrayList<ApplicationResource>();
		int p = 1;
		while (true) {
			ListApplicationsRequest r = ListApplicationsRequest.builder().page(p++).build();
			List<ApplicationResource> l = cfc.applicationsV2().list(r)
				.flatMap(response -> Flux.fromIterable(response.getResources()))
				.collectList().block(Duration.ofMinutes(2));
			if (l.size() <= 0) break;
			for (ApplicationResource ar : l) {
				ApplicationEntity ae = ar.getEntity();
				if ("STOPPED".equals(ae.getState()) && !isN(ae.getStagingFailedReason())) {
					failedApps.add(ar);
				}
			}
		}
		return failedApps;
	}
	

	@RequestMapping(value = "/v2/info/apps", method = RequestMethod.GET)
	public @ResponseBody AppAggregate apps() throws Throwable {
		// rebuilt a OSA
		OrgSpaceApp osa = new OrgSpaceApp();
		osa.orgs = H.orgs;
		osa.spaces = H.spaces;
		osa.appsBySpace = H.appsBySpace;
		osa.spacesByOrg = H.spacesByOrg;
		osa.servicesBySpace = H.servicesBySpace;
		// appsBySpace is empty
		// urlByApp is empty
		
		AppAggregateWorker appsw = new AppAggregateWorker();
		int p = 1;
		while (true) {
			ListApplicationsRequest r = ListApplicationsRequest.builder().page(p++).build();
			List<ApplicationResource> l = cfc.applicationsV2().list(r)
				.flatMap(response -> Flux.fromIterable(response.getResources()))
				.collectList().block(Duration.ofMinutes(2));
			if (l.size() <= 0) break;
			for (ApplicationResource ar : l) appsw.take(osa, ar.getMetadata().getId(), ar.getEntity());
		}
		// swap OSA
		H = osa;
		
		appsw.apps.orgCount = H.orgs.size();
		appsw.apps.spaceCount = H.appsBySpace.keySet().size();
		return appsw.apps;

	}
	
	private class UserAggregateWorker {
        SimpleDateFormat DF = new SimpleDateFormat("yyyy-MM-dd");//format same as in CC api
		UserAggregate users;
		public UserAggregateWorker() {
			users = new UserAggregate();
			for (int i = 35; i >= 0; i--) {//TODO is a const
				long t = System.currentTimeMillis() - i * (24*60*60*1000);
				users.createDayOverDay.put(DF.format(new Date(t)), 0);
			}
		}
		void take(UserResource u) {
			users.userCount++;
			String ymd = u.getMetadata().getCreatedAt().substring(0, 10);
			if (users.createDayOverDay.containsKey(ymd)) {//only if from the 21 sliding days
				int count = users.createDayOverDay.get(ymd);
				users.createDayOverDay.put(ymd, count+1);
			}
		}
	}

	private class AppAggregateWorker {
		AppAggregate apps;

		public AppAggregateWorker() {
			apps = new AppAggregate();
		}

		// we deal with a OSA to ensure we start with a fresh one yet avoid concurrent read/write
		void take(OrgSpaceApp osa, String id, ApplicationEntity a) {
			// feed org space app
			App app = new App();
			app.ai = a.getInstances();
			app.mb = a.getInstances() * a.getMemory();
			app.app_name = a.getName();
			app.app_guid = id;
			app.space_guid = a.getSpaceId();
			app.org_guid = osa.findOrgBySpace(a.getSpaceId());
			List<App> appList = osa.appsBySpace.get(app.space_guid);
			if (appList == null) {
				appList = new ArrayList<App>();
			}
			appList.add(app);
			osa.appsBySpace.put(app.space_guid, appList);
			LOG.debug("OrgSpaceApp " + app.space_guid + " " + appList.size());

			// also build nickname and url
			String nick = osa.orgs.get(app.org_guid);
			nick += "/" + osa.spaces.get(app.space_guid);
			nick += "/" + app.app_name;
			String url = "organizations/" + app.org_guid;
			url += "/spaces/" + app.space_guid;
			url += "/applications/" + app.app_guid;
			osa.urlByApp.put(app.app_guid, new String[]{nick, url});
			
			apps.TotalAppCount++;
			apps.TotalInstanceCount += a.getInstances();//if all would be STARTED
			apps.TotalMemory += (a.getInstances() * a.getMemory())/1024;
			apps.TotalDisk += (a.getInstances() * a.getDiskQuota())/1024;

			if ("STARTED".equals(a.getState())) {
				apps.StartedStateCount++;
				apps.TotalRunningCount+=a.getInstances();
				apps.RunningMemory += (a.getInstances() * a.getMemory())/1024;
				apps.RunningDisk += (a.getInstances() * a.getDiskQuota())/1024;
			} else if ("STOPPED".equals(a.getState())) {
				apps.StoppedStateCount++;
				if (!isN(a.getStagingFailedReason())) {
					System.err.println(a.getName() + " STOPPED "+a.getStagingFailedReason() + " - " + id);
					apps.FailedInStoppedStateCount++;
				}
			}
			
			
			

			String bp = a.getBuildpack();
			if (isN(bp)) {
				bp = a.getDetectedBuildpack();
				if (isN(bp)) {
					if (! isN(a.getDockerImage())) {
						bp = "docker";
						apps.DiegoAppsCount++;//TODO in UI
					} else {
						bp = "unknown";
						// can happen when STOPPED and staging failure packageState=FAILED stagingFailedReason=InsufficientResources
					}
				}
			}
			bp = bp.toLowerCase();
			if (bp.indexOf("java")==0)
				apps.JavaBPCount++;
			else if (bp.indexOf("staticfile")==0)
				apps.StaticFileBPCount++;
			else if (bp.indexOf("ruby")==0)
				apps.RubyBPCount++;
			else if (bp.indexOf("go")==0)
				apps.GOBPCount++;
			else if (bp.indexOf("python")==0)
				apps.PythonBPCount++;
			else if (bp.indexOf("php")==0)
				apps.PHPBPCount++;
			else if (bp.indexOf("node")==0)
				apps.NodeBPCount++;
			else if (bp.indexOf("binary")==0)
				apps.BinaryBPCount++;
			else if (bp.indexOf("http:")>=0 || bp.indexOf("https:")>=0) {
				apps.ExternalBPCount++;
				System.err.println("  " + bp);
			}
			else
				apps.OtherBPCount++;
		}
	}
	
	static boolean isN(String s) {
		return (s == null || s.length()==0);
	}

}
