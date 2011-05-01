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
	    
		final PlaceOverlayItem placeItem = (PlaceOverlayItem) this.getOverlayItems().get(index);
		String msg1; 
		List<String> joiners = placeItem.getPlace().getAllJoiners();
		if(joiners == null || joiners.isEmpty())
		{
			msg1 = "No prayers are listed to Shaharit.\n"; 
		}
		else
		{
			msg1 = "Prayer listed to Shaharit are:\n";
			for(String joiner : joiners)
			{
				msg1 = msg1 + joiner + "\n";			
			}
		}
		String msg2; 
		List<String> joiners2 = placeItem.getPlace().getAllJoiners2();
		if(joiners2 == null || joiners2.isEmpty())
		{
			msg2 = "No prayers listed to Minha.\n"; 
		}
		else
		{
			msg2 = "Prayer listed to Minha are:\n";
			for(String joiner2 : joiners2)
			{
				msg2 = msg2 + joiner2 + "\n";			
			}
		}
		
		String msg3; 
		List<String> joiners3 = placeItem.getPlace().getAllJoiners3();
		if(joiners3 == null || joiners3.isEmpty())
		{
			msg3 = "No prayers listed to Arvit.\n"; 
		}
		else
		{
			msg3 = "Prayer listed to Arvit are:\n";
			for(String joiner3 : joiners3)
			{
				msg3 = msg3 + joiner3 + "\n";			
			}
		}
		
		final PlaceArrayItemizedOverlay p = this;
		final String cmsg1=msg1;
		final String cmsg2=msg2;
		final String cmsg3=msg3;
		
		activity.runOnUiThread(new Runnable() {
			
			public void run() {
				UIUtils.createRegisterDialog(cmsg1,cmsg2,cmsg3, placeItem.getPlace(), p);
				
			}
		});
		
		
	    return true;
	}

}
