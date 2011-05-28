package il.ac.tau.team3.uiutils;

import android.accounts.Account;
import android.app.Dialog;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import il.ac.tau.team3.common.GeneralUser;
import il.ac.tau.team3.shareaprayer.FindPrayer;
import il.ac.tau.team3.shareaprayer.R;

public class MenuStatusUtils {
	
	public static void createEditStatusDialog(final GeneralUser user, final FindPrayer activity){
		final Dialog dialog = new Dialog(activity);
		 dialog.setContentView(R.layout.dialog_set_status);
		 dialog.getWindow().setLayout(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		 final EditText status = (EditText)dialog.findViewById(R.id.dss_status);
         
         dialog.setTitle("Status");
         Button okButton = (Button) dialog.findViewById(R.id.dss_button_ok);
         
         final int accountId[] = new int[1];
         final String names[] = new String[3];
         
         okButton.setOnClickListener(new OnClickListener()
         {                
             public void onClick(View v)
             {
               // activity.setStatus(status.getText().toString());
                dialog.dismiss();
             }
         });
      
     
         dialog.show();

     return;
		
	}
	
	
	static String formatFacebookHeader_Status(String status){
		return status;
	}
	
	static String formatFacebookDesc_Status(GeneralUser user){
		return user.getFullName() + " just changed her status on Share-A-Prayer!";
	}
}
