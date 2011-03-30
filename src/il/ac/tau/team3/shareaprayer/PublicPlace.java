package il.ac.tau.team3.shareaprayer;

import org.mapsforge.android.maps.GeoPoint;

// Synagogue
public class PublicPlace {
	private final GeoPoint location;
	private final String name;
	private final String type;
	private int numOfPeople;
	
	public PublicPlace(GeoPoint location, String name, String type) {
		this.setNumOfPeople(0);
		this.location = location;
		this.name = name;
		this.type = type;
	}
	
	public GeoPoint getLocation() {
		return location;
	}

	public String getName() {
		return name;
	}

	public String getType() {
		return type;
	}

	public void setNumOfPeople(int numOfPeople) {
		this.numOfPeople = numOfPeople;
	}

	public int getNumOfPeople() {
		return numOfPeople;
	}
	
	
}
