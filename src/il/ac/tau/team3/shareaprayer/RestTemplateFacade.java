package il.ac.tau.team3.shareaprayer;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import il.ac.tau.team3.common.GeneralPlace;
import il.ac.tau.team3.common.GeneralUser;
import il.ac.tau.team3.common.PlaceAndUser;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.util.DefaultPrettyPrinter;
import org.springframework.http.MediaType;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import android.util.Log;

public class RestTemplateFacade {
	
	static final String _sServerHeader = "http://share-a-prayer.appspot.com/resources/prayerjersy/";
	//static final String _sServerHeader = "http://10.0.2.2:8888/resources/prayerjersy/";
	
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
		 GeneralUser[] result;
		 try{
			 result = restTemplate.getForObject(_sServerHeader + "users?latitude={latitude}&longitude={longitude}&radius={radius}",GeneralUser[].class, locationMapping);
		 }catch (Exception e) {
				Log.d("Share A Prayer",e.getMessage(),e);
				return null;
		 }
		 return result;
	 }
	 
	 GeneralPlace[] GetAllPlaces(Map<String, String> locationMapping){
		 GeneralPlace[] result;
		 try{
		 result =  restTemplate.getForObject(_sServerHeader + "places?latitude={latitude}&longitude={longitude}&radius={radius}",
                 GeneralPlace[].class, locationMapping);
		 }catch (Exception e) {
				Log.d("Share A Prayer",e.getMessage(),e);
				return null;
		}
			 return result;
	 }
	 
	 
	 Long UpdateUserByName(GeneralUser user){
		 Long id;
		 try{
			id = restTemplate.postForObject(_sServerHeader + "updateuserbyname", user, Long.class);
		 }catch (Exception e) {
			Log.d("Share A Prayer",e.getMessage(),e);
			return null;
		}
		 return id;
	 }
	 boolean UpdatePlace(GeneralPlace place){
		 try{
			 StringWriter writer = new StringWriter();
		        ObjectMapper mapper = new ObjectMapper();
		        final JsonGenerator jsonGenerator = mapper.getJsonFactory().createJsonGenerator(writer);
		        jsonGenerator.setPrettyPrinter(new DefaultPrettyPrinter());

		        mapper.writeValue(jsonGenerator, place);
		        Log.e("share", writer.toString());
		        restTemplate.postForObject(_sServerHeader + "updateplacebylocation", place, Long.class);			 
		 }catch (Exception e) {
				Log.d("Share A Prayer",e.getMessage(),e);
				return false;
			}
		return true;
	 }
	 
	
}
