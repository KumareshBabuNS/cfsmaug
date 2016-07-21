package pivotal.smaug.json;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class OrgSpaceApp {
	
	public static class App {
		public String org_guid;
		public String space_guid;
		public String app_guid;
		public String app_name;
		public int ai;
		public int mb;
	}

	public Map<String, String> orgs = new HashMap<String, String>();
	public Map<String, String> spaces = new HashMap<String, String>();
	public Map<String, List<String>> spacesByOrg = new HashMap<String, List<String>>();
	public Map<String, List<String>> servicesBySpace = new HashMap<String, List<String>>();
	public Map<String, List<App>> appsBySpace = new HashMap<String, List<App>>();;
	public Map<String, String[]> urlByApp = new HashMap<String, String[]>();//guid, [org/space/name, "organizations/orgguid/spaces/spaceguid/..."]
	
	
	public OrgSpaceApp() {}
	
	public String findOrgBySpace(String s) {
		for (Entry<String, List<String>> e : spacesByOrg.entrySet()) {
			if (e.getValue().contains(s)) return e.getKey();
		}
		return "null";//should not happen
	}
}
