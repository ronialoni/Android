package il.ac.tau.team3.shareaprayer;

 

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import android.content.SharedPreferences;

import il.ac.tau.team3.addressQuery.MapsQueryLocation;
import il.ac.tau.team3.common.GeneralPlace;
import il.ac.tau.team3.common.GeneralUser;
import il.ac.tau.team3.common.SPGeoPoint;
import il.ac.tau.team3.common.SPUtils;
import il.ac.tau.team3.common.UnknownLocationException;
import il.ac.tau.team3.spcomm.ACommHandler;
import il.ac.tau.team3.spcomm.ICommHandler;
import il.ac.tau.team3.spcomm.SPComm;
import il.ac.tau.team3.uiutils.UIUtils;
import il.ac.tau.team3.uiutils.UIUtils.ISPMenuItem;
import il.ac.tau.team3.uiutils.UIUtils.ISPOnMenuItemSelectedListener;
import il.ac.tau.team3.uiutils.UIUtils.SPMenu;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.util.Log; 
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.MotionEvent;
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
import android.content.SharedPreferences.Editor;
import android.graphics.drawable.Drawable;





public class FindPrayer 
extends MapActivity 
{
	public final static int SHAHARIT = 4;
	public final static int MINHA = 2;
	public final static int ARVIT = 1;
    	
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
		} catch (NullPointerException e) {
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
    	                            thisUser));
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
    	                                usersOverlayList.add(new UserOverlayItem(user));
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
    	                        }catch (NullPointerException e)
    	                        {}
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
	private StatusBarOverlay statusBar; 
	
	
	
		
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
	    					statusBar.write("refreshing...", 4000);
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
	protected void onStart ()	{
		super.onStart();
		
		bindService(new Intent(LocServ.ACTION_SERVICE), svcConn, BIND_AUTO_CREATE);
        
        Toast toast = Toast.makeText(getApplicationContext(), "Long tap on map to create a new place", Toast.LENGTH_LONG);
        toast.show();
		
	}
        
	private void registerUser(GeneralUser user)	{
		 publicPlaceOverlay.setThisUser(user);
         closestPlaceOverlay.setThisUser(user);
         refreshTask.start();
         try {
			mapView.getController().setCenter(SPUtils.toGeoPoint(user.getSpGeoPoint()));
		} catch (UnknownLocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
      
         synchronized(refreshTask){
         	refreshTask.notify();
         };
	}
    	
		
	@Override
	public void onCreate(Bundle savedInstanceState) 	
	{		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		//mapView = new SPMapView(this);
		mapView = (SPMapView) findViewById(R.id.MAINview1);
		editText = (EditText) findViewById(R.id.addressBar);
		//UIUtils.activity = this;
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
        			statusBar.write("searching for the place...", 2000);
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
										statusBar.write("place found!", 2000);
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
        userDefaultMarker = this.getResources().getDrawable(R.drawable.user_red_sruga);
        // create objectan PrayerArrayItemizedOverlay for the user
		userOverlay       = new PrayerArrayItemizedOverlay(userDefaultMarker, this);
		 // add the PrayerArrayItemizedOverlay to the MapView
        mapView.getOverlays().add(userOverlay);
        
        // create an PrayerArrayItemizedOverlay for the user
        searchQueryOverlay = new PrayerArrayItemizedOverlay(userDefaultMarker, this);
		 // add the PrayerArrayItemizedOverlay to the MapView
        mapView.getOverlays().add(searchQueryOverlay);
        
        
        statusBar = new StatusBarOverlay(mapView.getPaddingTop() + 24, mapView.getWidth() / 100, 24);
        mapView.getOverlays().add(statusBar);
        
        /*
         * Synagouge overlay
         */
        synagougeMarker    = this.getResources().getDrawable(R.drawable.place_white);
      
        publicPlaceOverlay = new PlaceArrayItemizedOverlay(synagougeMarker, this);
        mapView.getOverlays().add(publicPlaceOverlay);
       
                
        synagougeClosestMarker    = this.getResources().getDrawable(R.drawable.place_white_david);
        closestPlaceOverlay = new PlaceArrayItemizedOverlay(synagougeClosestMarker, this);
        mapView.getOverlays().add(closestPlaceOverlay);
        
        /*
         * Other users overlay and icons:
         */
		othersDefaultMarker = this.getResources().getDrawable(R.drawable.user_blue_sruga);
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
                registerUser(user);
				if (!refreshTask.isAlive())	{
					refreshTask.start();
				}
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
                    
                    
                    // send the user to places overlay
                }
                catch (ServiceNotConnected e)
                {
                    Log.e("ShareAPrayer", "Service is not connected", e);
                } catch (UserNotFoundException e)	{
                		
                		String[] names; 
	                    Account[] accounts = AccountManager.get(FindPrayer.this).getAccounts();
	                    try	{
	                    	names = UIUtils.HandleFirstTimeDialog(accounts, FindPrayer.this);
	                    	service.setNames(names);
	                    } catch (NullPointerException e_)	{
	                    	// no accounts
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

        

        //FIXME @Matan: IMapTapDetect doesn't seem to invoke ANYTHING when the there is NO MOVEMENT.
        
        /**
         * TODO Decide where this should be:  
         *          1) return correct booleans, thus onTouchEvent(MotionEvent) will handle.
         *          2) built in Matan's design.
         *          3) in initializeMenu().  - because we KNOW it will be called once, one first menu show.
         *          4) here.  - because No.3 is not a good reason.
         *          5) maybe it belongs earlier in onCreate... don't know!   
         */
        mapView.registerTapListener(new IMapTapDetect()
        {
            
            public void onMoveEvent(SPGeoPoint sp)
            {
                SPUtils.debugFuncStart("**?** mapView.IMapTapDetect.onMoveEvent", sp);
                if (SPMenu.isShowing(FindPrayer.this.menu))
                {
                    FindPrayer.this.menu.hide();
                }
            }
            
            public void onTouchEvent(SPGeoPoint sp)
            {
                SPUtils.debugFuncStart("**?** mapView.IMapTapDetect.onTouchEvent", sp);
                if (SPMenu.isShowing(FindPrayer.this.menu))
                {
                    FindPrayer.this.menu.hide();
                }
            }
            
        });        
        
        
	}//@END: onCreate(..)
	
	
	
	
    /**
     * @menu
     * 
     * 
     */
    
    
    public enum ESPMenuItem
    implements ISPMenuItem
    {
        FIND_ME (0, R.drawable.menu_item_searchmap_compas),
        EXIT    (1, R.drawable.menu_item_exit_door_greener),
        ;
                
        private final int itemId;
        private final int resIconId;
                
        private ESPMenuItem(int itemId, int resIconId)
        {
            this.itemId    = itemId;
            this.resIconId = resIconId;
        }
        
        
        public String getTitle()
        {
            return this.toString().replace('_', ' ').toUpperCase();
        }
        
        public int getItemId()
        {
            return itemId;
        }
        
        public int getResIconId()
        {
            
            return resIconId;
        }
        
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
            SPUtils.error("centerMap", sne);
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
            SPUtils.error("centerMap", unfe);
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
            SPUtils.error("centerMap", ule);
            ule.printStackTrace();
            return;
        }
        
        this.mapView.getController().setCenter(SPUtils.toGeoPoint(center));
        
// I will enable if it happens.
//
//        catch (NullPointerException npe)
//        {
//            // XXX  (_FIXME_)
//            SPUtils.error("NullPointerException - Should have been WRAPED !!!", npe);
//            npe.printStackTrace();
//        } 
        
        
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
    private SPMenu<ESPMenuItem> menu = null;
    
    
    /**
     * @imp Lazy-initialization.
     */
    private void initializeMenu()
    {        
        if (null == this.menu)
        {   
            this.menu = new SPMenu<ESPMenuItem>(ESPMenuItem.values(), new ISPOnMenuItemSelectedListener<ESPMenuItem>()
            {
                public void onMenuItemSelected(ESPMenuItem item, View view)
                {
                    switch (item)
                    {
                        case FIND_ME:
                            FindPrayer.this.centerMap();
                            FindPrayer.this.menu.hide();
                            break;
                            
                        case EXIT:
                            FindPrayer.this.menu.hide();
                            FindPrayer.this.finish();   //FIXME Learn to end the activity !!!
                            break;
                        
                    }
                    
                    //XXX Maybe hide the menu smarter.
                }
            });
        }
    }
    
    
    
    
    /**
     * @callBack On first menu button push.
     * @invokes  onPrepareOptionsMenu().
     * @param    Menu menu: 
     *               Ignored. 
     *               TODO Try to make system release it.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        SPUtils.debugFuncStart("onCreateOptionsMenu", menu);
        this.initializeMenu();
        
        //super.onCreateOptionsMenu(menu);
        return true; 
    }
    
    
    /**
     * Handles all about menu showing, even closing.
     * @callBack On menu button push.
     * @pre      onCreateOptionsMenu(menu).
     * @param    Menu menu: 
     *               Ignored. 
     *               TODO Try to make system release it.
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu)
    {
        SPUtils.debugFuncStart("onPrepareOptionsMenu", menu);
        
        this.menu.handleMenuButtonClick(this, R.id.MAINview1);
        //super.onPrepareOptionsMenu(menu);
        return true; 
    }
    
    
    
    
     
    
    @Override   
    public void onBackPressed()
    {
        if (SPMenu.isShowing(this.menu))
        {
            //@imp I can actually just hide, but I want to show the pattern.
            this.menu.hide();
        }
        
        else
        {
            super.onBackPressed(); 
        }
    }
    
 
    
    
    // DO NOT DELETE THE FOLLOWING...
    
    
    
    
//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event)
//    {
//        if (keyCode == KeyEvent.KEYCODE_MENU)
//        {
//            SPUtils.debug("*** Menu Key pressed !!!!");
//            
//            this.onCreateOptionsMenu(null);
//            return true; // always eat it!
//        }
//        
//        return super.onKeyDown(keyCode, event);
//    }
    
    
   @Override
   public boolean onTouchEvent(MotionEvent event)
   {
       SPUtils.debugFuncStart("**?** onTouchEvent", event);
       return super.onTouchEvent(event);
   }  
   
    
////  Unused:
//  
    @Override
    public void onOptionsMenuClosed(Menu menu)
    {
        SPUtils.debugFuncStart("**?** onOptionsMenuClosed", menu);
        super.onOptionsMenuClosed(menu);
    }
     
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        SPUtils.debugFuncStart("**?** onOptionsItemSelected", item);
        return super.onOptionsItemSelected(item);
    }   
    
    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item)
    {
        SPUtils.debugFuncStart("**?**  onMenuItemSelected", featureId, item);
        return super.onMenuItemSelected(featureId, item);
    }   
//
////
    
    
    
    // THANK YOU.
    
    
    


	public void setUser(String[] names) {
		// TODO Auto-generated method stub
		try	{
			ILocationSvc service = svcGetter.getService();
			service.setNames(names);
		} catch (ServiceNotConnected e) {
			
		}
		
	}
	
    public static class StringArray{
    	private String[] stringArray;
    	private int index;

    	public StringArray(){
    		this.stringArray = new String[1];
    		this.index = 0;
    	}
    	
    	public StringArray(int size){
    		this.stringArray = new String[size];
    		this.index = 0;
    	}
    	
		public void setStringArray(String[] stringArray) {
			this.stringArray = stringArray;
		}

		public String[] getStringArray() {
			return stringArray;
		}

		public void setIndex(int index) {
			this.index = index;
		}
		// Same as getSize()
		public int getIndex() {
			return index;
		}
    	
		public void insert(String str){
			this.stringArray[this.index] = str;
			this.index++;
		}
    	
    	public String getEverything(){
    		String result = "";
    		for (int i=0; i<this.index; i++){
    			result = result + this.stringArray[i] + "\n";
    		}
    		return result;
    	}
    	
    }
    
}

