package il.ac.tau.team3.shareaprayer;



import il.ac.tau.team3.common.GeneralUser;
import il.ac.tau.team3.common.SPGeoPoint;
import il.ac.tau.team3.common.SPUtils;
import il.ac.tau.team3.spcomm.ACommHandler;
import il.ac.tau.team3.spcomm.ICommHandler;
import il.ac.tau.team3.spcomm.SPComm;

import java.util.ArrayList;
import java.util.List;

import org.mapsforge.android.maps.GeoPoint;

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
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.text.format.Time;
import android.util.Log;


public class LocServ 
extends Service 
{
	
	private final IBinder mBinder       = new LocalBinder();
	
	private SPGeoPoint    curr_loc      = null;
	List<ILocationProv>	  locationProvs = new ArrayList<ILocationProv>();
	
	private GeneralUser   user;
 
	private SharedPreferences settings;
	private SPComm	comm = new SPComm();
	private Runnable lastCallback = null;
	
	public static final String ACTION_SERVICE = "il.ac.tau.team3.shareaprayer.MAIN";

	
	private LocationManager locMgr;
	private LocationListener gpsLocationListener = null;
	private LocationListener networkLocationListener = null;
	private Location lastPostLocation = null;
	
	
	public class LocalBinder  extends Binder implements ILocationSvc 
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
		
		private void scheduleUpdateUser(final String[] names)	{
			if (null != lastCallback)	{
				updateHandler.removeCallbacks(lastCallback);
			}
			updateHandler.postDelayed(lastCallback = new Runnable() {

			public void run() {
				setNames(names);
				
			}}, 4000);
		}

		public void setNames(final String[] names) {

			final GeneralUser tempuser = new GeneralUser(names[2], curr_loc == null ? new SPGeoPoint() : curr_loc, "" , names[0], names[1]);
        	
			comm.updateUserByName(tempuser, new ICommHandler<Long>() {

				public void onRecv(Long Obj) {
					if (null != Obj)	{
						tempuser.setId(Obj);
						user = tempuser;
						if (null != lastCallback)	{
							updateHandler.removeCallbacks(lastCallback);
						}
						SharedPreferences.Editor edit = settings.edit();
						edit.putBoolean("UserExists", true);
						edit.putLong("UserKey", Obj);
						edit.putString("UserAccount", user.getName());
						edit.putString("UserStatus", user.getStatus());
						edit.putString("UserFirstName", user.getFirstName());
						edit.putString("UserLastName", user.getLastName());
						edit.commit();
						try	
		    			{
		    				for (ILocationProv locProv : locationProvs)
		    				{
		    					//locProv.LocationChanged(curr_loc);
		    					locProv.OnUserChange(user);
		    				}
		    			} 
		    			catch (Exception e)
		    			{
		    				Log.e("bind service", e.getMessage());
		    				// Do nothing
		    			}
						
					} else	{
						scheduleUpdateUser(names);
					}
					
				}

				public void onTimeout(Long Obj) {
					scheduleUpdateUser(names);
				}

				public void onError(Long Obj) {
					scheduleUpdateUser(names);					
					
				}
				
			});
			
		}

		public void setStatus(String status) throws UserNotFoundException {
			if (null == user)	{
				throw new UserNotFoundException();
			}
			
			user.setStatus(status);
			SharedPreferences.Editor edit = settings.edit();
			edit.putString("UserStatus", user.getStatus());
			edit.commit();
			LocServ.this.updateUserInServer();
			
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
	
	private void updateUserLocation(GeoPoint loc)	{
		if ((null == loc) || (null == user))	{
			return;
		}
		
		curr_loc = SPUtils.toSPGeoPoint(loc);
    	user.setSpGeoPoint(curr_loc);

    	updateUserInServer();
	}
	
	private void updateUserInServer()	{
		if (null == user)	{
			return;
		}
		
		comm.updateUserByName(user, new ACommHandler<Long>() {
			@Override
			public void onRecv(Long Obj)	{
				if (Obj == null)	{
					// ERROR: SHOULD NOT BE NULL
					return;
				}
				
				if (Obj != user.getId())	{
					// shouldn't happen !
					user.setId(Obj);
				}
			}
		});
	}


	private void queryCurrentLocation()	
	{
		if (null == user)	{
			return;
		}
		
		try	{
			Location loc = locMgr.getLastKnownLocation(LocationManager.GPS_PROVIDER );
			updateUserLocation(new GeoPoint(loc.getLatitude(), loc.getLongitude()));		
		} catch (NullPointerException e)	{
			Log.e("LocServ", "Share a prayer Service: Startup location unknown");
		}
	}
	
	private GeneralUser loadUserFromStorage()	{
		String firstName;
		String lastName;
		String account;
		String status;
		GeneralUser user = null;
		long id;
		
		try	{
			if (!settings.contains("UserExists"))	{
				throw new NullPointerException();
			}

			if (settings.contains("UserKey"))	{
				id = settings.getLong("UserKey", -1);
			} else	{
				throw new NullPointerException();
			}
			if (settings.contains("UserKey"))	{
				id = settings.getLong("UserKey", -1);
			} else	{
				throw new NullPointerException();
			}
			
			if (settings.contains("UserAccount"))	{
				account = settings.getString("UserAccount", "");
			} else	{
				throw new NullPointerException();
			}
			
			if (settings.contains("UserFirstName"))	{
				firstName = settings.getString("UserFirstName", "");
			} else	{
				throw new NullPointerException();
			}
			if (settings.contains("UserLastName"))	{
				lastName = settings.getString("UserLastName", "");
			} else	{
				throw new NullPointerException();
			}
			if (settings.contains("UserStatus"))	{
				status = settings.getString("UserStatus", "");
			} else	{
				throw new NullPointerException();
			}
			user = new GeneralUser(account, null, status, firstName, lastName);
			user.setId(id);
		} catch (NullPointerException e)	{
			user = null;
		}
		
		return user;
	}

	private HandlerThread updateThread;
	private Handler		  updateHandler;
	
	private void updateAllListeners(Location loc)	{
		lastPostLocation = getCurrentLocation(loc);
		
		if (null == user)	{
			return;
		}

		updateUserLocation(new GeoPoint(loc.getLatitude(), loc.getLongitude()));
		
		try	
		{
			for (ILocationProv locProv : locationProvs)
			{
				locProv.LocationChanged(curr_loc);
				//locProv.OnUserChange(user);
			}
		} 
		catch (Exception e)
		{
			Log.e("bind service", e.getMessage());
			// Do nothing
		}
	}
	
	static private Location getAccuracyTimeLocation(Location oldLoc, Location newLoc)	{
		if ((newLoc.getTime() - oldLoc.getTime())>120*1000)	{
			return newLoc;
		}
		
		return oldLoc;
	}
	
	private Location getCurrentLocation(Location newLocation)	{
		try	{
			if (newLocation.getAccuracy() > lastPostLocation.getAccuracy())	{
				return newLocation;
			}
			
			
			return getAccuracyTimeLocation(lastPostLocation, newLocation);
		} catch (NullPointerException e)	{
			return newLocation;
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
		
		settings = getSharedPreferences("ShareAPrayer", 0);
		
		user = loadUserFromStorage();
		
		updateThread = new HandlerThread("send thread");
        	
        
		updateThread.start();
        
        updateHandler = new Handler(updateThread.getLooper());
		
		
		gpsLocationListener = new LocationListener() 
    	{
	    	// Called when a new location is found by the network location provider.
    		public void onLocationChanged(Location loc) 
    	    { 	
    			

    			LocServ.this.updateAllListeners(loc);
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
    	
    	networkLocationListener = new LocationListener() 
    	{
	    	// Called when a new location is found by the network location provider.
    		public void onLocationChanged(Location loc) 
    	    { 	
    			
    			LocServ.this.updateAllListeners(loc);
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
    	
    	locMgr.requestLocationUpdates(LocationManager.NETWORK_PROVIDER , 0, 0, networkLocationListener);
		locMgr.requestLocationUpdates(LocationManager.GPS_PROVIDER , 0, 0, gpsLocationListener);
		
	}
	
	@Override
	public IBinder onBind(Intent intent) 
	{
		return mBinder;
	}

}
