package il.ac.tau.team3.shareaprayer;

 

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import il.ac.tau.team3.addressQuery.MapsQueryLocation;
import il.ac.tau.team3.common.GeneralPlace;
import il.ac.tau.team3.common.GeneralUser;
import il.ac.tau.team3.common.SPGeoPoint;
import il.ac.tau.team3.common.SPUtils;
import il.ac.tau.team3.common.UnknownLocationException;
import il.ac.tau.team3.spcomm.ACommHandler;
import il.ac.tau.team3.spcomm.SPComm;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log; 
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import org.mapsforge.android.maps.MapActivity;
//import org.mapsforge.android.maps.MapViewMode;
import org.mapsforge.android.maps.IOverlayChange;
import org.mapsforge.android.maps.OverlayItem;
import org.mapsforge.android.maps.PrayerArrayItemizedOverlay;
import org.mapsforge.android.maps.GeoPoint;


import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Dialog;
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
		
		try	{
			GeneralPlace closestPlace = null;
			double userLat = svcGetter.getService().getUser().getSpGeoPoint().getLatitudeInDegrees();
			double userLong = svcGetter.getService().getUser().getSpGeoPoint().getLongitudeInDegrees();
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
			
		} catch (UserNotFoundException e)	{
			return null;
		} catch (UnknownLocationException e)	{
			return null;
		} catch (ServiceNotConnected e) {
			// TODO Auto-generated catch block
			return null;
		}
		
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

    	comm.requestGetUsers(center.getLatitudeInDegrees(), center.getLongitudeInDegrees(), radius, new ACommHandler<GeneralUser[]>()	
    	{
    	    public void onRecv(final GeneralUser[] users)
    	    {
    	        FindPrayer.this.runOnUiThread(new Runnable()
    	        {    	            
    	            public void run()
    	            {
    	                // TODO Auto-generated method stub
    	                try
    	                {
    	                    GeneralUser thisUser = svcGetter
    	                    .getService().getUser();
    	                    List<UserOverlayItem> userOverlayList = new ArrayList<UserOverlayItem>();
    	                    userOverlayList.add(new UserOverlayItem(
    	                            thisUser, thisUser.getName(),
    	                            thisUser.getStatus()));
                                    userOverlay.changeItems(userOverlayList);
    	                }
    	                catch (UserNotFoundException e)
    	                {
    	                    // invalid user
    	                    // TODO Auto-generated catch block
    	                    e.printStackTrace();
    	                }
    	                catch (UnknownLocationException e)
    	                {
    	                    // TODO Auto-generated catch block
    	                    e.printStackTrace();
    	                }
    	                catch (ServiceNotConnected e)
    	                {
    	                    // service wasn't initialized yet
    	                    // TODO Auto-generated catch block
    	                    e.printStackTrace();
    	                }
    	                catch (NullPointerException npe)
    	                {
    	                    SPUtils.error("NullPointerException - Should have been WRAPED !!!", npe);
    	                    npe.printStackTrace();
    	                }
                                
    	                if (null != users)
    	                {
    	                    List<UserOverlayItem> usersOverlayList = new ArrayList<UserOverlayItem>(
    	                            users.length);
    	                    for (GeneralUser user : users)
    	                    {
    	                        try
    	                        {
    	                            GeneralUser thisUser = svcGetter.getService().getUser();
    	                            if (!thisUser.getName().equals(user.getName()))
    	                            {
    	                                usersOverlayList.add(new UserOverlayItem(user, user.getName(), user.getStatus()));
    	                            }
    	                        }
    	                        catch (UserNotFoundException e)
    	                        {
    	                            // TODO Auto-generated catch block
                                    e.printStackTrace();
    	                        }
    	                        catch (UnknownLocationException e)
    	                        {
    	                            // TODO Auto-generated catch block
    	                            e.printStackTrace();
    	                        }
    	                        catch (ServiceNotConnected e)
    	                        {
    	                            // TODO Auto-generated catch block
    	                            e.printStackTrace();
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
    					        	try {
										closestPlacesOverlayList.add(new PlaceOverlayItem(closestPlace, closestPlace.getName(), closestPlace.getAddress(), synagougeClosestMarker));
									} catch (UnknownLocationException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
    					        	closestPlaceOverlay.changeItems(closestPlacesOverlayList);
    					        }
    					        
    					        if (null != places)
    					        {
    					        	List<PlaceOverlayItem> placesOverlayList = new ArrayList<PlaceOverlayItem>(places.length);
    					            for (GeneralPlace place : places)
    					            {
    					            	if ((closestPlace == null) || (!(place.getId().equals(closestPlace.getId())))){
    					            		try {
												placesOverlayList.add(new PlaceOverlayItem(place, place.getName(), place.getAddress(), synagougeMarker));
											} catch (UnknownLocationException e) {
												// TODO Auto-generated catch block
												e.printStackTrace();
											}
    					            	}
    					            }
    					            
    					            publicPlaceOverlay.changeItems(placesOverlayList);
    					           
    					        }
    						}

    					});

    				}

    				
    			});

    }
    
    	
	private ServiceConnector  svcGetter = new ServiceConnector();;
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
	    				//if (service.getLocation() != null)
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
    	try	{
    		UIUtils.createNewPlaceDialog( point, this , svcGetter.getService().getUser());
    	} catch (UserNotFoundException e)	{
    		
    	} catch (ServiceNotConnected e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
	
	
	
	
	/*** @Override ***/	
	
	@Override
    public void onDestroy()
    {
        try {
			svcGetter.getService().UnRegisterListner(locationListener);
			svcGetter.setService(null);
		} catch (ServiceNotConnected e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        unbindService(svcConn);
        //refreshTask.destroy();   - @Depricated !!!! & throws (on exit of course).
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
        		if ((EditorInfo.IME_ACTION_DONE == actionId) || ((event != null) && 
        				(event.getAction() == KeyEvent.ACTION_DOWN) && 
        				(event.getKeyCode() == KeyEvent.KEYCODE_ENTER)))
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

			public void OnUserChange(GeneralUser user) {
				// TODO Auto-generated method stub
				try	{
					publicPlaceOverlay.setThisUser(svcGetter.getService().getUser());
					closestPlaceOverlay.setThisUser(svcGetter.getService().getUser());
				} catch (UserNotFoundException e)	{
				                                 	 
				} catch (ServiceNotConnected e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
    	};
    	


   	
   	
    	refreshTask.start();
    	
   	
   	
        svcConn = new ServiceConnection()
        {
            
            public void onServiceDisconnected(ComponentName className)
            {
            	svcGetter.setService(null);
            }
            
            public void onServiceConnected(ComponentName arg0, IBinder arg1)
            {
            	svcGetter.setService((ILocationSvc) arg1);
                
                
                try
                {
                	ILocationSvc service = svcGetter.getService(); 
                    service.RegisterListner(locationListener);
                    SPGeoPoint gp = service.getLocation();
                   
                    GeneralUser user = service.getUser();
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
   
                mapView.registerTapListener(new IMapTapDetect() 
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
  
      
        
        
////////startup dialog       
//
//Na
        final Account[] accounts = AccountManager.get(this).getAccounts();
        SPUtils.debug(accounts);
        if (SPUtils.DEBUGGING && null != accounts)
        {
            for (int i = 0; i < accounts.length; i++)
            {
                SPUtils.debug("accounts[" + i + "].name = " + accounts[i].name);
                SPUtils.debug("accounts[" + i + "].type = " + accounts[i].type);
            }
        }
        
        
        Dialog dialog = new Dialog(this);
        dialog.setCancelable(SPUtils.DEBUGGING); //@debug: Want to be able to cancel while developing.
        
        if (0 == accounts.length)
        {
            // TODO No Account In Sync dialog.
            Log.w("Share-A-Prayer", "No accounts found");
            
            dialog.setContentView(R.layout.dialog_startup_async);
            dialog.setTitle("title from code");
            
            ((Button) dialog.findViewById(R.id.dsa_button_exit)).setOnClickListener(new OnClickListener()
            {                
                public void onClick(View v)
                {
                    // TODO Auto-generated method stub
                    FindPrayer.this.finish();
                }
            });
            
            ((Button) dialog.findViewById(R.id.dsa_button_sync)).setOnClickListener(new OnClickListener()
            {                
                public void onClick(View v)
                {
                    // TODO Open Sync Center.
                }
            });
        }
        
        else
        {            
            dialog.setContentView(R.layout.dialog_startup_sync);
            // this.requestWindowFeature(Window.);
            // dialog.setFeatureDrawableResource(Window.FEATURE_LEFT_ICON,
            // R.drawable.icon);
            dialog.setTitle("title from code");
            // dialog.setTitle(R.id.image_row_root);
            
            // EditText more_text = (EditText)
            // dialog.findViewById(R.id.startup_name_first);
            // more_text.setText("");
            
            RadioGroup  accountsRadioGroup = (RadioGroup) dialog.findViewById(R.id.startup_accounts_radios);
            RadioButton tempRadioButton;
            
            for (int i = 0; i < accounts.length; i++)
            {
                tempRadioButton = new RadioButton(this);
                tempRadioButton.setId(i);
                tempRadioButton.setText(accounts[i].name);
                
                //tempRadioButton.setBackgroundColor(0x888888 / ((i % 2) + 1));
                //tempRadioButton.setGravity(Gravity.CENTER);
                
                accountsRadioGroup.addView(tempRadioButton);
            }
            
            accountsRadioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener()
            {
                //@Override
                public void onCheckedChanged(RadioGroup group, int checkedId)
                {
                    // TODO There is probably more to do
                    GeneralUser user = null;                    
                    
                    try
                    {
                        user = svcGetter.getService().getUser();                        
                    }
                    catch (UserNotFoundException e)
                    {
                        e.printStackTrace();
                    }                    
                    catch (ServiceNotConnected e)
                    {
                        e.printStackTrace();
                    }
                    
                    
                    if (null != user)
                    {
                        user.setName(accounts[checkedId].name);
                    }
                    
                    else
                    {
                        Log.w("*** SP ***", "RadioGroup Listener got a null user.");
                    }
                }
            });
            
        }        
        
        
        
        dialog.show();
//
// 
////////     
            
	}
	
	
	
	
	/**
	 * @menu
	 * 
	 * 
	 */
		

//    private MenuItem menuFindMe;
//    private MenuItem menuMyPlace;
    
	private String[]   menuItemesNames;
    private MenuItem[] menuItems;
	private Runnable[] menuListeners;
  
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        this.menuItemesNames = new String[]
        {
                "Find Me!", // 0
                "My Place"  // 1
        };
        
        int numOfMenuItems = this.menuItemesNames.length;
        
        this.menuItems = new MenuItem[numOfMenuItems];
        
        for (int i = 0; i < numOfMenuItems; i++)
        {
            this.menuItems[i] = menu.add(Menu.NONE, i, Menu.NONE, this.menuItemesNames[i]);
        }
        
        
//        {
//                menu.add(Menu.NONE, 0, Menu.NONE, "Find Me!"),
//                menu.add(Menu.NONE, 1, Menu.NONE, "My Place")
//        };
        
        this.menuListeners = new Runnable[numOfMenuItems];
        
        this.menuListeners[0] = new Runnable()
        {            
            public void run()
            {
                // TODO Auto-generated method stub
                try
                {
                    GeneralUser user = svcGetter.getService().getUser();
                    mapView.getController().setCenter(SPUtils.toGeoPoint(user.getSpGeoPoint()));
                }
                catch (UserNotFoundException e)
                {
                }
                catch (UnknownLocationException e)
                {
                }
                catch (ServiceNotConnected e)
                {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        };
        
        this.menuListeners[1] = new Runnable()
        {            
            public void run()
            {
                // TODO Auto-generated method stub
            }
        };
           
        
      
//        menuFindMe  = menu.add("Find Me!");
//        menuMyPlace = menu.add("My Place");
        
        return super.onCreateOptionsMenu(menu);
        
    }	
    
    
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // TODO Auto-generated method stub
        return super.onOptionsItemSelected(item);
    }
    
    
    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item)
    {
        int itemId = item.getItemId();         
        
        this.menuListeners[itemId].run();
        
        
//        if (menuFindMe.getItemId() == itemId)
//        {
//            try
//            {
//                GeneralUser user = svcGetter.getService().getUser();
//                mapView.getController().setCenter(SPUtils.toGeoPoint(user.getSpGeoPoint()));
//            }
//            catch (UserNotFoundException e)
//            {                
//            }
//            catch (UnknownLocationException e)
//            {
//            }
//            catch (ServiceNotConnected e)
//            {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//            }
//        }
//        
//        else if (menuMyPlace.getItemId() == itemId)
//        {
//            // TODO
//            //GeneralPlace place;
//        }
//        
//        else
//        {
//            // Should never get here!
//            throw new RuntimeException("WTFException");
//        }
        
        return super.onMenuItemSelected(featureId, item);
    }
	
    
    
}

