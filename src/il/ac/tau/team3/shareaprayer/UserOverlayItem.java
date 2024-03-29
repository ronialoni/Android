package il.ac.tau.team3.shareaprayer;



import il.ac.tau.team3.common.GeneralPlace;
import il.ac.tau.team3.common.GeneralUser;
import il.ac.tau.team3.common.SPUtils;
import il.ac.tau.team3.common.UnknownLocationException;

import org.mapsforge.android.maps.GeoPoint;
import org.mapsforge.android.maps.OverlayItem;

import android.graphics.drawable.Drawable;




public class UserOverlayItem 
extends OverlayItem
{
    
    
    private GeneralUser user;

    
    
    public UserOverlayItem(GeneralUser user) throws UnknownLocationException
    {
        super(SPUtils.toGeoPoint(user.getSpGeoPoint()), user.getFullName(), user.getName());
        this.user = user;
        // TODO Auto-generated constructor stub
    }
    
    
    
    
    public GeneralUser getUser()
    {
        return user;
    }
    
    public void setUser(GeneralUser user) throws UnknownLocationException
    {
        this.user = user;
        this.setPoint(SPUtils.toGeoPoint(user.getSpGeoPoint()));
    }
    
    
}
