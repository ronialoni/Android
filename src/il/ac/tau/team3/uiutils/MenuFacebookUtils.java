package il.ac.tau.team3.uiutils;

import il.ac.tau.team3.shareaprayer.FindPrayer;
import il.ac.tau.team3.shareaprayer.R;
import android.app.Activity;
import android.app.Dialog;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class MenuFacebookUtils {
	public MenuFacebookUtils(final FindPrayer context)	{
		final NoTitleDialog dialog = new NoTitleDialog(context);
		dialog.setContentView(R.layout.dialog_facebook_settings);
		dialog.getWindow().setLayout(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);

		Button setup = (Button)dialog.findViewById(R.id.DFS_setup_button);
		Button close = (Button)dialog.findViewById(R.id.DFS_Close);
		CheckBox cb = (CheckBox)dialog.findViewById(R.id.DFS_share);
		
		if (!context.getFacebookConnector().isFacebook_configured())	{
			cb.setEnabled(true);
		}
		
		if (context.getFacebookConnector().isFacebook_configured())	{
			setup.setEnabled(false);
		}
		
		cb.setChecked(context.getFacebookConnector().isFacebook_share());
		
		cb.setOnCheckedChangeListener(new OnCheckedChangeListener()	{

			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				context.getFacebookConnector().setFacebook_share(isChecked);
				
			}
			
		});

		setup.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				context.getFacebookConnector().connect();				
				dialog.dismiss();
			}
		});
		
		close.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		
		dialog.show();

	}

}
