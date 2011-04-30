package il.ac.tau.team3.common;

import java.io.Serializable;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

public class PlaceAndUser implements Serializable{
	private GeneralUser user;
	private GeneralPlace place;
	private static final long serialVersionUID = 7821097316432935684L;
	public PlaceAndUser(){
		this.user = new GeneralUser();
		this.place = new GeneralPlace();
	}
	public PlaceAndUser(GeneralUser user, GeneralPlace place) {
		super();
		this.user = user;
		this.place = place;
	}
	public GeneralUser getUser() {
		return user;
	}
	public void setUser(GeneralUser user) {
		this.user = user;
	}
	public GeneralPlace getPlace() {
		return place;
	}
	public void setPlace(GeneralPlace place) {
		this.place = place;
	}
	
}
