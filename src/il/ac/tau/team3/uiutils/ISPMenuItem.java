package il.ac.tau.team3.uiutils;



public interface ISPMenuItem
{
    abstract public int     id();
    abstract public String  title();
    abstract public int     resIconId();  
    
    /**
     * @return index in menu starting from 0 in the left side
     */
    abstract public int index();
    
    
    abstract public boolean hasSubMenu();
    
    /**
     * @pre this.hasSubMenu().
     */
    abstract public ISPSubMenuItem[] getSubMenuItems();
        
    
    
    /**
     * 
     * @author Team3
     *         I think it must be inner, because it implements 
     *         the outer class which also uses it.
     *
     */
    public interface ISPSubMenuItem
    extends ISPMenuItem
    {
        abstract public ISPMenuItem getParent();
       
    }
    
}

    
