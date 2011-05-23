package il.ac.tau.team3.addressQuery;

import java.io.Serializable;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)

public class Results implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1477272868383134953L;
	private Geometry geometry;
	private String	 formatted_address;

	public String getFormatted_address() {
		return formatted_address;
	}

	public void setFormatted_address(String formatted_address) {
		this.formatted_address = formatted_address;
	}

	public void setGeometry(Geometry a_geometry){
		geometry = a_geometry;
	}

	public Geometry getGeometry(){
		return geometry;
	}

	public Results()	{

	}
	
	
}
