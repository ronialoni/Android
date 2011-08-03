package il.ac.tau.team3.common;


import java.io.Serializable;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import java.util.ArrayList;


public class Pray  implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -6975030575322925440L;
	private Calendar startTime;
	private Calendar endTime;
	String name;
	List<GeneralUser> joiners;
	
	public Pray(Calendar startTime, Calendar endTime, String name,
			List<GeneralUser> joiners) {
		super();
		this.startTime = startTime;
		this.endTime = endTime;
		this.name = name;
		this.joiners = joiners;
	}
	
	public Pray() {
		super();
		this.startTime = new GregorianCalendar();
		this.endTime =  new GregorianCalendar();
		this.name = "";
		this.joiners = new ArrayList<GeneralUser>();
	}

	public Calendar getStartTime() {
		return startTime;
	}

	public void setStartTime(Calendar startTime) {
		this.startTime = startTime;
	}

	public Calendar getEndTime() {
		return endTime;
	}

	public void setEndTime(Calendar endTime) {
		this.endTime = endTime;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<GeneralUser> getJoiners() {
		return joiners;
	}

	public void setJoiners(List<GeneralUser> joiners) {
		this.joiners = joiners;
	}
	
	public boolean isJoinerSigned(GeneralUser joiner){
		for( GeneralUser signedJoiner : this.joiners){
			if(signedJoiner.getName().equals(joiner.getName())){
				return true;
			}
		}
		return false;
	}
	
	public int numberOfJoiners()
	{
		return this.joiners.size();
	}
	
	public void addJoiner(GeneralUser name){
		for( GeneralUser signedJoiner : this.joiners){
			if(signedJoiner.getName().equals(name.getName())){
				return;
			}
		}
		this.joiners.add(name);
	}
	
	public void removeJoiner(GeneralUser name){
		for( GeneralUser signedJoiner : this.joiners){
			if(signedJoiner.getName().equals(name.getName())){
				this.joiners.remove(signedJoiner);
				return;
			}
		}
	}
}
