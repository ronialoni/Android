package il.ac.tau.team3.shareaprayer;

import il.ac.tau.team3.common.GeneralUser;
import il.ac.tau.team3.common.SPGeoPoint;

public interface ILocationProv {
	void LocationChanged(SPGeoPoint point);
	void OnUserChange(GeneralUser user);
}
