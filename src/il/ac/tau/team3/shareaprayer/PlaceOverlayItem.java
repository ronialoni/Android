package il.ac.tau.team3.shareaprayer;



import org.mapsforge.android.maps.OverlayItem;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.util.Log;

import il.ac.tau.team3.common.GeneralPlace;
import il.ac.tau.team3.common.SPUtils;



/**
 * 
 * @author Team3
 *              Adding a reference to a GeneralPlace.
 *              Allowing the use of SPGeoPoint instead of GeoPoint.
 *
 */
public class PlaceOverlayItem 
extends OverlayItem
{
        
    private GeneralPlace place;
        
    
    /*** @constructor ***/
    
    public PlaceOverlayItem(GeneralPlace place, String title, String snippet, Drawable marker)
    {
        	super(SPUtils.toGeoPoint(place.getSpGeoPoint()), title, snippet, marker);
        this.place = place;
    }
    
    
    @Override
    public synchronized Drawable getMarker() {
    	if (null == this.marker)	{
    		return null;
    	}
    	
    	/*Drawable text = new Drawable()	{
    		
    		private Paint p = new Paint();

			@Override
			public void draw(Canvas canvas) {
				// TODO Auto-generated method stub
				
				p.setColor(Color.RED);
				p.setStyle(Paint.Style.STROKE);
				p.setStrokeWidth(2);
				//p.setARGB(255, 255, 0, 0);
				p.setTypeface(Typeface.DEFAULT);
				p.setTextSize(20);
				
				if ((null == place) || (null == place.getAllJoiners()))	{
					return;
				}
				canvas.drawText((new Integer(place.getAllJoiners().size())).toString() , getBounds().left, getBounds().top	, p);
			}

			@Override
			public int getOpacity() {
				// TODO Auto-generated method stub
				return PixelFormat.TRANSPARENT;
			}

			@Override
			public void setAlpha(int alpha) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void setColorFilter(ColorFilter cf) {
				// TODO Auto-generated method stub
			}
    		
    	};*/
    	
    	BitmapDrawable bitmapText = new BitmapDrawable(((BitmapDrawable)this.marker).getBitmap())	{ 
    		Paint p = new Paint();
    		
    		@Override
    		public void draw(Canvas arg)	{
    			
    			arg.drawBitmap(getBitmap(), this.getBounds().left, this.getBounds().top, p);
    			
    			p.setColor(Color.RED);
				p.setStyle(Paint.Style.STROKE);
				p.setStrokeWidth(2);
				//p.setARGB(255, 255, 0, 0);
				p.setTypeface(Typeface.DEFAULT);
				p.setTextSize(20);
    			
    			p.setAntiAlias(true);
    			arg.drawText((new Integer(place.getAllJoiners().size())).toString() , 
    					getBounds().left + getBitmap().getWidth()/2, 
    					getBounds().top + getBitmap().getHeight()/2 , p);
    		}
    		
    	};
    	
    	/*Drawable[] layers = {text, this.marker};
    	LayerDrawable ld = new LayerDrawable(layers);
    	ld.setAlpha(255);*/
    	
		return bitmapText;
	}
    
    
    public GeneralPlace getPlace()
    {
        return place;
    }
    
    
    public void setPlace(GeneralPlace place)
    {
        this.place = place;
        this.setPoint(SPUtils.toGeoPoint(place.getSpGeoPoint()));
    }
    
    
    
    // TODO Override methods like setPoint so the GeneralUser will update too & write a "setSPGeoPoint".
    
    
}
