package il.ac.tau.team3.shareaprayer;

import il.ac.tau.team3.common.GeneralPlace;
import il.ac.tau.team3.common.Pray;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;


public class BitmapDrawableNumbered extends BitmapDrawable {
	private GeneralPlace place;
	Paint p = new Paint();
	
	public BitmapDrawableNumbered(Bitmap image, GeneralPlace a_place)	{
		super(image);
		place = a_place;
		this.setBounds(0, 0, image.getWidth(), image.getHeight());
	}
	
	public int getNumber()	{
		int max = 0;
		
		try	{
			for (Pray p : place.getPraysOfTheDay())	{
				if (max < p.numberOfJoiners())	{
					max = p.numberOfJoiners();
				}
			}
		} catch (NullPointerException e)	{
			// no prays for place
		}
		
		
		return max;
	}
	
    @Override
    public void draw(Canvas arg)    {
            
            arg.drawBitmap(getBitmap(), this.getBounds().left, this.getBounds().top, p);
            
            p.setColor(Color.WHITE);
                    p.setStyle(Paint.Style.STROKE);
                    p.setStrokeWidth(2);
                    //p.setARGB(255, 255, 0, 0);
                    p.setTypeface(Typeface.DEFAULT);
                    p.setTextSize(16);
            
            p.setAntiAlias(true);
            arg.drawText((new Integer(getNumber())).toString() , 
                            getBounds().left + getBitmap().getWidth()/2 - 5, 
                            getBounds().top + getBitmap().getHeight()/2 + 5 , p);
    }
}
