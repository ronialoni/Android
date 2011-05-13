package il.ac.tau.team3.shareaprayer;

import java.util.Calendar;

import il.ac.tau.team3.common.GeneralPlace;
import il.ac.tau.team3.common.GeneralUser;
import il.ac.tau.team3.common.SPGeoPoint;
import il.ac.tau.team3.common.SPUtils;
import il.ac.tau.team3.spcomm.ACommHandler;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
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
	static String _sAddress = "Address:";
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

		// if(!place.IsJoinerSigned(placeOverlay.getThisUser().getName())){

		// TODO: add joiner
		// boolean suc =
		// placeOverlay.getActivity().getRestTemplateFacade().AddJoiner(place,
		// placeOverlay.getThisUser());
		placeOverlay
				.getActivity()
				.getSPComm()
				.requestPostRegister(place, user, praysWishes,
						new UpdateUI<String>(placeOverlay.getActivity()));

		// }else{
		// createAlertDialog(_sAlreadyRegisterAlertMsg,
		// placeOverlay.getActivity());
		// }

	}

	static void DeleteClick(final GeneralPlace place,
			final PlaceArrayItemizedOverlay placeOverlay) {
		if (place.getOwner().equals(placeOverlay.getThisUser().getName())) {
			// TODO: remmove place
			// boolean suc =
			// placeOverlay.getActivity().getRestTemplateFacade().RemovePlace(place);
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
		// if(place.IsJoinerSigned(placeOverlay.getThisUser().getName())){
		// TODO: add remove joiner
		// boolean suc =
		// placeOverlay.getActivity().getRestTemplateFacade().RemoveJoiner(place,
		// placeOverlay.getThisUser());
		placeOverlay
				.getActivity()
				.getSPComm()
				.removeJoiner(place, user, praysWishes,
						new UpdateUI<Void>(placeOverlay.getActivity()));

		// }else{
		// createAlertDialog(_sUserNotRegisterMsg, placeOverlay.getActivity());
		// }

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
		dialog.setContentView(R.layout.place_dialog);
		dialog.getWindow().setLayout(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
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
			if (!(place.getOwner().equals(placeOverlay.getThisUser().getName()))) {
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
	
	
	

	/*package*/ static void createAlertDialog(String msg, Context context) 
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setMessage(msg);
		builder.setCancelable(true);
		builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() 
		{
		    public void onClick(DialogInterface dialog, int id)
		    {
		    }
		});
		AlertDialog alert = builder.create();
		alert.show();
	}
	
	
	
	

	/*package*/ static void createNewPlaceDialog(final SPGeoPoint point, final FindPrayer activity, final GeneralUser user) 
	{
		// activity.getSPComm().requestGetUserByAccount(user.getName(),new
		// ACommHandler<Long>() {
		// @Override
		// public void onRecv(Long id) {
		// Long a = id;
		// Log.d(a.toString(),a.toString());
		// //
		// }
		// });

		final boolean prays[] = new boolean[] {false, false, false};

		if (point == null || activity == null || user == null)
		{
			Log.d("UIUtils::createRegisterDialog", "point == null || activity == null || user == null");
			//return;
		}

		final Dialog dialog = new Dialog(activity);
		dialog.setContentView(R.layout.dialog_place_create);
		dialog.setTitle(R.string.create_place_title);
		//dialog.setTitle(R.layout.image_row);
        
		
		//TextView text = (TextView) dialog.findViewById(R.id.TextMsgCreatePlace);
		//text.setText(_sNewPlaceQues);
		
		// Address field
		TextView text1 = (TextView) dialog.findViewById(R.id.CPDtextView1);
        text1.setText(_sAddress);
		
		
        EditText edittext = (EditText) dialog.findViewById(R.id.CPDeditText1);
		
		
		
		// TODO Create a function Point_to_Address
        //edittext.setText(point_to_address(point));
        edittext.setText("DEBUG: the address touched");
		
		        
        
        /*
         * Dates fields:
         */        
        final TextView fromDate = (TextView) dialog.findViewById(R.id.CPDFromDatetextView);
        final TextView toDate   = (TextView) dialog.findViewById(R.id.CPDToDatetextView);
        // get the current date 
        final Calendar calendar = Calendar.getInstance(); 
        final int mYear = calendar.get(Calendar.YEAR); 
        final int mMonth = calendar.get(Calendar.MONTH)+1; 
        final int mDay = calendar.get(Calendar.DAY_OF_MONTH); 
        fromDate.setText((mDay < 10 ? "0" : "") + mDay + "/" + (mMonth < 10 ? "0" : "") + mMonth + "/" + mYear + " ");
        
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        final int mYear2 = calendar.get(Calendar.YEAR); 
        final int mMonth2 = calendar.get(Calendar.MONTH)+1; 
        final int mDay2 = calendar.get(Calendar.DAY_OF_MONTH); 
        toDate.setText((mDay2 < 10 ? "0" : "") + mDay2 + "/" + (mMonth2 < 10 ? "0" : "") + mMonth2 + "/" + mYear2 + " ");
        
        ((Button) dialog.findViewById(R.id.CPDChange1button)).setOnClickListener(new OnClickListener()
        {            
            public void onClick(View v)
            {
                DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener()
                {
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth)
                    {
                        monthOfYear++;
                        fromDate.setText((dayOfMonth < 10 ? "0" : "") + dayOfMonth + "/" + (monthOfYear < 10 ? "0" : "") + monthOfYear + "/" + year + " ");
                        // TODO Send dates to server
                    }
                };
                DatePickerDialog datePickerDialog = new DatePickerDialog(activity, mDateSetListener, mYear, mMonth-1, mDay);
                datePickerDialog.show();
            }
        });
        
        ((Button) dialog.findViewById(R.id.CPDChange2button)).setOnClickListener(new OnClickListener()
        {            
            public void onClick(View v)
            {
                DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener()
                {
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth)
                    {
                        monthOfYear++;
                        toDate.setText((dayOfMonth < 10 ? "0" : "") + dayOfMonth + "/" + (monthOfYear < 10 ? "0" : "") + monthOfYear + "/" + year + " ");
                        // TODO Send dates to server
                    }
                };
                DatePickerDialog datePickerDialog = new DatePickerDialog(activity, mDateSetListener, mYear2, mMonth2-1, mDay2);
                datePickerDialog.show();
            }
        });
        
        
        
//        // TimePicker PopupWindow
//        final PopupWindow pop = new PopupWindow(activity);
//        View layout = ((LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.time_picker, (ViewGroup) activity.findViewById(R.id.time_picker_root));
//        pop.setContentView(layout);
//        pop.setWindowLayoutMode(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        
        
		/*
		 * Prayer type check-boxes.
		 */		
		final CheckBox pray1 = (CheckBox) dialog.findViewById(R.id.CPDcheckBox1);
		CheckBox pray2 = (CheckBox) dialog.findViewById(R.id.CPDcheckBox2);
		CheckBox pray3 = (CheckBox) dialog.findViewById(R.id.CPDcheckBox3);
	
		
        pray1.setOnCheckedChangeListener(new OnCheckedChangeListener()
        {              
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                SPUtils.debugFuncStart("pray1.onCheckedChanged", buttonView, isChecked);
                final TextView shaharitTime = (TextView) dialog.findViewById(R.id.CPDshahritTime);
                
                if (isChecked)
                {
                    int mHour   = 7;
                    int mMinute = 0;
                    
                    TimePickerDialog.OnTimeSetListener mTimeSetListener = new TimePickerDialog.OnTimeSetListener() 
                    {                        
                        public void onTimeSet(TimePicker view, int hourOfDay, int minute)
                        {
                            SPUtils.debugFuncStart("timePickerDialog.onTimeSet", view, hourOfDay, minute);
                            
                            prays[0] = true;
                            shaharitTime.setText((hourOfDay < 10 ? "0" : "") + hourOfDay + ":" + (minute < 10 ? "0" : "") + minute + " ");
                            
                            // TODO Update times of Shaharit in the server
                        }
                    };
                    
                    TimePickerDialog timePickerDialog = new TimePickerDialog(activity, mTimeSetListener, mHour, mMinute, true);
                    timePickerDialog.setIcon(R.drawable.shaharit_small);
                    timePickerDialog.setInverseBackgroundForced(true);          
                    timePickerDialog.setCancelable(true);              //
                    timePickerDialog.setCanceledOnTouchOutside(true);  //
                    timePickerDialog.setOnCancelListener(new OnCancelListener()
                    {                        
                        /**
                         * @imp Runs only when pressing the "back" button ????!!!!
                         *      So, I I will igno0re this function.
                         */
                        //@Override
                        public void onCancel(DialogInterface dialog)
                        {
                            // TODO This apparently does nothing, try making it work
                            SPUtils.debugFuncStart("timePickerDialog.onCancel", dialog);
                        }
                    });
                                                           
                    timePickerDialog.setOnDismissListener(new DialogInterface.OnDismissListener()
                    {                        
                        public void onDismiss(DialogInterface dialog)
                        {
                            SPUtils.debugFuncStart("timePickerDialog.onDismiss", dialog);
                            SPUtils.debug("--> prays[0] = " + prays[0]);
                            pray1.setChecked(prays[0]);
                            
                        }
                    });                    
                    
                    timePickerDialog.show();
                }
                
                else
                {
                    prays[0] = false;
                    shaharitTime.setText("");
                }                
            }
        });

        pray2.setOnCheckedChangeListener(new OnCheckedChangeListener()
        {              
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                SPUtils.debugFuncStart("pray2.onCheckedChanged", buttonView, isChecked);
                final TextView minhaTime = (TextView) dialog.findViewById(R.id.CPDminhaTime);
                if (isChecked)
                {
                    prays[1]    = true;
                    int mHour   = 16;
                    int mMinute = 0;
                    TimePickerDialog.OnTimeSetListener mTimeSetListener = new TimePickerDialog.OnTimeSetListener() 
                    {                        
                        public void onTimeSet(TimePicker view, int hourOfDay, int minute)
                        {
                            minhaTime.setText((hourOfDay < 10 ? "0" : "") + hourOfDay + ":" + (minute < 10 ? "0" : "") + minute + " ");
                            // TODO Update times of Minha in the server
                        }
                    };
                    TimePickerDialog timePickerDialog = new TimePickerDialog(activity, mTimeSetListener, mHour, mMinute, true);
                    timePickerDialog.setIcon(R.drawable.minha_small);
                    //timePickerDialog.setInverseBackgroundForced(true);
                    
                    timePickerDialog.show();
                }
                else
                {
                    prays[1] = false;
                    minhaTime.setText("");
                }                
            }
        });
        
        pray3.setOnCheckedChangeListener(new OnCheckedChangeListener()
        {              
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                SPUtils.debugFuncStart("pray3.onCheckedChanged", buttonView, isChecked);
                final TextView arvitTime = (TextView) dialog.findViewById(R.id.CPDarvitTime);
                if (isChecked)
                {
                    prays[2] = true;
                    int mHour = 18;
                    int mMinute = 0;
                    TimePickerDialog.OnTimeSetListener mTimeSetListener = new TimePickerDialog.OnTimeSetListener() 
                    {                        
                        public void onTimeSet(TimePicker view, int hourOfDay, int minute)
                        {
                            arvitTime.setText((hourOfDay < 10 ? "0" : "") + hourOfDay + ":" + (minute < 10 ? "0" : "") + minute + " ");
                            // TODO Update times of Arvit in the server
                        }
                    };
                    TimePickerDialog timePickerDialog = new TimePickerDialog(activity, mTimeSetListener, mHour, mMinute, true);
                    timePickerDialog.setIcon(R.drawable.arvit_small);
                    //timePickerDialog.setInverseBackgroundForced(true);

                    timePickerDialog.show();
                }
                else
                {
                    prays[2] = false;
                    arvitTime.setText("");
                }                
            }
        });
		

        
        /*
         * Yes/No buttons.
         */
        Button yesButton = (Button) dialog.findViewById(R.id.CPDYesButton);
        Button noButton = (Button) dialog.findViewById(R.id.CPDNoButton);
        
    
		yesButton.setOnClickListener(new OnClickListener() {

			public void onClick(View view) {

				CreateNewPlace_YesClick(prays, user, activity, point);
				dialog.dismiss();

			};

		});

		noButton.setOnClickListener(new OnClickListener() {

			public void onClick(View view) {

				dialog.dismiss();

			};

		});

		
		dialog.show();
	}

	
	
	
    /* package */static void CreateNewPlace_YesClick(boolean prays[], GeneralUser user, FindPrayer activity, SPGeoPoint point)
    {
        GeneralPlace newMinyan = new GeneralPlace(user, user.getName() + "'s Minyan Place", "", point);
        newMinyan.setPrays(prays);
        if (prays[0])
        {
            newMinyan.addJoiner(user.getName());
        }
        if (prays[1])
        {
            newMinyan.addJoiner2(user.getName());
        }
        if (prays[2])
        {
            newMinyan.addJoiner3(user.getName());
        }
        
        activity.getSPComm().requestPostNewPlace(newMinyan, new UpdateUI<Long>(activity));
        
        // synchronized (activity.getRefreshTask())
        // {
        // activity.getRefreshTask().notify();
        // }
    }

    
    
    
}
