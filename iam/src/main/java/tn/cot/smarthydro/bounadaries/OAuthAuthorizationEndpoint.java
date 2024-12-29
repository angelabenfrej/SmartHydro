package tn.cot.smarthydro.bounadaries;

import jakarta.ejb.EJBException;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import tn.cot.smarthydro.repositories.IdentityRepository;
import tn.cot.smarthydro.repositories.TenantRepository;
import tn.cot.smarthydro.utils.Argon2Utils;
import tn.cot.smarthydro.utils.Oauth2Pkce;

import java.net.URI;


@Path("/")
public class OAuthAuthorizationEndpoint {

    public static final String CHALLENGE_RESPONSE_COOKIE_ID = "signInId";
    @Inject
    Oauth2Pkce oauth2Pkce;
    @Inject
    TenantRepository tenantRepository;

    @GET
    @Path("/authorize")
    @Produces(MediaType.TEXT_HTML)
    public Response authorize(@Context UriInfo uriInfo) {
        var params = uriInfo.getQueryParameters();
        var clientId = params.getFirst("client_id");
        if (clientId == null || clientId.isEmpty()) {
            return informUserAboutError("Invalid client_id :" + clientId);
        }
        var tenant = tenantRepository.findByName(clientId);
        if (tenant == null) {
            return informUserAboutError("Invalid client_id :" + clientId);
        }
        if (tenant.getSupportedGrantTypes() != null && !tenant.getSupportedGrantTypes().contains("authorization_code")) {
            return informUserAboutError("Authorization Grant type, authorization_code, is not allowed for this tenant :" + clientId);
        }
        String redirectUri = params.getFirst("redirect_uri");
        if (tenant.getRedirectUri() != null && !tenant.getRedirectUri().isEmpty()) {
            if (redirectUri != null && !redirectUri.isEmpty() && !tenant.getRedirectUri().equals(redirectUri)) {
                return informUserAboutError("redirect_uri is pre-registred and should match");
            }
            redirectUri = tenant.getRedirectUri();
        } else {
            if (redirectUri == null || redirectUri.isEmpty()) {
                return informUserAboutError("redirect_uri is not pre-registred and should be provided");
            }
        }
        String responseType = params.getFirst("response_type");
        if (!"code".equals(responseType) && !"token".equals(responseType)) {
            String error = "invalid_grant :" + responseType + ", response_type params should be code or token:";
            return informUserAboutError(error);
        }
        String requestedScope = params.getFirst("scope");
        if (requestedScope == null || requestedScope.isEmpty()) {
            requestedScope = tenant.getRequiredScopes();
        }
        String codeChallengeMethod = params.getFirst("code_challenge_method");
        if(codeChallengeMethod==null || !codeChallengeMethod.equals("S256")){
            String error = "invalid_grant :" + codeChallengeMethod + ", code_challenge_method must be 'S256'";
            return informUserAboutError(error);
        }
        var state = params.getFirst("state");
        var code_challenge = params.getFirst("code_challenge");
        oauth2Pkce.addChallenge(state, code_challenge);
        URI redirect = UriBuilder.fromPath("/login/authorization").build();
        return Response.status(Response.Status.FOUND)
                .location(redirect)
                .cookie(new NewCookie.Builder(CHALLENGE_RESPONSE_COOKIE_ID)
                        .httpOnly(true).secure(true).sameSite(NewCookie.SameSite.STRICT).value(state+"#"+tenant.getName()+"$"+redirectUri).build()).build();
    }

    @GET
    @Path("/login/authorization")
    public Response loginAuthorization() {
            StreamingOutput stream = (output)->{
                try(var resourceStream = getClass().getResourceAsStream("/signin.html")){
                    assert resourceStream != null;
                    output.write(resourceStream.readAllBytes());
                }
            };
            return Response.ok()
                    .entity(stream)
                    .build();
    }

    private Response informUserAboutError(String error) {
        return Response.status(Response.Status.BAD_REQUEST)
                .entity("{\"message\":\""+error+"\"}")
                .build();
    }
}



