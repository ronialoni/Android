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

public class PlaceArrayItemizedOverlay extends PrayerArrayItemizedOverlay 
{
	FindPrayer activity;
	GeneralUser thisUser;

	public GeneralUser getThisUser() {
		return thisUser;
	}


	public void setThisUser(GeneralUser thisUser) {
		this.thisUser = thisUser;
	}


	public PlaceArrayItemizedOverlay(Drawable defaultMarker, Context context, FindPrayer activity) {
		super(defaultMarker, context);
		// TODO Auto-generated constructor stub
		this.activity = activity;
	}
	

	public PlaceArrayItemizedOverlay(Drawable synagougeMarker,
			FindPrayer findPrayer) {
		super(synagougeMarker,findPrayer);
		// TODO Auto-generated constructor stub
		this.activity = findPrayer;
	}


	private void createRegisterDialog(String message, final GeneralPlace place)
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		builder.setMessage(message);
		builder.setCancelable(true);
		builder.setPositiveButton("Register", new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int id) 
			{
				place.addJoiner(thisUser.getName());
				
			}
		});
		builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() 
		{
			public void onClick(DialogInterface dialog, int id) {}
		});
		
		AlertDialog alert = builder.create();
		
		alert.show();
	}
	
	
	
//	public boolean onTap (final GeoPoint p, final MapView mapView)
//	{
//	    boolean tapped = super.onTap(p, mapView);
//	    if (tapped)
//	    {            
//	    	createRegisterDialog("Would you like to register to this place?", p);         
//	    }           
//	    else
//	    {
//	    	// Note: no action needed, Activity handles
//	    }     
//	    
//	    return true;
//	}

	
	
	
	@Override
	protected boolean onTap(int index) {
		PlaceOverlayItem placeItem = (PlaceOverlayItem)this.getOverlayItems().get(index);
		String msg; 
		List<String> joiners = placeItem.getPlace().getAllJoiners();
		msg = (joiners == null ? "No prayers listed" : "Prayer listed are:\n");
		for(String joiner : joiners){
			msg = msg + joiner + "\n";
			
		}
		this.createRegisterDialog(msg + "Would you like to register to this place?", placeItem.getPlace());
		
	    return true;
	}

}
