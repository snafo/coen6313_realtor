package controller;


import provider.MongoDataProvider;
import provider.OpType;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/heatmap")
@Produces(MediaType.APPLICATION_JSON)
public class HeatMapController {

    @GET
    @Path("/price")
    public Response getPrices(){
        return new Response(1, "", MongoDataProvider.provideData(OpType.FIND_HEAT, null, null));
    }

}
