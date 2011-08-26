package il.ac.tau.team3.shareaprayer;


public class ServiceConnector {
	
	private ILocationSvc service;
	
	public ServiceConnector(ILocationSvc a_service)	{
		setService(a_service);
	}
	
	public ServiceConnector()	{
		setService(null);
	}
	
	public void setService(ILocationSvc a_service)	{
		service = a_service;
	}
	
	public ILocationSvc getService() throws ServiceNotConnected	{
		if (null == service)	{
			throw new ServiceNotConnected();
		}
		return service;
	}
}
