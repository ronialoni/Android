package il.ac.tau.team3.shareaprayer;

import il.ac.tau.team3.common.SPGeoPoint;

import org.mapsforge.android.maps.GeoPoint;

public class SPUtils {
	static SPGeoPoint toSPGeoPoint(GeoPoint gp)	{
		return new SPGeoPoint(gp.getLatitudeE6(), gp.getLongitudeE6());
		
	}
	
	static GeoPoint toGeoPoint(SPGeoPoint spgp)	{
		return new GeoPoint(spgp.getLatitudeE6(), spgp.getLongtitudeE6());
	}
}
