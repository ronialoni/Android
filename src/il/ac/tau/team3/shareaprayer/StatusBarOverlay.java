package il.ac.tau.team3.shareaprayer;

import org.mapsforge.android.maps.Overlay;
import org.mapsforge.android.maps.Projection;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.Paint.Style;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;

public class StatusBarOverlay extends Overlay {

	   
	    private String currentMessage = "Test";
	    private int yoffset;
	    private int xoffset;
	    private int textSz;
	    
	    private HandlerThread ht;
	    private Handler h;
	    
	    public final static String MESSAGE_KEY = "MESSAGE";
	    public final static String TIME_KEY = "TIME";
	   
	    
	    StatusBarOverlay(int a_offset, int a_xoffset, int a_textSize)	{
	    	super();
	    	yoffset = a_offset;
	    	xoffset = a_xoffset;
	    	textSz = a_textSize;
	 	  
	    	ht = new HandlerThread("status bar thread");
	        
	        ht.start();
	        
	        h = new Handler(ht.getLooper())	{
	        	@Override
	        	public void handleMessage(Message msg)	{
	        		String s = msg.getData().getString(MESSAGE_KEY);
	        		synchronized(currentMessage)	{
	        			currentMessage = s;
	        		}
	        		StatusBarOverlay.this.requestRedraw();
	        		long time_to_wait = msg.getData().getLong(TIME_KEY);

	        		synchronized(this)	{
	        		if (0 != time_to_wait)	{
	        			try {
							this.wait(time_to_wait);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
	        					
	        		}
	        		}
	        		synchronized(currentMessage)	{
	        			currentMessage = "";
	        		}
	        		StatusBarOverlay.this.requestRedraw();
	        	}
	        };
	        
	        
	    	
	    }
	    
	    Paint pFront = new Paint();
	    Paint pBack = new Paint();
	    
	public Handler getHandler()	{
		return h;
		
	}
	
	public void write(String s, long time)	{
		Bundle b = new Bundle();
		b.putString(StatusBarOverlay.MESSAGE_KEY, s);
		b.putLong(StatusBarOverlay.TIME_KEY, time);
		Message m = new Message();
		m.setData(b);
		getHandler().sendMessage(m);
	}
	
	@Override
	protected void drawOverlayBitmap(Canvas canvas, Point drawPosition,
			Projection projection, byte drawZoomLevel) {
		// TODO Auto-generated method stub
		
		Rect rect = new Rect();
		
        //p.setARGB(255, 255, 0, 0);
		pFront.setTypeface(Typeface.MONOSPACE);
		pFront.setTextSize(textSz);
		
		if (currentMessage.length() > 0)	{
			pFront.getTextBounds(currentMessage, 0, currentMessage.length(), rect);
			pBack.setColor(Color.LTGRAY);
			rect.offsetTo(xoffset/2, yoffset/2);
			rect.inset(-4, -4);
		}
		

		pFront.setAntiAlias(true);
		synchronized(currentMessage)	{
			if (currentMessage.length() > 0)	{
				canvas.drawRect(rect, pBack);
			}
    		canvas.drawText(currentMessage , 
            xoffset,yoffset , pFront);
		}
		
		
	}

}
