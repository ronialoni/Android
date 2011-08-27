package il.ac.tau.team3.uiutils;

import il.ac.tau.team3.common.SPGeoPoint;

public interface IAddressVerify {
	void geopointFound(SPGeoPoint geopoint, String address);
	void geopointNotFound(String address);
	void addressFound(SPGeoPoint geopoint, String address);
	void addressNotFound(SPGeoPoint geopoint);
}
