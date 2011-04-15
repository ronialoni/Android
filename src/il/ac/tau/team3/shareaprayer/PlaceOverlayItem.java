package il.ac.tau.team3.shareaprayer;



import il.ac.tau.team3.common.GeneralPlace;

import org.mapsforge.android.maps.GeoPoint;
import org.mapsforge.android.maps.OverlayItem;

import android.graphics.drawable.Drawable;



public class PlaceOverlayItem extends OverlayItem
{
    
    
    private GeneralPlace place;
    
    
    
    public PlaceOverlayItem(GeneralPlace place, String title, String snippet)
    {
        super(SPUtils.toGeoPoint(place.getSpGeoPoint()), title, snippet);
        this.place = place;
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
    
    
    
}
