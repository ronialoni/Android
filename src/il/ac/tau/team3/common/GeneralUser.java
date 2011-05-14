
package il.ac.tau.team3.common;

import java.io.Serializable;

import org.codehaus.jackson.annotate.JsonIgnore;


public class GeneralUser extends GeneralLocation implements Serializable {
		/**
	 * 
	 */
	private static final long serialVersionUID = -8100017600787664519L;
		
		private String status;
		private String firstName;
		private String lastName;
		
		public GeneralUser()	{
			super();
		}
		
		public GeneralUser(String name, SPGeoPoint spGeoPoint, String status) {
			super(spGeoPoint,name);
			
			this.status = status;
		}
		
		public GeneralUser(String name, SPGeoPoint spGeoPoint, String status, String firstName, String lastName) {
			super(spGeoPoint,name);
			this.firstName = firstName;
			this.lastName = lastName;
			this.status = status;
		}
	
		public String getStatus() {
			return status == null ? "" : status;
		}
		public void setStatus(String status) {
			this.status = status;
		}

		public String getFirstName() {
			return firstName;
		}

		public void setFirstName(String firstName) {
			this.firstName = firstName;
		}

		public String getLastName() {
			return lastName;
		}

		public void setLastName(String lastName) {
			this.lastName = lastName;
		}
		
		@JsonIgnore
		public String getFullName() {
			return (this.firstName + " " + this.lastName);
		}
		
		

		
		
}
