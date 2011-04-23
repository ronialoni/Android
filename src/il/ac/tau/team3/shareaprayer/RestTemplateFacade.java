package il.ac.tau.team3.shareaprayer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import il.ac.tau.team3.common.GeneralPlace;
import il.ac.tau.team3.common.GeneralUser;

import org.springframework.http.MediaType;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

public class RestTemplateFacade {
	private RestTemplate restTemplate;
	private   List<HttpMessageConverter<?>>      mc;
	private MappingJacksonHttpMessageConverter json;
	private List<MediaType> supportedMediaTypes;
	
	public RestTemplateFacade(){
		 restTemplate = new RestTemplate();
		 restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory());
		 mc   = restTemplate.getMessageConverters();
	     json = new MappingJacksonHttpMessageConverter();
	     supportedMediaTypes = new ArrayList<MediaType>();
	     InitializeVars();
	       
	}
	
	void InitializeVars(){
		 supportedMediaTypes.add(new MediaType("text", "javascript"));
	     json.setSupportedMediaTypes(supportedMediaTypes);
	     mc.add(json);
	     restTemplate.setMessageConverters(mc);
	}
	
	public RestTemplate getRestTemplate()
	{
		return restTemplate;
	}

	public void setRestTemplate(RestTemplate restTemplate)
	{
		this.restTemplate = restTemplate;
	}
	
	 GeneralUser[] GetAllUsers(Map<String, String> locationMapping){
		 return restTemplate.getForObject("http://share-a-prayer.appspot.com/resources/prayerjersy/users?latitude={latitude}&longitude={longitude}&radius={radius}",
                 GeneralUser[].class, locationMapping);
	 }
	 
	 GeneralPlace[] GetAllPlaces(Map<String, String> locationMapping){
		 return restTemplate.getForObject("http://share-a-prayer.appspot.com/resources/prayerjersy/places?latitude={latitude}&longitude={longitude}&radius={radius}",
                 GeneralPlace[].class, locationMapping);
	 }
	 
	 void UpdatePlace(GeneralPlace place){
		 restTemplate.postForObject("http://share-a-prayer.appspot.com/resources/prayerjersy/updateplacebylocation", place, Long.class);
	 
	 }
	 
	 void AddJoiner(GeneralPlace place, GeneralUser joiner){
		 place.addJoiner(joiner.getName());
		 restTemplate.postForObject("http://share-a-prayer.appspot.com/resources/prayerjersy/addjoiner", place, String.class);
	 }
}
