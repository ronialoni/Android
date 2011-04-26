package il.ac.tau.team3.shareaprayer;


import org.mapsforge.android.maps.OverlayItem;


import il.ac.tau.team3.common.GeneralUser;
import il.ac.tau.team3.common.SPUtils;



public class UserOverlayItem 
extends OverlayItem
{
    
    
    private GeneralUser user;

    
    
    public UserOverlayItem(GeneralUser user, String title, String snippet)
    {
        super(SPUtils.toGeoPoint(user.getSpGeoPoint()), title, snippet);
        this.user = user;
        // TODO Auto-generated constructor stub
    }
    
    
    
    
    public GeneralUser getUser()
    {
        return user;
    }
    
    public void setUser(GeneralUser user)
    {
        this.user = user;
        this.setPoint(SPUtils.toGeoPoint(user.getSpGeoPoint()));
    }
    
    
}
