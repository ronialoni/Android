package il.ac.tau.team3.shareaprayer;

public class UserNotFoundException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1942534287227897738L;

	@Override
	public String getMessage()	{
		return "User not found exception";
	}
}
