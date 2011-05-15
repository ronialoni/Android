package il.ac.tau.team3.common;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
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
	private GeneralUser owner;

	private Pray praysOfTheDay[];
	
	private boolean prays[];
	private Date startDate;
	private Date endDate;
	
	

	

	
	public GeneralPlace(){
		super();
		this.praysOfTheDay = new Pray[3];
		this.prays = new boolean[3];
		this.startDate = new Date();
		this.endDate = new Date();
	}
	
	public GeneralPlace(String name, String address , SPGeoPoint spGeoPoint){
		super(spGeoPoint,name);
		this.address = address;
		this.praysOfTheDay = new Pray[3];
		this.prays = new boolean[3];
		this.startDate = new Date();
		this.endDate = new Date();
	}
	
	public GeneralPlace(GeneralUser owner, String name, String address , SPGeoPoint spGeoPoint, Date startDate,Date endDate){
		super(spGeoPoint,name);
		this.address = address;
		this.praysOfTheDay = new Pray[3];
		this.prays = new boolean[3];
		this.owner = owner;
		this.startDate = startDate;
		this.endDate = endDate;
	}
	
	public Pray[] getPraysOfTheDay() {
		return praysOfTheDay;
	}

	public void setPraysOfTheDay(Pray[] praysOfTheDay) {
		this.praysOfTheDay = praysOfTheDay;
	}
	
	public void setPraysOfTheDay(int prayNumber, Pray praysOfTheDay) {
		if(prayNumber < 0 || prayNumber >= this.praysOfTheDay.length){
			return ;
		}
		this.praysOfTheDay[prayNumber] = praysOfTheDay;
	}

	
	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	
	public GeneralUser getOwner() {
		return owner;
	}

	public void setOwner(GeneralUser owner) {
		this.owner = owner;
	}
	
	public boolean[] getPrays() {
		return prays;
	}

	public void setPrays(boolean[] prays) {
		this.prays = prays;
	}

	

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}
	

	
	@JsonIgnore
	public boolean IsJoinerSigned(int prayNumber, GeneralUser joiner){
			if(prayNumber < 0 || prayNumber >= praysOfTheDay.length){
				return false;
			}
			return (this.praysOfTheDay[prayNumber].isJoinerSigned(joiner));
	}
	
	@JsonIgnore  
	public int getNumberOfPrayers(int prayNumber){
	    	return this.praysOfTheDay[prayNumber].numberOfJoiners();
	}
	
		
}

	    		
	
	
