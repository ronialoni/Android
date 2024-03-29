package il.ac.tau.team3.shareaprayer;


/** @import java.util.*;  */
import il.ac.tau.team3.common.SPGeoPoint;
import il.ac.tau.team3.common.SPUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.HashSet;


/** @import android...  */
//import android.os.Bundle;
//import android.content.Context;

/** @import android.graphics.*;  */
//import android.graphics.Color;
//import android.graphics.Paint;
//import android.graphics.Paint.Style;
//import android.graphics.drawable.Drawable;


/** @import android.location.*;  */
//import android.location.Location;
//import android.location.LocationListener;
//import android.location.LocationManager;


/** @import org.mapsforge.android.maps.*;  */
//import org.mapsforge.android.maps.MapActivity;
import org.mapsforge.android.maps.MapView;
import org.mapsforge.android.maps.MapViewMode;

//import org.mapsforge.android.maps.GeoPoint;

//import org.mapsforge.android.maps.Overlay;
import org.mapsforge.android.maps.GeoPoint;
import org.mapsforge.android.maps.MapActivity;
import org.mapsforge.android.maps.Overlay;
import org.mapsforge.android.maps.OverlayItem;
import org.mapsforge.android.maps.ArrayItemizedOverlay;
import org.mapsforge.android.maps.OverlayCircle;
import org.mapsforge.android.maps.Projection;

//import tomer.temp.common.GeoLocation;
//import org.mapsforge.android.maps.ArrayCircleOverlay;




import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Point;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.GestureDetector.OnGestureListener;







public class SPMapView
extends MapView
{	
    private List<IMapTapDetect> tapListeners;
	
	/*** @constructors ***/
		
	public SPMapView(Context context)
    {
        super(context);
        initFields();
    }
	
	public SPMapView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        initFields();
        
    }
    public SPMapView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs);
        initFields();
    }
    
  
    private void initFields()	
    {
    	this.setClickable(true);
        this.setBuiltInZoomControls(true);
        this.setMapViewMode(MapViewMode.MAPNIK_TILE_DOWNLOAD);
        tapListeners = new ArrayList<IMapTapDetect>();
    }
	
    
    
    
	public void registerTapListener(IMapTapDetect tapListen)	
	{
		synchronized(this)
		{
			tapListeners.add(tapListen);
		}
		
	}
	
	public void unregisterTapListener(IMapTapDetect tapListen)
	{
		synchronized(this)	
		{
			tapListeners.remove(tapListen);
		}
	}

	
	/**
	 * @pre   null != touchData
	 */
	private void notifyAnyTouchEvent(MotionEvent event)
	{
	    for (IMapTapDetect touchistenr : this.tapListeners)
	    {
	        touchistenr.onAnyEvent(event);
	    }
	}
	
	private float startPosX;
	private float startPosY;
	
	private float MaxPosX;
	private float MaxPosY;
	
	
	
	/*** @Override ***/
	
	@Override
	public boolean onTouchEvent(MotionEvent event) 
	{
	    this.notifyAnyTouchEvent(event);
	    
		if (event.getAction() == MotionEvent.ACTION_DOWN)
		{ 
			startPosX = event.getX();
            startPosY = event.getY();
            MaxPosX = 0;
            MaxPosY = 0;
        }
        else if ((event.getAction() == MotionEvent.ACTION_MOVE)
                || (event.getAction() == MotionEvent.ACTION_UP))
        {
            
        }
        else
        {
            MaxPosX = Float.POSITIVE_INFINITY;
            MaxPosY = Float.POSITIVE_INFINITY;
        }
        
        MaxPosX = Math.max(Math.abs(event.getX() - startPosX), MaxPosX);
        MaxPosY = Math.max(Math.abs(event.getY() - startPosY), MaxPosY);		
		
		//if ((event.getX() == startPosX) && (event.getY() == startPosY))
		{          
			//timeElapsed = event.getEventTime() - startTime;
			if (((event.getAction() == MotionEvent.ACTION_DOWN) || (event.getAction() == MotionEvent.ACTION_MOVE)
					|| (event.getAction() == MotionEvent.ACTION_UP)) && 
					(Math.abs(event.getEventTime() -  event.getDownTime()) > 300) && (MaxPosX < 20) && (MaxPosY < 20)) 
			{
				MaxPosX = Float.POSITIVE_INFINITY;
				MaxPosY = Float.POSITIVE_INFINITY;
				
				GeoPoint   p          = this.getProjection().fromPixels((int) event.getX(), (int) event.getY());
				SPGeoPoint eventPoint = new SPGeoPoint(p.getLatitudeE6(),p.getLongitudeE6());
				
				for (IMapTapDetect tap : tapListeners){
					tap.onTouchEvent(eventPoint);
					}
				
			}
		}
		
		if (event.getAction() == MotionEvent.ACTION_UP)	{
			MaxPosX = Float.POSITIVE_INFINITY;
			MaxPosY = Float.POSITIVE_INFINITY;
		}
		
		if (event.getAction() == MotionEvent.ACTION_MOVE)	{
			GeoPoint   p          = this.getProjection().fromPixels((int) startPosX, (int) startPosY);
			SPGeoPoint eventPoint = new SPGeoPoint(p.getLatitudeE6(),p.getLongitudeE6());
			for (IMapTapDetect tap : tapListeners)	
			{
				tap.onMoveEvent(eventPoint);
			}
		}
		
		try
		{
		  super.onTouchEvent(event);    
		}
        catch (NullPointerException npe)
        {
            npe.printStackTrace();
        } 
        
        return true;
	}
			
	
	
	
	
	
	
}




