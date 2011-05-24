package il.ac.tau.team3.shareaprayer;


import android.view.MotionEvent;
import il.ac.tau.team3.common.SPGeoPoint;


public abstract class IMapTapDetect 
{
	public void onTouchEvent(SPGeoPoint sp)   {}
	public void onMoveEvent(SPGeoPoint sp)    {}
	
	
	public void onAnyEvent(MotionEvent event) {}
}
