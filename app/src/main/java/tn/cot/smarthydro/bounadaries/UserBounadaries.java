package tn.cot.smarthydro.bounadaries;

import jakarta.inject.Inject;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.StreamingOutput;
import tn.cot.smarthydro.entities.User;
import tn.cot.smarthydro.services.UserServices;
import jakarta.ejb.EJBException;

@Path("/users")
public class UserBounadaries {


    @Inject
    UserServices userServices;

    @POST
    @Path("/register")
    public Response register(@Valid User user) {
        try {
            userServices.registerUser(user);
            return Response.status(Response.Status.CREATED)
                    .entity("{\"message\": \"Activation Code is sent to your Email : " + user.getEmail() + "\"}")
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
            userServices.activateUser(numericCode);
            return Response.ok("{\"message\": \"User successfully activated.\"}")
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
