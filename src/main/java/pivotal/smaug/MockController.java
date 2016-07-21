package pivotal.smaug;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;

import pivotal.smaug.json.AppAggregate;
import pivotal.smaug.json.UserAggregate;

@RestController
public class MockController {
	
	@Autowired
    private ResourceLoader resourceLoader;
	
	@RequestMapping(value="/v1/info/apps",method = RequestMethod.GET)	
    public @ResponseBody AppAggregate apps() throws Throwable{
		return new ObjectMapper().readValue(
				resourceLoader.getResource("classpath:mock/apps.json").getFile(), AppAggregate.class);
    }
	
	@RequestMapping(value="/v1/info/users",method = RequestMethod.GET)	
    public @ResponseBody UserAggregate users() throws Throwable{
		return new ObjectMapper().readValue(
				resourceLoader.getResource("classpath:mock/users.json").getFile(), UserAggregate.class);
    }

	@RequestMapping(value="/v1/info/events",method = RequestMethod.GET, produces=MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody String events(HttpServletResponse response) throws Throwable {

		// Get your file stream from wherever.
		InputStream inputStream = resourceLoader.getResource("classpath:mock/events.json").getInputStream();
		String json = new BufferedReader(new InputStreamReader(inputStream))
				  .lines().collect(Collectors.joining("\n"));
		
		//response.addHeader("Content-Type", "application/json");

		return json;
		
		// Set the content type and attachment header.
		//response.addHeader("Content-disposition", "attachment;filename=myfilename.txt");

		// Copy the stream to the response's output stream.
		//IOUtils.copy(myStream, response.getOutputStream());
		//response.flushBuffer();
	}
	
}
