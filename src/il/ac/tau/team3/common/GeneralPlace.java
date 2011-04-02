package il.ac.tau.team3.common;

import java.util.ArrayList;
import java.util.List;

public class GeneralPlace extends GeneralLocation{
	
	private String address;
	private List<String> minyanJoiners;
		
	
	public GeneralPlace(){
		super();
	}
	
	public GeneralPlace(String name, String address , SPGeoPoint spGeoPoint){
		super(spGeoPoint,name);
		this.address = address;
		this.minyanJoiners = new ArrayList<String>();
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}
	
	public void addJoiner(String name){
		minyanJoiners.add(name);
		return;
	}
	
	public void removeJoiner(String name){
		minyanJoiners.remove(name);
		return;
	}
	
}
