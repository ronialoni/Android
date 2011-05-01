package il.ac.tau.team3.addressQuery;

import java.io.Serializable;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Geometry implements Serializable	{
	/**
	 * 
	 */
	private static final long serialVersionUID = -1131564009655442521L;
	private Location location;
	
	public Geometry()	{
	}
	
	public void setLocation(Location a_location)	{
		location = a_location;
	}
	
	public Location getLocation()	{
		return location;
	}
	
	
};
