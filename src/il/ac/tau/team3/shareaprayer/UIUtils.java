package il.ac.tau.team3.shareaprayer;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import il.ac.tau.team3.common.GeneralPlace;
import il.ac.tau.team3.common.GeneralUser;
import il.ac.tau.team3.common.Pray;
import il.ac.tau.team3.common.SPGeoPoint;
import il.ac.tau.team3.common.SPUtils;
import il.ac.tau.team3.common.UnknownLocationException;
import il.ac.tau.team3.spcomm.ACommHandler;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.QuickContactBadge;
import android.widget.RemoteViews.ActionException;
import android.widget.TextView;
import android.widget.TimePicker;

public class UIUtils {

	static String _sNewPlaceQues = "Do you want to create a public praying place?";
	static String _sNoPraySelected = "Please select at least one pray before creating a new place.";
	static String _sAlreadyRegisterAlertMsg = "You are already registered to this place.";
	static String _sWantToRegisterQues = "Would you like to register to this place?";
	static String _sUserNotRegisterMsg = "You are not register to this place.";
	static String _sUserNotOwnerMsg = "You can't delete this place, because you are not the owner.";

	static class UpdateUI<T> extends ACommHandler<T> {
		FindPrayer activity;

		public UpdateUI(FindPrayer a_activity) {
			activity = a_activity;
		}

		@Override
		public void onRecv(T Obj) {
			synchronized (activity.getRefreshTask()) {
				activity.getRefreshTask().notify();
			}
		}

		@Override
		public void onError(T Obj) {
			synchronized (activity.getRefreshTask()) {
				activity.getRefreshTask().notify();
			}
		}
	}
	
	static void RegisterClick(final GeneralPlace place,
			final PlaceArrayItemizedOverlay placeOverlay, boolean praysWishes[]) {
		GeneralUser user = placeOverlay.getThisUser();
		if (user == null) {
			Log.d("UIUtils:createRegisterDialog", "Error: user is null");
			return;
		} else {
			String name = user.getName();
			if (name == null || name == "") {
				Log.d("UIUtils:createRegisterDialog",
						"Error: name is null or empty.");
				return;
			}
		}

		
		placeOverlay
				.getActivity()
				.getSPComm()
				.requestPostRegister(place, user, praysWishes,
						new UpdateUI<String>(placeOverlay.getActivity()));

		

	}

	static void DeleteClick(final GeneralPlace place,
			final PlaceArrayItemizedOverlay placeOverlay) {
		if (place.getOwner().getName().equals(placeOverlay.getThisUser().getName())) {
		
			placeOverlay
					.getActivity()
					.getSPComm()
					.deletePlace(place,
							new UpdateUI<String>(placeOverlay.getActivity()));
		} else {
			createAlertDialog(_sUserNotOwnerMsg, placeOverlay.getActivity());
		}
	}

	static void UnregisterClick(final GeneralPlace place,
			final PlaceArrayItemizedOverlay placeOverlay, boolean praysWishes[]) {
		GeneralUser user = placeOverlay.getThisUser();
		if (user == null) {
			Log.d("UIUtils:createRegisterDialog", "Error: user is null");
			return;
		} else {
			String name = user.getName();
			if (name == null || name == "") {
				Log.d("UIUtils:createRegisterDialog",
						"Error: name is null or empty.");
				return;
			}
		}
	
		placeOverlay
				.getActivity()
				.getSPComm()
				.removeJoiner(place, user, praysWishes,
						new UpdateUI<Void>(placeOverlay.getActivity()));

	

	}
	
	/*package*/ static void createRegisterDialog(String message1, String message2, String message3, final GeneralPlace place, final PlaceArrayItemizedOverlay placeOverlay)
	{
		if (placeOverlay == null || placeOverlay.getThisUser() == null || place == null) 
		{
			Log.d("UIUtils::createRegisterDialog",
					"placeOverlay == null || placeOverlay.getThisUser() == null || place == null");
			return;
		}

		final boolean praysWishes[] = new boolean[3];
 
		final Dialog dialog = new Dialog(placeOverlay.getActivity());
//		dialog.setContentView(R.layout.dialog_place_registration);
 

//		//dialog.setTitle(removeAtGmailDotCom(place.getName()));
//		
//		//People Button:
//		Button peopleButton = (Button) dialog.findViewById(R.id.DPRShowPeople);
//		
//		// Address and Dates
//		TextView placeAddress = (TextView) dialog.findViewById(R.id.DPRaddress);
//		placeAddress.setText(place.getAddress());
//		
//		TextView placeDates = (TextView) dialog.findViewById(R.id.DPRdates);
//		//TODO placeDates.setText(place.getClosingDate());
//		
//		// Checkboxes:
//		CheckBox[] praysCB = new CheckBox[3];
//		praysCB[0] = (CheckBox) dialog.findViewById(R.id.DPRcheckboxShaharit);
//		praysCB[1] = (CheckBox) dialog.findViewById(R.id.DPRcheckboxMinha);
//		praysCB[2] = (CheckBox) dialog.findViewById(R.id.DPRcheckboxArvit);
//		
//		for (int i=0; i<3 ; i++)
//		{
//			final int temp = i;
//			praysCB[i].setClickable(place.getPrays()[i]);
//			praysCB[i].setTextColor(place.getPrays()[i] ? Color.WHITE : Color.GRAY);
//			praysCB[i].setOnCheckedChangeListener(new OnCheckedChangeListener() 
//			{
//				public void onCheckedChanged(CompoundButton buttonView,	boolean isChecked) 
//				{
//					praysWishes[temp] = isChecked;
//				}
//			});
//		}
//		for (int i=0; i<3 ; i++)
//		{		
//			try 
//			{
//				//praysCB[i].setChecked(place.IsJoinerSigned(i+1, placeOverlay.getThisUser()));
//			} catch (NullPointerException e){}
//					
//		}
////		praysCB[1].setChecked(place.IsJoinerSigned(2, placeOverlay.getThisUser()));
////		praysCB[2].setChecked(place.IsJoinerSigned(3, placeOverlay.getThisUser()));
////		praysCB[0].setChecked(place.IsJoinerSigned(placeOverlay.getThisUser().getName()));
////		praysCB[1].setChecked(place.IsJoinerSigned2(placeOverlay.getThisUser().getName()));
////		praysCB[2].setChecked(place.IsJoinerSigned3(placeOverlay.getThisUser().getName()));
//
//		//Prayers times:
//		TextView[] prayersTimes = new TextView[3];
//		prayersTimes[0] = (TextView) dialog.findViewById(R.id.DPRtimeShaharit);
//		prayersTimes[1] = (TextView) dialog.findViewById(R.id.DPRtimeMinha);
//		prayersTimes[2] = (TextView) dialog.findViewById(R.id.DPRtimeArvit);
//		// TODO get times of prayers and set the texts
//		
//		
//		// Number of registered users:
//		TextView[] numberOfUsers = new TextView[3];
//		numberOfUsers[0] = (TextView) dialog.findViewById(R.id.DPRnumberOfUsersShaharit);
//		numberOfUsers[1] = (TextView) dialog.findViewById(R.id.DPRnumberOfUsersMinha);
//		numberOfUsers[2] = (TextView) dialog.findViewById(R.id.DPRnumberOfUsersArvit);
//		
//		numberOfUsers[0].setText(String.valueOf(place.getNumberOfPrayers(0)));
//		numberOfUsers[1].setText(String.valueOf(place.getNumberOfPrayers(1)));
//		numberOfUsers[2].setText(String.valueOf(place.getNumberOfPrayers(2)));
//			
//		
//		// Set and Cancel Buttons:
//		Button setButton = (Button) dialog.findViewById(R.id.DPRSetButton);
//		Button cancelButton = (Button) dialog.findViewById(R.id.DPRCancelButton);
//
//		setButton.setOnClickListener(new OnClickListener() 
//		{
//			public void onClick(View view) 
//			{
//				//TODO Check for problems
//				UnregisterClick(place, placeOverlay, praysWishes);
//				RegisterClick(place, placeOverlay, praysWishes);
//				dialog.dismiss();
//			};
//		});
//		
//		cancelButton.setOnClickListener(new OnClickListener() 
//		{
//			public void onClick(View view) 
//			{
//				dialog.dismiss();
//			};
//		});
//				
//		dialog.show();
//		
//		
//		
//		
//		
		
		
		
		dialog.setContentView(R.layout.place_dialog);
		if (place.getPrays()[0]) {
			
			TextView text = (TextView) dialog.findViewById(R.id.RTextMsg);
			//String msg = message1 + _sWantToRegisterQues;
			text.setText(message1);
		}
		if (place.getPrays()[1]) {
			TextView text2 = (TextView) dialog.findViewById(R.id.RTextMsg1);
			//String msg2 = message2 + _sWantToRegisterQues;
			text2.setText(message2);
		}
		if (place.getPrays()[2]) {
			TextView text3 = (TextView) dialog.findViewById(R.id.RTextMsg2);
			//String msg3 = message3 + _sWantToRegisterQues;
			text3.setText(message3);
		}

		CheckBox pray1 = (CheckBox) dialog.findViewById(R.id.checkBoxPlace1);
		if (!place.getPrays()[0]) {
			pray1.setVisibility(View.INVISIBLE);
		}

		CheckBox pray2 = (CheckBox) dialog.findViewById(R.id.checkBoxPlace2);
		if (!place.getPrays()[1]) {
			pray2.setVisibility(View.INVISIBLE);
		}
		CheckBox pray3 = (CheckBox) dialog.findViewById(R.id.checkBoxPlace3);
		if (!place.getPrays()[2]) {
			pray3.setVisibility(View.INVISIBLE);
		}

		pray1.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (isChecked) {
					praysWishes[0] = true;
				} else {
					praysWishes[0] = false;
				}

			}
		});

		pray2.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (isChecked) {
					praysWishes[1] = true;
				} else {
					praysWishes[1] = false;
				}

			}
		});

		pray3.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (isChecked) {
					praysWishes[2] = true;
				} else {
					praysWishes[2] = false;
				}

			}
		});

		Button regButton = (Button) dialog.findViewById(R.id.button1);
		regButton.setText("Reg");
		// if(place.IsJoinerSigned(placeOverlay.getThisUser().getName())){
		// regButton.setVisibility(View.INVISIBLE);
		// }

		Button unregButton = (Button) dialog.findViewById(R.id.button2);
		unregButton.setText("Unreg");
		// if(!place.IsJoinerSigned(placeOverlay.getThisUser().getName())){
		// unregButton.setVisibility(View.INVISIBLE);
		// }

		Button deleteButton = (Button) dialog.findViewById(R.id.button3);
		deleteButton.setText("Delete");
		if (place.getOwner() != null && placeOverlay.getThisUser() != null) {
			if (!(place.getOwner().getName().equals(placeOverlay.getThisUser().getName()))) {
				deleteButton.setVisibility(View.INVISIBLE);
			}
		}

		Button cancelButton = (Button) dialog.findViewById(R.id.button4);
		cancelButton.setText("Cancel");

		regButton.setOnClickListener(new OnClickListener() {

			public void onClick(View view) {

				RegisterClick(place, placeOverlay, praysWishes);
				dialog.dismiss();

			};

		});

		unregButton.setOnClickListener(new OnClickListener() {

			public void onClick(View view) {

				UnregisterClick(place, placeOverlay, praysWishes);
				dialog.dismiss();

			};

		});

		deleteButton.setOnClickListener(new OnClickListener() {

			public void onClick(View view) {

				DeleteClick(place, placeOverlay);
				dialog.dismiss();

			};

		});

		cancelButton.setOnClickListener(new OnClickListener() {

			public void onClick(View view) {
				dialog.dismiss();

			};

		});

		dialog.show();
		// regButton.setVisibility(visibility)

	}

	static void createAlertDialog(String msg, Context context) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setMessage(msg);
		builder.setCancelable(true);
		builder.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
					}
				});
		AlertDialog alert = builder.create();
		alert.show();
	}
	
	static class CreatePlaceDialog	{		private Dialog dialog;
		private EditText editAddress;
		private Calendar startDate = new GregorianCalendar(); 
		private Calendar endDate = new GregorianCalendar(); 
		private TextView fromDate;
        private TextView toDate;
        private FindPrayer activity;
        private Button changeStartDate;
        private Button changeEndDate;
        private final int NUMBER_OF_PRAYS = 3;
        private boolean prays[] = new boolean[NUMBER_OF_PRAYS];
        private CheckBox[] checkBoxes = new CheckBox[NUMBER_OF_PRAYS];
        private TextView[] timeTextViews = new TextView[NUMBER_OF_PRAYS];
        private Calendar[] prayTimes = new GregorianCalendar[NUMBER_OF_PRAYS];
      
        
        private class DatePickerClickListener implements OnClickListener	{
        	
        	private Calendar cal;
        	private TextView textStr;
        	
        	public DatePickerClickListener(Calendar a_cal, TextView a_textStr)	{
        		cal = a_cal;
        		textStr = a_textStr;
        	}

			public void onClick(View v) {
				DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener()
                {
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth)
                    {
                    	cal.set(year, monthOfYear, dayOfMonth);
                        //monthOfYear++;
                    	textStr.setText(printDateFromCalendar(cal));
                        // TODO Send dates to server
                    }
                };
                DatePickerDialog datePickerDialog = 
                	new DatePickerDialog(
                			CreatePlaceDialog.this.activity, 
                			mDateSetListener, 
                			cal.get(Calendar.YEAR), 
                			cal.get(Calendar.MONTH), 
                			cal.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.show();
			}
        	
        }
        
        class PrayTimePickDialog extends TimePickerDialog {

        	private CheckBox checkBox;
        	private int prayIndex;
        	
        	public PrayTimePickDialog(final TextView a_timeStr, int defHour, int defMin, 
        			CheckBox a_checkBox, final int a_prayIndex, int a_resIcon)	{
        		super(activity, 
        				new OnTimeSetListener() {
        			
        			public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
    					SPUtils.debugFuncStart("timePickerDialog.onTimeSet", view, hourOfDay, minute);
    					CreatePlaceDialog.this.prays[a_prayIndex] = true;
    					prayTimes[a_prayIndex].set(2000, 1, 1, hourOfDay, minute, 0);
    					// TODO: CLEAN THIS
    					a_timeStr.setText((hourOfDay < 10 ? "0" : "") + hourOfDay + ":" + (minute < 10 ? "0" : "") + minute + " ");
    					
    				}
        			
        		}, defHour, defMin, true);
        		
                this.setIcon(a_resIcon);
                this.setInverseBackgroundForced(true);          
                this.setCancelable(true);              //
                this.setCanceledOnTouchOutside(true);  //
        		checkBox = a_checkBox;
        	}
        	
        	@Override
			public void cancel()	{
        		SPUtils.debugFuncStart("timePickerDialog.onCancel", dialog);
        		super.cancel();
        	}
        	
        	@Override
        	public void dismiss()	{
        		SPUtils.debugFuncStart("timePickerDialog.onDismiss", dialog);
                SPUtils.debug("--> prays["+prayIndex+"] = " + "prays["+prayIndex+"]");
                checkBox.setChecked(prays[prayIndex]);
                super.dismiss();
        	}
        	
        	
        	
        }
        
        class CheckBoxListener implements OnCheckedChangeListener
        {              
        	private TextView timeTextView;
        	private int		 index;
        	private CheckBox checkBox;
        	int defHour;
        	int defMinutes;
        	int resIcon;
        	
        	
            public CheckBoxListener(TextView timeTextView, int index, CheckBox checkBox, 
            		int defHour, int defMinutes, int resIcon) {
				super();
				this.timeTextView = timeTextView;
				this.index = index;
				this.checkBox = checkBox;
				this.defHour = defHour;
				this.defMinutes = defMinutes;
				this.resIcon = resIcon;
				
			}

			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                SPUtils.debugFuncStart("pray1.onCheckedChanged", buttonView, isChecked);
                
                if (isChecked)
                {
                                        
                    (new PrayTimePickDialog(timeTextView, defHour, defMinutes, checkBox, index, resIcon)).show();
                    
                }
                
                else
                {
                    prays[index] = false;
                    timeTextView.setText("");
                }                
            }
        };
		
		public CreatePlaceDialog(final SPGeoPoint point, final FindPrayer a_activity, final GeneralUser user)	{
			if (point == null || a_activity == null || user == null)
			{
				
				Log.d("UIUtils::createRegisterDialog", "point == null || activity == null || user == null");
				// TODO: change to checked exception
				throw new NullPointerException("CreatePlaceDialog: executed with NULL!!!!");
				//return;
			}
			
			for (int i = 0; i < prayTimes.length; prayTimes[i] = new GregorianCalendar(),i++);
			  
			activity = a_activity;
			
			dialog = new Dialog(activity);
			dialog.setContentView(R.layout.dialog_place_create);
			dialog.setTitle(R.string.create_place_title);
			
			editAddress = (EditText) dialog.findViewById(R.id.CPDeditText1);
			
			fromDate = (TextView) dialog.findViewById(R.id.CPDFromDatetextView);
			toDate   = (TextView) dialog.findViewById(R.id.CPDToDatetextView);
			fromDate.setText(printDateFromCalendar(startDate)); 
	        toDate.setText(printDateFromCalendar(endDate)); 
	        
	        changeStartDate = (Button) dialog.findViewById(R.id.CPDChange1button);
	        changeEndDate = (Button) dialog.findViewById(R.id.CPDChange2button);
	        
			changeStartDate.setOnClickListener(new DatePickerClickListener(startDate, fromDate));
			changeEndDate.setOnClickListener(new DatePickerClickListener(endDate, toDate));
			
			
			checkBoxes[0] = (CheckBox) dialog.findViewById(R.id.CPDcheckBox1);
			timeTextViews[0] = (TextView) dialog.findViewById(R.id.CPDshahritTime);
			checkBoxes[0].setOnCheckedChangeListener(new CheckBoxListener(timeTextViews[0], 
					0, checkBoxes[0], 7, 0, R.drawable.shaharit_small));
			
			checkBoxes[1] = (CheckBox) dialog.findViewById(R.id.CPDcheckBox2);
			timeTextViews[1] = (TextView) dialog.findViewById(R.id.CPDminhaTime);
			checkBoxes[1].setOnCheckedChangeListener(new CheckBoxListener(timeTextViews[1], 
					1, checkBoxes[0], 15, 0, R.drawable.minha_small));
			
			checkBoxes[2] = (CheckBox) dialog.findViewById(R.id.CPDcheckBox3);
			timeTextViews[2] = (TextView) dialog.findViewById(R.id.CPDarvitTime);
			checkBoxes[2].setOnCheckedChangeListener(new CheckBoxListener(timeTextViews[2], 
					2, checkBoxes[2], 19, 0, R.drawable.arvit_small));
			
			Button createButton = (Button) dialog.findViewById(R.id.CPDCreateButton);
	        Button cancelButton = (Button) dialog.findViewById(R.id.CPDCancelButton);
	        
	    
			createButton.setOnClickListener(new OnClickListener() {

				public void onClick(View view) {
					final Date finalstartDate = new Date(startDate.get(Calendar.YEAR),startDate.get(Calendar.MONTH),startDate.get(Calendar.DAY_OF_MONTH));
					final Date finalendDate = new Date(endDate.get(Calendar.YEAR),endDate.get(Calendar.MONTH),endDate.get(Calendar.DAY_OF_MONTH));
					CreateNewPlace_YesClick(prays, user, activity, point, finalstartDate, finalendDate, prayTimes);
					dialog.dismiss();

				};

			});

			cancelButton.setOnClickListener(new OnClickListener() {

				public void onClick(View view) {

					dialog.dismiss();

				};

			});

			
			dialog.show();
			
		}

	};

	static void createNewPlaceDialog(final SPGeoPoint point, final FindPrayer activity, final GeneralUser user) 
	{
		try {
			new CreatePlaceDialog(point, activity, user);
		} catch (NullPointerException e)	{
			
		}
		
     }
	
	static void CreateNewPlace_YesClick(boolean prays[], GeneralUser user,
			FindPrayer activity, SPGeoPoint point, Date startDate, Date endDate , Calendar[] prayTimes) {
		GeneralPlace newMinyan = new GeneralPlace(user, user.getName()
				+ "'s Minyan Place", "", point, startDate,endDate);
		newMinyan.setPrays(prays);
		//Calendar c = new GregorianCalendar(2011,2,2,15,30);
		Calendar c = new GregorianCalendar();
		Pray praysOfTheDay[] = new Pray[3];
		List<GeneralUser> j = new ArrayList<GeneralUser>();
		j.add(user);
		if (prays[0]) {
			
			Pray pray = new Pray(prayTimes[0], c, "Shaharit", j);
			newMinyan.setPraysOfTheDay(0, pray);
		}
		if (prays[1]) {
			Pray pray = new Pray(prayTimes[1], c, "Minha", j);
			newMinyan.setPraysOfTheDay(1, pray);
		}
		if (prays[2]) {
			Pray pray = new Pray(prayTimes[2], c, "Arvit", j);
			newMinyan.setPraysOfTheDay(2, pray);
		}

		activity.getSPComm().requestPostNewPlace(newMinyan,
				new UpdateUI<Long>(activity));

		
	}

	public static String printDateFromCalendar(Calendar c) {
		int day = c.get(Calendar.DAY_OF_MONTH);
		int month = (c.get(Calendar.MONTH)+1);
		int year = c.get(Calendar.YEAR);
		return ((month < 10 ? "0" : "") + month + "/" + (day < 10 ? "0" : "") + day + "/" + year);
	}


    
    
    
}
