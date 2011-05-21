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
	private String address;
	
	public GeneralLocation(){
	
	}
	
	
	public Long getId() {
		return id;
	}

	
	public String getAddress() {
		return address;
	}


	public void setAddress(String address) {
		this.address = address;
	}


	public void setId(Long id) {
		this.id = id;
	}


	public GeneralLocation(SPGeoPoint spGeoPoint, String name) {
		this.spGeoPoint = spGeoPoint;
		this.name = name;
	}
	
	public GeneralLocation(SPGeoPoint spGeoPoint, String name, String address) {
		this.spGeoPoint = spGeoPoint;
		this.name = name;
		this.address = address;
	}
	
	public void setSpGeoPoint(SPGeoPoint spGeoPoint) {
		this.spGeoPoint = spGeoPoint;
	}
	public SPGeoPoint getSpGeoPoint() throws UnknownLocationException {
		if (spGeoPoint == null)	{
			throw new UnknownLocationException();
		}
		return spGeoPoint;
	}
	public String getName() {
		return name == null ? "" : name;
	}
	public void setName(String name) {
		this.name = name;
	}
}
