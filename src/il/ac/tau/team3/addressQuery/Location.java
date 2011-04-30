package il.ac.tau.team3.addressQuery;

import java.io.Serializable;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Location implements Serializable	{
	/**
	 * 
	 */
	private static final long serialVersionUID = 8561017581545516775L;
	private double lat;
	private double lng;
	
	public double getLat()	{
		return lat;
	}
	
	public void setLat(double a_lat)	{
		lat = a_lat;
	}
	
	public double getLng()	{
		return lng;
	}
	
	public void setLng(double a_lng)	{
		lng = a_lng;
	}
	
	public Location()	{
		
	}
}
