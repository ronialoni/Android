package il.ac.tau.team3.shareaprayer;

import il.ac.tau.team3.common.GeneralUser;
import il.ac.tau.team3.common.SPGeoPoint;
import il.ac.tau.team3.common.User;

public interface ILocationSvc {

	public SPGeoPoint getLocation();
	
	public void RegisterListner(ILocationProv prov);
	
	public void UnRegisterListner(ILocationProv a_prov);
	
	public LocServ getService();

	public GeneralUser getUser();
}
