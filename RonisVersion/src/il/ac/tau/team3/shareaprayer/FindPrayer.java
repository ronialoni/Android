package il.ac.tau.team3.shareaprayer;

 

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import il.ac.tau.team3.common.GeneralPlace;
import il.ac.tau.team3.common.GeneralUser;
import il.ac.tau.team3.common.SPGeoPoint;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.ImageView;

import org.mapsforge.android.maps.MapActivity;
//import org.mapsforge.android.maps.MapViewMode;
import org.mapsforge.android.maps.IOverlayChange;
import org.mapsforge.android.maps.OverlayItem;
import org.mapsforge.android.maps.PrayerArrayItemizedOverlay;
import org.mapsforge.android.maps.GeoPoint;


import org.springframework.http.MediaType;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;





public class FindPrayer 
extends MapActivity 
{
    
    	
	private Drawable userDefaultMarker;
	private Drawable othersDefaultMarker;
	private Drawable synagougeMarker;
	private Drawable synagougeClosestMarker;
	
	private PrayerArrayItemizedOverlay userOverlay;
	private PrayerArrayItemizedOverlay otherUsersOverlay;
	private PlaceArrayItemizedOverlay publicPlaceOverlay;
	private PlaceArrayItemizedOverlay closestPlaceOverlay;
	
	private RestTemplateFacade restTemplateFacade;
	private ServiceConnection svcConn;
	
	
	public RestTemplateFacade getRestTemplateFacade() {
		return restTemplateFacade;
	}

	public void setRestTemplateFacade(RestTemplateFacade restTemplateFacade) {
		this.restTemplateFacade = restTemplateFacade;
	}

	/*** @draw ***/
	
//	public void drawUserOnMap(GeneralUser user)	
//	{
//        //Clears the last location
//		userOverlay.clear();
//		
//		// create an OverlayItem with title and description
//        UserOverlayItem item = new UserOverlayItem(user, user.getName(), user.getStatus());
//
//        // add the OverlayItem to the PrayerArrayItemizedOverlay
//        userOverlay.addItem(item);
//	}
//	

	
	private  Map<String, String> calculateLocationParameters(SPGeoPoint center)	
	{
		
		if (center == null)
        {
            return null;
        }
		
		GeoPoint screenEdge = mapView.getProjection().fromPixels(mapView.getWidth(), mapView.getHeight());
        if (screenEdge == null)
        {
            return null;
        }
		
		double distance = SPUtils.calculateDistanceMeters(center.getLongitudeInDegrees(), center.getLatitudeInDegrees(),
                                                          screenEdge.getLongitude()     , screenEdge.getLatitude());
        
        int distancemeters = (int) Math.ceil(distance);
        
        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("latitude", new Double(center.getLatitudeInDegrees()).toString());
        parameters.put("longitude", new Double(center.getLongitudeInDegrees()).toString());
        parameters.put("radius", new Integer(distancemeters).toString());
        
        return parameters;
	}
	
	
	private GeneralPlace determineClosestPlace(GeneralPlace[] places){
		
		GeneralPlace closestPlace = null;
		double userLat = service.getUser().getSpGeoPoint().getLatitudeInDegrees();
		double userLong = service.getUser().getSpGeoPoint().getLongitudeInDegrees();
		double distance = SPUtils.INFINITY;
		double tmp = 0;
		for (GeneralPlace place : places){
			tmp = SPUtils.calculateDistanceMeters(userLong, userLat,
                    place.getSpGeoPoint().getLongitudeInDegrees() , place.getSpGeoPoint().getLatitudeInDegrees());
			if(tmp < distance){
				distance = tmp;
				closestPlace = place;
			}
		}
		return closestPlace;
		
	}
	
	
    private GeneralUser[] getUsers(SPGeoPoint center)
    {
        Map<String, String> locationMapping = calculateLocationParameters(center);
        
        if (null == locationMapping)
        {
            return null;
        }
        
        return restTemplateFacade.GetAllUsers(locationMapping);
        
        
    }
    
    
    private GeneralPlace[] getPlaces(SPGeoPoint center)
    {
        Map<String, String> locationMapping = calculateLocationParameters(center);
        
        if (null == locationMapping)
        {
            return null;
        }
        
        return restTemplateFacade.GetAllPlaces(locationMapping);
    }
    
    
    
    private void updateMap(SPGeoPoint center)
    {
        
        final GeneralUser[] users = getUsers(center);
        if (null == users)
        {
            return;
        }
        
        final GeneralPlace[] places = getPlaces(center);
        if (null == places)
        {
            return;
        }
        
        mapView.post(new Runnable()
        {
            
            public void run()
            {
                updateMap(service.getLocation(), users, places);
            }
        });
        
    }
	
    
    private void updateMap(SPGeoPoint center, GeneralUser[] users, GeneralPlace[] places)
    {
        if (center == null)
        {
            return;
        }
        
        
      
        
        GeneralPlace closestPlace = determineClosestPlace(places);
        if(closestPlace!=null){
        	List<PlaceOverlayItem> closestPlacesOverlayList = new ArrayList<PlaceOverlayItem>();
        	closestPlacesOverlayList.add(new PlaceOverlayItem(closestPlace, closestPlace.getName(), closestPlace.getAddress()));
        	closestPlaceOverlay.changeItems(closestPlacesOverlayList);
        }
        
        if (null != places)
        {
        	List<PlaceOverlayItem> placesOverlayList = new ArrayList<PlaceOverlayItem>(places.length);
            for (GeneralPlace place : places)
            {
            	if(!(place.getId().equals(closestPlace.getId()))){
            		placesOverlayList.add(new PlaceOverlayItem(place, place.getName(), place.getAddress()));
            	}
            }
            
            publicPlaceOverlay.changeItems(placesOverlayList);
           
        }
        
        GeneralUser thisUser = null;
        
        if (service != null)
        {
            thisUser = service.getUser();
            if (null != thisUser)
            {
            	List<UserOverlayItem> userOverlayList = new ArrayList<UserOverlayItem>();
            	userOverlayList.add(new UserOverlayItem(thisUser, thisUser.getName(), thisUser.getStatus()));
            	userOverlay.changeItems(userOverlayList);
            
            }
        }    
        
        
        
        
        if (null != users)
        {
        	List<UserOverlayItem> usersOverlayList = new ArrayList<UserOverlayItem>(users.length);
            for (GeneralUser user : users)
            {
            	if ((thisUser == null) || (! thisUser.getId().equals(user.getId())))	
            	{
            		usersOverlayList.add(new UserOverlayItem(user, user.getName(), user.getStatus()));
            	}
            }
            otherUsersOverlay.changeItems(usersOverlayList);
        }
    }
	
	
    
    
	
	
	private ILocationSvc  service = null;
	private ILocationProv locationListener;
	
	
	
	
		
	private SPMapView mapView;
	
	
	
    private Thread    refreshTask = new Thread()
                                      {
                                          
                                          @Override
                                          public void run()
                                          {
                                              while (! isInterrupted())
                                              {
                                                  try
                                                  {
                                                      synchronized (this)
                                                      {
                                                          wait(10000);
                                                      }
                                                      if (service.getLocation() != null)
                                                      {
                                                          updateMap(service.getLocation());
                                                      }
                                                  }
                                                  catch (InterruptedException e)
                                                  {
                                                      Thread.currentThread().interrupt();
                                                  }                                                  
                                              }
                                          }
                                          
                                      };
	
	

    public Thread getRefreshTask() {
		return refreshTask;
	}

	

	
    
    private void NewPlaceCall(SPGeoPoint point){
    	UIUtils.createNewPlaceDialog( point, this , service.getUser());
    }
	
	
	
	
	/*** @Override ***/	
	
	@Override
    public void onDestroy()
    {
        if (null != service)
        {
            service.UnRegisterListner(locationListener);
        }
        unbindService(svcConn);
        super.onDestroy();
    }
        
    	
		
	@Override
	public void onCreate(Bundle savedInstanceState) 	
	{		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		//mapView = new SPMapView(this);
		mapView = (SPMapView) findViewById(R.id.view1);
		
		
        mapView.registerTapListener(new IMapTapDetect()	
        {
			public void onTouchEvent(SPGeoPoint sp) 
			{
				NewPlaceCall(sp);
			}
        });
     
        restTemplateFacade = new RestTemplateFacade();
    	
       
        
        /*
         * User overlay and icon:
         */
        //Creates the user's map-icon
        userDefaultMarker = this.getResources().getDrawable(R.drawable.user_kipa_pin);
        // create an PrayerArrayItemizedOverlay for the user
		userOverlay       = new PrayerArrayItemizedOverlay(userDefaultMarker, this);
		 // add the PrayerArrayItemizedOverlay to the MapView
        mapView.getOverlays().add(userOverlay);
        
        /*
         * Synagouge overlay
         */
        synagougeMarker    = this.getResources().getDrawable(R.drawable.synagouge2);
      
        publicPlaceOverlay = new PlaceArrayItemizedOverlay(synagougeMarker, this);
        mapView.getOverlays().add(publicPlaceOverlay);
       
        
        synagougeClosestMarker    = this.getResources().getDrawable(R.drawable.synagouge_closest);
        closestPlaceOverlay = new PlaceArrayItemizedOverlay(synagougeClosestMarker, this);
        mapView.getOverlays().add(closestPlaceOverlay);
        
        /*
         * Other users overlay and icons:
         */
		othersDefaultMarker = this.getResources().getDrawable(R.drawable.others_kipa_pin);
		otherUsersOverlay   = new PrayerArrayItemizedOverlay(othersDefaultMarker, this);
		mapView.getOverlays().add(otherUsersOverlay);	 
        
        
		
		
        // Define a listener that responds to location updates
    	locationListener = new ILocationProv() 
    	{
	    	// Called when a new location is found by the network location provider.
			public void LocationChanged(SPGeoPoint point) 
			{
				mapView.getController().setCenter(SPUtils.toGeoPoint(point));
				
				synchronized(refreshTask)	
				{
					refreshTask.notify();
				}
			    	
    	    }
    	};
    	


   	
   	
    	refreshTask.start();
    	
   	
   	
        svcConn = new ServiceConnection()
        {
            
            public void onServiceDisconnected(ComponentName className)
            {
                service = null;
            }
            
            public void onServiceConnected(ComponentName arg0, IBinder arg1)
            {
                service = (ILocationSvc) arg1;
                
                try
                {
                    service.RegisterListner(locationListener);
                    SPGeoPoint gp = service.getLocation();
                   
                    publicPlaceOverlay.setThisUser(service.getUser());
                    closestPlaceOverlay.setThisUser(service.getUser());
                    if (gp == null)
                    {
                        return;
                    }
                    mapView.getController().setCenter(SPUtils.toGeoPoint(gp));
                    
                    Thread t = new Thread()
                    {
                        
                        @Override
                        public void run()
                        {
                            updateMap(service.getLocation());
                        }
                    };
                    t.run();
                    // send the user to places overlay
                }
                catch (Throwable t)
                {
                    Log.e("ShareAPrayer", "Exception in call to registerListner()", t);
                }
   
                
                otherUsersOverlay.RegisterListner(new IOverlayChange()
                {
                    
                    class TimerRefreshTask 
                    extends TimerTask
                    {
                        
                        @Override
                        public void run()
                        {
                            synchronized (refreshTask)
                            {
                                refreshTask.notify();
                            }
                        }
                        
                    };
                    
                    private Timer     t  = new Timer();                    
                    private TimerTask ts = new TimerRefreshTask();
                    
                    @Override
                    public void OverlayChangeCenterZoom()
                    {
                        ts.cancel();
                        t.purge();
                        ts = new TimerRefreshTask();
                        t.schedule(ts, 1000);
                        
                    }
                    
                });
            }
            
            
            
        };
        
        bindService(new Intent(LocServ.ACTION_SERVICE), svcConn, BIND_AUTO_CREATE);
        
    }
	
	
	
}

