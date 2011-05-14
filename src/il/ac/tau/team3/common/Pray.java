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
	List<String> joiners;
	
	public Pray(Calendar startTime, Calendar endTime, String name,
			List<String> joiners) {
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
		this.joiners = new ArrayList<String>();
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

	public List<String> getJoiners() {
		return joiners;
	}

	public void setJoiners(List<String> joiners) {
		this.joiners = joiners;
	}
	
	public boolean isJoinerSigned(String joiner){
		return this.joiners.contains(joiner);
	}
	
	public int numberOfJoiners(){
		return this.joiners.size();
	}
	
	public void addJoiner(String name){
		this.joiners.add(name);
	}
	
	public void removeJoiner(String name){
		if(this.joiners.contains(name)){
			this.joiners.remove(name);
		}
	}
	
}
