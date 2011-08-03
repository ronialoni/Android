package il.ac.tau.team3.shareaprayer;

import il.ac.tau.team3.common.GeneralPlace;
import il.ac.tau.team3.common.Pray;
import il.ac.tau.team3.uiutils.MenuSettingsUtils;
import il.ac.tau.team3.uiutils.MenuUtils;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.widget.Toast;


public class BitmapDrawableNumbered  extends BitmapDrawable 
{
	private GeneralPlace place;
	private Paint pBmp = new Paint();
	private Paint p    = new Paint();
	private Bitmap glow = null;
	
	//private static int lastDisplayed = 0;
	
	public BitmapDrawableNumbered(Bitmap image, GeneralPlace a_place)	{
		this(image, a_place, null);
	}
	
	public BitmapDrawableNumbered(Bitmap image, GeneralPlace a_place, Bitmap glow)	{
		super(image);
		place = a_place;
		this.setBounds(0, 0, image.getWidth(), image.getHeight());
		this.glow = glow;
	}
	
	public int getNumber()	{
		return MenuSettingsUtils.chooseMaxOrMin(place);
	}
	
	
	
	/*
	 * 10 seems like the rational choice, but 12 make the coloring more intuitive.
	 */
	private static final int NUM_OF_COLOR_LEVELS = 12;
	
	
	private ColorFilter determineColor(int num)	
	{
		final int HALF_THE_COLOR_LEVELS = NUM_OF_COLOR_LEVELS / 2;
		
		int   color;
				
		/*
		 * The short is like an "unsigned byte"
		 * big enough for calculations.
		 */
		short red   = 0xFF;
		short green = 0xFF;
		
		if (0 == num)
		{
			color = Color.GRAY;
		}		
		else
		{
			if (num > NUM_OF_COLOR_LEVELS)
			{
				num = NUM_OF_COLOR_LEVELS;
			}
			
			if (num < HALF_THE_COLOR_LEVELS)
			{	green = (short) ((green * (num - 1)) / HALF_THE_COLOR_LEVELS);
			
			}
			else
			{
				red   = (short) ((red * (NUM_OF_COLOR_LEVELS - num)) / HALF_THE_COLOR_LEVELS);
			}
			
			color = Color.argb(0x00, red, green, 0x00);			
		}
				
		Log.d("    <*>    ", "green = " + green + "\n"
				           + "red   = " + red);
				
		return new LightingColorFilter(0, color);
	}
	
	
	
//	private ColorFilter determineColor(double num)	{
//		if (num > 10)	{
//			return new LightingColorFilter(Color.argb(0, 0x01, 0xFF, 0x01), 0);
//		}
//		
//		final int colorSat = 0xFF;
//		
//		int redAdd = (int) Math.min(Math.max(Math.exp(-num/4)*colorSat,1),colorSat);
//		int BlueAdd = (int) Math.min(Math.max((1.0-Math.exp(-(num-3)/2))*Math.exp(-(num-7)/2)/Math.exp(2)*4*colorSat,1),colorSat);
//		int GreenAdd = (int) Math.min(Math.max((1.0-Math.exp(-(num-5)))*colorSat,1),colorSat);
//		
//		return new LightingColorFilter(Color.argb(0, redAdd, GreenAdd, BlueAdd), 0);
//	}
	
    @Override
    public void draw(Canvas arg)    {
    	
    		int  numToDisplay= getNumber();
    		pBmp.setColorFilter(determineColor(numToDisplay));
            
    		int x = this.getBounds().left;
    		int y = this.getBounds().top;
            arg.drawBitmap(getBitmap(), x, y, pBmp);
            
            if (null != glow)	{
            	arg.drawBitmap(glow, x, y, new Paint());
            }
            
            p.setColor(Color.WHITE);
                    p.setStyle(Paint.Style.STROKE);
                    p.setStrokeWidth(2);
                    //p.setARGB(255, 255, 0, 0);
                    p.setTypeface(Typeface.DEFAULT);
                    p.setTextSize(16);
            
            p.setAntiAlias(true);
            arg.drawText((new Integer(numToDisplay)).toString() , 
                            getBounds().left + getBitmap().getWidth()/2 - 5, 
                            getBounds().top + getBitmap().getHeight()/2 + 5 , p);
    }
}
