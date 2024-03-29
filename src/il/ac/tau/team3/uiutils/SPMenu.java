package il.ac.tau.team3.uiutils;


import il.ac.tau.team3.common.SPUtils;
import il.ac.tau.team3.shareaprayer.FindPrayer;
import il.ac.tau.team3.shareaprayer.R;
import il.ac.tau.team3.uiutils.SPMenus.ESPMenuItem;
import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;



public class SPMenu
{
    
    private static final int SP_MENU_ITEM_SIZE = 64;
    
    private static final int SP_MENU_RES_ROOT  = R.layout.menu_options_main;
    
    private static final int _sItemsPerRow = 3;
       
    
/////////////////////////////////////////////////////////////////////////////////////////////////////
	///////// Menu: /////////////////////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////////////////////////////


	public static int getContextWidth(Context context)
	{
		return ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getWidth();
	}


	public static void setPadding(View view, int padding)
	{
		view.setPadding(padding, padding, padding, padding);
	}
    
    /**
     * A safe  help method.
     * @usage  Use this to avoid a null check & exceptions.
     * @pre    true.
     * @param  SPMenu menu
     * @return Is the menu visible to the user.
     */
    public static boolean isShowing(SPMenu menu)
    {
        return null != menu && menu.isInitialized() && menu.isShowing();
    }
    
    
    /**
     * A safe  help method.
     * @usage  Use this to avoid a null check & exceptions.
     * @pre    true.
     * @param  SPMenu menu
     * @return Is there a sub menu visible to the user.
     */
    public static boolean isSubShowing(SPMenu menu)
    {
        return isShowing(menu) && menu.isSubShowing();
    }
    
    
    
    
    public interface ISPOnMenuItemSelectedListener
    {
        abstract public void onMenuItemSelected(ISPMenuItem item, View view);
    }

    
    
    
    private final ISPMenuItem[] items;
    
    private final ISPOnMenuItemSelectedListener menuListener;
    
    private       PopupWindow menuWindow;    
    private       PopupWindow subWindow;
    
    
    
    
    /** @constructor */
    public SPMenu(ISPMenuItem[] items, ISPOnMenuItemSelectedListener menuListener)
    {
        this.items         = items;
        this.menuListener  = menuListener;
        this.menuWindow    = null;
        this.subWindow  = null; 
        
        
    }    
    
    
    
    public boolean isInitialized()
    {
        return null != this.items && 0 != this.items.length && null != this.menuListener;
    }
   

    /**
     * @pre    this.isShowing()
     * @return true:  There is a Window/Layout allocated and inflated.
     *         false: Not even allocated (counting on Garbage-Collector).
     */
    private boolean isSubShowing()
    {
        return null != this.subWindow && this.subWindow.isShowing();
    }
    
    /**
     * @pre    this.isInitialized()
     * @return true:  There is a Window/Layout allocated and inflated.
     *         false: Not even allocated (counting on Garbage-Collector).
     */
    private boolean isShowing()
    {
        return null != this.menuWindow && this.menuWindow.isShowing();
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
    
    
    
    private TextView getMenuItemTextView(ISPMenuItem item, Activity activity)
    {
        TextView itemView = new TextView(activity);
        
        itemView.setClickable(true);
        itemView.setGravity(Gravity.CENTER);
        setPadding(itemView, 5);
        itemView.setFadingEdgeLength(5);
        itemView.setWidth(getContextWidth(activity) / Math.min(this.items.length ,_sItemsPerRow));
        itemView.setText(item.title());
        itemView.setCompoundDrawablesWithIntrinsicBounds(0, item.resIconId(), 0, 0);
        itemView.setBackgroundResource(R.drawable.selector_menu_item);
        itemView.setCompoundDrawablePadding(5);
        
        return itemView;
    }
    
    
    private TextView getSubItemTextView(ISPMenuItem subItem, Activity activity)
    {
        TextView subView = new TextView(activity);
        
        subView.setClickable(true);
        subView.setGravity(Gravity.CENTER);
        setPadding(subView, 5);
        subView.setFadingEdgeLength(5);
        subView.setText(subItem.title());
        subView.setCompoundDrawablesWithIntrinsicBounds(subItem.resIconId(), 0, 0, 0);
        subView.setBackgroundResource(R.drawable.selector_menu_item);//R.drawable.action_item_btn);
        subView.setCompoundDrawablePadding(5);
        
        return subView;
    }
    
    
    public synchronized void show(final Activity activity, final int buttomViewResId)
    {
        if (! this.isInitialized())
        {
            Log.w("*SPMenu*", "Trying to show() an uninitialized one.");
            return;
        }
        
        TableLayout menuRoot = (TableLayout) activity.getLayoutInflater().inflate(SP_MENU_RES_ROOT, null);
        
        
        menuWindow = new PopupWindow(menuRoot, LayoutParams.FILL_PARENT,  LayoutParams.WRAP_CONTENT, false);       
       
        menuWindow.setAnimationStyle(android.R.style.Animation_Dialog);
        menuWindow.setWidth(getContextWidth(activity));
        
        menuWindow.setTouchable(true);
        menuWindow.setOutsideTouchable(true);
        
        TableRow[] itemTableRows = new TableRow[2];
        
       itemTableRows[0]= (TableRow) menuRoot.findViewById(R.id.mom_items_row);
       
        itemTableRows[1] = (TableRow) menuRoot.findViewById(R.id.mom_items_row2);
        for (TableRow row :  itemTableRows){
        	  row.removeAllViews();
        }
      
                
        TextView  itemView  = null;
        for (final ISPMenuItem item : this.items)
        {
            itemView = getMenuItemTextView(item, activity);   
            
            if (! item.hasSubMenu())
            {
                itemView.setOnClickListener(new OnClickListener()
                {                    
                    public void onClick(View v)
                    {
                        SPMenu.this.menuListener.onMenuItemSelected(item, v);
                    }
                });                
            }
            
            
            else
            {
                itemView.setOnClickListener(new OnClickListener()
                {                    
                    public void onClick(View v)
                    {
                        if (SPMenu.this.isSubShowing())
                        {
                            SPMenu.this.hide();
                        }
                        
                        
                        RelativeLayout subRootView   = (RelativeLayout) activity.getLayoutInflater().inflate(R.layout.popup, null);
                        TableLayout    subItemsTable = (TableLayout)    subRootView.findViewById(R.id.tracks);
                        subItemsTable.removeAllViews();
                        ImageView      arrowDown     = (ImageView)      subRootView.findViewById(R.id.arrow_down);
                        arrowDown.setPadding(SPMenu.SP_MENU_ITEM_SIZE / 3, 0, 0, 0);
                        
                        
                        TextView subView = null;
                        for (final ISPMenuItem subItem : item.getSubMenuItems())
                        {
                            subView = SPMenu.this.getSubItemTextView(subItem, activity);
                            
                            subView.setOnClickListener(new OnClickListener()
                            {                    
                                public void onClick(View v)
                                {
                                    SPMenu.this.menuListener.onMenuItemSelected(subItem, v);
                                }
                            }); 
                            
                          
                            subItemsTable.addView(subView);
                        }              
                        
                        
                        
                        SPMenu.this.subWindow = new PopupWindow(subRootView, LayoutParams.WRAP_CONTENT,  LayoutParams.WRAP_CONTENT, false);
                        subWindow.setAnimationStyle(android.R.style.Animation_Dialog);
                        
                        SPMenu.this.menuWindow.getContentView().measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
                        
                        
                        final int menuItemWidth = SPMenu.this.menuWindow.getContentView().getMeasuredWidth() / Math.min(_sItemsPerRow,items.length);
                        final int menuHight     = SPMenu.this.menuWindow.getContentView().getMeasuredHeight();
                      
                        subWindow.setOutsideTouchable(true);
                        subWindow.setTouchable(true);
                                        
                                              
                        subWindow.showAtLocation(activity.findViewById(buttomViewResId), Gravity.BOTTOM | Gravity.LEFT, menuItemWidth * (item.index()%_sItemsPerRow), menuHight / (1+(item.index()/_sItemsPerRow)));
                      
                        
                        SPMenu.this.menuListener.onMenuItemSelected(item, v);
                    }
                }); 
                
                
                
                
                

                
            }
            
            
            
            itemTableRows[item.index()/_sItemsPerRow].addView(itemView, (item.index()%_sItemsPerRow));
           
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
        if (this.isShowing())
        {               
            if (this.isSubShowing())
            {
                this.subWindow.dismiss();
                this.subWindow = null;
            }
            
            else
            {
                this.menuWindow.dismiss();
                this.menuWindow = null;
            }
        }
    }
    
    
    /**
     * NOTE:  Bad name!!! this function also "dismisses".
     * @pre   this.menuWindow.isShowimg().
     * @post  listener.onDismiss().
     *        !this.menuWindow.isShowimg().
     * @param listener
     *        Do whatever you want, it will invoke after all the menu windows are gone.
     *        Of course, no need to hide(), we assume you want to hide. 
     * @imp   Being cheap! It's messier in the activity, 
     *        but I LIKE having the menu completely gone only after the action started.
     *        So there is a need for ONLY ONE of those.
     */
    public void onMenuDismiss(OnDismissListener listener)
    {
        this.menuWindow.setOnDismissListener(listener);
        this.hide();
    }
    
    
}

