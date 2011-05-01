package il.ac.tau.team3.shareaprayer;

import org.mapsforge.android.maps.Overlay;
import org.mapsforge.android.maps.Projection;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Typeface;
import android.graphics.Paint.Style;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;

public class StatusBarOverlay extends Overlay {

	   
	    private String currentMessage = "Test";
	    private int offset;
	   
	    
	    StatusBarOverlay(int a_offset)	{
	    	super();
	    	offset = a_offset;
	 	  
	    	
	    }
	    
	    Paint pFront = new Paint();
	
	@Override
	protected void drawOverlayBitmap(Canvas canvas, Point drawPosition,
			Projection projection, byte drawZoomLevel) {
		// TODO Auto-generated method stub
		

        
        //p.setARGB(255, 255, 0, 0);
		pFront.setTypeface(Typeface.DEFAULT);
		pFront.setTextSize(16);

		pFront.setAntiAlias(true);
    		canvas.drawText(currentMessage , 
            0,offset , pFront);
		
		
	}

}
