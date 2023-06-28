package de.home.vs.resource;

import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import de.home.vs.model.*;
@Path("orders/{id}")
public class OrderDetailedViewResource {

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String getDetailedViewResource(@PathParam("id") int id) {
		DataSource ds = DataSource.getInstance();
		Order order = ds.getOrderById(id);
		JsonArrayBuilder jab = javax.json.Json.createArrayBuilder();
		jab.add(javax.json.Json.createObjectBuilder()
				.add("id", order.getId())
				.add("customerid", order.getCustomerid())
				.add("total", order.getTotal()));
		for (Itemblock itemblock : order.getItemblocks()) {
			jab.add(javax.json.Json.createObjectBuilder()
					.add("item", itemblock.getItem().getName())
					.add("price", itemblock.getItem().getPrice())
					.add("amount", itemblock.getAmount()));
		}
		return jab.build().toString();
	}

	@POST
	// @Produces(MediaType.TEXT_PLAIN)
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public String addOrder(@PathParam("id") int id, JsonObject json) {
		DataSource ds = DataSource.getInstance();
		Order order = ds.getOrderById(id);
		if (order == null) {
			return "Error: Order not found";
		}
		String flag = json.getString("flag");
		Item item = ds.getItemById(json.getInt("itemid"));
		int amount = json.getInt("amount");
		Itemblock newItemblock = new Itemblock(item, 0);
		// check if itemblock already exists in order
		// if yes, add amount to existing itemblock

		for (Itemblock itemblock : order.getItemblocks()) {
			if (itemblock.getItem().getId() == item.getId()) {
				newItemblock.setAmount(itemblock.getAmount());
				order.removeItemblock(itemblock);
				break;
			}
		}

		if (flag.equalsIgnoreCase("add")) {
			newItemblock.setAmount(newItemblock.getAmount() + amount);
			order.addItemblock(newItemblock);
		} else if (flag.equalsIgnoreCase("remove")) {
			if ((newItemblock.getAmount() - amount) < 0) {
				order.removeItemblock(newItemblock);
			} else {
				newItemblock.setAmount(newItemblock.getAmount() - amount);
				order.addItemblock(newItemblock);
			}
		} else if (flag.equalsIgnoreCase("delete")) {
			order.removeItemblock(newItemblock);
		} else {
			return "Error: Flag not recognized";
		}

		ds.editOrder(order);
		return getDetailedViewResource(id);
	}
}