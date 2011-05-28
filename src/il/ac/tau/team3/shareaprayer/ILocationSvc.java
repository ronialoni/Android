package il.ac.tau.team3.shareaprayer;

import android.accounts.Account;
import il.ac.tau.team3.common.GeneralUser;
import il.ac.tau.team3.common.SPGeoPoint;

public interface ILocationSvc {

	public SPGeoPoint getLocation();
	
	public void RegisterListner(ILocationProv prov);
	
	public void UnRegisterListner(ILocationProv a_prov);
	
	public LocServ getService();
		
	public void setNames(String[] new_names);
	
	public void setStatus(String status) throws UserNotFoundException;

	public GeneralUser getUser() throws UserNotFoundException;
}
