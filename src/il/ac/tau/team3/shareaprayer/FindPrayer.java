
package il.ac.tau.team3.shareaprayer;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import il.ac.tau.team3.common.GeneralPlace;
import il.ac.tau.team3.common.GeneralUser;
import il.ac.tau.team3.common.SPGeoPoint;
import il.ac.tau.team3.common.User;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import org.mapsforge.android.maps.ArrayCircleOverlay;
import org.mapsforge.android.maps.ArrayItemizedOverlay;
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
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;



public class FindPrayer 
extends MapActivity 
{
	private Paint circleDefaultPaintFill;
	private Paint circleDefaultPaintOutline;
	private Drawable userDefaultMarker;
	private Drawable othersDefaultMarker;
	private Drawable synagougeMarker;
	private ArrayItemizedOverlay userOverlay;
	private ArrayItemizedOverlay otherUsersOverlay;
	private ArrayItemizedOverlay publicPlaceOverlay;
	private ArrayCircleOverlay circleOverlay;
	
	public void drawUserOnMap(User user)	
	{
        //Clears the last location
		userOverlay.clear();
		
		// create an OverlayItem with title and description
        OverlayItem item = new OverlayItem(SPUtils.toGeoPoint(user.getSpGeoPoint()), user.getName(), user.getStatus());

        // add the OverlayItem to the ArrayItemizedOverlay
        userOverlay.addItem(item);
	}
	
	public void drawOtherUserOnMap(GeneralUser otheruser)	
	{
		// create an OverlayItem with title and description
        OverlayItem other = new OverlayItem(SPUtils.toGeoPoint(otheruser.getSpGeoPoint()), otheruser.getName(), otheruser.getStatus());

        // add the OverlayItem to the ArrayItemizedOverlay
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

        // add the ArrayItemizedOverlay to the MapView
        mapView.getOverlays().add(circleOverlay);
        
        return;
    }
	
	public void drawPublicPlaceOnMap(GeneralPlace place)	
	{
		// create an OverlayItem with title and description
		String address = place.getAddress();
		String name = place.getName();
        OverlayItem synagouge = new OverlayItem(SPUtils.toGeoPoint(place.getSpGeoPoint()), name, address);

        // add the OverlayItem to the ArrayItemizedOverlay
        publicPlaceOverlay.addItem(synagouge);
        return;
    }
	
	private RestTemplate restTemplate;
	
	private ILocationSvc service = null;
	private ILocationProv locationListener;
	
	@Override
	public void onDestroy()	{
		if (null != service)	{
			service.UnRegisterListner(locationListener);
		}
	}
		
	private ServiceConnection svcConn=new ServiceConnection() {
		

		public void onServiceDisconnected(ComponentName className) {
			service=null;
		}

		public void onServiceConnected(ComponentName arg0, IBinder arg1) {
			service=(ILocationSvc)arg1;
			

    		try {
    			service.RegisterListner(locationListener);
    			SPGeoPoint gp = service.getLocation();
    			updateMap(gp);
    			
    		}
    		catch (Throwable t) {
    			Log.e("ShareAPrayer", "Exception in call to registerListner()", t);
    		}

			
		}

	};
	
	private SPMapView mapView;
	private final static int EARTH_RADIUS_KM = 6371;
	
	private void updateMap(SPGeoPoint center)	{
		if (center == null)	{
			return;
		}
		
		GeoPoint screenEdge = mapView.getProjection().fromPixels(mapView.getWidth(), mapView.getHeight());
		if (screenEdge == null)	{
			return;
		}
		
		double lat1Rad = Math.toRadians(center.getLatitudeInDegrees());
		double lat2Rad = Math.toRadians(screenEdge.getLatitude());
		double deltaLonRad = Math.toRadians(Math.abs(center.getLongitudeInDegrees() - screenEdge.getLongitude()));
		double distance = Math.acos(Math.sin(lat1Rad)*Math.sin(lat2Rad) + Math.cos(lat2Rad)*Math.cos(lat2Rad)*
			Math.acos(deltaLonRad))/EARTH_RADIUS_KM;
		int distancemeters = (int)(distance * 1000);
		
		
		
		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("latitude", new Double(center.getLatitudeInDegrees()).toString());
		parameters.put("longitude", new Double(center.getLongitudeInDegrees()).toString());
		parameters.put("radius", new Integer(distancemeters).toString());
		
		List<GeneralUser> users = restTemplate.getForObject("http://share-a-prayer.appspot.com/resources/prayerjersy/users?latitude={latitude}&longitude={longitude}&radius={radius}", List.class, parameters);
		
		
		if (null != users)
		{
			for (GeneralUser user : users)	{
				drawOtherUserOnMap(user);
			}
		}
	}
	
	public void createDialog(String message, final SPGeoPoint point)
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(message);
		builder.setCancelable(false);
		builder.setPositiveButton("Yes", new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int id) 
			{
				GeneralPlace newMinyan = new GeneralPlace("New Minyan Place", "", point);
				// create in server
				drawPublicPlaceOnMap(newMinyan);
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
		mapView = new SPMapView(this);
        mapView.setClickable(true);
        mapView.setBuiltInZoomControls(true);
        mapView.setMapViewMode(MapViewMode.MAPNIK_TILE_DOWNLOAD);
        setContentView(mapView); 
     
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
        // create an ArrayItemizedOverlay for the user
		userOverlay = new ArrayItemizedOverlay(userDefaultMarker, this);
		 // add the ArrayItemizedOverlay to the MapView
        mapView.getOverlays().add(userOverlay);
        
        /*
         * Synagouge overlay
         */
        //Creates the Synagouge's map-icon
        synagougeMarker = this.getResources().getDrawable(R.drawable.synagouge2);
        // create an ArrayItemizedOverlay for the user
        publicPlaceOverlay = new ArrayItemizedOverlay(synagougeMarker, this);
		 // add the ArrayItemizedOverlay to the MapView
        mapView.getOverlays().add(publicPlaceOverlay);
        
        /*
         * Other users overlay and icons:
         */
		//Creates the otherUser's map-icon
		othersDefaultMarker = this.getResources().getDrawable(R.drawable.others_kipa_pin);
        // create an ArrayItemizedOverlay for the others
		otherUsersOverlay = new ArrayItemizedOverlay(othersDefaultMarker, this);
		// add the ArrayItemizedOverlay to the MapView
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

        // Acquire a reference to the system Location Manager
    	LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
    	
        // Define a listener that responds to location updates
    	locationListener = new ILocationProv() 
    	{
	    	// Called when a new location is found by the network location provider.
			public void LocationChanged(SPGeoPoint point) {
				
				updateMap(point);
				DrawPointOnMap(mapView, point);
				
    	    	//System.out.println(user.getName());
    	    	
    	    	//int numServerUsers = resource.getNumUsers();
    	    	int numServerUsers = restTemplate.getForObject("http://share-a-prayer.appspot.com/resources/prayerjersy", Integer.class);
        		for (int i = 0; i < numServerUsers; i++)	{ 
        			/*GeneralUser gUser = resource.retrieve(i);
        			if (null != gUser)	{
        				drawOtherUserOnMap(gUser);
        			}*/
        			GeneralUser gUser =restTemplate.getForObject("http://share-a-prayer.appspot.com/resources/prayerjersy/{a}", GeneralUser.class, i);
        			drawOtherUserOnMap(gUser);
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
        			updateMap(gp);
        			
        		}
        		catch (Throwable t) {
        			Log.e("ShareAPrayer", "Exception in call to registerListner()", t);
        		}
    		}	
    	};
    	
    	bindService(new Intent(LocServ.ACTION_SERVICE), svcConn,
    			BIND_AUTO_CREATE);
    	


	}
}

