package tn.cot.smarthydro.bounadaries;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.CookieParam;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.NewCookie;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import tn.cot.smarthydro.utils.Oauth2Pkce;

@Path("/")
public class OAuthAuthorizationEndpoint {

    @Inject
    Oauth2Pkce oauth2Pkce;
    @Context
    private UriInfo uriInfo;

    @GET
    @Path("/authorize")
    public Response authorize(@QueryParam("client_id") String client_id, @QueryParam("code_challenge") String code_challenge) {
        if (code_challenge == null || code_challenge.isEmpty() || client_id == null || client_id.isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST).entity("clientId or codeChallenge is missing").build();
        }
        oauth2Pkce.addChallenge(client_id, code_challenge);
        NewCookie cookie = new NewCookie(
                "xssCookie",
                client_id,
                uriInfo.getBaseUri().getPath(),
                uriInfo.getBaseUri().getHost(),
                "Secure Http Only Cookie",
                3600,
                true,
                true
        );
        return Response.status(Response.Status.FOUND)
                .cookie(cookie)
                .entity("{\"clientId\": \"" + client_id + "\"}")
                .build();
    }

    @GET
    @Path("/login/authorization")
    public Response loginAuthorization() {

        return Response.ok()
                .build();
    }
}
