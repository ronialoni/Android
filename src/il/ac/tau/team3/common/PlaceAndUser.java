package il.ac.tau.team3.common;

import java.io.Serializable;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

public class PlaceAndUser implements Serializable{
	
	private GeneralUser user;
	private GeneralPlace place;
	private boolean[] praysWishes;
	private static final long serialVersionUID = 7821097316432935684L;
	public PlaceAndUser(){
		this.user = new GeneralUser();
		this.place = new GeneralPlace();
		this.praysWishes = new boolean[3];
	}
	public PlaceAndUser(GeneralUser user, GeneralPlace place, boolean praysWishes[]) {
		super();
		this.user = user;
		this.place = place;
		this.praysWishes = praysWishes;
	}
	public boolean[] getPraysWishes() {
		return praysWishes;
	}
	public void setPraysWishes(boolean[] praysWishes) {
		this.praysWishes = praysWishes;
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
