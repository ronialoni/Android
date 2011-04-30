package il.ac.tau.team3.common;

import java.io.Serializable;

//import java.util.List;
import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

//import org.codehaus.jackson.annotate.JsonIgnore;
//import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
public class GeneralPlace extends GeneralLocation implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3680632953183211194L;
	private String address;
	private String owner;
	private List<String> allJoiners;
		
	
	public GeneralPlace(){
		super();
		this.allJoiners = new ArrayList<String>();
	}
	
	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public GeneralPlace(String name, String address , SPGeoPoint spGeoPoint){
		super(spGeoPoint,name);
		this.address = address;
		this.allJoiners = new ArrayList<String>();
	}
	
	public GeneralPlace(GeneralUser owner, String name, String address , SPGeoPoint spGeoPoint){
		super(spGeoPoint,name);
		this.address = address;
		this.allJoiners = new ArrayList<String>();
		this.owner = owner.getName();
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}
	
	@JsonIgnore
	public void addJoiner(String name){
		if(allJoiners == null){
			this.allJoiners = new ArrayList<String>();
		}
		allJoiners.add(name);
		return;
	}
	
	@JsonIgnore
	public void removeJoiner(String name){
		if(allJoiners!=null){
			allJoiners.remove(name);
		}
		return;
	}
	
	//@JsonIgnore
	public List<String> getAllJoiners(){
		return this.allJoiners;
	}
	
	//@JsonIgnore
	public void setAllJoiners(ArrayList<String> joiners){
		//this.minyanJoiners = joiners;
		this.allJoiners.clear();
		for(String joiner : joiners){
			this.allJoiners.add(joiner);
			
		}
		
	}
	
	@JsonIgnore
	public boolean IsJoinerSigned(String joiner){
	    	return (this.allJoiners.contains(joiner));
	    }
	
	@JsonIgnore  
	public int getNumberOfPrayers(){
	    	return this.allJoiners.size();
	    }
}
