package il.ac.tau.team3.uiutils;

import il.ac.tau.team3.common.SPUtils;
import il.ac.tau.team3.shareaprayer.R;
import android.app.Activity;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.PopupWindow;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;


public class SPMenu<  E_MenuItem  extends  Enum<E_MenuItem>  &  ISPMenuItem  >
{
    public static boolean isShowing(SPMenu<?> menu)
    {
        return null != menu && menu.isInitialized() && menu.isShowing();
    }
    
    private static final int SP_MENU_RES_ROOT    = R.layout.menu_options_main;
    //private static final int SP_MENU_ITEM_RES_ID = R.id.mom_items_row;
    
       
    
    public interface ISPOnMenuItemSelectedListener<E_MenuItem extends Enum<E_MenuItem> & ISPMenuItem>
    {
        abstract public void onMenuItemSelected(E_MenuItem item, View view);
    }
    
    
    
    

    
    
    
    private final E_MenuItem[] items;
    private final ISPOnMenuItemSelectedListener<E_MenuItem> menuListener;
    private       PopupWindow  menuWindow;
    private final OnTouchListener outsideTouchListener;
    
    
    /** @constructor */
    public SPMenu(E_MenuItem[] items, ISPOnMenuItemSelectedListener<E_MenuItem> menuListener)
    {
        this.items                = items;
        this.menuListener         = menuListener;
        this.menuWindow           = null;
        this.outsideTouchListener = new OnTouchListener()
        {            
            /**
             * @pre SPMenu.this.menuWindow.isShowing()
             */
            public boolean onTouch(View v, MotionEvent event)
            {
                SPUtils.debugFuncStart("*** menuWindow.OnTouchListener.onTouch", v, event);
                // This method should invoke only if the menu isShowing(), so no check.
                if (MotionEvent.ACTION_OUTSIDE == event.getAction())
                {
                    SPMenu.this.hide();
                }
                
                // TODO Figure out what should be the return value ???
                return false;
                
            }
            
        };
    }    
    
    

    /**
     * @pre    this.isInitialized()
     * @return true:  There a Window/Layout allocated and inflated.
     *         false: Not even allocated (counting on Garbage-Collector).
     */
    private boolean isShowing()
    {
        return null != this.menuWindow && this.menuWindow.isShowing();
    }
    
    public boolean isInitialized()
    {
        return null != this.items && 0 != this.items.length && null != this.menuListener;
    }
    
    /** @main */
    public void handleMenuButtonClick(Activity activity, int buttomViewResId)
    {
        /*
         * Second push on menu button will hide.
         */
        if (isShowing())
        {
            this.hide();
        }
        else
        {
            this.show(activity, buttomViewResId);
        }
    }
    
    
    /**
     * @pre    this.isShowing()
     * @param  v 
     *             Should be "outside" of the menu.
     * @param  event
     * @return
     */
    public boolean onOutsideTouch(View v, MotionEvent event)
    {
        return this.outsideTouchListener.onTouch(v, event);
    }
    
    
    
    public synchronized void show(Activity activity, int buttomViewResId)
    {
        if (! this.isInitialized())
        {
            Log.w("*SPMenu*", "Trying to show() an uninitialized one.");
            return;
        }
        
        TableLayout menuRoot     = (TableLayout) activity.getLayoutInflater().inflate(SP_MENU_RES_ROOT, null);
        
        menuWindow = new PopupWindow(menuRoot, LayoutParams.FILL_PARENT,  LayoutParams.WRAP_CONTENT, false);
        
       
        menuWindow.setAnimationStyle(android.R.style.Animation_Dialog);
        menuWindow.setWidth(UIUtils.getContextWidth(activity));
        
        menuWindow.setTouchable(true);
        menuWindow.setOutsideTouchable(true);
        
// TODO This never got invoked !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
// So I tried something dirty!
        menuWindow.setTouchInterceptor(new OnTouchListener()
        {                
            /**
             * @pre SPMenu.this.menuWindow.isShowing()
             */
            public boolean onTouch(View v, MotionEvent event)
            {
                SPUtils.debugFuncStart("*** menuWindow.OnTouchListener.onTouch", v, event);
                // This method should invoke only if the menu isShowing(), so no check.
                if (MotionEvent.ACTION_OUTSIDE == event.getAction())
                {
                    SPMenu.this.hide();
                }
                
                
                // Let the world (map view) take care of the touch event.
                // (OLD: Tell the world that we took care of the touch event.)
                return false;
            }

        });
        
        
        
// _ALL_ the net examples have this line ???????????????????
        //menuWindow.setFocusable(true);
        
        
        TableRow    itemTableRow = (TableRow)    menuRoot.findViewById(R.id.mom_items_row);
        itemTableRow.removeAllViews();
        
        TextView  itemTitle  = null;
        for (final E_MenuItem item : this.items)
        {
            itemTitle = new TextView(activity);
            
            itemTitle.setClickable(true);
            itemTitle.setGravity(Gravity.CENTER);
            itemTitle.setWidth(UIUtils.getContextWidth(activity) / this.items.length);
            itemTitle.setText(item.getTitle());
            itemTitle.setCompoundDrawablesWithIntrinsicBounds(0, item.getResIconId(), 0, 0);
            itemTitle.setBackgroundResource(R.drawable.selector_menu_item);
            
            itemTitle.setOnClickListener(new OnClickListener()
            {                    
                public void onClick(View v)
                {
                    SPMenu.this.menuListener.onMenuItemSelected(item, v);
                }
            });
            
            itemTableRow.addView(itemTitle);
        }        
        
        
        menuWindow.showAtLocation(activity.findViewById(buttomViewResId), Gravity.BOTTOM, 0, 0);
    }
    
    
    
    /**
     * @post !this.isShowing()
     * @imp  Garbage-Collector will finish the job.
     *       It's good to free, because menu is not often pressed.
     *       The rest doesn't sum to a lot of memory. 
     */
    public synchronized void hide()
    {
        if (SPMenu.this.isShowing())
        {                
            SPMenu.this.menuWindow.dismiss();
            SPMenu.this.menuWindow = null;
        }
        
    }
    
    
    
}//@END: class SPMenu
