package il.ac.tau.team3.uiutils;



/**
 * NOT USED!
 * DO NOT DELETE! (might need it for code duplication)
 * 
 * 
 * @author Team3
 *
 * @param <E_MenuItem>
 */
abstract public class AESPMenuItem<  E_MenuItem  extends  Enum<E_MenuItem>  &  ISPMenuItem  >
implements ISPMenuItem
{
    E_MenuItem enumeration;
    
    public AESPMenuItem(E_MenuItem enumeration)
    {
        this.enumeration = enumeration;
    }

    public int id()
    {
        return this.enumeration.id();
    }

    public String title()
    {
        return this.enumeration.title();
    }

    public int resIconId()
    {
        return this.enumeration.resIconId();
    }

    public int index()
    {
        return this.enumeration.index();
    }

    
    
    public abstract boolean hasSubMenu();

    public abstract ISPSubMenuItem[] getSubMenuItems();
    
    
    
}
