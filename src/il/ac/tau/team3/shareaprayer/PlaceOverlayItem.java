package il.ac.tau.team3.shareaprayer;



import il.ac.tau.team3.common.GeneralPlace;
import il.ac.tau.team3.common.SPUtils;
import il.ac.tau.team3.common.UnknownLocationException;

import org.mapsforge.android.maps.OverlayItem;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;



public class PlaceOverlayItem extends OverlayItem
{
    
    
    private GeneralPlace place;
    private Drawable glow;
    
    /*** @throws UnknownLocationException 
     * @constructor ***/
    
    public PlaceOverlayItem(GeneralPlace place, String title, String snippet, Drawable marker) throws UnknownLocationException	{
    	this(place, title, snippet, marker, null);
    }
    
    public PlaceOverlayItem(GeneralPlace place, String title, String snippet, Drawable marker, Drawable glow) throws UnknownLocationException
    {
        super(SPUtils.toGeoPoint(place.getSpGeoPoint()), title, snippet, marker);
        this.place = place;
        this.glow = glow;
    }
    
    @Override
    public synchronized Drawable getMarker() {
    	//return this.marker;
    	if (this.marker instanceof BitmapDrawable)	{
    		
    		return new BitmapDrawableNumbered(((BitmapDrawable)marker).getBitmap(), place, glow == null ? null : ((BitmapDrawable)glow).getBitmap());
    	}
    	
    	return this.marker;
    }
    
    
    public PlaceOverlayItem(GeneralPlace place, String title, String snippet) throws UnknownLocationException
    {
        super(SPUtils.toGeoPoint(place.getSpGeoPoint()), title, snippet);
        this.place = place;
    }
    
    
    
    public GeneralPlace getPlace()
    {
        return place;
    }
    
    public void setPlace(GeneralPlace place) throws UnknownLocationException
    {
        this.place = place;
        this.setPoint(SPUtils.toGeoPoint(place.getSpGeoPoint()));
    }
    
    
    
}
