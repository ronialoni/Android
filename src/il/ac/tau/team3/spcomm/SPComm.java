package il.ac.tau.team3.spcomm;

import il.ac.tau.team3.addressQuery.MapsQueryLocation;
import il.ac.tau.team3.addressQuery.MapsQueryLonLat;
import il.ac.tau.team3.common.GeneralPlace;
import il.ac.tau.team3.common.GeneralUser;
import il.ac.tau.team3.common.PlaceAndUser;
import il.ac.tau.team3.common.SPGeoPoint;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import android.util.Log;

public class SPComm {

    private static final String GET_FOR_OBJECT_USERS  = "getallusers";
    private static final String GET_FOR_OBJECT_PLACES = "getallplaces";
    private static final String GET_USER_BY_ID = "getuserbyid";
    private static final String GET_PLACES_OF_OWNER = "getplacesbyowner";  
    private static final String GET_PLACES_OF_JOINER = "getplacesbyjoiner";
    private static final String POST_FOR_OBJECT_UPDATE_PLACE  = "updateplace";
    private static final String POST_FOR_OBJECT_UPDATE_JOINER_STATUS = "updatejoinerstatus";
    private static final String POST_FOR_OBJECT_DELETE_PLACE = "deleteplace";
    private static final String POST_FOR_OBJECT_UPDATE_USER = "updateuser";
     
    
    private SPGenComm com = new SPGenComm();
    
    public void closeHandler()	{
    	com.close();
    }
    
    private Map<String, String> getParameters(double latitude, double longitude, int radius)
    {
        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("latitude" , String.valueOf(latitude));
        parameters.put("longitude", String.valueOf(longitude));
        parameters.put("radius"   , String.valueOf(radius));        
        
        return  parameters;
    }
    
    
    public void requestGetPlaces(double latitude, double longitude, int radius, ICommHandler<GeneralPlace[]> callback)
    {
        Map<String, String> parameters = getParameters(latitude, longitude, radius);
    
        String request = GET_FOR_OBJECT_PLACES;
        
        com.requestGet(parameters, GeneralPlace[].class, request, callback);
        

    }
    
    
   
    
    public void searchForAddress(String address, ICommHandler<MapsQueryLonLat[]> callback)	{
    	Map<String, String> parameters = new HashMap<String, String>();
    	parameters.put("format", "json");
    	parameters.put("accept-language", "en");
    	parameters.put("addressdetails", "0");
    	parameters.put("q", /*URLEncoder.encode(address)*/ address);
    	
    	final String URL = "http://nominatim.openstreetmap.org/search"; 
    	com.requestGet(parameters, MapsQueryLonLat[].class, URL, callback);
    	
    	
    }
    
    public void requestGetUserById(long id, ICommHandler<GeneralUser> callback)
    {
        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("id" , String.valueOf(id));
        
        
        String request = GET_USER_BY_ID;
        
        //requestGet(parameters, responseType, request);
        com.requestGet(parameters, GeneralUser.class, request, callback);
        

    }
    
    public void requestGetUsers(double latitude, double longitude, int radius, ICommHandler<GeneralUser[]> callback)
    {
        Map<String, String> parameters = getParameters(latitude, longitude, radius);
       
        String request = GET_FOR_OBJECT_USERS;
        
        com.requestGet(parameters, GeneralUser[].class, request, callback);
        

    }
    
    public void requestPostRegister(GeneralPlace to ,GeneralUser user, boolean[] praysWishes, ICommHandler<Integer> callback)
    {
    	PlaceAndUser pau = new PlaceAndUser(user, to,praysWishes);
        com.requestPost(pau, Integer.class, POST_FOR_OBJECT_UPDATE_JOINER_STATUS, callback);
    }
    
    public void requestPostNewPlace(GeneralPlace place, ICommHandler<Long> callback)
    {
    	
    	com.requestPost(place, Long.class, POST_FOR_OBJECT_UPDATE_PLACE, callback);
        
        
    }
  
    
   
    
    public void updateUserByName(GeneralUser user, ICommHandler<Long> callback){
    	com.requestPost(user, Long.class, POST_FOR_OBJECT_UPDATE_USER, callback);
	 }
    
    
    public void deletePlace(GeneralPlace place, ICommHandler<Long> callback)	{
    	com.requestPost(place, Long.class, POST_FOR_OBJECT_DELETE_PLACE, callback);
    }
    
    public void getAddressObj(double latitude, double longitude, ICommHandler<MapsQueryLocation> callback)	{
    	Map<String, String> parameters = new HashMap<String, String>();
    	parameters.put("format", "json");
    	parameters.put("accept-language", "en");
    	parameters.put("addressdetails", "0");
    	parameters.put("lat", new Double(latitude).toString());
    	parameters.put("lon", new Double(longitude).toString());
    	    	
    	final String URL = "http://nominatim.openstreetmap.org/reverse"; 
    	com.requestGet(parameters, MapsQueryLocation.class, URL, callback);
    }


	public void requestGetOwnerPlaces(long ownerId, ICommHandler<GeneralPlace[]> callback)
    {
        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("id" , String.valueOf(ownerId));
        
        
        String request = GET_PLACES_OF_OWNER;
        
        com.requestGet(parameters, GeneralPlace[].class, request, callback);
		
	};
	
	public void requestGetJoinerPlaces(long joinerId, ICommHandler<GeneralPlace[]> callback)
    {
        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("id" , String.valueOf(joinerId));
        
        
        String request = GET_PLACES_OF_JOINER;
        
        com.requestGet(parameters, GeneralPlace[].class, request, callback);
		
	};
    
    
    
    
       
    
}
