package pivotal.smaug;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class TempController {
	@RequestMapping(value="/tmpindex",method = RequestMethod.GET)
	
	    public String homepage(){
	        return "tmpindex";
	    }

}
