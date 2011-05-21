package il.ac.tau.team3.shareaprayer;



import java.util.HashMap;
import java.util.List;
import java.util.Map;

import il.ac.tau.team3.common.GeneralPlace;
import il.ac.tau.team3.common.GeneralUser;
import il.ac.tau.team3.common.Pray;
import il.ac.tau.team3.shareaprayer.FindPrayer;
import il.ac.tau.team3.shareaprayer.FindPrayer.StringArray;

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
		final Map<String, StringArray> msgs = new HashMap<String, StringArray>();

		// A map containing users and their prays
//		final Map<String, Integer> joinersMap = new HashMap<String, Integer>();
//		try{
//		
//			for (Pray p : placeItem.getPlace().getPraysOfTheDay())	{
//				for (GeneralUser joiner : p.getJoiners()){
//					String name = joiner.getName();
//					// If the user isn't listed, insert him without prays
//					if (!joinersMap.containsKey(name)) joinersMap.put(joiner.getName(), 0);
//					final int i = fromPrayNameToInteger(p.getName()) + joinersMap.get(name);
//					joinersMap.put(name, i);
//				}
//			}
		
		try{
			for (Pray p : placeItem.getPlace().getPraysOfTheDay())	{
				StringArray msg = new StringArray(p.getJoiners().size() + 1);
				try	{
					if (p.getJoiners().size() == 0)	{
						msg.insert("No prayers are listed.");
					} else	{
						for (GeneralUser joiner : p.getJoiners())	{
							String str = (joiner.getFullName()== null || joiner.getFullName() == "" ? joiner.getName() : joiner.getFullName());
							if (!(str.equals("null"))) msg.insert(str); 
						}
					}
				} catch (NullPointerException e)	{
					if (null != p){ 
						msg.getStringArray()[0] = "No prayers are listed.";
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

	public int fromPrayNameToIndex(String prayName){ 
		if (prayName.equals(R.string.Shaharit)) return 0;
		if (prayName.equals(R.string.Minha)) return 1;
		if (prayName.equals(R.string.Arvit)) return 2;
		return -1;
	}
//	public int fromPrayNameToInteger(String prayName){
//		if (prayName.equals(R.string.Shaharit)) return FindPrayer.SHAHARIT;
//		if (prayName.equals(R.string.Minha)) return FindPrayer.MINHA;
//		if (prayName.equals(R.string.Arvit)) return FindPrayer.ARVIT;
//		return -1;
//	}
	public int fromPrayNameToInteger(String prayName){
		if (prayName.equals("Shaharit")) return FindPrayer.SHAHARIT;
		if (prayName.equals("Minha")) return FindPrayer.MINHA;
		if (prayName.equals("Arvit")) return FindPrayer.ARVIT;
		return -1;
	}
}
