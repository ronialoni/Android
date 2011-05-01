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

	public void setGeometry(Geometry a_geometry){
				geometry = a_geometry;
			}
			
			public Geometry getGeometry(){
				return geometry;
			}
			
			public Results()	{
				
			}
	
	
}
