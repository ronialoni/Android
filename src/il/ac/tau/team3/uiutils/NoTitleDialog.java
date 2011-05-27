package il.ac.tau.team3.uiutils;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;

public class NoTitleDialog extends Dialog implements OnClickListener {

	public NoTitleDialog(Context context) {
		super(context);
		/** 'Window.FEATURE_NO_TITLE' - Used to hide the title */
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
	}

	public void onClick(View v) {
		// TODO Auto-generated method stub
		
	}
}