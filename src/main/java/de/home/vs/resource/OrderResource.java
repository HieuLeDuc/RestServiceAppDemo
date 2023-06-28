package de.home.vs.resource;

import java.util.ArrayList;
import java.util.List;

import javax.json.JsonArray;
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

@Path("orders")
public class OrderResource {
    @GET
    @Produces(MediaType.APPLICATION_JSON)

    public String getOrderResource() {
        DataSource ds = DataSource.getInstance();
        List<Order> orders = ds.getOrders();
        JsonArrayBuilder jab = javax.json.Json.createArrayBuilder();
        for (Order o : orders) {
            jab.add(javax.json.Json.createObjectBuilder()
                    .add("id", o.getId())
                    .add("customerid", o.getCustomerid())
                    .add("total", o.getTotal()));
        }
        return jab.build().toString();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public String addOrder(JsonObject json) {
        DataSource ds = DataSource.getInstance();
        Order o = new Order(ds.getNextOrderId(), json.getInt("customerid"), 0, new ArrayList<Itemblock>());
        JsonArray ja = json.getJsonArray("orderitems");
        // handling collisions
        for (Order order : ds.getOrders()) {
            if (order.getId() == o.getId()) {
                return "Order with id " + o.getId() + " already exists";
            }
        }
        for (int i = 0; i < ja.size(); i++) {
            JsonObject item = ja.getJsonObject(i);
            Itemblock ib = new Itemblock(ds.getItemById(item.getInt("id")), item.getInt("amount"));
            o.addItemblock(ib);
        }
        ds.createOrder(o);

        JsonObjectBuilder job = javax.json.Json.createObjectBuilder()
                .add("id", o.getId())
                .add("customerid", o.getCustomerid())
                .add("total", o.getTotal());
        return job.build().toString();
    }
}
