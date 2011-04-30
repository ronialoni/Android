package il.ac.tau.team3.addressQuery;

import java.io.Serializable;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;


@JsonIgnoreProperties(ignoreUnknown = true)

public class MapsQueryLocation implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 533627910597277058L;
	
	
	private String status;
	private Results[] results;
	
	
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public MapsQueryLocation()	{
	}

	public void setResults(Results[] results) {
		this.results = results;
	}

	public Results[] getResults() {
		return results;
	}
	
	
};
