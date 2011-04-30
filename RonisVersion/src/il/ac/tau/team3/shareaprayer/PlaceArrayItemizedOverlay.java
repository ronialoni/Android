package il.ac.tau.team3.shareaprayer;



import java.util.List;

import il.ac.tau.team3.common.GeneralPlace;
import il.ac.tau.team3.common.GeneralUser;

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

	
	
	
	
//	private void createRegisterDialog(String message, final GeneralPlace place, final PlaceArrayItemizedOverlay p)
//	{
//		AlertDialog.Builder builder = new AlertDialog.Builder(activity);
//		builder.setMessage(message);
//		builder.setCancelable(true);
//		builder.setPositiveButton("Register", new DialogInterface.OnClickListener()
//		{
//			public void onClick(DialogInterface dialog, int id) 
//			{
//				
//				if(!place.IsJoinerSigned(thisUser.getName())){
//					p.getActivity().getRestTemplateFacade().AddJoiner(place, thisUser);
//				}else{
//					AlertDialog.Builder builder = new AlertDialog.Builder(activity);
//					builder.setMessage("You are already registered to this place.");
//					builder.setCancelable(true);
//					builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() 
//					{
//						public void onClick(DialogInterface dialog, int id) {}
//					});
//					AlertDialog alert = builder.create();
//					alert.show();
//				}
//				
//				
//				
//			}
//		});
//		builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() 
//		{
//			public void onClick(DialogInterface dialog, int id) {}
//		});
//		
//		AlertDialog alert = builder.create();
//		
//		alert.show();
//	}
	

	
	
	@Override
	protected boolean onTap(int index)
	{
	    
		PlaceOverlayItem placeItem = (PlaceOverlayItem) this.getOverlayItems().get(index);
		String msg; 
		List<String> joiners = placeItem.getPlace().getAllJoiners();
		if(joiners == null || joiners.isEmpty())
		{
			msg = "No prayers listed.\n"; 
		}
		else
		{
			msg = "Prayer listed are:\n";
			for(String joiner : joiners)
			{
				msg = msg + joiner + "\n";			
			}
		}
		
		UIUtils.createRegisterDialog(msg, placeItem.getPlace(), this);
		
	    return true;
	}

}
