package il.ac.tau.team3.shareaprayer;

import il.ac.tau.team3.common.GeneralPlace;
import il.ac.tau.team3.common.GeneralUser;
import il.ac.tau.team3.common.SPGeoPoint;
import il.ac.tau.team3.spcomm.ACommHandler;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

public class UIUtils {

	static String _sNewPlaceQues = "Do you want to create a public praying place?";
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

	static void createRegisterDialog(String message1, String message2,
			String message3, final GeneralPlace place,
			final PlaceArrayItemizedOverlay placeOverlay) {

		if (placeOverlay == null || placeOverlay.getThisUser() == null
				|| place == null) {
			Log.d("UIUtils::createRegisterDialog",
					"placeOverlay == null || placeOverlay.getThisUser() == null || place == null");
			return;
		}

		final boolean praysWishes[] = new boolean[3];

		final Dialog dialog = new Dialog(placeOverlay.getActivity());
		if (place.getPrays()[0]) {
			dialog.setContentView(R.layout.place_dialog);
			TextView text = (TextView) dialog.findViewById(R.id.TextMsg);
			//String msg = message1 + _sWantToRegisterQues;
			text.setText(message1);
		}
		if (place.getPrays()[1]) {
			TextView text2 = (TextView) dialog.findViewById(R.id.TextMsg1);
			//String msg2 = message2 + _sWantToRegisterQues;
			text2.setText(message2);
		}
		if (place.getPrays()[2]) {
			TextView text3 = (TextView) dialog.findViewById(R.id.TextMsg2);
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
		regButton.setText("Register");
		// if(place.IsJoinerSigned(placeOverlay.getThisUser().getName())){
		// regButton.setVisibility(View.INVISIBLE);
		// }

		Button unregButton = (Button) dialog.findViewById(R.id.button2);
		unregButton.setText("Unregister");
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

	static void createNewPlaceDialog(final SPGeoPoint point,
			final FindPrayer activity, final GeneralUser user) {

		// activity.getSPComm().requestGetUserByAccount(user.getName(),new
		// ACommHandler<Long>() {
		// @Override
		// public void onRecv(Long id) {
		// Long a = id;
		// Log.d(a.toString(),a.toString());
		// //
		// }
		// });

		final boolean prays[] = new boolean[3];

		if (point == null || activity == null || user == null) {
			Log.d("UIUtils::createRegisterDialog",
					"point == null || activity == null || user == null");
			return;
		}

		final Dialog dialog = new Dialog(activity);
		dialog.setContentView(R.layout.create_place_dialog);
		TextView text = (TextView) dialog.findViewById(R.id.TextMsgCreatePlace);
		text.setText(_sNewPlaceQues);

		Button yesButton = (Button) dialog.findViewById(R.id.CPDYesButton);

		Button noButton = (Button) dialog.findViewById(R.id.CPDNoButton);

		CheckBox pray1 = (CheckBox) dialog.findViewById(R.id.CPDcheckBox1);

		CheckBox pray2 = (CheckBox) dialog.findViewById(R.id.CPDcheckBox2);
		CheckBox pray3 = (CheckBox) dialog.findViewById(R.id.CPDcheckBox3);

		pray1.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (isChecked) {
					prays[0] = true;
				} else {
					prays[0] = false;
				}

			}
		});

		pray2.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (isChecked) {
					prays[1] = true;
				} else {
					prays[1] = false;
				}

			}
		});

		pray3.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (isChecked) {
					prays[2] = true;
				} else {
					prays[2] = false;
				}

			}
		});

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

	static void CreateNewPlace_YesClick(boolean prays[], GeneralUser user,
			FindPrayer activity, SPGeoPoint point) {
		GeneralPlace newMinyan = new GeneralPlace(user, user.getName()
				+ "'s Minyan Place", "", point);
		newMinyan.setPrays(prays);
		if (prays[0]) {
			newMinyan.addJoiner(user.getName());
		}
		if (prays[1]) {
			newMinyan.addJoiner2(user.getName());
		}
		if (prays[2]) {
			newMinyan.addJoiner3(user.getName());
		}

		activity.getSPComm().requestPostNewPlace(newMinyan,
				new UpdateUI<Long>(activity));

		// synchronized (activity.getRefreshTask())
		// {
		// activity.getRefreshTask().notify();
		// }
	}

}
