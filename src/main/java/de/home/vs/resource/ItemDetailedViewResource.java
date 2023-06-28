package de.home.vs.resource;

import javax.json.JsonArrayBuilder;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import de.home.vs.model.DataSource;
import de.home.vs.model.Item;


@Path("items/{id}")
public class ItemDetailedViewResource {
    
        @GET
        @Produces(MediaType.APPLICATION_JSON)
        public String getDetailedViewResource(@PathParam("id") int id) {
            DataSource ds = DataSource.getInstance();
            Item item = ds.getItemById(id);
            JsonArrayBuilder jab = javax.json.Json.createArrayBuilder();
            jab.add(javax.json.Json.createObjectBuilder()
                    .add("id", item.getId())
                    .add("name", item.getName())
                    .add("description", item.getDescription())
                    .add("price", item.getPrice()));
            return jab.build().toString();
        }        
}
