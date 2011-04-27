package il.ac.tau.team3.shareaprayer;

import il.ac.tau.team3.common.GeneralPlace;
import il.ac.tau.team3.common.GeneralUser;
import il.ac.tau.team3.common.SPGeoPoint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

public class UIUtils {
	
    public static final String _sNewPlaceQues= "Do you want to create a public praying place?";
    public static final String _sAlreadyRegisterAlertMsg = "You are already registered to this place.";
    public static final String _sWantToRegisterQues = "Would you like to register to this place?";
	
	
	static void createRegisterDialog(String message, final GeneralPlace place, final PlaceArrayItemizedOverlay placeOverlay)
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(placeOverlay.getActivity());
		builder.setMessage(message + _sWantToRegisterQues);
		builder.setCancelable(true);
		
		builder.setPositiveButton("Register", new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int id) 
			{
                        
			    if (! place.IsJoinerSigned(placeOverlay.getThisUser().getName()))
			    {
			        placeOverlay.getActivity().getSPCommunicationManager().requestPostRegister(place);
			    }
			    else
			    {
			        createAlertDialog(_sAlreadyRegisterAlertMsg, placeOverlay.getActivity());
			    }
			}
		});
				
		builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() 
		{
			public void onClick(DialogInterface dialog, int id) {}
		});
		
		AlertDialog alert = builder.create();
		
		alert.show();
	}
	
	
	static void createAlertDialog(String msg, Context context)
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setMessage(msg);
		builder.setCancelable(true);
		builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() 
		{
			public void onClick(DialogInterface dialog, int id) {}
		});
	
		AlertDialog alert = builder.create();
		alert.show();
	}
	

	static void createNewPlaceDialog(final SPGeoPoint point, final FindPrayer activity, final GeneralUser user)
	{
	    AlertDialog.Builder builder = new AlertDialog.Builder(activity);
	    builder.setMessage(_sNewPlaceQues);
	    builder.setCancelable(false);
	    
	    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener()
	    {
	        public void onClick(DialogInterface dialog, int id)
	        {
	            Thread t = new Thread()
	            {	                
	                @Override
	                public void run()
	                {
	                    GeneralPlace newMinyan = new GeneralPlace("New Minyan Place", "", point);
	                    newMinyan.addJoiner(user.getName());
	                    
	                    activity.getSPCommunicationManager().requestPostNewPlace(newMinyan);
	                    
	                    synchronized (activity.getRefreshTask())
	                    {
	                        activity.getRefreshTask().notify();
	                    }
	                }
	            };

	            t.run();	                	            
	        }
	    });
	    
	    builder.setNegativeButton("No", new DialogInterface.OnClickListener()
	    {
	        public void onClick(DialogInterface dialog, int id) {}
	    });
		
	    AlertDialog alert = builder.create();
	    alert.show();
	} 
	
}
