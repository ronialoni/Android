package il.ac.tau.team3.addressQuery;

import java.io.Serializable;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MapsQueryLonLat implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 261383341575302254L;

	private double lon;
	private double lat;
	private double[] boundingbox;
	private String display_name;
	
	
	public void setLon(double lon) {
		this.lon = lon;
	}
	public double getLon() {
		return lon;
	}
	public void setLat(double lat) {
		this.lat = lat;
	}
	public double getLat() {
		return lat;
	}
	public void setBoundingbox(double[] boundingbox) {
		this.boundingbox = boundingbox;
	}
	public double[] getBoundingbox() {
		return boundingbox;
	}
	public void setDisplay_name(String display_name) {
		this.display_name = display_name;
	}
	public String getDisplay_name() {
		return display_name;
	}
}
