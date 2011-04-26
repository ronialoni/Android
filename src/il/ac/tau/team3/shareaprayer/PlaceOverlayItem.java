package il.ac.tau.team3.shareaprayer;



import org.mapsforge.android.maps.OverlayItem;

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
    
    
    
    // TODO Override methods like setPoint so the GeneralUser will update too & write a "setSPGeoPoint".
    
    
}
