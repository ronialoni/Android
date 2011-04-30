package il.ac.tau.team3.common;

import org.restlet.resource.Delete;
import org.restlet.resource.Get;
import org.restlet.resource.Put;


public interface GeneralUserResource {
    @Get("form:xml")
    public GeneralUser retrieve(Integer i);
    
    @Get
    public Integer getNumUsers();

    @Put
    public void store(GeneralUser user);

    @Delete
    public void remove();
}
