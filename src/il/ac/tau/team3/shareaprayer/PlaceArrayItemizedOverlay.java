package il.ac.tau.team3.shareaprayer;



import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import il.ac.tau.team3.common.GeneralPlace;
import il.ac.tau.team3.common.GeneralUser;
import il.ac.tau.team3.common.Pray;
import il.ac.tau.team3.shareaprayer.FindPrayer;
import il.ac.tau.team3.shareaprayer.FindPrayer.StringArray;
import il.ac.tau.team3.uiutils.UIUtils;

import org.mapsforge.android.maps.GeoPoint;
import org.mapsforge.android.maps.MapView;
import org.mapsforge.android.maps.OverlayItem;
import org.mapsforge.android.maps.PrayerArrayItemizedOverlay;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;





public class PlaceArrayItemizedOverlay extends PrayerArrayItemizedOverlay 
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
	    synchronized (this.getOverlayItems())	{
			final PlaceOverlayItem placeItem = (PlaceOverlayItem) this.getOverlayItems().get(index);
			final Map<String, List<String>> msgs = new HashMap<String, List<String>>();
			final PlaceArrayItemizedOverlay p = this;
			
			activity.runOnUiThread(new Runnable() {
				public void run() {
					UIUtils.createRegisterDialog(placeItem.getPlace(), p);
				}
			});
	    }
	    return true;
	}

	public int fromPrayNameToIndex(String prayName){ 
		if (prayName.equals(R.string.Shaharit)) return 0;
		if (prayName.equals(R.string.Minha)) return 1;
		if (prayName.equals(R.string.Arvit)) return 2;
		return -1;
	}

	public int fromPrayNameToInteger(String prayName){
		if (prayName.equals("Shaharit")) return FindPrayer.SHAHARIT;
		if (prayName.equals("Minha")) return FindPrayer.MINHA;
		if (prayName.equals("Arvit")) return FindPrayer.ARVIT;
		return -1;
	}
}
