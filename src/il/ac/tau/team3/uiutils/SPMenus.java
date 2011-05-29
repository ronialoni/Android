package il.ac.tau.team3.uiutils;



import java.lang.reflect.InvocationTargetException;
import java.util.SortedSet;
import java.util.TreeSet;

import il.ac.tau.team3.shareaprayer.R;
import il.ac.tau.team3.common.SPUtils;

import il.ac.tau.team3.uiutils.ISPMenuItem.ISPSubMenuItem;


/**
 * 
 * @author Team3
 *         Packing all the menu enums into one class.
 * 
 */
public final class SPMenus
{

    /**
     * @debug 
     * 
     */
        
    public static void debug(ISPMenuItem item)
    {
        if (! SPUtils.DEBUGGING)
        {
            return;
        }
        
        SPUtils.debug(item.title());
        SPUtils.debug("    index = " + item.index());
        SPUtils.debug("    id    = " + item.id());
    }
    
    
      
    /**
     * 
     * @author Team3
     *
     */
    public enum ESPMenuItem
    implements ISPMenuItem
    {
        FIND   (0, R.drawable.menu_item_find_green_wifi,   ESPSubMenuFind.class  ),
        PLACES   (1, R.drawable.places_icon,   ESPSubMenuPlaces.class  ),
        SETTINGS (2, R.drawable.settings_icon,  ESPSubMenuSettings.class),
        FACEBOOK (3, R.drawable.fb_icon,  null),
        STATUS    (4, R.drawable.status_icon, null),
        EXIT   (5, R.drawable.exit_icon, null),
        ;

        private final int                       index;
        private final int                       resIconId;
        private final SortedSet<ISPSubMenuItem> subItems;     
        
        private ESPMenuItem(int index, int resIconId, Class<? extends ISPSubMenuItem> subItems)
        {
           
            this.index     = index;
            this.resIconId = resIconId;
            this.subItems  = new TreeSet<ISPSubMenuItem>();
            if (null == subItems ){
            	return;
            }
            for (ISPSubMenuItem item : subItems.getEnumConstants())	{
            	this.subItems.add(item);
            }
            
        }
        
        
        public String title()
        {
            return this.toString().replace('_', ' ').toUpperCase();
        }
        
        public int id()
        {
            return this.index;
        }
        
        public int resIconId()
        {
            
            return resIconId;
        }

        public int index()
        {
            return index;
        }

        
        public boolean hasSubMenu()
        {
            return null != this.subItems && 0 != this.subItems.size(); 
        }
        
        public ISPSubMenuItem[] getSubMenuItems()
        {
            return this.subItems.toArray(new ISPSubMenuItem[0]);
        }
        
        /**
         * @pre this.hasSubMenu().
         * Note: It's private, but used by sub menus, all in  this class SPMenus.
         */
        private void addSubItem(ISPSubMenuItem subItem)
        {
            this.subItems.add(subItem);
        }
        
        public int offsetId()
        {
            return 10 * (this.id() + 1);
        }
        
    }     
        
    
    
    
    /**
     * 
     * @author Team3
     *
     */
    public enum ESPSubMenuFind
    implements ISPSubMenuItem
    {
        
        ME      (0, R.drawable.menu_item_user_red_sruga ),
        CLOSEST (1, R.drawable.menu_item_mesure_package_graphics),
        ADDRESS (2, R.drawable.menu_item_new_edit_find_replace),
        ;
          
        public ISPMenuItem getParent()
        {
             return ESPMenuItem.FIND;
        }
        
        private final int index;
        private final int resIconId;
        
        
        private ESPSubMenuFind(int index, int resIconId)
        {
            this.index     = index ;
            this.resIconId = resIconId;
            
        }
        
        
        public String title()
        {
            return this.toString().replace('_', ' ').toUpperCase();
        }
                
        public int id()
        {
            return this.index + ESPMenuItem.FIND.offsetId();
        }
                
        public int resIconId()
        {           
            return resIconId;
        }

        public int index()
        {
            return this.index;
        }



        public boolean hasSubMenu()
        {
            return false;
        }
        
        public ISPSubMenuItem[] getSubMenuItems()
        {
            return null;
        }
    }

    public enum ESPSubMenuSettings
    implements ISPSubMenuItem
    {
        
        PROFILE      (0, R.drawable.menu_item_user_red_sruga ),
        VIEW  (1, R.drawable.settings_view_icon),
        
        ;
          
        public ISPMenuItem getParent()
        {
             return ESPMenuItem.SETTINGS;
        }
        
        private final int index;
        private final int resIconId;
        
        
        private ESPSubMenuSettings(int index, int resIconId)
        {
                
            this.index     = index ;
            this.resIconId = resIconId;
           // ESPMenuItem.SETTINGS.addSubItem(this);
        }
        
        
        
        public String title()
        {
            return this.toString().replace('_', ' ').toUpperCase();
        }
                
        public int id()
        {
            return this.index + ESPMenuItem.SETTINGS.offsetId();
        }
                
        public int resIconId()
        {           
            return resIconId;
        }

        public int index()
        {
            return this.index;
        }



        public boolean hasSubMenu()
        {
            return false;
        }
        
        public ISPSubMenuItem[] getSubMenuItems()
        {
            return null;
        }
    }

    public enum ESPSubMenuPlaces
    implements ISPSubMenuItem
    {
        
        CREATE	(0, R.drawable.create_icon ),
    	OWNED   (1, R.drawable.owned_icon ),
        JOINED 	(2, R.drawable.joined_icon),
        ;
          
        public ISPMenuItem getParent()
        {
             return ESPMenuItem.FIND;
        }
        
        private final int index;
        private final int resIconId;
        
        
        private ESPSubMenuPlaces(int index, int resIconId)
        {
            this.index     = index ;
            this.resIconId = resIconId;
            
        }
        
        
        public String title()
        {
            return this.toString().replace('_', ' ').toUpperCase();
        }
                
        public int id()
        {
            return this.index + ESPMenuItem.PLACES.offsetId();
        }
                
        public int resIconId()
        {           
            return resIconId;
        }

        public int index()
        {
            return this.index;
        }



        public boolean hasSubMenu()
        {
            return false;
        }
        
        public ISPSubMenuItem[] getSubMenuItems()
        {
            return null;
        }
    }

    
    
    
   
    
    
    
    
    
    /**
     * 
     * @author Team3
     *
     */
/*    public enum ESPSubMenuMap
    implements ISPSubMenuItem
    {
                
        NUMBERS (0, R.drawable.menu_item_numbers_blcakboard),
        MAX_MIN (1, R.drawable.menu_item_maxmin_calc),
        COLORS  (2, R.drawable.menu_item_colors),
        
        ;
          
        public ISPMenuItem getParent()
        {
             return ESPMenuItem.MAP;
        }
        
        private final int index;
        private final int resIconId;
        
        
        private ESPSubMenuMap(int index, int resIconId)
        {
            this.index     = index ;
            this.resIconId = resIconId;
           // ESPMenuItem.MAP.addSubItem(this);
        }
        
        
        
        public String title()
        {
            return this.toString().replace('_', ' ').toUpperCase();
        }
                
        public int id()
        {
            return this.index + ESPMenuItem.MAP.offsetId();
        }
                
        public int resIconId()
        {           
            return resIconId;
        }

        public int index()
        {
            return this.index;
        }



        public boolean hasSubMenu()
        {
            return false;
        }
        
        public ISPSubMenuItem[] getSubMenuItems()
        {
            return null;
        }
        
    }
*/    
    
    
    
}
