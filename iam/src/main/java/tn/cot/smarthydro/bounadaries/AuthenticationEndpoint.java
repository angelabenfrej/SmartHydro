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
import tn.cot.smarthydro.entities.Identity;
import tn.cot.smarthydro.services.IdentityServices;
import tn.cot.smarthydro.utils.Oauth2Pkce;

@Path("/authenticate")
public class AuthenticationEndpoint {

    @Inject
    IdentityServices identityServices;
    @Inject
    Oauth2Pkce oauth2Pkce;

    @POST
    public Response authenticate(@FormParam("username") String username, @FormParam("password") String password, @CookieParam("signInId") Cookie cookie ) {
        if(username == null || password == null || cookie == null){
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"message\":\"Invalid Credentials!\"}")
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }
        try {
            Identity attemptedIdentity = identityServices.authenticateIdentity(username,password);
            var state = cookie.getValue().split("#")[0];
            return Response.ok()
                    .entity("{\"AuthorizationCode\":\"" + oauth2Pkce.generateAuthorizationCode(state, attemptedIdentity) + "\", \"state\":\"" + state + "\"}")
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