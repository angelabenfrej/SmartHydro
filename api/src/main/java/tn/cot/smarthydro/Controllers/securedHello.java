package tn.cot.smarthydro.Controllers;


import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;
import tn.cot.smarthydro.security.Secured;

@Path("/secure")
public class securedHello {

    @Secured
    @GET
    @RolesAllowed("Admin")
    @Path("/admin/hello")
    public Response helloAdmin() {
        return Response.ok("Hello this is Admin Ressource !").build();
    }
    @Secured
    @GET
    @RolesAllowed({"Admin","Client"})
    @Path("/hello")
    public Response helloClient() {
        return Response.ok("Hello this is Client Ressource !").build();
    }
}
