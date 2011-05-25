package il.ac.tau.team3.uiutils;



import android.app.Dialog;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;
import il.ac.tau.team3.addressQuery.MapsQueryLocation;
import il.ac.tau.team3.common.GeneralUser;
import il.ac.tau.team3.common.UnknownLocationException;
import il.ac.tau.team3.shareaprayer.FindPrayer;
import il.ac.tau.team3.shareaprayer.R;
import il.ac.tau.team3.spcomm.ACommHandler;

public class UserDetailsDialog {
	private GeneralUser user;
	private FindPrayer activity;
	private Dialog dialog;
	//private TextView user_full_name;
	private TextView user_mail;
	private TextView user_address;
	private TextView user_status;
	private Button btn_dismiss;
	
	public UserDetailsDialog(GeneralUser user, FindPrayer activity)	{
		this.user = user;
		this.activity = activity;
		
		dialog = new Dialog(this.activity);
		dialog.setContentView(R.layout.dialog_user_details);
		
		dialog.setTitle(this.user.getFullName());
		dialog.getWindow().setLayout(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		
		user_mail = (TextView)dialog.findViewById(R.id.UDDMail);
		user_address = (TextView)dialog.findViewById(R.id.UDDAddress);
		user_status = (TextView)dialog.findViewById(R.id.UDDStatus);
		
		btn_dismiss = (Button)dialog.findViewById(R.id.UUDDismiss);
		
		user_mail.setText(this.user.getName());
		user_status.setText(this.user.getStatus());
	
		
		try {
			activity.getSPComm().getAddressObj(this.user.getSpGeoPoint().getLatitudeInDegrees(), 
					this.user.getSpGeoPoint().getLongitudeInDegrees(),
					new ACommHandler<MapsQueryLocation>() {
				@Override
				public void onRecv(final MapsQueryLocation Obj)	{
					UserDetailsDialog.this.activity.runOnUiThread(new Runnable() {

						public void run() {
							// TODO Auto-generated method stub
							try	{
								user_address.setText(Obj.getResults()[0].getFormatted_address());
							} catch (Exception e)	{
								user_address.setText("Error getting address");
							}
						}

					});};

					@Override
					public void onError(MapsQueryLocation Obj)	{
						UserDetailsDialog.this.activity.runOnUiThread(new Runnable() {

							public void run() {
								user_address.setText("Error getting address");
							}
						});
					}
			});
		} catch (UnknownLocationException e) {
			user_address.setText("Error getting address");
		}
		
		btn_dismiss.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				dialog.dismiss();
				
			}
		
		});
		
		dialog.show();
		
		
		
	}
	
}
