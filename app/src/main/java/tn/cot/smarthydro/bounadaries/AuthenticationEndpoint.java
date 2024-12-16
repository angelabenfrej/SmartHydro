package tn.cot.smarthydro.bounadaries;


import jakarta.ejb.EJBException;
import jakarta.inject.Inject;
import jakarta.ws.rs.CookieParam;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Cookie;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import tn.cot.smarthydro.entities.User;
import tn.cot.smarthydro.services.UserServices;
import tn.cot.smarthydro.utils.Oauth2Pkce;

@Path("/authenticate")
public class AuthenticationEndpoint {

    @Inject
    UserServices userServices;
    @Inject
    Oauth2Pkce oauth2Pkce;

    @POST
    public Response authenticate(@FormParam("email") String email, @FormParam("password") String password, @CookieParam("XSS-Cookie") Cookie xssCookie ) {
        if(email == null || password == null || xssCookie == null){
            return Response.status(Response.Status.NOT_ACCEPTABLE)
                    .entity("{\"message\":\"Invalid Credentials!\"}")
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }
        try {
            User AttemptedUser  = userServices.authenticateUser(email,password);
            var state = xssCookie.getValue();
            return Response.status(Response.Status.FOUND)
                    .entity("{\"AuthorizationCode\":\"" + oauth2Pkce.generateAuthorizationCode(state, AttemptedUser) + "\", \"state\":\"" + state + "\"}")
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        } catch (EJBException e) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("{\"message\":\""+e.getMessage()+"\"}")
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }
    }
}