package il.ac.tau.team3.spcomm;

import android.util.Log;

public class ACommHandler<T> implements ICommHandler<T> {

	public void onError(T Obj) {
		Log.e("Share-A-Prayer", "Failed in comm");
		
	}
	
	public void onRecv(T Obj)	{
		
	}


	public void onTimeout(T Obj) {
		
	}

}
