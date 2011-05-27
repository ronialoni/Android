package il.ac.tau.team3.uiutils;



import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
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
	private TextView user_full_name;
	private TextView user_mail;
	private TextView user_address;
	private TextView user_status;
	private Button btn_dismiss;
	//private PopupWindow window;
	
	public UserDetailsDialog(GeneralUser user, final FindPrayer activity)	
	{
		this.user = user;
		this.activity = activity;
		
		dialog = new NoTitleDialog(this.activity);
        dialog.setContentView(R.layout.dialog_user_details);
        
        dialog.setTitle(this.user.getFullName());
        dialog.setCanceledOnTouchOutside(true);
        
        dialog.getWindow().setLayout(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
        
        user_full_name = (TextView)dialog.findViewById(R.id.DUDFullName);
        user_mail = (TextView)dialog.findViewById(R.id.DUDMail);
        user_address = (TextView)dialog.findViewById(R.id.DUDAddress);
        user_status = (TextView)dialog.findViewById(R.id.DUDStatus);
        btn_dismiss = (Button)dialog.findViewById(R.id.DUDClose);
        
        // Title
        
        user_full_name.setText(this.user.getFullName());
        user_full_name.setTextColor(Color.WHITE);
        user_full_name.setTextSize(30);
        
        // Status
        user_status.setText("\"" + this.user.getStatus() + "\"");
        user_status.setTextColor(Color.WHITE);
        user_status.setTextSize(20);
        
		// Address
        
        
        // Mail
        user_mail.setText(this.user.getName());
        user_mail.setClickable(true);
        user_mail.setLinkTextColor(Color.WHITE);
        user_mail.setOnClickListener(new OnClickListener()
        {                
            public void onClick(View v)
            {
            	try{
            		Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
            	   	emailIntent.setType("plain/text");
	            	//activity.startActivity(Intent.createChooser(emailIntent, "Send your email in:"));
	            	activity.startActivity(emailIntent);
            	}
            	catch(UnsupportedOperationException e){
            		e.printStackTrace();
            	}
            	catch(NullPointerException e){
            		e.printStackTrace();
            	}
            	catch(ActivityNotFoundException e){
            		e.printStackTrace();
            	}
            }
        });
        
        
		// POPUP TRY
//		RelativeLayout userPopRoot = (RelativeLayout) activity.getLayoutInflater().inflate(R.layout.dialog_user_details, null);
//                
//		window = new PopupWindow(userPopRoot, LayoutParams.WRAP_CONTENT,  LayoutParams.WRAP_CONTENT, false);       
//       
//		window.setAnimationStyle(R.anim.grow_from_bottom);
//		window.setWidth(UIUtils.getContextWidth(activity));
//		
//		
//		window.setTouchable(true);
//		window.setOutsideTouchable(true);
//		
//		TextView user_name = (TextView)userPopRoot.findViewById(R.id.PUDFullName);
//		user_mail = (TextView)userPopRoot.findViewById(R.id.PUDMail);
//		user_address = (TextView)userPopRoot.findViewById(R.id.PUDAddress);
//		user_status = (TextView)userPopRoot.findViewById(R.id.PUDStatus);
//		
//		btn_dismiss = (Button)userPopRoot.findViewById(R.id.PUDDismiss);
//		
//		user_name.setText(user.getFullName());
//		user_mail.setText(this.user.getName());
//		user_status.setText(this.user.getStatus());
		
		//ImageView arrowdown = (ImageView) userPopRoot.findViewById(R.id.PUDarrow_down);
		
		
		try 
		{
			activity.getSPComm().getAddressObj(this.user.getSpGeoPoint().getLatitudeInDegrees(),
					                           this.user.getSpGeoPoint().getLongitudeInDegrees(),
					                           new ACommHandler<MapsQueryLocation>() 
		{
			@Override
			public void onRecv(final MapsQueryLocation Obj) 
			{
				UserDetailsDialog.this.activity.runOnUiThread(new Runnable()
				{
					public void run() {
						// TODO Auto-generated method stub
						try {
							user_address.setText(Obj
									.getResults()[0]
									              .getFormatted_address());
						} catch (Exception e) {
							user_address
							.setText("Error getting address");
						}
					}

				});
			};

			@Override
			public void onError(MapsQueryLocation Obj) 
			{
				UserDetailsDialog.this.activity.runOnUiThread(new Runnable()
				{
						public void run() 
						{
							user_address.setText("Error getting address");
						}
					});
				}
			});
			
		}
		
		catch (UnknownLocationException e)
		{
			user_address.setText("Error getting address");
		}

		btn_dismiss.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v)
			{
				dialog.dismiss();
			}
		});
		
		dialog.show();
		
		//window.showAtLocation(activity.getCurrentFocus(), Gravity.TOP, 0, 0);
		
	}
	
	
}
