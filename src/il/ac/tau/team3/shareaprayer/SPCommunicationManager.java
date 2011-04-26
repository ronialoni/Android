package il.ac.tau.team3.shareaprayer;



import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;


import org.springframework.web.client.RestTemplate;

import org.springframework.http.MediaType;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;

import org.springframework.web.client.RestClientException;


import android.util.Log;


import il.ac.tau.team3.common.GeneralPlace;
import il.ac.tau.team3.common.GeneralUser;
import il.ac.tau.team3.common.SPUtils;



import android.os.Handler;
import android.os.Looper;
//import android.os.Message;
import android.os.HandlerThread;




/**
 *
 * @author Team3
 *
 */
public class SPCommunicationManager
{
    
    /*** @URL ***/
    
    
    //TODO this static final implementation is not well thought.
    
    private static final String SERVER_URL = "http://share-a-prayer.appspot.com/resources/prayerjersy/";
        
    private static final String GET_FOR_OBJECT_PARAMS = "latitude={latitude}&longitude={longitude}&radius={radius}";
    private static final String GET_FOR_OBJECT_USERS  = "users";
    private static final String GET_FOR_OBJECT_PLACES = "places";
        
    private static final String POST_FOR_OBJECT_NEW_PLACE  = "updateplacebylocation";
    private static final String POST_FOR_OBJECT_ADD_JOINER = "addjoiner";
    
    
    /*----------------------------------------------------------------------------------------------------------*/
    
    
    /**
     * @server
     * 
     */       
    
    
    private RestTemplate restTemplate;
    
    
    
    public RestTemplate getRestTemplate()
    {
        return restTemplate;
    }

    public void setRestTemplate(RestTemplate restTemplate)
    {
        this.restTemplate = restTemplate;
    }
    
    
    /*----------------------------------------------------------------------------------------------------------*/
    
    
    /**
     * @client
     * 
     */
    

    public interface ISPCommunicationClient
    {
        abstract public <T> void recieveResponse(T response);    
        
        //abstract public <T> void sendGetRequest();
        //abstract public SPCommunicationManager getCommunicationManager();
    }   
     
    
    
    private ISPCommunicationClient client;
    
    
    
    /*----------------------------------------------------------------------------------------------------------*/
    
    
    /**
     * @constructor
     *       A standard default constructor.
     *       
     * @imp  Using local variables and not fields because they are only needed for initializing.
     * @post restTemplate is properly initialized.
     */
    public SPCommunicationManager(ISPCommunicationClient client)
    {
        // rest
        restTemplate = new RestTemplate();
        restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory());
        
        List<HttpMessageConverter<?>>      mc   = restTemplate.getMessageConverters();
        
        MappingJacksonHttpMessageConverter json = new MappingJacksonHttpMessageConverter();
 
        List<MediaType>     supportedMediaTypes = new ArrayList<MediaType>();
        supportedMediaTypes.add(new MediaType("text", "javascript"));
        json.setSupportedMediaTypes(supportedMediaTypes);
        
        mc.add(json);
        restTemplate.setMessageConverters(mc);   
                   
        // set client
        this.client = client; ////
        
        
        // run threads
        //this.responseThread.run(); //@imp: I think it must be first (maybe even a timeout).
        //this.requestThread.run();
    }
    
    
    /*----------------------------------------------------------------------------------------------------------*/
    
    
    /**
     * @Get
     *      The public interface is the last two methods.
     *      
     * @imp Note: Next 3 are void, while the last 1 isn't.
     * @imp Generic methods are great for Get, and adding a new Get is very simple.
     * @imp Note the BottleNeck tag.
     */
    //TODO consider <T extends ...> in generic.
    
    
    
    private Map<String, String> getParameters(double latitude, double longitude, int radius)
    {
        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("latitude" , String.valueOf(latitude));
        parameters.put("longitude", String.valueOf(longitude));
        parameters.put("radius"   , String.valueOf(radius));        
        
        return  parameters;
    }
    
    private String getGetRequestString(Class<?> requestType)
    {
        String requestString = null;
        
        if (requestType.equals(GeneralUser[].class))
        {
            requestString = GET_FOR_OBJECT_USERS;
        }
        
        if (requestType.equals(GeneralPlace[].class))
        {
            requestString = GET_FOR_OBJECT_PLACES; 
        }
        
        return requestString;
    }

       
    
    /**
     * @BottleNeck for all the Get calls (via the next function & SPGetRequest). 
     * 
     * A nice wrap to the original getForObject.
     * Used by SPGetRequest.run().
     *  
     * @param  <T>
     * @param  parameters
     * @param  responseType
     * @param  request
     * @return 
     */
    private <T> T requestGet(Map<String, String> parameters, Class<T> responseType, String request)
    {
       T response = null;
       
       try 
       {
           response = restTemplate.getForObject(SERVER_URL + request + "?" + GET_FOR_OBJECT_PARAMS, responseType, parameters);
       }
       catch (RestClientException rce)
       {
           Log.e("ShareAPrayer", "Exception in call to requestGet(...)", rce);
       }    
       
       return response;        
    }
    
       
    /**
     * Just a wrap for the last function.
     * 
     * @param <T>
     * @param latitude
     * @param longitude
     * @param radius
     * @param responseType
     */
    private <T> void requestGet(double latitude, double longitude, int radius, Class<T> responseType)
    {
        Map<String, String> parameters = getParameters(latitude, longitude, radius);
        
        String request = getGetRequestString(responseType);
        
        //requestGet(parameters, responseType, request);
        requestHandler.post(new SPGetRequest<T>(parameters, responseType, request));
    }
    
       
    
    /**
     * This two post a Runnable to requestHandler
     *     
     * @param latitude
     * @param longitude
     * @param radius
     */
    public void requestGetUsers(double latitude, double longitude, int radius)
    {        
        requestGet(latitude, longitude, radius, GeneralUser[].class);
    }
    
    public void requestGetPlaces(double latitude, double longitude, int radius)
    {
        requestGet(latitude, longitude, radius, GeneralPlace[].class);
    }
    
    
    
    
    
    /*----------------------------------------------------------------------------------------------------------*/
        
    
    /**
     * @Post
     *      The public interface is the last two methods.
     *      
     * @imp Again, generic methods are useful. This time, a bit differently.
     * 
     */
     //TODO consider <O extends ...> in generic.  - beside T.
        
    
    
    //TODO Figure out how to queue Post, with the return value taken care of.   - maybe with an anonymous in the FindPrayer.
    
    
    private <T, O> T requestPost(O object, Class<T> responseType, String request)
    {   
        T response = null;
        
        try 
        {
            response = restTemplate.postForObject(SERVER_URL + request, object, responseType);
        }
        catch (RestClientException rce)
        {
            Log.e("ShareAPrayer", "Exception in call to requestGet(...)", rce);
        }    
     
        return response;    
    }
    
    
    public String requestPostRegister(GeneralPlace to)
    {
        SPUtils.debug("requestPostRegister to = ...");
        SPUtils.debug(to);

        return requestPost(to, String.class, POST_FOR_OBJECT_ADD_JOINER);
    }
    
    public Long requestPostNewPlace(GeneralPlace place)
    {
        SPUtils.debug("requestPostNewPlace place = ...");
        SPUtils.debug(place);

        return requestPost(place, Long.class, POST_FOR_OBJECT_NEW_PLACE);
        
        
    }
    
    
    /*----------------------------------------------------------------------------------------------------------*/
    
    /**
     * @request
     * 
     * 
     */
    
    

//    public SPLooper  requestThread = new SPLooper();
    
    public Handler requestHandler = new Handler();   
    
    public class SPGetRequest<T>
    implements Runnable
    {
        Map<String, String> parameters;
        Class<T>            responseType;
        String              request;
        
        //@Override
        public void run()
        {
            SPUtils.debug("SPGetRequest.run()");
            
            // Do the @Get from server 
            T response = requestGet(parameters, responseType, request);
            if (null != response)
            {
//                // Wait a little (I thought it might help with cpu overload ???)
//                try
//                {
//                    synchronized (this)
//                    {
//                        wait(10);
//                    }
//                }
//                catch (InterruptedException e)
//                {
//                    // TODO Auto-generated catch block
//                    e.printStackTrace();
//                }
                
                // Send the response
                responseHandler.post(new SPGetResponse<T>(response, SPCommunicationManager.this.client));
            }
        }
        
        
        public SPGetRequest(Map<String, String> parameters, Class<T> responseType, String request)
        {
            this.parameters  = parameters;
            this.responseType = responseType;
            this.request      = request;
        }
    }
    
     
    /*----------------------------------------------------------------------------------------------------------*/
//    public class SPLooper
//    extends Thread
//    {
//        public Handler handler;
//        
//        
//        public void post(Runnable r)
//        {
//            this.handler.post(r);
//        }
//        
//        
//        @Override
//        public void run() 
//        {
//            Looper.prepare();
//
//            handler = new Handler();
//
//            Looper.loop();
//        }
//    };
    /*----------------------------------------------------------------------------------------------------------*/
        
    
    
    /**
     * @response
     * 
     * 
     */
    
    

    //public SPLooper responseThread = new SPLooper();
    
    public Handler responseHandler = new Handler();
    
    
    public class SPGetResponse<T>
    implements Runnable
    {
        T                      response;
        ISPCommunicationClient client;
        
        public SPGetResponse(T response, ISPCommunicationClient client)
        {
            this.response = response;
            this.client   = client;
        }
         
        //@Override
        public void run()
        {
            SPUtils.debug("SPGetResponse.run() started with: response = ...");
            SPUtils.debug(response);
            SPUtils.debug("... = " + (null == response ? "NULL" : "SOMETHING"));
            
//            // Wait 1 second before actually receiving.
//            try
//            {
//                wait(1000);  /////NOT GOOD !!!
//            }
//            catch (InterruptedException ie)
//            {
//                ie.printStackTrace(); // Auto-generated catch block
//                SPUtils.error("got InterruptedException while wait() in SPGetResponse.run().", ie);                
//            }
            
            client.recieveResponse(response); 
        }                       
    }
    
     
    
    
}



 


/*----------------------------------------------------------------------------------------------------------*/

    
////////////// OLD & GOOD    
    

//    /**
//     * @BottleNeck for all the Get calls. 
//     *  
//     * @param  <T>
//     * @param  parameters
//     * @param  responseType
//     * @param  request
//     * @return 
//     */
//    private <T> T requestGet(Map<String, String> parameters, Class<T> responseType, String request)
//    {
//       T response = null;
//       
//       try 
//       {
//           response = restTemplate.getForObject(SERVER_URL + request + "?" + GET_FOR_OBJECT_PARAMS, responseType, parameters);
//       }
//       catch (RestClientException rce)
//       {
//           Log.e("ShareAPrayer", "Exception in call to requestGet(...)", rce);
//       }    
//       
//       return response;        
//    }
//    
//    
//
//    
//    private <T> T requestGet(double latitude, double longitude, int radius, Class<T> responseType)
//    {
//        Map<String, String> parameters = getParameters(latitude, longitude, radius);
//        
//        String request = getGetRequestString(responseType);
//        
//        return requestGet(parameters, responseType, request);
//    }
//    
//       
//    
//        
//    public GeneralUser[] requestGetUsers(double latitude, double longitude, int radius)
//    {
//        return requestGet(latitude, longitude, radius, GeneralUser[].class);
//    }
//    
//    public GeneralPlace[] requestGetPlaces(double latitude, double longitude, int radius)
//    {
//        return requestGet(latitude, longitude, radius, GeneralPlace[].class);
//    }
//    
//    
//    /*----------------------------------------------------------------------------------------------------------*/
//        
//    
//    /**
//     * @Post
//     *      The public interface is the last two methods.
//     * @imp Again, generic methods are useful.
//     *      This time, a bit differently.
//     */
//     //TODO consider <O extends ...> in generic.  - beside T.
//        
//    
//    
//    private <T, O> T requestPost(O object, Class<T> responseType, String request)
//    {   
//        T response = null;
//        
//        try 
//        {
//            response = restTemplate.postForObject(SERVER_URL + request, object, responseType);
//        }
//        catch (RestClientException rce)
//        {
//            Log.e("ShareAPrayer", "Exception in call to requestGet(...)", rce);
//        }    
//     
//        return response;    
//    }
//    
//    
//    public String requestPostRegister(GeneralPlace to)
//    {
//        return requestPost(to, String.class, POST_FOR_OBJECT_ADD_JOINER);
//    }
//    
//    public Long requestPostNewPlace(GeneralPlace place)
//    {
//        return requestPost(place, Long.class, POST_FOR_OBJECT_NEW_PLACE);
//    }
//    
//    
//    /*----------------------------------------------------------------------------------------------------------*/
     
     
    
    
    
    
    
    
/////////////////// GARBAZHE    
    
//    /*    
//    private static final int GET_MESSAGE = 1;
//    
//    
//    private class SPCommRequestParams
//    {
//        String              request;
//        Class<?>            responseType;
//        Map<String, String> parameters;
//        
//        
//        private SPCommRequestParams(String url, Class<?> responseType, Map<String, String> parameters)
//        {
//            this.request          = url;
//            this.responseType = responseType;
//            this.parameters   = parameters;
//        }  
//    }
//
//    
//    
//    private Message getMessage(int what, String url, Class<?> responseType, Map<String, String> parameters)
//    {
//        Message message = Message.obtain();
//        message.what    = what;
//        message.obj     = new SPCommRequestParams(url, responseType, parameters);     
//        
//        return message;
//    }
//    
//    
//    
//    
//    
//    private Handler requestHandler = new Handler()
//    {
//        @Override
//        public void handleMessage(Message msg)
//        {
//            //TODO post to updateHandler
//            
//            // get the params
//            SPCommRequestParams requestParams = (SPCommRequestParams) msg.obj;
//            
//            
//            
//            
//            super.handleMessage(msg);
//        }
//      
//    };
//    */
 