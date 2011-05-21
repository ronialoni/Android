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
import il.ac.tau.team3.spcomm.RestTemplateFacade;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Dialog;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
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
	private SharedPreferences settings;
	private SharedPreferences.Editor edit;
	private Account[] accounts;
	private String[] names = new String[3];
	public boolean isUserReady = true;
	
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

		public Account[] getAccounts() {
			// TODO Auto-generated method stub
			return accounts;
		}

		public void setNames(String[] new_names) {
			names=new_names;
			user = new GeneralUser(names[2], curr_loc == null ? new SPGeoPoint() : curr_loc, "my status" , names[0], names[1]);
        	
			try	{
				long id = restTemplateFacade.UpdateUserByName(user);
				edit = settings.edit(); 
				edit.putLong("UserKey", id);
			} catch (RestClientException e)	{
				
			}
			
		}

		public void isUserReady(boolean is) {
			isUserReady = is;
			
		}

		public boolean isUserReady() {
			// TODO Auto-generated method stub
			return isUserReady;
		}
    }
	
	private void HandleAppStartUp(Account[] accounts){
		 settings =  getSharedPreferences("ShareAPrayerPrefs", 0);       
		 Long key = settings.getLong("UserKey", 0);
		 if(key == 0){
			 isUserReady = false;
			//names = UIUtils.HandleFirstTimeDialog(accounts);
			
		 }else{
			 //start activity as usual
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
	
	
	public String[] getNames() {
		return names;
	}


	public void setNames(String[] names) {
		this.names = names;
	}


	private void queryCurrentLocation()	
	{
		Location loc = locMgr.getLastKnownLocation(LocationManager.GPS_PROVIDER );
		if (null != loc)	
		{	
        	curr_loc = SPUtils.toSPGeoPoint(new GeoPoint(loc.getLatitude(), loc.getLongitude()));
        	user = (isUserReady ? new GeneralUser(names[2], curr_loc, "my status" , names[0], names[1]): null);
        	Long id = null;
        	if(null != user){
			try	{
				id = restTemplateFacade.UpdateUserByName(user);
				edit = settings.edit(); 
				edit.putLong("UserKey", id);
			} catch (RestClientException e)	{
				
			};
        	}
        	if (id != null)	
        	{
        		user.setId(id);
        	}        	
		}		
	}
	
	


	public Account[] getAccounts() {
		return accounts;
	}


	@Override
	public void onCreate()
	{
		super.onCreate();
		
		locMgr = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		
		IntentFilter iFilter = new IntentFilter();
		iFilter.addAction(LocationManager.GPS_PROVIDER);
		iFilter.addAction(LocationManager.NETWORK_PROVIDER);
		
		accounts = AccountManager.get(this).getAccounts();
		this.HandleAppStartUp(accounts);
		
        restTemplateFacade = new RestTemplateFacade();
    		    
		
		LocationListener locationListener = new LocationListener() 
    	{
	    	// Called when a new location is found by the network location provider.
    		public void onLocationChanged(Location loc) 
    	    { 	
    	    	// create a GeoPoint with the latitude and longitude coordinates
    			curr_loc = SPUtils.toSPGeoPoint(new GeoPoint(loc.getLatitude(), loc.getLongitude()));
    			user = (isUserReady ? new GeneralUser(names[2], curr_loc, "my status" , names[0], names[1]): null);
    			
    			Long id = null;
    			if(null != user){
    			try	{
    				id = restTemplateFacade.UpdateUserByName(user);
    			} catch (RestClientException e)	{
    				
    			};
    			}
    			
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
