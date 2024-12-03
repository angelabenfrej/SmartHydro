package tn.cot.smarthydro.bounadaries;

import jakarta.ejb.EJBException;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.json.JSONObject;
import tn.cot.smarthydro.entities.User;
import tn.cot.smarthydro.services.UserServices;
import tn.cot.smarthydro.utils.Oauth2Pkce;

@Path("/")
public class AuthenticationEndpoint {

    @Inject
    UserServices userServices;
    @Inject
    Oauth2Pkce oauth2Pkce;

    @POST
    @Path("/authenticate")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response signIn(String json){
        JSONObject obj = new JSONObject(json);
        String email=obj.getString("email");
        String password=obj.getString("password");
        String clientId=obj.getString("clientId");
        if(email == null || password == null || clientId == null){
            return Response.status(Response.Status.NOT_ACCEPTABLE).entity("{\"message\":\"Invalid Credentials!\"}").build();
        }
        try {
            User AttemptedUser  = userServices.authenticateUser(email,password);
            return Response.ok()
                    .entity("{\"AuthorizationCode\":\""+oauth2Pkce.generateAuthorizationCode(clientId,AttemptedUser)+"\"}") //return authorization code
                    .build();
        } catch (EJBException e) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("{\"message\":\""+e.getMessage()+"\"}").build();
        }
    }

}
