package il.ac.tau.team3.shareaprayer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import il.ac.tau.team3.common.GeneralPlace;
import il.ac.tau.team3.common.GeneralUser;
import il.ac.tau.team3.spcomm.ICommHandler;
import il.ac.tau.team3.spcomm.SPComm;

import org.springframework.http.MediaType;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import android.util.Log;

public class RestTemplateFacade {
	private RestTemplate restTemplate;
	private   List<HttpMessageConverter<?>>      mc;
	private MappingJacksonHttpMessageConverter json;
	private List<MediaType> supportedMediaTypes;
	private SPComm com = new SPComm();
	
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
		 return restTemplate.getForObject("10.0.2.2:8888/resources/prayerjersy/users?latitude={latitude}&longitude={longitude}&radius={radius}",
                 GeneralUser[].class, locationMapping);
	 }
	 
	 private GeneralPlace[] ret;
	 
	 GeneralPlace[] GetAllPlaces(double latitude, double longitude, int raious){
		 /*return restTemplate.getForObject("http://share-a-prayer.appspot.com/resources/prayerjersy/places?latitude={latitude}&longitude={longitude}&radius={radius}",
                 GeneralPlace[].class, locationMapping);*/
		 
		 ICommHandler<GeneralPlace[]> handler = new ICommHandler<GeneralPlace[]>()	{

			public void onError(GeneralPlace[] Obj) {
				// TODO Auto-generated method stub
				
			}

			public void onRecv(GeneralPlace[] Obj) {
				ret = Obj;
				
			}

			public void onTimeout(GeneralPlace[] Obj) {
				// TODO Auto-generated method stub
				
			}

			
		 };
		 
		 com.requestGetPlaces(latitude, longitude, raious, handler);
		 
		 return ret;
	 }
	 
	 void UpdatePlace(GeneralPlace place){
		 try {
			 restTemplate.postForObject("http://10.0.2.2:8888/resources/prayerjersy/updateplacebylocation", place, Long.class);
		 } catch (RestClientException e)	{
			 Log.e("Comm Error", e.toString());
		 }
	 
	 }
	 
	 void AddJoiner(GeneralPlace place, GeneralUser joiner){
		 place.addJoiner(joiner.getName());
		 try {
		 	restTemplate.postForObject("http://10.0.2.2:8888/resources/prayerjersy/addjoiner", place, String.class);
		 } catch (RestClientException e)	{
			 Log.e("Comm Error", e.toString());
		 }
	 }
}
