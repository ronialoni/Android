package il.ac.tau.team3.shareaprayer;



import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.map.MappingJsonFactory;
import org.codehaus.jackson.map.ObjectMapper;
import org.mapsforge.android.maps.GeoPoint;
import org.springframework.http.MediaType;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import il.ac.tau.team3.common.SPUtils;
import il.ac.tau.team3.common.GeneralUser;
import il.ac.tau.team3.common.SPGeoPoint;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Dialog;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;


public class LocServ 
extends Service 
{
	
	private final IBinder mBinder       = new LocalBinder();
	
	private SPGeoPoint    curr_loc      = null;
	List<ILocationProv>	  locationProvs = new ArrayList<ILocationProv>();
	
	private GeneralUser   user;
	private String        userId;
	private RestTemplateFacade  restTemplateFacade; 
	
	
	public static final String ACTION_SERVICE = "il.ac.tau.team3.shareaprayer.MAIN";

	
	private LocationManager locMgr;
	
	
	
	public class LocalBinder 
	extends Binder 
	implements ILocationSvc 
	{
		public LocServ getService() {
            return LocServ.this;
        }
		
		public SPGeoPoint getLocation()
		{
			if (curr_loc == null)
				queryCurrentLocation();
			return curr_loc;
		}
		
		public void RegisterListner(ILocationProv a_prov)	{
			locationProvs.add(a_prov);
		}
		
		public void UnRegisterListner(ILocationProv a_prov)	{
			locationProvs.remove(a_prov);
		}

		public GeneralUser getUser() throws UserNotFoundException {
			if (null == user)	{
				throw new UserNotFoundException();
			}
			return user;
		}
    }
	
	
	
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) 
	{
		Log.i("LocSrv", "Received start id " + startId + ": " + intent);
		// We want this service to continue running until it is explicitly
		// stopped, so return sticky.
		
		queryCurrentLocation();
		
		return START_STICKY;
	}
	
	
	private void queryCurrentLocation()	
	{
		Location loc = locMgr.getLastKnownLocation(LocationManager.GPS_PROVIDER );
		if (null != loc)	
		{	
        	curr_loc = SPUtils.toSPGeoPoint(new GeoPoint(loc.getLatitude(), loc.getLongitude()));
        	user = new GeneralUser(userId, curr_loc, "bla");
        	Long id = null;
			try	{
				id = restTemplateFacade.UpdateUserByName(user);
			} catch (RestClientException e)	{
				
			};
        	if (id != null)	
        	{
        		user.setId(id);
        	}        	
		}		
	}
	
	


	@Override
	public void onCreate()
	{
		super.onCreate();
		
		locMgr = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		
		IntentFilter iFilter = new IntentFilter();
		iFilter.addAction(LocationManager.GPS_PROVIDER);
		iFilter.addAction(LocationManager.NETWORK_PROVIDER);
		
		
        
		
		
//		Account[] accounts = AccountManager.get(this).getAccounts();
//		if (accounts.length != 0)	
//		{	
//			userId = accounts[0].name;
//			user = new GeneralUser(null, null, null);
//			
//			//userId = "miki@gmail.com";
//		} 
//		else
//		{
//			userId = "NoGmailAccount@gmail.com";
//		}
        
        
        
        
        
        restTemplateFacade = new RestTemplateFacade();
    		    
		
		LocationListener locationListener = new LocationListener() 
    	{
	    	// Called when a new location is found by the network location provider.
    		public void onLocationChanged(Location loc) 
    	    { 	
    	    	// create a GeoPoint with the latitude and longitude coordinates
    			curr_loc = SPUtils.toSPGeoPoint(new GeoPoint(loc.getLatitude(), loc.getLongitude()));
    			user = new GeneralUser(userId, curr_loc, "bla");
    			/*StringWriter sw = new StringWriter();
				ObjectMapper mapper = new ObjectMapper();
				MappingJsonFactory jsonFactory = new MappingJsonFactory();
				try
				{
					JsonGenerator jsonGenerator = jsonFactory.createJsonGenerator(sw);
					mapper.writeValue(jsonGenerator, user);
					sw.close();
				} 
				catch (Throwable T)
				{
					
				}
				
				Log.e("post message", sw.getBuffer().toString());*/
    			Long id = null;
    			try	{
    				id = restTemplateFacade.UpdateUserByName(user);
    			} catch (RestClientException e)	{
    				
    			};
    			
    			if(id !=null){
    				user.setId(id);
    			}
    			
    			try	
    			{
    				for (ILocationProv locProv : locationProvs)
    				{
    					locProv.LocationChanged(curr_loc);
    					locProv.OnUserChange(user);
    				}
    			} 
    			catch (Exception e)
    			{
    				Log.e("bind service", e.getMessage());
    				// Do nothing
    			}
    			
    	    }

			public void onProviderDisabled(String provider) {
				// TODO Auto-generated method stub
				
			}

			public void onProviderEnabled(String provider) {
				// TODO Auto-generated method stub
				
			}

			public void onStatusChanged(String provider, int status,
					Bundle extras) {
				// TODO Auto-generated method stub
				
			}
    	};
    	
		locMgr.requestLocationUpdates(LocationManager.GPS_PROVIDER , 0, 0, locationListener);
		
	}
	
	@Override
	public IBinder onBind(Intent intent) 
	{
		return mBinder;
	}

}
