package il.ac.tau.team3.uiutils;

import il.ac.tau.team3.shareaprayer.R;


public enum ESPMenuItem
implements ISPMenuItem
{
    FIND        (0, R.drawable.menu_item_find_green_wifi),
    MAP_OPTIONS (1, R.drawable.menu_item_map_optiond_starthere),
    PROFILE     (2, R.drawable.menu_item_sign_in_men_colorful),
    EXIT        (3, R.drawable.menu_item_exit_door_greener),
    ;
            
    private final int itemId;
    private final int resIconId;
            
    private ESPMenuItem(int itemId, int resIconId)
    {
        this.itemId    = itemId;
        this.resIconId = resIconId;
    }
    
    
    public String getTitle()
    {
        return this.toString().replace('_', ' ').toUpperCase();
    }
    
    public int getItemId()
    {
        return itemId;
    }
    
    public int getResIconId()
    {
        
        return resIconId;
    }
    
}

