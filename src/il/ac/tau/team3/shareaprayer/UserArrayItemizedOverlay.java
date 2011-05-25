package il.ac.tau.team3.shareaprayer;

import il.ac.tau.team3.common.GeneralUser;
import il.ac.tau.team3.uiutils.UserDetailsDialog;

import org.mapsforge.android.maps.OverlayItem;
import org.mapsforge.android.maps.PrayerArrayItemizedOverlay;

import android.graphics.drawable.Drawable;

public class UserArrayItemizedOverlay extends PrayerArrayItemizedOverlay {

	FindPrayer activity;
	
	public UserArrayItemizedOverlay(Drawable defaultMarker, FindPrayer context) {
		super(defaultMarker, context);
		this.activity = context;
	}
	
	@Override
	protected void tapOverlayItem(OverlayItem item)	{
		UserOverlayItem userOverlay = (UserOverlayItem)item;
		GeneralUser user = userOverlay.getUser();
		new UserDetailsDialog(user, activity);
	}

}
