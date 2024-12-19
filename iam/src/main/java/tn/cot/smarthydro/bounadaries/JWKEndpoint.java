package tn.cot.smarthydro.bounadaries;

import jakarta.ejb.EJBException;
import jakarta.inject.Inject;
import jakarta.json.Json;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import tn.cot.smarthydro.security.JwtManager;

@Path("/jwk")
public class JWKEndpoint {

    @Inject
    private JwtManager jwtManager;

    @GET
    @Path("/{kid}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getJWK(@PathParam("kid")String kid) {
        try {
            return Response.ok(jwtManager.getPublicKeyAsJWK(kid)).build();
        }catch (EJBException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Json.createObjectBuilder().add("error",e.getMessage()).build()).build();
        }
    }
}