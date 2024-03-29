package il.ac.tau.team3.shareaprayer;

 

import il.ac.tau.team3.addressQuery.MapsQueryLocation;
import il.ac.tau.team3.addressQuery.MapsQueryLonLat;

import il.ac.tau.team3.common.GeneralPlace;
import il.ac.tau.team3.common.GeneralUser;
import il.ac.tau.team3.common.SPGeoPoint;
import il.ac.tau.team3.common.SPUtils;
import il.ac.tau.team3.common.UnknownLocationException;

import il.ac.tau.team3.spcomm.ACommHandler;
import il.ac.tau.team3.spcomm.ICommHandler;
import il.ac.tau.team3.spcomm.SPComm;


import il.ac.tau.team3.uiutils.ISPMenuItem;
import il.ac.tau.team3.uiutils.MenuFacebookUtils;
import il.ac.tau.team3.uiutils.MenuSettingsUtils;
import il.ac.tau.team3.uiutils.MenuStatusUtils;
import il.ac.tau.team3.uiutils.MenuUtils;
import il.ac.tau.team3.uiutils.SPMenu;
import il.ac.tau.team3.uiutils.PlacesDetailsUI;
import il.ac.tau.team3.uiutils.SPMenu.ISPOnMenuItemSelectedListener;
import il.ac.tau.team3.uiutils.SPMenus;
import il.ac.tau.team3.uiutils.SPMenus.ESPMenuItem;
import il.ac.tau.team3.uiutils.SPMenus.ESPSubMenuFind;
import il.ac.tau.team3.uiutils.SPMenus.ESPSubMenuPlaces;
import il.ac.tau.team3.uiutils.UIUtils;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


import org.mapsforge.android.maps.GeoPoint;
import org.mapsforge.android.maps.ItemizedOverlay;
import org.mapsforge.android.maps.MapActivity;
import org.mapsforge.android.maps.OverlayItem;
import org.mapsforge.android.maps.PrayerArrayItemizedOverlay;


import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.IBinder;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.TextView;
import android.widget.Toast;





public class FindPrayer 
extends MapActivity
{
	public final static int SHAHARIT = 4;
	public final static int MINHA = 2;
	public final static int ARVIT = 1;
    	
	private Drawable userDefaultMarker; 
	private Drawable glowClosestMarker;
	private Drawable othersDefaultMarker;
	private Drawable synagougeMarker;
	private Drawable synagougeClosestMarker;
	private Drawable searchMarker;
	
	private PrayerArrayItemizedOverlay userOverlay;
	private PrayerArrayItemizedOverlay searchQueryOverlay;
	private PrayerArrayItemizedOverlay otherUsersOverlay;
	private PlaceArrayItemizedOverlay publicPlaceOverlay;
	private PlaceArrayItemizedOverlay closestPlaceOverlay;
	
	private SPComm comm = new SPComm();
	private FacebookConnector facebookConnector = null;
	
	private boolean tracking_mode = true;

	public IStatusWriter getStatusBar()	{
		return statusBar;
	}
	
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
			Log.d("FindPrayer:determineClosestPlace","Unable to find user");
			return null;
		} catch (UnknownLocationException e)	{
			//Log.e("FidPrayer:determineClosestPlace",e.getMessage());
			Log.d("FindPrayer:determineClosestPlace","Unknown location");
			return null;
		} catch (ServiceNotConnected e) {
			Log.d("FindPrayer:determineClosestPlace","service not connected");
			//Log.d("FinndPrayer:determineClosestPlace",e.getMessage());
			return null;
		} catch (NullPointerException e) {
			e.printStackTrace();
			//Log.d("FindPrayer:determineClosestPlace",e.getMessage());
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
    	            	ILocationSvc locSvc;
    	            	GeneralUser thisUser = null;
    	            	
    	                try 
    	                {
							locSvc = svcGetter.getService();
						}
    	                catch (ServiceNotConnected e) 
						{
    	                	Log.d("FindPrayer:updateUsersOnMap","service not connected");
							e.printStackTrace();
							return;
						}
    	                
    	                try
    	                {
    	                	thisUser = locSvc.getUser();
    	                }
    	                catch (UserNotFoundException e) 
    	                {
    	                	Log.d("FindPrayer:updateUsersOnMap","user not found exception");
    	                	e.printStackTrace();
    	                	return;
						}
    	                catch (NullPointerException e)
    	                {
    	                	Log.d("FindPrayer:updateUsersOnMap",e.getMessage());
    	                	e.printStackTrace();
    	                	
    	                }
    	                
    	                boolean drewThisUser = false;
    	                
    	                try
    	                {	
    	                    
    	                    List<UserOverlayItem> userOverlayList = new ArrayList<UserOverlayItem>();
    	                    userOverlayList.add(new UserOverlayItem(thisUser));
                            userOverlay.changeItems(userOverlayList);
                            drewThisUser = true;
    	                }
    	                catch (UnknownLocationException e)
    	                {
    	                	
    	                    e.printStackTrace();
    	                }
    	                catch (NullPointerException e)
    	                {
    	                	Log.d("FindPrayer:updateUsersOnMap",e.getMessage());
    	                    e.printStackTrace();
    	                }
                        
    	                
    	                if (null != users)
    	                {
    	                    List<UserOverlayItem> usersOverlayList = new ArrayList<UserOverlayItem>(users.length);
    	                    for (GeneralUser user : users)
    	                    {
    	                        try
    	                        {
    	                            if (!thisUser.getName().equals(user.getName()))
    	                            {
    	                                usersOverlayList.add(new UserOverlayItem(user));
    	                            } else if (!drewThisUser)	{
    	                            	List<UserOverlayItem> userOverlayList = new ArrayList<UserOverlayItem>();
    	        	                    userOverlayList.add(new UserOverlayItem(user));
    	                                userOverlay.changeItems(userOverlayList);
    	                                drewThisUser = true;
    	                            }
    	                        }
    	                        catch (UnknownLocationException e)
    	                        {
    	                        	Log.d("FindPrayer:updateUsersOnMap","unknown location exception");
    	                            e.printStackTrace();
    	                        }
    	                        catch (NullPointerException e)
    	                        {
    	                        	Log.d("FindPrayer:updateUsersOnMap",e.getMessage());
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
    							GeneralPlace closestPlace = determineClosestPlace(places);
    					        if(closestPlace!=null){
    					        	List<PlaceOverlayItem> closestPlacesOverlayList = new ArrayList<PlaceOverlayItem>();
    					        	try {
										closestPlacesOverlayList.add(new PlaceOverlayItem(closestPlace, closestPlace.getName(), closestPlace.getAddress(), synagougeClosestMarker, glowClosestMarker));
									} catch (UnknownLocationException e) {
										e.printStackTrace();
									}
    					        	closestPlaceOverlay.changeItems(closestPlacesOverlayList);
    					        } else	{
    					        	closestPlaceOverlay.clear();
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
												Log.d("FindPrayer:updatePlacesOnMap","unknown location");
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
    
    	
	private ServiceConnector  svcGetter = new ServiceConnector();
	
	public ServiceConnector getSvcGetter() {
		return svcGetter;
	}


	private ILocationProv locationListener;
	private StatusBarOverlay statusBar; 
	
		
	private SPMapView mapView;
	private EditText  editText;
	
	public void close()	{
		try	{
			refreshTask.interrupt();
			refreshTask.join();
		} catch (InterruptedException 	e)	{
			return;
		}
		
	}
	
    private Thread    refreshTask = new Thread()
    {
    	private int checkLocationCounter = 0;
    	
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
	    				
	    					updateUsersOnMap(SPUtils.toSPGeoPoint(mapView.getMapCenter()));
	    					updatePlacesOnMap(SPUtils.toSPGeoPoint(mapView.getMapCenter()));
	    					statusBar.write("refreshing...", R.drawable.action_refresh, 1000);
	    					if (checkLocationCounter++ % 30 == 0)	{
		    					try {
									android.location.Location loc = getSvcGetter().getService().getRecentLocationFix();
									Calendar cal = Calendar.getInstance();
									cal.add(Calendar.SECOND, -125);
									if ((null == loc) || (loc.getTime() < (cal.getTimeInMillis())))	{
										statusBar.write("lost location fix", R.drawable.antenna_image, 2000);
									}
								} catch (ServiceNotConnected e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
	    					}
	    					
	    			
    				} catch (NullPointerException e)	{
    					//Log.d("FindPrayer",e.getMessage());
    					e.printStackTrace();
    					statusBar.write("Unable to connect to server.", R.drawable.status_bar_error_icon, 1000);
    					
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
        statusBar.closeHandler();
        comm.closeHandler();
        close();
        super.onDestroy();
    }
	
	@Override
	protected void onStart ()	
	{
		super.onStart();
		
		Display d = ((WindowManager)getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
		int width = d.getWidth();
		int height = d.getHeight();
		
		statusBar = new StatusBarOverlay(this, mapView.getPaddingTop() + height/40, width / 100, height/32);
        mapView.getOverlays().add(statusBar);
		
		bindService(new Intent(LocServ.ACTION_SERVICE), svcConn, BIND_AUTO_CREATE);
        
        Toast toast = Toast.makeText(getApplicationContext(), "Long tap on map to create a new place", Toast.LENGTH_LONG);
        toast.show();
        
        LocationManager locMgr = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if(!locMgr.isProviderEnabled(LocationManager.GPS_PROVIDER) && !locMgr.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
        	UIUtils.createAlertDialog(this.getResources().getString(R.string.NoLocationProviderMsg), this, "OK");
        }
        
        ConnectivityManager connectivityManager  
        = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE); 
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo(); 

        if(null == activeNetworkInfo){
        	UIUtils.createAlertDialog(this.getResources().getString(R.string.NoInternetConnectionMsg), this, "OK");
        }
		
        
	}
        
	private void registerUser(final GeneralUser user)	{

		synchronized(refreshTask)	{
			if (!refreshTask.isAlive())	{ 
				refreshTask.start();
			}
		}

		FindPrayer.this.runOnUiThread(new Runnable(){

			public void run() {
				try {
					try {
						user.setSpGeoPoint(svcGetter.getService().getLocation());
					} catch (ServiceNotConnected e) {

					}
					if ((0 == user.getSpGeoPoint().getLatitudeE6()) && (0 == user.getSpGeoPoint().getLongtitudeE6())){
						throw new UnknownLocationException();
					}

					mapView.getController().setCenter(SPUtils.toGeoPoint(user.getSpGeoPoint()));


				} catch (UnknownLocationException e) {
					comm.requestGetUserById(user.getId(), new ACommHandler<GeneralUser>(){
						public void onRecv(final GeneralUser Obj)	{
							FindPrayer.this.runOnUiThread(new Runnable(){

								public void run() {
									try {
										if (!((0 == Obj.getSpGeoPoint().getLatitudeE6()) && (0 == Obj.getSpGeoPoint().getLongtitudeE6()))){
											mapView.getController().setCenter(SPUtils.toGeoPoint(Obj.getSpGeoPoint()));
										} else	{
											final Timer timer = new Timer();
											TimerTask timerTask = new TimerTask()	{
												@Override
												public void run() {
													try {
														user.setSpGeoPoint(svcGetter.getService().getLocation());
														if ((0 == user.getSpGeoPoint().getLatitudeE6()) && (0 == user.getSpGeoPoint().getLongtitudeE6())){
															throw new UnknownLocationException();
														}
														FindPrayer.this.runOnUiThread(new Runnable()	{

															public void run() {
																try {
																	mapView.getController().setCenter(SPUtils.toGeoPoint(user.getSpGeoPoint()));
																} catch (UnknownLocationException e) {
																}
															}
														});
														this.cancel();
														timer.cancel();

													} catch (ServiceNotConnected e) {
														return;
													} catch (UnknownLocationException e) {
														return;
													}

												}														
											};

											timer.schedule(timerTask, 1000, 1000);		


										}
									} catch (UnknownLocationException e) {
										e.printStackTrace();
									}
								}
							});
						}
					});
				}
			}
		});

		synchronized(refreshTask){
			refreshTask.notify();
		};
	}
    	
	
	@Override
	public void onCreate(Bundle savedInstanceState) 	
	{		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		mapView = (SPMapView) findViewById(R.id.view1);
		editText = (EditText) findViewById(R.id.addressBar);
		UIUtils.initSearchBar(editText);
        mapView.registerTapListener(new IMapTapDetect()	
        {
			public void onTouchEvent(SPGeoPoint sp)  {
				NewPlaceCall(sp);
			}
			
			public void onMoveEvent(SPGeoPoint sp)	{
				tracking_mode = false;
			}
        });
        
        
        
        editText.setOnEditorActionListener (new EditText.OnEditorActionListener()	{


        	public boolean onEditorAction(final TextView v, int actionId, KeyEvent event) {
        		if ((EditorInfo.IME_ACTION_DONE == actionId) || ((event != null) && 
        				(event.getAction() == KeyEvent.ACTION_DOWN) && 
        				(event.getKeyCode() == KeyEvent.KEYCODE_ENTER)))
        		{
        			statusBar.write("Searching for the place...", R.drawable.action_refresh, 2000);
        			
        			editText.setBackgroundResource(R.drawable.selector_edittext_yellow);
        			editText.refreshDrawableState();
        			
        			comm.searchForAddress(v.getText().toString(), new ACommHandler<MapsQueryLonLat[]>() 
					{
						@Override
						public void onRecv(final MapsQueryLonLat[] Obj)	
						{
							FindPrayer.this.runOnUiThread(new Runnable() 
							{
								public void run() 
								{
									try	
									{
										double latitude = Obj[0].getLat();
										double longitude = Obj[0].getLon(); 
										
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
										
										statusBar.write("Search: place found!", R.drawable.status_bar_accept_icon, 2000);
										
										editText.setBackgroundResource(R.drawable.selector_edittext_green);
										editText.refreshDrawableState();
									} 
									catch (NullPointerException e)
									{
										if(statusBar != null){
										statusBar.write("Search: Place wasn't found.", R.drawable.status_bar_error_icon, 2000);
										}
										//Log.d("FindPrayer",e.getMessage());
										e.printStackTrace();
										onError(Obj);
									} 
									catch (ArrayIndexOutOfBoundsException e)	
									{
										if(statusBar != null){
										statusBar.write("Search: Place wasn't found.", R.drawable.status_bar_error_icon, 2000);
										}
										e.printStackTrace();
										//Log.d("FindPrayer",e.getMessage());
										onError(Obj);
									}	
								}
							
							});//@END: Runnable.
						}
						
						
						@Override
						public void onError(MapsQueryLonLat[] Obj) 
						{
							FindPrayer.this.runOnUiThread(new Runnable() {
								public void run() 
								{
									FindPrayer.this.editText.setBackgroundResource(R.drawable.selector_edittext_red);	
									FindPrayer.this.editText.refreshDrawableState();
									//super.onError(Obj);

								}
							});
						}
						
						
						
					});
					
					return true;
				}
        		
        		editText.setBackgroundResource(R.drawable.selector_edittext_yellow);
				return false;
			}
        	
        });
        
        
            
        
        /*
         * User overlay and icon:
         */ 
        userDefaultMarker = ItemizedOverlay.boundCenterBottom(this.getResources().getDrawable(R.drawable.user_red_sruga));
		userOverlay       = new UserArrayItemizedOverlay(userDefaultMarker, this);
        mapView.getOverlays().add(userOverlay);
        
        searchMarker = ItemizedOverlay.boundCenterBottom(this.getResources().getDrawable(R.drawable.search_found_icon_green));
        searchQueryOverlay = new PrayerArrayItemizedOverlay(searchMarker, this);
        mapView.getOverlays().add(searchQueryOverlay);
        
        /*
         * Synagouge overlay
         */
        synagougeMarker    = this.getResources().getDrawable(R.drawable.place_white);
      
        publicPlaceOverlay = new PlaceArrayItemizedOverlay(synagougeMarker, this);
        mapView.getOverlays().add(publicPlaceOverlay);
       
                
        synagougeClosestMarker    = this.getResources().getDrawable(R.drawable.place_white_david);
        glowClosestMarker    = this.getResources().getDrawable(R.drawable.place_glow_thin);
        closestPlaceOverlay = new PlaceArrayItemizedOverlay(synagougeClosestMarker, this);
        mapView.getOverlays().add(closestPlaceOverlay);
        
        /*
         * Other users overlay and icons:
         */
		othersDefaultMarker = this.getResources().getDrawable(R.drawable.user_blue_sruga);
		otherUsersOverlay   = new UserArrayItemizedOverlay(othersDefaultMarker, this);
		mapView.getOverlays().add(otherUsersOverlay);	 
		
        // Define a listener that responds to location updates
    	locationListener = new ILocationProv() 
    	{
	    	// Called when a new location is found by the network location provider.
			public void LocationChanged(final SPGeoPoint point) 
			{
				FindPrayer.this.runOnUiThread(new Runnable(){

					public void run() {
						if (tracking_mode)	{
							mapView.getController().setCenter(SPUtils.toGeoPoint(point));
						}

						synchronized(refreshTask)	
						{
							refreshTask.notify();
						}
					}
				});
			    	
    	    }

			public void OnUserChange(final GeneralUser user) 
			{
				FindPrayer.this.runOnUiThread(new Runnable(){

					public void run() {
		                registerUser(user);
						if (! refreshTask.isAlive())	
						{
							refreshTask.start();
						}
					}
				});
			}
    	};
    	
    	
    	
   	
   	
        svcConn = new ServiceConnection()
        {
            
            public void onServiceDisconnected(ComponentName className)
            {
            	svcGetter.setService(null);
            }
            
            public void onServiceConnected(ComponentName arg0, IBinder arg1)
            {
            	svcGetter.setService((ILocationSvc) arg1);
                
            	ILocationSvc service = null;
            	
                try
                {
                	service = svcGetter.getService(); 
                    service.RegisterListner(locationListener);
                         
                    GeneralUser user = service.getUser();
                    
                    registerUser(user);
                 
                }
                catch (ServiceNotConnected e)
                {
                    Log.d("ShareAPrayer", "Service is not connected");
                } catch (UserNotFoundException e)	{
            		
            		String[] names;
            		List<Account> accounts = new LinkedList<Account>();
                    for (Account a : AccountManager.get(FindPrayer.this).getAccounts())	{
                    	if (a.name.contains("@"))	{
                    		accounts.add(a);
                    	}
                    }
                    
                    try	{
                    	names = UIUtils.HandleFirstTimeDialog(accounts.toArray(new Account[0]), FindPrayer.this);
                    	//service.setNames(names);
                    	
                    } catch (NullPointerException e_)	{
                    	e.printStackTrace();
                    }
				} 

	          
            }
           
        };
        
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
            
            
            @Override
			public void onMoveEvent(SPGeoPoint sp) {
				// TODO Auto-generated method stub
				ts.cancel();
                t.purge();
                ts = new TimerRefreshTask();
                t.schedule(ts, 1000);
			}
        });
        
       facebookConnector = new FacebookConnector(this);

       facebookConnector.connectOnStartup();
        
        
        /*
         * Registering one listener for passing all events to activity with out making it consume them.
         */
        mapView.registerTapListener(new IMapTapDetect()
        {
            /**
             * Delegating to the original function for comfort.
             * Note: The method is now final, because it's dangerous!.
             *       This way if we fix this bypasses, all the code will be in the appropriate place.
             */
            @Override
            public void onAnyEvent(MotionEvent event)
            {
                FindPrayer.this.onTouchEvent(event);
            }
        });        
        
        
	}
	
	public FacebookConnector getFacebookConnector() {
		return facebookConnector;
	}


	public void setFacebookConnector(FacebookConnector facebookConnector) {
		this.facebookConnector = facebookConnector;
	}


	@Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (null != facebookConnector)	{
        	facebookConnector.autherizeCallback(requestCode, resultCode, data);                                                  ////
        }
    }
	
	private void createWishToQuitDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(this.getResources().getString(R.string.AboutToQuitMsg));
		builder.setCancelable(false);
		builder.setNegativeButton("No",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.dismiss();
					}
				});
		builder.setPositiveButton("Yes",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						FindPrayer.this.finish();
				}
				});
		AlertDialog alert = builder.create();
		alert.show();
	}
	
		
    private void centerMap()
    {
        ILocationSvc service;
        try
        {
            service = this.svcGetter.getService();
        }
        catch (ServiceNotConnected sne)
        {
           
            sne.printStackTrace();
            return;
        }
        
        GeneralUser user;
        try
        {
            user = service.getUser();
        }
        catch (UserNotFoundException unfe)
        {
        
            unfe.printStackTrace();
            return;
        }
        
        SPGeoPoint center;
        try
        {
            center = user.getSpGeoPoint();
        }
        catch (UnknownLocationException ule)
        {
         
            ule.printStackTrace();
            return;
        }
        
        this.mapView.getController().setCenter(SPUtils.toGeoPoint(center));
    }
    
    
    
    private Account[] getAccounts()
    {
        Account[] accounts = AccountManager.get(FindPrayer.this).getAccounts();
        if (null == accounts)
        {
            accounts = new Account[0];
        }
        
        return accounts;  
    }    
    
    
    
/////////////////////////////////////////////////////////////////////////////////////////////////////
///////// Menu: /////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////////////////

    
    /**
     * The custom Options-Menu for this Activity.
     * @init  null:
     *            Dew to @imp restrains.
     * @imp   Lazy-initialization done via initializeMenu().
     */
    private SPMenu menu = null;
   
    
    
    /**
     * @imp Lazy-initialization.
     */
    private void initializeMenu()
    {   
       
        
        if (null == this.menu)
        {
            this.menu = new SPMenu(ESPMenuItem.values(), new ISPOnMenuItemSelectedListener()
            {
                public void onMenuItemSelected(ISPMenuItem item, View view)
                {
                    
                    final int id = item.id();
                   
                    if (id == SPMenus.ESPSubMenuFind.ME.id())
                    {
                    	tracking_mode = true;
                        FindPrayer.this.centerMap();
                        FindPrayer.this.menu.hide();
                    }
                    
                    else if (id == ESPMenuItem.FACEBOOK.id())
                    {
                    	FindPrayer.this.menu.hide();
                        new MenuFacebookUtils(FindPrayer.this);
                    
                    }
                                        
                    else if (id == ESPSubMenuFind.CLOSEST.id())
                    {
                        // Taking the closest (for now) from the map's overlay.
                        ArrayList<OverlayItem> listOfOneItemIfAnyOnMap = FindPrayer.this.closestPlaceOverlay.getOverlayItems();
                        if (null != listOfOneItemIfAnyOnMap && listOfOneItemIfAnyOnMap.size() > 0)
                        {
                            FindPrayer.this.mapView.getController().setCenter(listOfOneItemIfAnyOnMap.get(0).getPoint());
                        }
                        else
                        {
                            Toast.makeText(FindPrayer.this, "Sorry, there seem to be no places open for prayers.\nPlese consider creating one.", Toast.LENGTH_LONG).show();
                        }
                        
                        FindPrayer.this.menu.hide();
                    }   
                    
                  
                    
                    else if (id == SPMenus.ESPSubMenuFind.ADDRESS.id())
                    {
                        EditText edittext = (EditText) FindPrayer.this.findViewById(R.id.addressBar);
                        edittext.setVisibility(View.VISIBLE);
                        edittext.setFocusable(true);
                        
                        FindPrayer.this.menu.onMenuDismiss(new OnDismissListener()
                        {
                            public void onDismiss()
                            {
                                InputMethodManager keyboardMenager = (InputMethodManager) FindPrayer.this.getSystemService(INPUT_METHOD_SERVICE);
                                keyboardMenager.showSoftInput(FindPrayer.this.editText, InputMethodManager.SHOW_FORCED /* | InputMethodManager.SHOW_IMPLICIT */);
                            }
                        });
                        
                        // Apparently, only the sub gets closed...
                        FindPrayer.this.menu.hide(); // TODO this is BAD, make separate methods in SPMenu.
                        
                    } 
                    
                    else if (id == SPMenus.ESPSubMenuSettings.PROFILE.id())
                    {
                    	try {
							MenuSettingsUtils.createEditDetailsDialog(svcGetter.getService().getUser(), FindPrayer.this);
						} catch (UserNotFoundException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (ServiceNotConnected e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
                        FindPrayer.this.menu.hide();
                    }
                    
                    else if (id == SPMenus.ESPSubMenuSettings.VIEW.id())
                    {
                        MenuSettingsUtils.CreateChooseMinMaxDialog(FindPrayer.this);
                        FindPrayer.this.menu.hide();
                    } else if (id == SPMenus.ESPSubMenuPlaces.JOINED.id()){
                    		FindPrayer.this.menu.hide(); 
                    		new PlacesDetailsUI(FindPrayer.this, svcGetter, comm, "Places I joined to", PlacesDetailsUI.Actions.JOINER, publicPlaceOverlay);
                    		
							
                    } else if (id == SPMenus.ESPSubMenuPlaces.OWNED.id()){
                    	FindPrayer.this.menu.hide(); 
                    	new PlacesDetailsUI(FindPrayer.this, svcGetter, comm, "Places I own", PlacesDetailsUI.Actions.OWNER, publicPlaceOverlay);              
                    } else if (id == ESPSubMenuPlaces.CREATE.id())	{
                    	try {
							UIUtils.createNewPlaceDialog(null, FindPrayer.this, svcGetter.getService().getUser());
						} catch (UserNotFoundException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (ServiceNotConnected e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
                    }  else if (id == ESPMenuItem.STATUS.id()){
                    	try {
							MenuStatusUtils.createEditStatusDialog(svcGetter.getService().getUser(), FindPrayer.this);
						} catch (UserNotFoundException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (ServiceNotConnected e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
						 FindPrayer.this.menu.hide();
                    }                           
                    
                    
                    
                                                       
                    else if (id == ESPMenuItem.EXIT.id())
                    {
                        FindPrayer.this.menu.onMenuDismiss(new OnDismissListener()
                        {                            
                            public void onDismiss()
                            {
                                FindPrayer.this.finish();
                                //android.os.Process.killProcess(android.os.Process.myPid());
                            }
                        });                        

                    }   
                        
                    
                    else
                    {
                        if (!item.hasSubMenu()){
                      
                            FindPrayer.this.menu.hide();
   
                        }
                    }              
                  
                }
            });
        }
    }
    
    
    
    
    /**
     * @callBack On first menu button push.
     * @invokes  onPrepareOptionsMenu().
     * @param    Menu menu: 
     *               Ignored. 
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {    
        this.initializeMenu();
        return true; 
    }
    
    
    /**
     * Handles all about menu showing, even closing.
     * @callBack On menu button push.
     * @pre      onCreateOptionsMenu(menu).
     * @param    Menu menu: 
     *                Ignored. 
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu)
    {       	
    	this.menu.handleMenuButtonClick(this, R.id.view1);
      
        return true; 
    }
    
    
    
    /**
     * @final because of menu showing.
     * @pre   this.mapView.onTouchEvent()
     */
    @Override
    public final boolean onTouchEvent(MotionEvent event)
    {
        /* Handle menu */
        if (SPMenu.isShowing(this.menu))
        {
             this.menu.hide();  
            
        }
        
        return true;
    } 
    
    
    
    
    @Override   
    public void onBackPressed()
    {
        if (SPMenu.isShowing(this.menu))
        {
            this.menu.hide();
               
        }
        
        
        else
        {
        	this.createWishToQuitDialog();
            //super.onBackPressed(); 
        }
    }
    
    @Override
    public void onOptionsMenuClosed(Menu menu)
    {
        super.onOptionsMenuClosed(menu);
    }
     
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        return super.onOptionsItemSelected(item);
    }   
    
    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item)
    {
        return super.onMenuItemSelected(featureId, item);
    }   
 


	public void setUser(String[] names) {
		// TODO Auto-generated method stub
		try	{
			ILocationSvc service = svcGetter.getService();
			service.setNames(names);
		} catch (ServiceNotConnected e) {
			
		}
		
	}
	
	public void setStatus(String status)	{
		
			ILocationSvc service;
			try {
				service = svcGetter.getService();
				service.setStatus(status);
			} catch (ServiceNotConnected e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (UserNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		
	}
	
    
}

