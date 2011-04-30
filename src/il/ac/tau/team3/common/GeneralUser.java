
package il.ac.tau.team3.common;

import java.io.Serializable;


public class GeneralUser extends GeneralLocation implements Serializable {
		/**
	 * 
	 */
	private static final long serialVersionUID = -8100017600787664519L;
		
		private String status;
		
		public GeneralUser()	{
			super();
		}
		
		public GeneralUser(String name, SPGeoPoint spGeoPoint, String status) {
			super(spGeoPoint,name);
			
			this.status = status;
		}
	
		public String getStatus() {
			return status;
		}
		public void setStatus(String status) {
			this.status = status;
		}
	
		 
		
		

		
		
}
