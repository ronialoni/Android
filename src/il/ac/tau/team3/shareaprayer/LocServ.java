package il.ac.tau.team3.shareaprayer;

import java.util.ArrayList;
import java.util.List;

import org.mapsforge.android.maps.GeoPoint;

import il.ac.tau.team3.common.SPGeoPoint;
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

public class LocServ extends Service {
	
	private final IBinder mBinder = new LocalBinder();
	private SPGeoPoint curr_loc = null;
	List<ILocationProv>	locationProvs = new ArrayList<ILocationProv>();
	
	public static final String ACTION_SERVICE = "il.ac.tau.team3.shareaprayer.MAIN";

	
	private LocationManager locMgr;
	
	public class LocalBinder extends Binder implements ILocationSvc {
		public LocServ getService() {
            return LocServ.this;
        }
		
		public SPGeoPoint getLocation()	{
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
    }
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.i("LocSrv", "Received start id " + startId + ": " + intent);
		// We want this service to continue running until it is explicitly
		// stopped, so return sticky.
		
		queryCurrentLocation();
		
		return START_STICKY;
	}
	
	private void queryCurrentLocation()	{
		Location loc = locMgr.getLastKnownLocation(LocationManager.GPS_PROVIDER );
        if (null != loc)	
        	curr_loc = SPUtils.toSPGeoPoint(new GeoPoint(loc.getLatitude(), loc.getLongitude()));
	}
	
	



	@Override
	public void onCreate()	{
		super.onCreate();
		
		locMgr = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
		
		IntentFilter iFilter = new IntentFilter();
		iFilter.addAction(LocationManager.GPS_PROVIDER);
		iFilter.addAction(LocationManager.NETWORK_PROVIDER);
		
		
		
		LocationListener locationListener = new LocationListener() 
    	{
	    	// Called when a new location is found by the network location provider.
    		public void onLocationChanged(Location loc) 
    	    { 	
    	    	// create a GeoPoint with the latitude and longitude coordinates
    			curr_loc = SPUtils.toSPGeoPoint(new GeoPoint(loc.getLatitude(), loc.getLongitude()));
    			
    			try	{
    				for (ILocationProv locProv : locationProvs)	{
    					locProv.LocationChanged(curr_loc);
    					
    				}
    			} catch	(Exception e)	{
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
	public IBinder onBind(Intent intent) {
		return mBinder;
	}

}
