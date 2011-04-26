package il.ac.tau.team3.shareaprayer;


import il.ac.tau.team3.common.SPGeoPoint;


import java.util.List;
import java.util.ArrayList;
//import java.util.Set;
//import java.util.HashSet;


import org.mapsforge.android.maps.MapView;
import org.mapsforge.android.maps.MapViewMode;

import org.mapsforge.android.maps.GeoPoint;


import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

/*
import org.mapsforge.android.maps.MapActivity;
import org.mapsforge.android.maps.OverlayItem;
import org.mapsforge.android.maps.ArrayItemizedOverlay;
import org.mapsforge.android.maps.OverlayCircle;
*/

//import android.app.AlertDialog;
//import android.content.DialogInterface;



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


	
	
	private long startTime;
	private long timeElapsed;
	
	private float startPosX;
	private float startPosY;
	
	
	
	/*** @Override ***/
	
	@Override
	public boolean onTouchEvent(MotionEvent event) 
	{
	    
		if (event.getAction() == MotionEvent.ACTION_DOWN)
		{
			startTime = event.getEventTime(); 
			startPosX = event.getX();
            startPosY = event.getY(); 
		}
		
		
		/*
		 * 
		 */
		if ((event.getX() == startPosX) && (event.getY() == startPosY))
		{          
			timeElapsed = event.getEventTime() - startTime;
			if (timeElapsed > 800) 
			{
				GeoPoint   p          = this.getProjection().fromPixels((int) startPosX, (int) startPosY);
				SPGeoPoint eventPoint = new SPGeoPoint(p.getLatitudeE6(),p.getLongitudeE6());
				//createDialog("Do you want to create a public praying place?");
				for (IMapTapDetect tap : tapListeners)	
				{
					tap.onTouchEvent(eventPoint);
					//this.mapActivity.createDialog("Do you want to create a public praying place?", eventPoint);
				}
				
			}
		} 
		
		return super.onTouchEvent(event);		
	}
			
	
	
	
	
	
	
}




