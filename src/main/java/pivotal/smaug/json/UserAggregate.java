package pivotal.smaug.json;

import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

public class UserAggregate {
	public int
		userCount;//        ,
		//UAACount         ,
		//ExternalCount    ,
		//OrphanedCount    ;
	public SortedMap<String, Integer> createDayOverDay = new TreeMap<String, Integer>();
	
	public UserAggregate() {}
}
