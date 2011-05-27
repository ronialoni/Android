package il.ac.tau.team3.uiutils;



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
    
    
    public static void debug(String where)
    {
        if (! SPUtils.DEBUGGING)
        {
            return;
        }
        
        SPUtils.debug("ESPMenuItem... (in: " + where + ")");
        for (ESPMenuItem item : ESPMenuItem.values())
        {
            debug(item);
        }
        SPUtils.debug("");
        
        SPUtils.debug("ESPSubMenuFind... (in: " + where + ")");
        for (ESPSubMenuFind item : ESPSubMenuFind.values())
        {
            debug(item);
        }
        SPUtils.debug("");
        
        SPUtils.debug("ESPSubMenuPeople... (in: " + where + ")");
        for (ESPSubMenuPeople item : ESPSubMenuPeople.values())
        {
            debug(item);
        }
        SPUtils.debug("");
    }
    
    
    
    
    /**
     * 
     * @author Team3
     *
     */
    public enum ESPMenuItem
    implements ISPMenuItem
    {
        FIND   (0, R.drawable.menu_item_find_green_wifi,       new TreeSet<ISPSubMenuItem>()),
        PEOPLE (1, R.drawable.menu_item_sign_in_men_colorful,  new TreeSet<ISPSubMenuItem>()),
        MAP    (2, R.drawable.menu_item_map_optiond_starthere, new TreeSet<ISPSubMenuItem>()),
        EXIT   (3, R.drawable.menu_item_exit_door_greener,     null),
        ;

        private final int                       index;
        private final int                       resIconId;
        private final SortedSet<ISPSubMenuItem> subItems;     
        
        private ESPMenuItem(int index, int resIconId, SortedSet<ISPSubMenuItem> subItems)
        {
            //SPUtils.debugFuncStart("ESPMenuItem", index, resIconId, subMenuItems);
            //SPUtils.debug("id = " + this.getItemId());
            
            this.index     = index;
            this.resIconId = resIconId;
            this.subItems  = subItems;
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
        PLACES  (2, R.drawable.menu_item_place_dark_blue),
        ADDRESS (3, R.drawable.menu_item_new_edit_find_replace),
        ;
          
        public ISPMenuItem getParent()
        {
             return ESPMenuItem.FIND;
        }
        
        private final int index;
        private final int resIconId;
        
        
        private ESPSubMenuFind(int index, int resIconId)
        {
            //SPUtils.debugFuncStart("ESPSubMenuFind", index, resIconId);
            //SPUtils.debug("id = " + this.getItemId());
            
            this.index     = index ;
            this.resIconId = resIconId;
            ESPMenuItem.FIND.addSubItem(this);
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

    
    
    
    
    /**
     * 
     * @author Team3
     *
     */
    public enum ESPSubMenuPeople
    implements ISPSubMenuItem
    {
        
        MY_POFILE (0, R.drawable.menu_item_edit_profile),
        SHARE     (1, R.drawable.menu_item_facebook_bubble_alt),
        ;
          
        public ISPMenuItem getParent()
        {
             return ESPMenuItem.PEOPLE;
        }
        
        private final int index;
        private final int resIconId;
        
        
        private ESPSubMenuPeople(int index, int resIconId)
        {
            this.index     = index ;
            this.resIconId = resIconId;
            ESPMenuItem.PEOPLE.addSubItem(this);
        }
        
        
        
        public String title()
        {
            return this.toString().replace('_', ' ').toUpperCase();
        }
                
        public int id()
        {
            return this.index + ESPMenuItem.PEOPLE.offsetId();
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
    public enum ESPSubMenuMap
    implements ISPSubMenuItem
    {
                
        NUNBERS (0, R.drawable.menu_item_numbers_blcakboard),
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
            ESPMenuItem.MAP.addSubItem(this);
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
    
    
    
    
}
