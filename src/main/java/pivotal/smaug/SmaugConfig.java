package pivotal.smaug;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import io.pivotal.labs.cfenv.CloudFoundryEnvironment;
import io.pivotal.labs.cfenv.CloudFoundryService;

@Component
public class SmaugConfig {

	@Value("${cf.host:}") String host;
	@Value("${cf.username:}") String username;
	@Value("${cf.password:}") String password;

	//@Autowired
	//SmaugConfig smaugConfigDefault;
	
	public SmaugConfig() {
	}
	
}
