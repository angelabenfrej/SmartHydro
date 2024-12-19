package tn.cot.smarthydro.bounadaries;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import tn.cot.smarthydro.entities.Identity;
import tn.cot.smarthydro.services.IdentityServices;

@Path("/identity")
public class IdentityRegistration {


    @Inject
    IdentityServices identityServices;

    @POST
    @Path("/register")
    public Response register(@Valid Identity identity) {
        try {
            identityServices.registerUser(identity);
            return Response.status(Response.Status.CREATED)
                    .entity("{\"message\": \"Activation Code is sent to your Email : " + identity.getEmail() + "\"}")
                    .type(MediaType.APPLICATION_JSON)
                    .build();}
        catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"" + e.getMessage() + "\"}")
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }
    }

    @GET
    @Path("/register/activate/{activation_code}")
    public Response activate(@PathParam("activation_code") String code) {
        String numericCode = code.replaceAll("[^\\d]", "");
        try {
            identityServices.activateUser(numericCode);
            return Response.ok("{\"message\": \"Identity successfully activated.\"}")
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        } catch (Exception e){
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"" + e.getMessage() + "\"}")
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }
    }

    
}
