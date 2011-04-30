package il.ac.tau.team3.common;

import java.io.Serializable;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
public class SPGeoPoint implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2185461079728756509L;
	private int LatitudeE6;
	private int LongtitudeE6;
	
	public SPGeoPoint()	{
		
	}
	
	public SPGeoPoint(int latitudeE6, int longtitudeE6) {
		super();
		LatitudeE6 = latitudeE6;
		LongtitudeE6 = longtitudeE6;
	}
	public int getLatitudeE6() {
		return LatitudeE6;
	}
	public void setLatitudeE6(int latitudeE6) {
		LatitudeE6 = latitudeE6;
	}
	public int getLongtitudeE6() {
		return LongtitudeE6;
	}
	public void setLongtitudeE6(int longtitudeE6) {
		LongtitudeE6 = longtitudeE6;
	}
	
	public double getLatitudeInDegrees(){
		return this.LatitudeE6/(double)1000000;
	}
	public double getLongitudeInDegrees(){
		return this.LongtitudeE6/(double)1000000;
	}
	
}
