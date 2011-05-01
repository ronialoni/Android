package il.ac.tau.team3.common;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
public class GeneralPlace extends GeneralLocation implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3680632953183211194L;
	private String address;
	private String owner;
	private List<String> allJoiners;
	private List<String> allJoiners2;
	private List<String> allJoiners3;
	private boolean prays[];	
	
	

	

	public GeneralPlace(){
		super();
		this.allJoiners = new ArrayList<String>();
		this.allJoiners2 = new ArrayList<String>();
		this.allJoiners3 = new ArrayList<String>();
		this.prays = new boolean[3];
	}
	
	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}
	
	public boolean[] getPrays() {
		return prays;
	}

	public void setPrays(boolean[] prays) {
		this.prays = prays;
	}

	public GeneralPlace(String name, String address , SPGeoPoint spGeoPoint){
		super(spGeoPoint,name);
		this.address = address;
		this.allJoiners = new ArrayList<String>();
		this.allJoiners2 = new ArrayList<String>();
		this.allJoiners3 = new ArrayList<String>();
		this.prays = new boolean[3];
	}
	
	public GeneralPlace(GeneralUser owner, String name, String address , SPGeoPoint spGeoPoint){
		super(spGeoPoint,name);
		this.address = address;
		this.allJoiners = new ArrayList<String>();
		this.allJoiners2 = new ArrayList<String>();
		this.allJoiners3 = new ArrayList<String>();
		this.prays = new boolean[3];
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
	
	@JsonIgnore
	public void addJoiner2(String name){
		if(allJoiners2 == null){
			this.allJoiners2 = new ArrayList<String>();
		}
		allJoiners2.add(name);
		return;
	}
	
	@JsonIgnore
	public void removeJoiner2(String name){
		if(allJoiners2!=null){
			allJoiners2.remove(name);
		}
		return;
	}
	@JsonIgnore
	public void addJoiner3(String name){
		if(allJoiners3 == null){
			this.allJoiners3 = new ArrayList<String>();
		}
		allJoiners3.add(name);
		return;
	}
	
	@JsonIgnore
	public void removeJoiner3(String name){
		if(allJoiners3!=null){
			allJoiners3.remove(name);
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
	
	public void setAllJoiners2(ArrayList<String> joiners){
		//this.minyanJoiners = joiners;
		this.allJoiners2.clear();
		for(String joiner : joiners){
			this.allJoiners2.add(joiner);
			
		}
		
	}
	
	public void setAllJoiners3(ArrayList<String> joiners){
		//this.minyanJoiners = joiners;
		this.allJoiners3.clear();
		for(String joiner : joiners){
			this.allJoiners3.add(joiner);
			
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
	
	@JsonIgnore
	public boolean IsJoinerSigned2(String joiner){
	    	return (this.allJoiners2.contains(joiner));
	    }
	
	@JsonIgnore  
	public int getNumberOfPrayers2(){
	    	return this.allJoiners2.size();
	    }
	
	@JsonIgnore
	public boolean IsJoinerSigned3(String joiner){
	    	return (this.allJoiners3.contains(joiner));
	    }
	
	@JsonIgnore  
	public int getNumberOfPrayers3(){
	    	return this.allJoiners3.size();
	    }
	
	public List<String> getAllJoiners2() {
		return allJoiners2;
	}

	

	public List<String> getAllJoiners3() {
		return allJoiners3;
	}

	
}
