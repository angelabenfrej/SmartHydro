package tn.cot.smarthydro.bounadaries;


import jakarta.ejb.EJB;
import jakarta.inject.Inject;
import jakarta.json.Json;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import tn.cot.smarthydro.security.JwtManager;
import tn.cot.smarthydro.utils.Oauth2Pkce;
import java.util.Map;



@Path("/oauth/token")
public class OAuthTokenEndpoint {

    @EJB
    JwtManager jwtManager;
    @Inject
    Oauth2Pkce oauth2Pkce;
    @GET
    public Response generateToken(@QueryParam("authorization_code") String authorizationCode,
                                  @QueryParam("code_verifier")String codeVerifier){

        try {
            Map<String, Object> cred = oauth2Pkce.CheckChallenge(authorizationCode, codeVerifier);
            String tenantId = (String) cred.get("tenantId");
            String subject = (String) cred.get("subject");
            String approvedScopes = (String) cred.get("approvedScopes");
            String[] roles = (String[]) cred.get("roles");
            var token =  jwtManager.generateToken(tenantId, subject, approvedScopes, roles);
            return Response
                    .ok(Json.createObjectBuilder()
                            .add("accessToken", token)
                            .add("tokenType", "Bearer")
                            .add("expiresIn", 1020)
                            .build())
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        } catch (Exception e) {
            return Response.serverError()
                    .entity("{\"message\":\""+e.getMessage()+"\"}")
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }
    }

}
