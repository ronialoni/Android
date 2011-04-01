
package il.ac.tau.team3.shareaprayer;


import java.util.ArrayList;
import java.util.List;

import il.ac.tau.team3.common.GeneralPlace;
import il.ac.tau.team3.common.GeneralUser;
import il.ac.tau.team3.common.User;
import android.os.Bundle;

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

import android.content.Context;
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
	private User user;
	
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
	
	public void DrawPointOnMap(final MapView mapView, final GeoPoint geoPoint)	
	{
		//removes previous location from the map
		circleOverlay.clear();
		
		// set center
		mapView.getController().setCenter(geoPoint);
        OverlayCircle circle = new OverlayCircle(geoPoint, 16, "My Location");
        
        circleOverlay.addCircle(circle);

        // add the ArrayItemizedOverlay to the MapView
        mapView.getOverlays().add(circleOverlay);
        
              
        return;
    }
	
	public void drawPublicPlaceOnMap(GeneralPlace place)	
	{
		// create an OverlayItem with title and description
		String description = "8 Currently registered prayers";
		String name = place.getName();
        OverlayItem synagouge = new OverlayItem(SPUtils.toGeoPoint(place.getSpGeoPoint()), name, description);

        // add the OverlayItem to the ArrayItemizedOverlay
        publicPlaceOverlay.addItem(synagouge);
        return;
    }
	
	private RestTemplate restTemplate;   
		
	@Override
	public void onCreate(Bundle savedInstanceState) 	
	{		
		super.onCreate(savedInstanceState);
        final MapView mapView = new MapView(this);
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
    	LocationListener locationListener = new LocationListener() 
    	{
	    	// Called when a new location is found by the network location provider.
    		public void onLocationChanged(Location location) 
    	    { 	
    	    	// create a GeoPoint with the latitude and longitude coordinates
    	    	GeoPoint geoPoint = new GeoPoint(location.getLatitude(), location.getLongitude());
    	    	DrawPointOnMap(mapView, geoPoint);
    	    	user.setSpGeoPoint(SPUtils.toSPGeoPoint(geoPoint));
    	    	drawUserOnMap(user);
    	    	
    	    	
    	    	
    	    	//System.out.println(user.getName());
    	    	
    	    	//int numServerUsers = resource.getNumUsers();
    	    	int numServerUsers = restTemplate.getForObject("http://share-a-prayer.appspot.com/resources/prayerjersy/user", Integer.class);
    	    	int numServerPlaces = restTemplate.getForObject("http://share-a-prayer.appspot.com/resources/prayerjersy/place", Integer.class);
        		for (int i = 0; i < numServerUsers; i++)	{ 
        			/*GeneralUser gUser = resource.retrieve(i);
        			if (null != gUser)	{
        				drawOtherUserOnMap(gUser);
        			}*/
        			GeneralUser gUser =restTemplate.getForObject("http://share-a-prayer.appspot.com/resources/prayerjersy/user/{a}", GeneralUser.class, i);
        			drawOtherUserOnMap(gUser);
        		}
        		
        		for (int i = 0; i < numServerPlaces; i++)	{ 
        			/*GeneralUser gUser = resource.retrieve(i);
        			if (null != gUser)	{
        				drawOtherUserOnMap(gUser);
        			}*/
        			GeneralPlace place =restTemplate.getForObject("http://share-a-prayer.appspot.com/resources/prayerjersy/place/{a}", GeneralPlace.class, i);
        			drawPublicPlaceOnMap(place);
        		}
    	    	
    	    }

    	    public void onStatusChanged(String provider, int status, Bundle extras) {}
    	    public void onProviderEnabled(String provider) {}
    	    public void onProviderDisabled(String provider) {}
    	};

    	// Register the listener with the Location Manager to receive location updates
    	locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER , 0, 0, locationListener);
        Location loc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER );
        if (null != loc)	
        {
	        GeoPoint geoPoint = new GeoPoint(loc.getLatitude(), loc.getLongitude());
	        GeoPoint geoPoint1 = new GeoPoint(loc.getLatitude()-0.005 , loc.getLongitude()-0.012);
	        GeoPoint geoPoint2 = new GeoPoint(loc.getLatitude()+0.017, loc.getLongitude()+0.013);
	        GeoPoint geoPoint3 = new GeoPoint(loc.getLatitude()+0.01, loc.getLongitude()-0.014);
	        GeoPoint geoPointSynagouge = new GeoPoint(loc.getLatitude()-0.005, loc.getLongitude()+0.01);
	        user = new User("Tomer (user)", SPUtils.toSPGeoPoint(geoPoint), "An orthodax extremest");
	        GeneralUser otheruser1 = new GeneralUser("Aviad", SPUtils.toSPGeoPoint(geoPoint1), "Looking for Minyan") ;
	        GeneralUser otheruser2 = new GeneralUser("Roni", SPUtils.toSPGeoPoint(geoPoint2), "Looking for something to cook...") ;
	        GeneralUser otheruser3 = new GeneralUser("Matan", SPUtils.toSPGeoPoint(geoPoint3), "Looking for Minyan") ;
	        GeneralPlace synagogue = new GeneralPlace("My minyan place", "10 Sokolov st, Tel Aviv", SPUtils.toSPGeoPoint(geoPointSynagouge));
	        
	                
	        DrawPointOnMap(mapView, geoPoint);
	        drawUserOnMap(user);
	        drawOtherUserOnMap(otheruser1);
	        drawOtherUserOnMap(otheruser2);
	        drawOtherUserOnMap(otheruser3);
	        drawPublicPlaceOnMap(synagogue);
	   }
        
	}
}

