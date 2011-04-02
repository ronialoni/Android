package il.ac.tau.team3.common;

public class GeneralLocation {

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
