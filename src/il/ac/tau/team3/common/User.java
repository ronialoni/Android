package il.ac.tau.team3.common;

import java.io.Serializable;

public class User extends GeneralUser implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -242765279991641599L;

	public User(String name, SPGeoPoint spGeoPoint, String status) {
		super(name, spGeoPoint, status);
		
	}

}
