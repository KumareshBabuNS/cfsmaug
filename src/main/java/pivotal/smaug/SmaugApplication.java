package pivotal.smaug;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@SpringBootApplication
@EnableAutoConfiguration
@Controller
public class SmaugApplication {

	public static void main(String[] args) {
		SpringApplication.run(SmaugApplication.class, args);
	}
	
	@RequestMapping(value="/",method = RequestMethod.GET)
    public String homepage(){
        return "index.html";
    }
	
}
