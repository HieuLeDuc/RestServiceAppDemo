package de.home.vs.resource;

import java.util.List;

import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import de.home.vs.model.*;

@Path("items")
public class ItemsResource {
    @GET
    @Produces(MediaType.APPLICATION_JSON)

    public String getItemsResource() {
        DataSource ds = DataSource.getInstance();
        List<Item> items = ds.getItems();
        JsonArrayBuilder jab = javax.json.Json.createArrayBuilder();

        for (Item i : items) {
            jab.add(javax.json.Json.createObjectBuilder()
                    .add("id", i.getId())
                    .add("name", i.getName())
                    .add("price", i.getPrice()));
        }
        return jab.build().toString();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public String addItem(JsonObject json) {
        DataSource ds = DataSource.getInstance();
        Item i = new Item(ds.getNextItemId(), json.getString("name"), json.getString("description"),
                json.getJsonNumber("price").doubleValue());
        ds.createItem(i);

        JsonObjectBuilder job = javax.json.Json.createObjectBuilder()
                .add("id", i.getId())
                .add("name", i.getName())
                .add("description", i.getDescription())
                .add("price", i.getPrice());
        return job.build().toString();
    }
}
