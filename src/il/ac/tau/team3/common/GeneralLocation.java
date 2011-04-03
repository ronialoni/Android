package il.ac.tau.team3.common;

import java.io.Serializable;

public class GeneralLocation implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 6359704904127673323L;
	private Long id;
	private SPGeoPoint spGeoPoint;
	private String name;
	
	public GeneralLocation(){
	
	}
	
	
	public Long getId() {
		return id;
	}


	public void setId(Long id) {
		this.id = id;
	}


	public GeneralLocation(SPGeoPoint spGeoPoint, String name) {
		this.spGeoPoint = spGeoPoint;
		this.name = name;
	}
	public void setSpGeoPoint(SPGeoPoint spGeoPoint) {
		this.spGeoPoint = spGeoPoint;
	}
	public SPGeoPoint getSpGeoPoint() {
		return spGeoPoint;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
}
