package il.ac.tau.team3.shareaprayer;

import il.ac.tau.team3.common.GeneralPlace;
import il.ac.tau.team3.common.GeneralUser;
import il.ac.tau.team3.common.SPGeoPoint;
import il.ac.tau.team3.spcomm.ACommHandler;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.XmlResourceParser;
import android.graphics.AvoidXfermode;
import android.opengl.Visibility;
import android.preference.DialogPreference;
import android.text.Layout;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class UIUtils {
	
	static String _sNewPlaceQues= "Do you want to create a public praying place?";
	static String _sAlreadyRegisterAlertMsg = "You are already registered to this place.";
	static String _sWantToRegisterQues = "Would you like to register to this place?";
	static String _sUserNotRegisterMsg = "You are not register to this place.";
	static String _sUserNotOwnerMsg = "You can't delete this place, because you are not the owner.";
	static void _createRegisterDialog(String message, final GeneralPlace place, final PlaceArrayItemizedOverlay placeOverlay)
	{
		String msg = message + _sWantToRegisterQues;
		AlertDialog.Builder builder = new AlertDialog.Builder(placeOverlay.getActivity());
		builder.setMessage(msg);
		builder.setCancelable(true);
		builder.setPositiveButton("Register", new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int id) 
			{
				GeneralUser user = placeOverlay.getThisUser();
				if(user == null){
					Log.d("UIUtils:createRegisterDialog", "Error: user is null");
					return;
				}else{
					String name = user.getName();
					if(name == null || name == ""){
						Log.d("UIUtils:createRegisterDialog", "Error: name is null or empty.");
						return;
					}
				}
				
				if(!place.IsJoinerSigned(placeOverlay.getThisUser().getName())){
					placeOverlay.getActivity().getSPComm().requestPostRegister(place,placeOverlay.getThisUser(), new ACommHandler<String>());
				}else{
					createAlertDialog(_sAlreadyRegisterAlertMsg, placeOverlay.getActivity());
				}
				
				
				
				
			}
		});
		
		builder.setNegativeButton("Unregister", new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int id) 
			{
				GeneralUser user = placeOverlay.getThisUser();
				if(user == null){
					Log.d("UIUtils:createRegisterDialog", "Error: user is null");
					return;
				}else{
					String name = user.getName();
					if(name == null || name == ""){
						Log.d("UIUtils:createRegisterDialog", "Error: name is null or empty.");
						return;
					}
				}
				if(place.IsJoinerSigned(placeOverlay.getThisUser().getName())){
					//TODO: remove joiner
					//boolean suc = placeOverlay.getActivity().getRestTemplateFacade().RemoveJoiner(place, placeOverlay.getThisUser());
					
				}else{
					createAlertDialog(_sUserNotRegisterMsg, placeOverlay.getActivity());
				}
				
				
				
				
			}
		});
		
		builder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() 
		{
			public void onClick(DialogInterface dialog, int id) {}
		});
		
		builder.setNeutralButton("Delete", new DialogInterface.OnClickListener() 
		{
			public void onClick(DialogInterface dialog, int id) {
				if(place.getOwner().equals(placeOverlay.getThisUser().getName())){
					//TODO: add remove place
					//boolean suc = placeOverlay.getActivity().getRestTemplateFacade().RemovePlace(place);
				}else{
					createAlertDialog(_sUserNotOwnerMsg, placeOverlay.getActivity());
				}
			}
		});
		
		AlertDialog alert = builder.create();
		
		alert.show();
	}
	
	static void RegisterClick(final GeneralPlace place, final PlaceArrayItemizedOverlay placeOverlay){
		GeneralUser user = placeOverlay.getThisUser();
		if(user == null){
			Log.d("UIUtils:createRegisterDialog", "Error: user is null");
			return;
		}else{
			String name = user.getName();
			if(name == null || name == ""){
				Log.d("UIUtils:createRegisterDialog", "Error: name is null or empty.");
				return;
			}
		}
		
		if(!place.IsJoinerSigned(placeOverlay.getThisUser().getName())){
		
			// TODO: add joiner
			//boolean suc = placeOverlay.getActivity().getRestTemplateFacade().AddJoiner(place, placeOverlay.getThisUser());
			
		}else{
			createAlertDialog(_sAlreadyRegisterAlertMsg, placeOverlay.getActivity());
		}
		
	}
	
	static void DeleteClick(final GeneralPlace place, final PlaceArrayItemizedOverlay placeOverlay){
		if(place.getOwner().equals(placeOverlay.getThisUser().getName())){
			// TODO: remmove place
			//boolean suc = placeOverlay.getActivity().getRestTemplateFacade().RemovePlace(place);
		}else{
			createAlertDialog(_sUserNotOwnerMsg, placeOverlay.getActivity());
		}
	}
	
	static void UnregisterClick(final GeneralPlace place, final PlaceArrayItemizedOverlay placeOverlay){
		GeneralUser user = placeOverlay.getThisUser();
		if(user == null){
			Log.d("UIUtils:createRegisterDialog", "Error: user is null");
			return;
		}else{
			String name = user.getName();
			if(name == null || name == ""){
				Log.d("UIUtils:createRegisterDialog", "Error: name is null or empty.");
				return;
			}
		}
		if(place.IsJoinerSigned(placeOverlay.getThisUser().getName())){
			// TODO: add remove joiner
			//boolean suc = placeOverlay.getActivity().getRestTemplateFacade().RemoveJoiner(place, placeOverlay.getThisUser());
			
		}else{
			createAlertDialog(_sUserNotRegisterMsg, placeOverlay.getActivity());
		}
		
		
	}
	
	static void createRegisterDialog(String message, final GeneralPlace place, final PlaceArrayItemizedOverlay placeOverlay){
		
		if(placeOverlay == null || placeOverlay.getThisUser() == null || place == null){
			Log.d("UIUtils::createRegisterDialog", "placeOverlay == null || placeOverlay.getThisUser() == null || place == null");
			return;
		}
		
		final Dialog dialog = new Dialog(placeOverlay.getActivity());
		dialog.setContentView(R.layout.place_dialog);
		TextView text = (TextView) dialog.findViewById(R.id.TextMsg);
		String msg = message + _sWantToRegisterQues;
		text.setText(msg);
		
		Button regButton = (Button) dialog.findViewById(R.id.button1);
		regButton.setText("Register");
		if(place.IsJoinerSigned(placeOverlay.getThisUser().getName())){
			regButton.setVisibility(View.INVISIBLE);
		}
		
		Button unregButton = (Button) dialog.findViewById(R.id.button2);
		unregButton.setText("Unregister");
		if(!place.IsJoinerSigned(placeOverlay.getThisUser().getName())){
			unregButton.setVisibility(View.INVISIBLE);
		}
		
		
		Button deleteButton = (Button) dialog.findViewById(R.id.button3);
		deleteButton.setText("Delete");
		if(place.getOwner() != null && placeOverlay.getThisUser() != null){
			if(!(place.getOwner().equals(placeOverlay.getThisUser().getName()))){
				deleteButton.setVisibility(View.INVISIBLE);
			}
		}
		
		
		Button cancelButton = (Button) dialog.findViewById(R.id.button4);
		cancelButton.setText("Cancel");
		
		regButton.setOnClickListener(new OnClickListener() {
			
			public void onClick(View view) 
			{
				
				RegisterClick(place,placeOverlay);
				dialog.dismiss();
				
			};
			
		});
		
		unregButton.setOnClickListener(new OnClickListener() {
			
			public void onClick(View view) 
			{
				
				UnregisterClick(place,placeOverlay);
				dialog.dismiss();
				
			};
			
		});
		
		deleteButton.setOnClickListener(new OnClickListener() {
			
			public void onClick(View view) 
			{
				
				DeleteClick(place,placeOverlay);
				dialog.dismiss();
				
			};
			
		});
		
		cancelButton.setOnClickListener(new OnClickListener() {
			
			public void onClick(View view) 
			{
				dialog.dismiss();
				
			};
			
		});
		
		dialog.show();
		//regButton.setVisibility(visibility)
			
		
	}
	
	static void createAlertDialog(String msg, Context context ){
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
	
	 static void createNewPlaceDialog(final SPGeoPoint point, final FindPrayer activity, final GeneralUser user){
		 if(point == null || activity == null || user == null){
				Log.d("UIUtils::createRegisterDialog", "point == null || activity == null || user == null");
				return;
			}
			
			final Dialog dialog = new Dialog(activity);
			dialog.setContentView(R.layout.create_place_dialog);
			TextView text = (TextView) dialog.findViewById(R.id.TextMsgCreatePlace);
			text.setText(_sNewPlaceQues);
			
			Button yesButton = (Button) dialog.findViewById(R.id.YesButton);
			
			
			Button noButton = (Button) dialog.findViewById(R.id.NoButton);
			
			yesButton.setOnClickListener(new OnClickListener() {
				
				public void onClick(View view) 
				{
					
					CreateNewPlace_YesClick(user,activity,point);
					dialog.dismiss();
					
				};
				
			});
			
			noButton.setOnClickListener(new OnClickListener() {
				
				public void onClick(View view) 
				{
										
					dialog.dismiss();
					
				};
				
			});
			
			dialog.show();
	 }
	 
	 static void CreateNewPlace_YesClick(GeneralUser user, FindPrayer activity, SPGeoPoint point){
		 GeneralPlace newMinyan = new GeneralPlace(user, user.getName() + "'s Minyan Place", "", point);
         newMinyan.addJoiner(user.getName());
         
         activity.getSPComm().requestPostNewPlace(newMinyan, new ACommHandler<Long>());
                                 
         synchronized (activity.getRefreshTask())
         {
         	activity.getRefreshTask().notify();
         }
	 }
	
	 static void _createNewPlaceDialog(final SPGeoPoint point, final FindPrayer activity, final GeneralUser user)
	    {
	        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
	        builder.setMessage(_sNewPlaceQues);
	        builder.setCancelable(false);
	        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener()
	        {
	            
	            public void onClick(DialogInterface dialog, int id)
	            {
	                
                    GeneralPlace newMinyan = new GeneralPlace(user, user.getName() + "'s Minyan Place", "", point);
                    newMinyan.addJoiner(user.getName());
                    
                    activity.getSPComm().requestPostNewPlace(newMinyan, new ACommHandler<Long>());
                                            
                    synchronized (activity.getRefreshTask())
                    {
                    	activity.getRefreshTask().notify();
                    }
                }
            
	      
	        });
	        builder.setNegativeButton("No", new DialogInterface.OnClickListener()
	        {
	            
	            public void onClick(DialogInterface dialog, int id)
	            {
	            }
	        });
			
			AlertDialog alert = builder.create();
			
			alert.show();
		} 
	
}
