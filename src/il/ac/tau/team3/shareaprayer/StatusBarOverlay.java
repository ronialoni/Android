package il.ac.tau.team3.shareaprayer;

import org.mapsforge.android.maps.Overlay;
import org.mapsforge.android.maps.Projection;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
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
import android.text.TextPaint;

public class StatusBarOverlay extends Overlay implements IStatusWriter {

	   
	    private String currentMessage = "Test";
	    private Bitmap icon = null;
	    private int yoffset;
	    private int xoffset;
	    private int textSz;
	    private Context context;
	    
	    private HandlerThread ht;
	    private Handler h;
	    
	    public final static String MESSAGE_KEY = "MESSAGE";
	    public final static String TIME_KEY = "TIME";
	    public final static String ICON_KEY = "ICON";
	   
	    
	    StatusBarOverlay(Context a_context, int a_offset, int a_xoffset, int a_textSize)	{
	    	super();
	    	yoffset = a_offset;
	    	xoffset = a_xoffset;
	    	textSz = a_textSize;
	    	context = a_context;
	 	  
	    	ht = new HandlerThread("status bar thread");
	        
	        ht.start();
	        
	        h = new Handler(ht.getLooper())	{
	        	@Override
	        	public void handleMessage(Message msg)	{
	        		String s = msg.getData().getString(MESSAGE_KEY);
	        		int iconRes = msg.getData().getInt(ICON_KEY);
	        		synchronized(currentMessage)	{
	        			currentMessage = s;
	        			if (iconRes != 0)	{
	        				Bitmap bmp = BitmapFactory.decodeResource(context.getResources(), iconRes);
	        				Matrix matrix  = new Matrix();
	        				int width = bmp.getWidth();
	        		        int height = bmp.getHeight();
	        		        TextPaint tp = new TextPaint(new Paint());
	        		        tp.setTypeface(Typeface.MONOSPACE);
	        		        tp.setTextSize(textSz);
	        		        Rect rect = new Rect();
	        		        tp.getTextBounds(currentMessage, 0, currentMessage.length(), rect);
	        		        int pixelTextSize = rect.height() + 4;
	        				float scaleWidth = ((float) pixelTextSize) / width;
	        		        float scaleHeight = ((float) pixelTextSize) / height;
	        				matrix.postScale(scaleWidth, scaleHeight);
	        				icon = Bitmap.createBitmap(bmp, 0, 0,
	        						width, height, matrix, true); 
	        				
	        			}
	        			
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
	        			icon = null;
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
	
	public void write(String s, int iconRes, long time)	{
		Bundle b = new Bundle();
		b.putString(StatusBarOverlay.MESSAGE_KEY, s);
		b.putLong(StatusBarOverlay.TIME_KEY, time);
		b.putInt(StatusBarOverlay.ICON_KEY, iconRes);
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
			if (icon != null)	{
				rect.inset(-icon.getWidth(), 0);
			}
			pBack.setColor(0xaabbbbbb);
			rect.offsetTo(xoffset/2, yoffset/2);
			rect.inset(-4, -4);
		}
		

		pFront.setAntiAlias(true);
		synchronized(currentMessage)	{
			if ((currentMessage.length() > 0) || (icon != null))	{
				canvas.drawRect(rect, pBack);
			}
			int added_offset = 0;
			if (icon != null)	{
				canvas.drawBitmap(icon, xoffset+2, yoffset/2 - 2, pFront);
				added_offset += icon.getWidth() + 4;
			}
    		canvas.drawText(currentMessage , 
            xoffset+added_offset,yoffset , pFront);
		}
		
		
	}
	
	public void closeHandler()	{
		h.getLooper().quit();
	}

}
