package il.ac.tau.team3.uiutils;


import il.ac.tau.team3.shareaprayer.R;
import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.TextView;


public class NoTitleDialog 
extends Dialog 
implements OnClickListener 
{
    public final int headerId = R.id.CPDtitle;
    
    
    private TextView getHeader()
    {
        return (TextView) this.findViewById(headerId);
    }
    
    
	public NoTitleDialog(Context context) 
	{
		super(context, android.R.style.Animation_Translucent);
		
		/** 'Window.FEATURE_NO_TITLE' - Used to hide the title */
		requestWindowFeature(Window.FEATURE_NO_TITLE);
	}

	
//	@Override
//	public void setTitle(int titleId)
//	{
//	    this.getHeader().setText(titleId);
//	}
//	
//	@Override
//    public void setTitle(CharSequence titleString)
//    {
//	    this.getHeader().setText(titleString);
//    }
//	
//	
//	
//	
//    public void setTitleIcons(int left, int top, int right, int bottom)
//	{
//	    this.getHeader().setCompoundDrawablesWithIntrinsicBounds(left, top, right, bottom);
//	}
	
	
	
    
    
    

    public void onClick(View v) 
    {
        // TODO Auto-generated method stub
    }
    
    
}



