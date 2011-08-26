package il.ac.tau.team3.common;


public class UnknownLocationException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -210437500181178773L;

	@Override
	public String getMessage()	{
		return "Unknown location exception";
	}
}
