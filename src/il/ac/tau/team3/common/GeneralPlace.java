package il.ac.tau.team3.common;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;


public class GeneralPlace extends GeneralLocation implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3680632953183211194L;
	private String address;
	private ArrayList<String> minyanJoiners;
		
	
	public GeneralPlace(){
		super();
		//this.minyanJoiners = new ArrayList<String>();
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
		if(minyanJoiners == null){
			this.minyanJoiners = new ArrayList<String>();
		}
		minyanJoiners.add(name);
		return;
	}
	
	public void removeJoiner(String name){
		if(minyanJoiners!=null){
			minyanJoiners.remove(name);
		}
		return;
	}
	
	//@JsonIgnore
	public ArrayList<String> getAllJoiners(){
		return this.minyanJoiners;
	}
	
	//@JsonIgnore
	public void setAllJoiners(ArrayList<String> joiners){
		this.minyanJoiners = joiners;
	}
}
