package pivotal.smaug;

import javax.annotation.PostConstruct;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import io.pivotal.labs.cfenv.CloudFoundryEnvironment;
import io.pivotal.labs.cfenv.CloudFoundryEnvironmentException;
import io.pivotal.labs.cfenv.CloudFoundryService;

@SpringBootApplication
@EnableAutoConfiguration
@EnableScheduling
@Controller
public class SmaugApplication {

	public static void main(String[] args) {
		SpringApplication.run(SmaugApplication.class, args);
	}
	
	@Autowired
	ApplicationContext ctx;
	
	@Autowired
	SmaugConfig smaugConfig;
	
	static SmaugConfig CONFIG;
	
	@PostConstruct
	void finalizeConfig() throws Throwable {
		if (ctx.getEnvironment().acceptsProfiles("cloud")) {
			CONFIG = smaugConfigCloud();
		} else {
			CONFIG = smaugConfig;
		}
	}
	
	static SmaugConfig smaugConfigCloud() throws Throwable {
		LoggerFactory.getLogger(SmaugConfig.class).info("Running in CF, expecting to find a service binding");
		SmaugConfig s = new SmaugConfig();
		CloudFoundryEnvironment env = new CloudFoundryEnvironment(System::getenv);
		 CloudFoundryService service = env.getService("smaug-api");
		 if (service == null) LoggerFactory.getLogger(SmaugConfig.class).error("No smaug-api service found");
		 s.host = (String) service.getCredentials().get("host");
		 s.username = (String) service.getCredentials().get("username");
		 s.password = (String) service.getCredentials().get("password");
		 return s;
	}
	
	@RequestMapping(value="/",method = RequestMethod.GET)
    public String homepage(){
        return "index.html";
    }
	
}
