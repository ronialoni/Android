package il.ac.tau.team3.common;




import org.mapsforge.android.maps.GeoPoint;


import android.util.Log;




public class SPUtils
{
    
    
    public static SPGeoPoint toSPGeoPoint(GeoPoint gp)
    {
        return new SPGeoPoint(gp.getLatitudeE6(), gp.getLongitudeE6());
    }
    
    
    public static GeoPoint toGeoPoint(SPGeoPoint spgp)
    {
        return new GeoPoint(spgp.getLatitudeE6(), spgp.getLongtitudeE6());
    }
    
    
    
    public static double INFINITY = 1000000000000000000000000000000.0 ;
        
    public static final double EARTH_RADIUS_KM = 6384; // km
    
    
    
    public static double calculateDistanceMeters(double aLong, double aLat, double bLong, double bLat)
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
    
    
    
    
    
    private static final boolean DEBUGGING = true; 
    
    
    
    public static void debug(String message)
    {
        if (DEBUGGING)
        {
            Log.d("ShareAPrayer", message);
        }
    }
    
    public static <O> void debug(O object)
    {
        debug(object.toString());
    }
    
    
    
    public static void error(String message)
    {
        Log.e("ShareAPrayer", message);
    }
    
    public static void error(String message, Throwable throwable)
    {
        Log.e("ShareAPrayer", message, throwable);
    }
    
    public static <O> void error(O object)
    {
        error(object.toString());
    }
    
    
    public static <O> void error(O object, String message)
    {
        error(message);
        error("\t" + object.toString());
    }
    
    
    
}
