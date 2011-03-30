
package il.ac.tau.team3.common;

import java.io.Serializable;


public class GeneralUser implements Serializable {
		/**
	 * 
	 */
	private static final long serialVersionUID = -8100017600787664519L;
		private String name;
		private SPGeoPoint spGeoPoint;
		private String status;
		
		public GeneralUser()	{
			
		}
		
		public GeneralUser(String name, SPGeoPoint spGeoPoint, String status) {
			super();
			this.name = name;
			this.spGeoPoint = spGeoPoint;
			this.status = status;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String getStatus() {
			return status;
		}
		public void setStatus(String status) {
			this.status = status;
		}
		public void setSpGeoPoint(SPGeoPoint spGeoPoint) {
			this.spGeoPoint = spGeoPoint;
		}
		public SPGeoPoint getSpGeoPoint() {
			return spGeoPoint;
		}
		 
		
		

		
		
}
