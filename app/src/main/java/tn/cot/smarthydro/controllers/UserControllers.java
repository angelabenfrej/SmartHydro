package tn.cot.smarthydro.controllers;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import tn.cot.smarthydro.entities.User;
import tn.cot.smarthydro.services.UserServices;
import jakarta.ejb.EJBException;


@RequestScoped
@Path("/users")
@Produces({"application/json"})
@Consumes(MediaType.APPLICATION_JSON)
public class UserControllers {


    @Inject
    UserServices userServices;

    @POST
    @Path("/register")
    public Response register(User user) {
        try {
            userServices.registerUser(user);
            return Response.ok()
                    .entity("{\"message\": \"User Registred Successfully!\"}")
                    .type(MediaType.APPLICATION_JSON)
                    .build();}
        catch (EJBException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"" + e.getMessage() + "\"}")
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }
    }

}
