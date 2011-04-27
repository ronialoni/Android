package il.ac.tau.team3.shareaprayer;



import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
//import java.util.LinkedList;

import java.util.Timer;
import java.util.TimerTask;

import il.ac.tau.team3.common.GeneralPlace;
import il.ac.tau.team3.common.GeneralUser;
import il.ac.tau.team3.common.SPGeoPoint;
import il.ac.tau.team3.common.SPUtils;

import il.ac.tau.team3.shareaprayer.SPCommunicationManager.ISPCommunicationClient;


import android.os.Bundle;
import android.os.IBinder;


import android.util.Log;
import android.widget.ImageView;


import org.mapsforge.android.maps.MapActivity;
//import org.mapsforge.android.maps.MapViewMode;
import org.mapsforge.android.maps.IOverlayChange;
//import org.mapsforge.android.maps.OverlayItem;
import org.mapsforge.android.maps.PrayerArrayItemizedOverlay;
import org.mapsforge.android.maps.GeoPoint;


import android.content.ComponentName;
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
extends    MapActivity 
implements ISPCommunicationClient
{
    
    	
	private Drawable userDefaultMarker;
	private Drawable othersDefaultMarker;
	private Drawable synagougeMarker;
	private Drawable synagougeClosestMarker;
	
	private PrayerArrayItemizedOverlay userOverlay;
	private PrayerArrayItemizedOverlay otherUsersOverlay;
	private PlaceArrayItemizedOverlay  publicPlaceOverlay;
	private PlaceArrayItemizedOverlay  closestPlaceOverlay;
    
	
	/*----------------------------------------------------------------------------------------------------------*/
	
	
	
	private GeneralPlace determineClosestPlace(GeneralPlace[] places)
    {        
        GeneralPlace closestPlace = null;
        double userLat = service.getUser().getSpGeoPoint().getLatitudeInDegrees();
        double userLong = service.getUser().getSpGeoPoint().getLongitudeInDegrees();
        double distance = SPUtils.INFINITY;
        double tmp = 0;
        for (GeneralPlace place : places)
        {
            tmp = SPUtils.calculateDistanceMeters(userLong, userLat, place.getSpGeoPoint().getLongitudeInDegrees(), place.getSpGeoPoint().getLatitudeInDegrees());
            if (tmp < distance)
            {
                distance = tmp;
                closestPlace = place;
            }
        }

        return closestPlace;
    }

	
	//private RestTemplateFacade restTemplateFacade;
	
//	private  Map<String, String> calculateLocationParameters(SPGeoPoint center)	
//	{
//		
//		if (center == null)
//        {
//            return null;
//        }
//		
//		GeoPoint screenEdge = mapView.getProjection().fromPixels(mapView.getWidth(), mapView.getHeight());
//        if (screenEdge == null)
//        {
//            return null;
//        }
//		
//		double distance = SPUtils.calculateDistanceMeters(center.getLongitudeInDegrees(), center.getLatitudeInDegrees(),
//                                                          screenEdge.getLongitude()     , screenEdge.getLatitude());
//        
//        int distancemeters = (int) Math.ceil(distance);
//        
//        Map<String, String> parameters = new HashMap<String, String>();
//        parameters.put("latitude", new Double(center.getLatitudeInDegrees()).toString());
//        parameters.put("longitude", new Double(center.getLongitudeInDegrees()).toString());
//        parameters.put("radius", new Integer(distancemeters).toString());
//        
//        return parameters;
//	}

	
	
	
	/*----------------------------------------------------------------------------------------------------------*/
    
	
	
	/**
	 * @draw
	 * 
	 * 
	 */
	

	
	public void drawUserOnMap(GeneralUser user)	
	{
	    SPUtils.debug("<draw user> FindPrayer.drawUserOnMap(...)");
        
        //Clears the last location
		userOverlay.clear();
		
		// create an OverlayItem with title and description
        UserOverlayItem item = new UserOverlayItem(user, user.getName(), user.getStatus());

        // add the OverlayItem to the PrayerArrayItemizedOverlay
        userOverlay.addItem(item);

	}

	
	
	/*----------------------------------------------------------------------------------------------------------*/
    
	
	/*
	private void drawOtherUserOnMap(GeneralUser otheruser) //p	
	{
	    SPUtils.debug("<draw other> FindPrayer.drawOtherUserOnMap(...)");
        	    
		UserOverlayItem other = new UserOverlayItem(otheruser, otheruser.getName(), otheruser.getStatus());
=======
>>>>>>> .r75
<<<<<<< .mine
        otherUsersOverlay.addItem(other);
=======

	public void setRestTemplateFacade(RestTemplateFacade restTemplateFacade) {
		this.restTemplateFacade = restTemplateFacade;
>>>>>>> .r75
	}
<<<<<<< .mine

    private void drawPublicPlaceOnMap(GeneralPlace place) //p
    {
        SPUtils.debug("<draw place> FindPrayer.drawPublicPlaceOnMap(...)");
                
        // create an OverlayItem with title and description
        String address = place.getAddress();
        String name    = place.getName();
        PlaceOverlayItem synagouge = new PlaceOverlayItem(place, name, address);
=======
>>>>>>> .r75

<<<<<<< .mine
        publicPlaceOverlay.addItem(synagouge);
    }
    */
    
	
//  private GeneralUser[] getUsers(SPGeoPoint center)
//  {
//      Map<String, String> locationMapping = calculateLocationParameters(center);
//      
//      if (null == locationMapping)
//      {
//          return null;
//      }
//      
//      return restTemplateFacade.GetAllUsers(locationMapping);
//      
//      
//  }



	
	
    /*----------------------------------------------------------------------------------------------------------*/
    
    
	
	/**
	 * @update
	 * 
	 *  
	 *  
	 */
    
    
	/*
	private void updatePublicPlace(GeneralPlace place) //p	
	{
	    SPUtils.debug("<update place> FindPrayer.updatePublicPlace(...)");
        	    
		boolean found = false;
		
		PlaceOverlayItem placeItem;
		for (OverlayItem item : publicPlaceOverlay.getOverlayItems())
		{
		    placeItem = (PlaceOverlayItem) item;
			if (place.getId().equals(((GeneralPlace) placeItem.getPlace()).getId()))
			{
				publicPlaceOverlay.removeItem(item);
				placeItem.setPlace(place);
				publicPlaceOverlay.addItem(item);
				found = true;
				break;
			}
		}
		if (!found)
		{
			drawPublicPlaceOnMap(place);
		}
	}
	private void updateUser(GeneralUser user, GeneralUser thisUser) //p	
	{
	    SPUtils.debug("<update user> FindPrayer.updateUser(...)");
	    
		boolean found = false;
		for (OverlayItem item : otherUsersOverlay.getOverlayItems())
		{
			UserOverlayItem userItem = (UserOverlayItem) item;
			if ( (user.getId().equals(((GeneralUser) userItem.getUser()).getId()))	&&
				 ((thisUser == null) || (!thisUser.getId().equals(userItem.getUser().getId()))) )	
			{				
				otherUsersOverlay.removeItem(item);
				userItem.setUser(user);
				otherUsersOverlay.addItem(item);
				found = true;
				break;
			}
		}
		
		if (!found)	
		{
			if ((thisUser == null) || (!thisUser.getId().equals(user.getId())))	
			{ 
				drawOtherUserOnMap(user);
			}
		}
	}	
	
	/*----------------------------------------------------------------------------------------------------------*/
    // COMMENTED-BLOCK was here !!!
	/*----------------------------------------------------------------------------------------------------------*/
    
    
    /**
     * @communication ...
     * 
     * ...
     * 
     */
    
	

    private SPCommunicationManager commManager;
    
    
    /**
     * Needed (only) in PlaceArrayItemizedOverlay.
     * 
     * @return
     */
    public SPCommunicationManager getSPCommunicationManager()
    {
        return this.commManager;
    }  

    
    
    
    //@Override
    public <T> void recieveResponse(final T response)
    {
        SPUtils.debug("<recive T> FindPrayer.recieveResponse(" + response + ")");
        
        /**
         * @imp I think I can just call the function (dynamic type overload), but I'm not taking the chance.
         */        
        
        mapView.post(new Runnable()
        {            
            public void run()
            {
                if (response instanceof GeneralUser[])
                {
                    recieveUsersUpdate((GeneralUser[]) response);       
                }
                
                else if (response instanceof GeneralPlace[])
                {
                    recievePlacesUpdate((GeneralPlace[]) response);
                }
                
                else
                {
                    SPUtils.error("recieveResponse got an unknown type:  response = " + response);
                }
            }
        });
    }   
    
    
	
    public void recieveUsersUpdate(GeneralUser[] users)
    {
        SPUtils.debug("<recive GeneralUser[]> FindPrayer.recieveUsersUpdate(" + users + ")");
        
            
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
    
    
        
    public void recievePlacesUpdate(GeneralPlace[] places)
    {
        SPUtils.debug("<recive GeneralPlace[]> FindPrayer.recievePlacesUpdate(" + places + ")");
        
        /**
         * @imp I think the `if` should be in the communication manager.  
         */
        if (null != places)
        {
            List<PlaceOverlayItem> placesOverlayList = new ArrayList<PlaceOverlayItem>(places.length);
            for (GeneralPlace place : places)
            {
                placesOverlayList.add(new PlaceOverlayItem(place, place.getName(), place.getAddress()));
            }
            publicPlaceOverlay.changeItems(placesOverlayList);
        }

        
        GeneralPlace closestPlace = determineClosestPlace(places);
        if (closestPlace != null)
        {
            List<PlaceOverlayItem> closestPlacesOverlayList = new ArrayList<PlaceOverlayItem>();
            closestPlacesOverlayList.add(new PlaceOverlayItem(closestPlace, closestPlace.getName(), closestPlace.getAddress()));
            closestPlaceOverlay.changeItems(closestPlacesOverlayList);
        }
    }
    
    

    
    private void requestUpdate(SPGeoPoint center)
    {     
        GeoPoint screenEdge = mapView.getProjection().fromPixels(mapView.getWidth(), mapView.getHeight());
        if (null == screenEdge || null == center)  //// ugly!  &  I added the center null check.
        {
            return;
        } 
        
        double distance  = SPUtils.calculateDistanceMeters(center.getLongitudeInDegrees(), center.getLatitudeInDegrees(), screenEdge.getLongitude(), screenEdge.getLatitude());
        
        int    radius    = (int) Math.ceil(distance);        
        double latitude  =  center.getLatitudeInDegrees();
        double longitude = center.getLongitudeInDegrees();
        
        
        
        this.commManager.requestGetPlaces(latitude, longitude, radius);
        this.commManager.requestGetUsers(latitude, longitude, radius);
    }
        
        
 
    private Thread refreshTask = new Thread()
    {   
        /**
         * Send Get requests every 10 seconds.
         */
        @Override
        public void run()
        {
            // Set to null as default.
            SPGeoPoint center = null;
            
            while (!isInterrupted())
            {
                SPUtils.debug("refreshTask.run()");
                
                // First, wait.
                try
                {
                    synchronized (this)
                    {
                        wait(30000);
                    }
                }
                catch (InterruptedException e)
                {
                    Thread.currentThread().interrupt();
                }
                
                SPUtils.debug(service);
                
                // Then, try getting the service.
                if (null != service)
                {                    
                    SPUtils.debug("SOF-SOF: service != null");
                    
                    // Then, try getting the center.
                    center = service.getLocation();
                    if (null != center)
                    {
                        // Then, send the request.
                        ////////////////////////////updateMap(service.getLocation());
                        FindPrayer.this.requestUpdate(center);
                        
                        // And, set the center back to default (null).
                        center = null;
                    }
                    
                    /*
                    // else 
                    continue; // Retry.
                    */
                    
                    /*
                    // Then, send the request.
                    ////////////////////////////updateMap(service.getLocation());
                    FindPrayer.this.requestUpdate(center);
                    */
                }
            }
            
            
        }
    };
                                      


    public Thread getRefreshTask()
    {
        return this.refreshTask;
    }

   
    /*----------------------------------------------------------------------------------------------------------*/
    
    
	
	
	private ILocationSvc  service = null;
	private ILocationProv locationListener;
	
	
	
	
		
	private SPMapView mapView;
	
	
	
    
    
                                      
                                      
//    public void createNewPlaceDialog(String message, final SPGeoPoint point)
//    {
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setMessage(message);
//        builder.setCancelable(false);
//        
//        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener()
//        {
//            public void onClick(DialogInterface dialog, int id)
//            {
//                Thread t = new Thread()
//                {                    
//                    @Override
//                    public void run()
//                    {
//                        // Create the place and add the user as the first joiner, locally, to inform the server.
//                        GeneralPlace newMinyan = new GeneralPlace("New Minyan Place", "", point);
//                        newMinyan.addJoiner(service.getUser().getName());
//                        
//                        FindPrayer.this.commManager.requestPostNewPlace(newMinyan);
//                        
//                        // The Task should get us the information back from the server.
//                        synchronized (refreshTask)
//                        {
//                            refreshTask.notify();
//                        }
//                    }
//                };
//                t.run();                
//            }
//        });
//               
//        builder.setNegativeButton("No", new DialogInterface.OnClickListener()
//        {            
//            public void onClick(DialogInterface dialog, int id) {}            
//        });
//		
//		AlertDialog alert = builder.create();
//		
//		alert.show();
//	} 

	

	
    
    private void NewPlaceCall(SPGeoPoint point)
    {
    	UIUtils.createNewPlaceDialog( point, this , service.getUser());
    }
	
	
	private ServiceConnection svcConn; ////onDestroy
	


	
	
	/*----------------------------------------------------------------------------------------------------------*/
    
    
	/**
	 * @destructor 
	 * 
	 * @imp Calling super.onDestroy() at the END.
	 * 
	 * @post Unregistering locationListener.
	 * @post(super.onDestroy()).
	 */
	@Override
    public void onDestroy()
    {
	    if (null != service)
        {
            service.UnRegisterListner(locationListener);
        }
        
	    this.unbindService(svcConn);
	    
	    this.otherUsersOverlay.cancelAllListeners(); //TODO This doesn't work. figure out how to stop the timer task.
	    
	    
	    super.onDestroy();
    }
        
    	
	
	/*----------------------------------------------------------------------------------------------------------*/
    
	
        
	/**
	 * @constructor
	 * 
	 * 
	 */
	@Override
    public void onCreate(Bundle savedInstanceState) 	
	{		
		super.onCreate(savedInstanceState);
		
		
        this.commManager = new SPCommunicationManager(this);
        
		
		setContentView(R.layout.main);
		//mapView = new SPMapView(this);
		mapView = (SPMapView) findViewById(R.id.view1);
		
		
        mapView.registerTapListener(new IMapTapDetect()	
        {
			public void onTouchEvent(SPGeoPoint sp) 
			{
			    NewPlaceCall(sp);
				//NewPlaceCall(sp);
			}
        });
     
      
        //restTemplateFacade = new RestTemplateFacade();
    	
       
                
        
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
         * Synagogue overlay
         */
        synagougeMarker    = this.getResources().getDrawable(R.drawable.synagouge2);
        publicPlaceOverlay = new PlaceArrayItemizedOverlay(synagougeMarker, this);
        mapView.getOverlays().add(publicPlaceOverlay);

        
        synagougeClosestMarker = this.getResources().getDrawable(R.drawable.synagouge_closest);
        closestPlaceOverlay    = new PlaceArrayItemizedOverlay(synagougeClosestMarker, this);
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
    	
   	
    	
   	
        /*ServiceConnection*/ svcConn = new ServiceConnection() ////onDestroy
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
                    //mapView.getController().setCenter(SPUtils.toGeoPoint(gp));
                    if (gp == null)
                    {
                        SPUtils.debug("gp = null");
                        return;
                    }
                    mapView.getController().setCenter(SPUtils.toGeoPoint(gp));
                    
                    Thread t = new Thread()
                    {                        
                        @Override
                        public void run()
                        {
                            synchronized (FindPrayer.this)
                            {
                                FindPrayer.this.requestUpdate(service.getLocation());
                            }
                        }
                    };
                    
                    t.run();
                }
                catch (Throwable t)
                {
                    Log.e("ShareAPrayer", "Exception in call to registerListner()", t);
                }               
            }               
        };
        
        bindService(new Intent(LocServ.ACTION_SERVICE), svcConn, BIND_AUTO_CREATE);
        
        
        
        
        
        
        /*
         * Loading a listener to otherUsersOverlay.
         * It contains a Timer & TimmerTask that just notify() the refreshTask Thread.
         * On call, it cancels the last Timer & TimmerTask, and sets new ones.
         */
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
            
            
            //@Override
            public void OverlayChangeCenterZoom()
            {
                ts.cancel();
                t.purge();
                ts = new TimerRefreshTask();
                t.schedule(ts, 30000);
            }
            
            
            //@Override
            public void onDestroy()
            {
                ts.cancel();
                t.purge();
            }
        });
        
    } // END: onCreate();




	
}






/////////////////////
// COMMENTED-BLOCK //
//////////////////////////////////////////////////////////////////////////////////////////////
//private void/*GeneralUser[]*/ getUsers(double latitude, double longitude, int radius)
//{         
//  if (center == null)
//  {
//      return /*null*/;
//  }
//  
//  GeoPoint screenEdge = mapView.getProjection().fromPixels(mapView.getWidth(), mapView.getHeight());
//  if (screenEdge == null)
//  {
//      return /*null*/;
//  }        
//  double distance       = SPUtils.calculateDistanceMeters(center.getLongitudeInDegrees(), center.getLatitudeInDegrees(), screenEdge.getLongitude(), screenEdge.getLatitude());
//  int    distancemeters = (int) Math.ceil(distance);
//          
//  /*return*/ this.commManager.requestGetUsers(latitude, longitude, radius, this);       
//}
//
//
//private void/*GeneralPlace[]*/ getPlaces(double latitude, double longitude, int radius)
//{
//  if (center == null)
//  {
//      return null;
//  }
//  
//  GeoPoint screenEdge = mapView.getProjection().fromPixels(mapView.getWidth(), mapView.getHeight());
//  if (screenEdge == null)
//  {
//      return null;
//  }        
//  double distance       = SPUtils.calculateDistanceMeters(center.getLongitudeInDegrees(), center.getLatitudeInDegrees(), screenEdge.getLongitude(), screenEdge.getLatitude());
//  int    distancemeters = (int) Math.ceil(distance);
//  
//  /*return*/ this.commManager.requestGetPlaces(latitude, longitude, radius);          
//}



     
  
//  mapView.post(new Runnable()
//  {
//      
//      public void run()
//      {
//          updateMap(service.getLocation(), users, places);
//      }
//  });
  



//private void updateMap(SPGeoPoint center, GeneralUser[] users, GeneralPlace[] places)
//{
//  if (center == null)
//  {
//      return;
//  }     
//  
//  GeneralUser thisUser = null;
//  
//  if (service != null)
//  {
//      thisUser = service.getUser();
//      if (null != thisUser)
//      {
//          drawUserOnMap(thisUser);
//      }
//  }               
//  
//  if (null != users)
//  {
//    List<UserOverlayItem> usersOverlayList = new ArrayList<UserOverlayItem>(users.length);
//      for (GeneralUser user : users)
//      {
//        if ((thisUser == null) || (! thisUser.getId().equals(user.getId())))    
//        {
//            usersOverlayList.add(new UserOverlayItem(user, user.getName(), user.getStatus()));
//        }
//      }
//      otherUsersOverlay.changeItems(usersOverlayList);
//  }               
//  
//  if (null != places)
//  {
//    List<PlaceOverlayItem> placesOverlayList = new ArrayList<PlaceOverlayItem>(places.length);
//      for (GeneralPlace place : places)
//      {
//        placesOverlayList.add(new PlaceOverlayItem(place, place.getName(), place.getAddress()));
//      }
//      publicPlaceOverlay.changeItems(placesOverlayList);
//  }
//}
//////////////////////////////////////////////////////////////////////////////////////////////



