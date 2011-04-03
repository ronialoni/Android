

package il.ac.tau.team3.shareaprayer;


import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
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

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.map.MappingJsonFactory;
import org.codehaus.jackson.map.ObjectMapper;
import org.mapsforge.android.maps.ArrayCircleOverlay;
import org.mapsforge.android.maps.PrayerArrayItemizedOverlay;
import org.mapsforge.android.maps.GeoPoint;
import org.mapsforge.android.maps.MapActivity;
import org.mapsforge.android.maps.MapView;
import org.mapsforge.android.maps.MapViewMode;
import org.mapsforge.android.maps.OverlayCircle;
import org.mapsforge.android.maps.OverlayItem;
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
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;


public class FindPrayer 
extends MapActivity 
{
	private Paint circleDefaultPaintFill;
	private Paint circleDefaultPaintOutline;
	private Drawable userDefaultMarker;
	private Drawable othersDefaultMarker;
	private Drawable synagougeMarker;
	private PrayerArrayItemizedOverlay userOverlay;
	private PrayerArrayItemizedOverlay otherUsersOverlay;
	private PlaceArrayItemizedOverlay publicPlaceOverlay;
	private ArrayCircleOverlay circleOverlay;
	
	public void drawUserOnMap(GeneralUser user)	
	{
        //Clears the last location
		userOverlay.clear();
		
		// create an OverlayItem with title and description
        UserOverlayItem item = new UserOverlayItem(user, user.getName(), user.getStatus());

        // add the OverlayItem to the PrayerArrayItemizedOverlay
        userOverlay.addItem(item);
	}
	
	public void drawOtherUserOnMap(GeneralUser otheruser)	
	{
		// create an OverlayItem with title and description
		UserOverlayItem other = new UserOverlayItem(otheruser, otheruser.getName(), otheruser.getStatus());

        // add the OverlayItem to the PrayerArrayItemizedOverlay
        otherUsersOverlay.addItem(other);
	}
	
	public void DrawPointOnMap(final MapView mapView, final SPGeoPoint a_point)	
	{
		//removes previous location from the map
		circleOverlay.clear();
		
		GeoPoint point = SPUtils.toGeoPoint(a_point);
		
		// set center
		mapView.getController().setCenter(point);
        OverlayCircle circle = new OverlayCircle(point, 16, "My Location");
        
        circleOverlay.addCircle(circle);

        // add the PrayerArrayItemizedOverlay to the MapView
        mapView.getOverlays().add(circleOverlay);
        
        return;
    }

	public void updatePublicPlace(GeneralPlace place)	{
		boolean found = false;
		for ( OverlayItem item : publicPlaceOverlay.getOverlayItems()){
			PlaceOverlayItem placeItem = (PlaceOverlayItem) item;
			if (place.getId().equals(((GeneralPlace)placeItem.getPlace()).getId()))	{
				publicPlaceOverlay.removeItem(item);
				placeItem.setPlace(place);
				publicPlaceOverlay.addItem(item);
				found = true;
				break;
			}
			

		}
		if (!found)	{
			drawPublicPlaceOnMap(place);

		}
	}


	public void updateUser(GeneralUser user, GeneralUser thisUser)	{
		boolean found = false;
		for ( OverlayItem item : otherUsersOverlay.getOverlayItems()){
			UserOverlayItem userItem = (UserOverlayItem) item;
			if ((user.getId().equals(((GeneralUser)userItem.getUser()).getId()))	&&
				 ((thisUser == null) || (!thisUser.getId().equals(userItem.getUser().getId()))))	{
				
				otherUsersOverlay.removeItem(item);
				userItem.setUser(user);
				otherUsersOverlay.addItem(item);
				found = true;
				break;
			}
		}
		if (!found)	{
			if ((thisUser == null) || (!thisUser.getId().equals(user.getId())))	{ 
				drawOtherUserOnMap(user);
			}
		}

	}

	
	public void drawPublicPlaceOnMap(GeneralPlace place)	
	{
		// create an OverlayItem with title and description
		String address = place.getAddress();
		String name = place.getName();
        PlaceOverlayItem synagouge = new PlaceOverlayItem(place, name, address);

        // add the OverlayItem to the PrayerArrayItemizedOverlay
        publicPlaceOverlay.addItem(synagouge);
        return;
    }
	
	private RestTemplate restTemplate;
	
	public RestTemplate getRestTemplate() {
		return restTemplate;
	}

	public void setRestTemplate(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}

	private ILocationSvc service = null;
	private ILocationProv locationListener;
	
	@Override
	public void onDestroy()	{
		if (null != service)	{
			service.UnRegisterListner(locationListener);
		}
	}
		
	
	
	public static double EARTH_RADIUS_KM = 6384;// km


	

	public static double calculateDistanceMeters(double aLong, double aLat,
	         double bLong, double bLat) {

	      double d2r = (Math.PI / 180);

	      double dLat = (bLat - aLat) * d2r;
	      double dLon = (bLong - aLong) * d2r;
	      double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
	            + Math.cos(aLat * d2r) * Math.cos(bLat * d2r)
	            * Math.sin(dLon / 2) * Math.sin(dLon / 2);
	      double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

	      return EARTH_RADIUS_KM * c * 1000;

	};
	
	private SPMapView mapView;
	
	private void updateMap(SPGeoPoint center)	{
		if (center == null)	{
			return;
		}
		
		mapView.getController().setCenter(SPUtils.toGeoPoint(center));
		
		GeneralUser thisUser = null;
		
		if (service != null)	{
			thisUser = service.getUser();
			if (null != thisUser)	{
				drawUserOnMap(thisUser);
			}
		}
		
		GeoPoint screenEdge = mapView.getProjection().fromPixels(mapView.getWidth(), mapView.getHeight());
		if (screenEdge == null)	{
			return;
		}
		
		double distance = calculateDistanceMeters(center.getLongitudeInDegrees(), center.getLatitudeInDegrees(), screenEdge.getLongitude(), screenEdge.getLatitude());
		
		int distancemeters = (int)Math.ceil(distance)*100;
		
		
		
		
		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("latitude", new Double(center.getLatitudeInDegrees()).toString());
		parameters.put("longitude", new Double(center.getLongitudeInDegrees()).toString());
		parameters.put("radius", new Integer(distancemeters).toString());
		
		GeneralUser[] users = restTemplate.getForObject("http://share-a-prayer.appspot.com/resources/prayerjersy/users?latitude={latitude}&longitude={longitude}&radius={radius}", GeneralUser[].class, parameters);
		
		
		if (null != users)
		{
			for (GeneralUser user : users)	{
				updateUser(user, thisUser);
				
			}
		}
		
		GeneralPlace[] places = restTemplate.getForObject("http://share-a-prayer.appspot.com/resources/prayerjersy/places?latitude={latitude}&longitude={longitude}&radius={radius}", GeneralPlace[].class, parameters);
		
			
		if (null != places)
		{
			for (GeneralPlace place : places)	{
				updatePublicPlace(place);
				
			}
		}
	}
	
	public void createNewPlaceDialog(String message, final SPGeoPoint point)
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(message);
		builder.setCancelable(false);
		builder.setPositiveButton("Yes", new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int id) 
			{
				GeneralPlace newMinyan = new GeneralPlace("New Minyan Place", "", point);
				newMinyan.addJoiner(service.getUser().getName());
				StringWriter sw = new StringWriter();
				ObjectMapper mapper = new ObjectMapper();
				MappingJsonFactory jsonFactory = new MappingJsonFactory();
				try	{
					JsonGenerator jsonGenerator = jsonFactory.createJsonGenerator(sw);
					mapper.writeValue(jsonGenerator, newMinyan);
					sw.close();
				} catch (Throwable T)	{
					
				}
				
				Log.e("post message", sw.getBuffer().toString());
				restTemplate.postForObject("http://share-a-prayer.appspot.com/resources/prayerjersy/updateplacebylocation", newMinyan, Long.class);
				

				// create in server
				/*drawPublicPlaceOnMap(newMinyan);*/
	        	updateMap(point);
			}
		});
		builder.setNegativeButton("No", new DialogInterface.OnClickListener() 
		{
			public void onClick(DialogInterface dialog, int id) {}
		});
		
		AlertDialog alert = builder.create();
		
		alert.show();
	} 
	
	
		
	@Override
	public void onCreate(Bundle savedInstanceState) 	
	{		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		//mapView = new SPMapView(this);
		mapView = (SPMapView)findViewById(R.id.view1);
		
        mapView.RegisterTapListener(new IMapTapDetect()	{

			public void onTouchEvent(SPGeoPoint sp) {
				createNewPlaceDialog("Do you want to create a public praying place?", sp);
				
			}
        	
        });
     
        restTemplate = new RestTemplate();
    	restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory());
        List<HttpMessageConverter<?>> mc = restTemplate.getMessageConverters();
        MappingJacksonHttpMessageConverter json = new MappingJacksonHttpMessageConverter();
 
        List<MediaType> supportedMediaTypes = new ArrayList<MediaType>();
        supportedMediaTypes.add(new MediaType("text", "javascript"));
        json.setSupportedMediaTypes(supportedMediaTypes);
        mc.add(json);
        restTemplate.setMessageConverters(mc);
        /*
         * User overlay and icon:
         */
        //Creates the user's map-icon
        userDefaultMarker = this.getResources().getDrawable(R.drawable.user_kipa_pin);
        // create an PrayerArrayItemizedOverlay for the user
		userOverlay = new PrayerArrayItemizedOverlay(userDefaultMarker, this);
		 // add the PrayerArrayItemizedOverlay to the MapView
        mapView.getOverlays().add(userOverlay);
        
        /*
         * Synagouge overlay
         */
        //Creates the Synagouge's map-icon
        synagougeMarker = this.getResources().getDrawable(R.drawable.synagouge2);
        // create an PrayerArrayItemizedOverlay for the user
        publicPlaceOverlay = new PlaceArrayItemizedOverlay(synagougeMarker, this);
		 // add the PrayerArrayItemizedOverlay to the MapView
        mapView.getOverlays().add(publicPlaceOverlay);
        
        /*
         * Other users overlay and icons:
         */
		//Creates the otherUser's map-icon
		othersDefaultMarker = this.getResources().getDrawable(R.drawable.others_kipa_pin);
        // create an PrayerArrayItemizedOverlay for the others
		otherUsersOverlay = new PrayerArrayItemizedOverlay(othersDefaultMarker, this);
		// add the PrayerArrayItemizedOverlay to the MapView
        mapView.getOverlays().add(otherUsersOverlay);	 
        
        /*
         * Circle overlay:
         */
        // create the default paint objects for overlay circles
    	circleDefaultPaintFill = new Paint(Paint.ANTI_ALIAS_FLAG);
        circleDefaultPaintFill.setStyle(Paint.Style.FILL);
        circleDefaultPaintFill.setColor(Color.BLUE);
        circleDefaultPaintFill.setAlpha(64);
        
        circleDefaultPaintOutline = new Paint(Paint.ANTI_ALIAS_FLAG);
        circleDefaultPaintOutline.setStyle(Paint.Style.STROKE);
        circleDefaultPaintOutline.setColor(Color.BLUE);
        circleDefaultPaintOutline.setAlpha(128);
        circleDefaultPaintOutline.setStrokeWidth(3);
        
        circleOverlay = new ArrayCircleOverlay(circleDefaultPaintFill, circleDefaultPaintOutline, this);
    	
        // Define a listener that responds to location updates
    	locationListener = new ILocationProv() 
    	{
	    	// Called when a new location is found by the network location provider.
			public void LocationChanged(SPGeoPoint point) {
				
				updateMap(point);
				DrawPointOnMap(mapView, point);
				
    	    	
    	    }
    	};
    	

   	final Timer     timer = new Timer();
   	final TimerTask task = new TimerTask() {
			
   		@Override
   		public void run() {
   			if(service.getLocation()!=null){
   				mapView.post(new Runnable()	{



   					public void run() {
   						// TODO Auto-generated method stub
   						updateMap(service.getLocation());
   					}
   				});

   			}
   		}
   	}; 
    	
    	ServiceConnection svcConn=new ServiceConnection() {
    		
    		

    		public void onServiceDisconnected(ComponentName className) {
    			service=null;
    		}

    		public void onServiceConnected(ComponentName arg0, IBinder arg1) {
    			service=(ILocationSvc)arg1;
    			

        		try {
        			service.RegisterListner(locationListener);
        			SPGeoPoint gp = service.getLocation();
        			publicPlaceOverlay.setThisUser(service.getUser());
        			updateMap(gp);
        			//send the user to places overlay
        			
        			
        		}
        		catch (Throwable t) {
        			Log.e("ShareAPrayer", "Exception in call to registerListner()", t);
        		}
//        		
//        		timer = new Timer();
//        		task  = new TimerTask()
//        		{
//        		@Override
//        		public void run()
//        		{
//        			if(service.getLocation()!=null){
//        				updateMap(service.getLocation());
//        			}
//        		}
//        		};
        		timer.schedule(task, 1, 10000);
        		     
        		
    			
    		}

    	};
    	
    	bindService(new Intent(LocServ.ACTION_SERVICE), svcConn,
    			BIND_AUTO_CREATE);
    	


	}
}

