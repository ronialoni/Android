package il.ac.tau.team3.shareaprayer;



import java.util.HashMap;
import java.util.List;
import java.util.Map;

import il.ac.tau.team3.common.GeneralPlace;
import il.ac.tau.team3.common.GeneralUser;
import il.ac.tau.team3.common.Pray;

import org.mapsforge.android.maps.GeoPoint;
import org.mapsforge.android.maps.MapView;
import org.mapsforge.android.maps.OverlayItem;
import org.mapsforge.android.maps.PrayerArrayItemizedOverlay;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;





public class PlaceArrayItemizedOverlay
extends PrayerArrayItemizedOverlay 
{
	
    
    private FindPrayer  activity;
	private GeneralUser thisUser;

	
	
    public GeneralUser getThisUser()
    {
        return thisUser;
    }
    
    public FindPrayer getActivity()
    {
        return activity;
    }
    
    public void setActivity(FindPrayer activity)
    {
        this.activity = activity;
    }
    
    public void setThisUser(GeneralUser thisUser)
    {
        this.thisUser = thisUser;
    }

	
	
    
	public PlaceArrayItemizedOverlay(Drawable defaultMarker, Context context, FindPrayer activity)
	{
		super(defaultMarker, context);
		this.activity = activity;
	}
	

	public PlaceArrayItemizedOverlay(Drawable synagougeMarker, FindPrayer findPrayer)
	{
		super(synagougeMarker,findPrayer);
		this.activity = findPrayer;
	}

	
	

	@Override
	protected boolean onTap(int index)
	{
	    
		final PlaceOverlayItem placeItem = (PlaceOverlayItem) this.getOverlayItems().get(index);
		
		final Map<String, String> msgs = new HashMap<String, String>();
		try{
		
			for (Pray p : placeItem.getPlace().getPraysOfTheDay())	{
				String msg = null;
				try	{
					if (p.getJoiners().size() == 0)	{
						msg = "No prayers are listed to " + p.getName() + ".\n";
					} else	{
						msg = "Prayer listed to " + p.getName() + " are:\n";
					}
					for (GeneralUser joiner : p.getJoiners())	{
						msg += (joiner.getFullName()== null || joiner.getFullName() == "" ? joiner.getName() : joiner.getFullName()) + "\n";
					}
				} catch (NullPointerException e)	{
					if (null != p){ 
						msg = "No prayers are listed to " + p.getName() + ".\n";
					}
				}
				if (null != p){ 
					msgs.put(p.getName(), msg);
				}
			}
		} catch (NullPointerException e)	{
		}
		
		
		final PlaceArrayItemizedOverlay p = this;
		
		activity.runOnUiThread(new Runnable() {
			
			public void run() {
				UIUtils.createRegisterDialog(msgs, placeItem.getPlace(), p);
				
			}
		});
		
		
	    return true;
	}

}
