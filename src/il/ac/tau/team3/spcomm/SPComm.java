package il.ac.tau.team3.spcomm;

import il.ac.tau.team3.addressQuery.MapsQueryLocation;
import il.ac.tau.team3.common.GeneralPlace;
import il.ac.tau.team3.common.GeneralUser;
import il.ac.tau.team3.common.SPGeoPoint;

import java.util.HashMap;
import java.util.Map;

public class SPComm {

    private static final String GET_FOR_OBJECT_USERS  = "users";
    private static final String GET_FOR_OBJECT_PLACES = "places";
        
    private static final String POST_FOR_OBJECT_NEW_PLACE  = "updateplacebylocation";
    private static final String POST_FOR_OBJECT_ADD_JOINER = "addjoiner";
    
    private SPGenComm com = new SPGenComm();
    
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
        
        //requestGet(parameters, responseType, request);
        com.requestGet(parameters, GeneralPlace[].class, request, callback);
        

    }
    
    public void requestGetUsers(double latitude, double longitude, int radius, ICommHandler<GeneralUser[]> callback)
    {
        Map<String, String> parameters = getParameters(latitude, longitude, radius);
        
        
        String request = GET_FOR_OBJECT_USERS;
        
        //requestGet(parameters, responseType, request);
        com.requestGet(parameters, GeneralUser[].class, request, callback);
        

    }
    
    public void requestPostRegister(GeneralPlace to, ICommHandler<String> callback)
    {

        com.requestPost(to, String.class, POST_FOR_OBJECT_ADD_JOINER, callback);
    }
    
    public void requestPostNewPlace(GeneralPlace place, ICommHandler<Long> callback)
    {
    	
    	com.requestPost(place, Long.class, POST_FOR_OBJECT_NEW_PLACE, callback);
        
        
    }
    
    public void searchForAddress(String address, ICommHandler<MapsQueryLocation> callback)	{
    	Map<String, String> parameters = new HashMap<String, String>();
    	parameters.put("address", address);
    	parameters.put("sensor", "false");
    	final String URL = "http://maps.googleapis.com/maps/api/geocode/json"; 
    	com.requestGet(parameters, MapsQueryLocation.class, URL, callback);
    };
       
    
}
