package il.ac.tau.team3.shareaprayer;

 

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import il.ac.tau.team3.addressQuery.MapsQueryLocation;
import il.ac.tau.team3.common.GeneralPlace;
import il.ac.tau.team3.common.GeneralUser;
import il.ac.tau.team3.common.SPGeoPoint;
import il.ac.tau.team3.spcomm.ACommHandler;
import il.ac.tau.team3.spcomm.SPComm;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log; 
import android.view.KeyEvent;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.mapsforge.android.maps.MapActivity;
//import org.mapsforge.android.maps.MapViewMode;
import org.mapsforge.android.maps.IOverlayChange;
import org.mapsforge.android.maps.OverlayItem;
import org.mapsforge.android.maps.PrayerArrayItemizedOverlay;
import org.mapsforge.android.maps.GeoPoint;


import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.drawable.Drawable;





public class FindPrayer 
extends MapActivity 
{
    
    	
	private Drawable userDefaultMarker;
	private Drawable othersDefaultMarker;
	private Drawable synagougeMarker;
	private Drawable synagougeClosestMarker;
	
	private PrayerArrayItemizedOverlay userOverlay;
	private PrayerArrayItemizedOverlay searchQueryOverlay;
	private PrayerArrayItemizedOverlay otherUsersOverlay;
	private PlaceArrayItemizedOverlay publicPlaceOverlay;
	private PlaceArrayItemizedOverlay closestPlaceOverlay;
	
	private SPComm comm = new SPComm();
	/*** @draw ***/
	
	
	private Integer calculateViewableRadius(SPGeoPoint center)	{
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
        
        return (int) Math.ceil(distance);
	}
	
	
	
	
	private GeneralPlace determineClosestPlace(GeneralPlace[] places){
		if (null == places)	{
			return null;
		}
		
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
	
	public SPComm getSPComm()	{
		return comm;
	}
	
	
	
    private void updateUsersOnMap(SPGeoPoint center)
    {
    	Integer radius = calculateViewableRadius(center);
    	if (null == radius)	{
    		return;
    	}

    	comm.requestGetUsers(center.getLatitudeInDegrees(), center.getLongitudeInDegrees(), radius, 
    			new ACommHandler<GeneralUser[]>()	{


    				public void onRecv(final GeneralUser[] users) {
    					FindPrayer.this.runOnUiThread(new Runnable() {

    						public void run() {
    							// TODO Auto-generated method stub
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
    									try	{
	    									if (! thisUser.getId().equals(user.getId()))	
	    									{
	    										usersOverlayList.add(new UserOverlayItem(user, user.getName(), user.getStatus()));
	    									}
    									} catch (NullPointerException e)	{
    										
    									}
    								}
    								otherUsersOverlay.changeItems(usersOverlayList);
    							}
    						}

    					});

    				}


    			});

    }
    
    
    private void updatePlacesOnMap(SPGeoPoint center)
    {
    	Integer radius = calculateViewableRadius(center);
    	if (null == radius)	{
    		return;
    	}

    	comm.requestGetPlaces(center.getLatitudeInDegrees(), center.getLongitudeInDegrees(), radius, 
    			new ACommHandler<GeneralPlace[]>()	{

    				public void onRecv(final GeneralPlace[] places) {
    					FindPrayer.this.runOnUiThread(new Runnable() {

    						public void run() {
    							// TODO Auto-generated method stub
    							GeneralPlace closestPlace = determineClosestPlace(places);
    					        if(closestPlace!=null){
    					        	List<PlaceOverlayItem> closestPlacesOverlayList = new ArrayList<PlaceOverlayItem>();
    					        	closestPlacesOverlayList.add(new PlaceOverlayItem(closestPlace, closestPlace.getName(), closestPlace.getAddress(), synagougeClosestMarker));
    					        	closestPlaceOverlay.changeItems(closestPlacesOverlayList);
    					        }
    					        
    					        if (null != places)
    					        {
    					        	List<PlaceOverlayItem> placesOverlayList = new ArrayList<PlaceOverlayItem>(places.length);
    					            for (GeneralPlace place : places)
    					            {
    					            	if(!(place.getId().equals(closestPlace.getId()))){
    					            		placesOverlayList.add(new PlaceOverlayItem(place, place.getName(), place.getAddress(), synagougeMarker));
    					            	}
    					            }
    					            
    					            publicPlaceOverlay.changeItems(placesOverlayList);
    					           
    					        }
    						}

    					});

    				}

    				
    			});

    }
    
    	
	private ILocationSvc  service = null;
	private ILocationProv locationListener;
	
	
	
	
		
	private SPMapView mapView;
	private EditText  editText;
	
	
	
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
    				try	{
	    				if (service.getLocation() != null)
	    				{
	    					updateUsersOnMap(SPUtils.toSPGeoPoint(mapView.getMapCenter()));
	    					updatePlacesOnMap(SPUtils.toSPGeoPoint(mapView.getMapCenter()));
	    				}
    				} catch (NullPointerException e)	{
    					
    				}
    			}
    			catch (InterruptedException e)
    			{
    				Thread.currentThread().interrupt();
    			}                                                  
    		}
    	}

    };
	private ServiceConnection svcConn;

    public Thread getRefreshTask() {
		return refreshTask;
	}

	

	
    
    private void NewPlaceCall(SPGeoPoint point){
    	if ((service != null) && (service.getUser() != null))
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
        refreshTask.destroy();
        super.onDestroy();
    }
        
    	
		
	@Override
	public void onCreate(Bundle savedInstanceState) 	
	{		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		//mapView = new SPMapView(this);
		mapView = (SPMapView) findViewById(R.id.view1);
		editText = (EditText) findViewById(R.id.addressBar);
		
        mapView.registerTapListener(new IMapTapDetect()	
        {
			public void onTouchEvent(SPGeoPoint sp) 
			{
				NewPlaceCall(sp);
			}

			public void onMoveEvent(SPGeoPoint sp) {
				// TODO Auto-generated method stub
				
			}
        });
        
        editText.setOnEditorActionListener (new EditText.OnEditorActionListener()	{


        	public boolean onEditorAction(final TextView v, int actionId, KeyEvent event) {
        		if (null == event)	{
        			return false;
        		}
        		if ((event.getAction() == KeyEvent.ACTION_DOWN) && 
        				(event.getKeyCode() == KeyEvent.KEYCODE_ENTER))
        		    {
					comm.searchForAddress(v.getText().toString(), new ACommHandler<MapsQueryLocation>() {
						public void onRecv(final MapsQueryLocation Obj)	{
							FindPrayer.this.runOnUiThread(new Runnable() {

								public void run() {
									try	{
										double latitude = Obj.getResults()[0].getGeometry().getLocation().getLat();
										double longitude = Obj.getResults()[0].getGeometry().getLocation().getLng(); 
										
										GeoPoint gp = new GeoPoint(latitude, longitude);
										mapView.getController().setCenter(gp);
										mapView.getController().setZoom(mapView.getMaxZoomLevel());
										synchronized(refreshTask)	
										{
											refreshTask.notify();
										}
										searchQueryOverlay.clear();
										searchQueryOverlay.addItem(new OverlayItem(gp, "Search query result", v.getText().toString()));
										Toast toast = Toast.makeText(getApplicationContext(), "Long tap on map to create a new place", Toast.LENGTH_LONG);
										toast.show();
									} catch (NullPointerException e)	{
										
									} catch (ArrayIndexOutOfBoundsException e)	{
										
									}
							
									
								}
							
							});
						}
					});
					return true;
				}
				return false;
			}

			
        	
        });
     
        /*
         * User overlay and icon:
         */
        //Creates the user's map-icon
        userDefaultMarker = this.getResources().getDrawable(R.drawable.user_kipa_pin);
        // create an PrayerArrayItemizedOverlay for the user
		userOverlay       = new PrayerArrayItemizedOverlay(userDefaultMarker, this);
		 // add the PrayerArrayItemizedOverlay to the MapView
        mapView.getOverlays().add(userOverlay);
        
        // create an PrayerArrayItemizedOverlay for the user
        searchQueryOverlay = new PrayerArrayItemizedOverlay(userDefaultMarker, this);
		 // add the PrayerArrayItemizedOverlay to the MapView
        mapView.getOverlays().add(searchQueryOverlay);
        
        /*
         * Synagouge overlay
         */
        synagougeMarker    = this.getResources().getDrawable(R.drawable.place_dark_blue);
      
        publicPlaceOverlay = new PlaceArrayItemizedOverlay(synagougeMarker, this);
        mapView.getOverlays().add(publicPlaceOverlay);
       
        
        synagougeClosestMarker    = this.getResources().getDrawable(R.drawable.place_red_david);
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
                        	synchronized(refreshTask){
                        		refreshTask.notify();
                        	}
                        }
                    };
                    t.run();
                    // send the user to places overlay
                }
                catch (Throwable t)
                {
                    Log.e("ShareAPrayer", "Exception in call to registerListner()", t);
                }
   
                mapView.registerTapListener(new IMapTapDetect() {
                	
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

					public void onMoveEvent(SPGeoPoint sp) {
						// TODO Auto-generated method stub
						ts.cancel();
                        t.purge();
                        ts = new TimerRefreshTask();
                        t.schedule(ts, 1000);
					}

					public void onTouchEvent(SPGeoPoint sp) {
						// TODO Auto-generated method stub
						
					}
                	
                });
                /*otherUsersOverlay.RegisterListner(new IOverlayChange()
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
                      t.schedule(ts, 10000);
                        
                    }
                    
                });*/
            }
            
            
            
        };
        
        bindService(new Intent(LocServ.ACTION_SERVICE), svcConn, BIND_AUTO_CREATE);
        
        Toast toast = Toast.makeText(getApplicationContext(), "Long tap on map to create a new place", Toast.LENGTH_LONG);
        toast.show();
    }
	
	
	
}

