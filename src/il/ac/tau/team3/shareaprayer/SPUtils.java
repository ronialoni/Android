package il.ac.tau.team3.shareaprayer;



import il.ac.tau.team3.common.SPGeoPoint;

import org.mapsforge.android.maps.GeoPoint;




public class SPUtils
{
    
    
    static SPGeoPoint toSPGeoPoint(GeoPoint gp)
    {
        return new SPGeoPoint(gp.getLatitudeE6(), gp.getLongitudeE6());
    }
    
    
    static GeoPoint toGeoPoint(SPGeoPoint spgp)
    {
        return new GeoPoint(spgp.getLatitudeE6(), spgp.getLongtitudeE6());
    }
    
    
    
    
    
    public static double EARTH_RADIUS_KM = 6384; // km
    
    
    
    public static double calculateDistanceMeters(double aLong, double aLat,
            double bLong, double bLat)
    {
        double d2r = (Math.PI / 180);
        
        double dLat = (bLat - aLat) * d2r;
        double dLon = (bLong - aLong) * d2r;
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(aLat * d2r) * Math.cos(bLat * d2r)
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        
        return EARTH_RADIUS_KM * c * 1000;
    }
    
    
    
}
