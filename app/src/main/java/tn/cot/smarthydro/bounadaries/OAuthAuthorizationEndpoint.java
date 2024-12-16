package tn.cot.smarthydro.bounadaries;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import tn.cot.smarthydro.utils.Oauth2Pkce;

import java.net.URI;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Path("/")
public class OAuthAuthorizationEndpoint {

    @Inject
    Oauth2Pkce oauth2Pkce;
    @Context
    private UriInfo uriInfo;

    @GET
    @Path("/authorize")
    public Response authorize(@QueryParam("state") String state, @QueryParam("code_challenge") String code_challenge) {
        if (code_challenge == null  || state == null ) {
            return Response.status(Response.Status.BAD_REQUEST).entity("state or codeChallenge is missing").build();
        }
        oauth2Pkce.addChallenge(state, code_challenge);
        var secureCookie = new NewCookie.Builder("XSS-Cookie")
                .httpOnly(true)
                .secure(true)
                .sameSite(NewCookie.SameSite.STRICT)
                .domain(uriInfo.getRequestUri().getHost())
                .expiry(Date.from(Instant.now().plus(17, ChronoUnit.MINUTES)))
                .value(state)
                .build();
        URI redirectUri = UriBuilder.fromPath("/login/authorization").build();
        return Response.status(Response.Status.FOUND)
                .location(redirectUri)
                .cookie(secureCookie)
                .build();
    }

    @GET
    @Path("/login/authorization")
    public Response loginAuthorization(){
        StreamingOutput stream = (output)->{
            try(var resourceStream = getClass().getResourceAsStream("/signin.html")){
                assert resourceStream != null;
                output.write(resourceStream.readAllBytes());
            }
        };
        return Response.ok().entity(stream).build();
    }
}



