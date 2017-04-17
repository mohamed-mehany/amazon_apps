package commands.cart;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Map;

import org.bson.Document;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import static com.mongodb.client.model.Filters.eq;


import commands.Command;
import commands.Dispatcher;

public class ChangeItemQuantityCmd extends Command implements Runnable {

	@Override
	public StringBuffer execute(Connection connection, Map<String, Object> mapUserData) throws Exception {
		int userID = (int) mapUserData.get("userID");
		int itemID = (int) mapUserData.get("itemID");
		int newItemQuantity = (int) mapUserData.get("newItemQuantity");
		MongoDatabase mongoDB = Dispatcher.getDataBase("Amazon");
		MongoCollection<Document> carts = mongoDB.getCollection("carts");
		Document cart = MongoDBUtils.getUserCart(mongoDB, userID);
		ArrayList<Document> items = (ArrayList<Document>) cart.get("items");
		boolean found = false;
		Document item = null;
		for (int i = 0; i < items.size(); i++)
			if ((int) items.get(i).get("id") == itemID) {
				item = items.remove(i);
				found = true;
				break;
			}
		if (!found)
			return makeJSONResponseEnvelope(404, null, new StringBuffer("{\"error\":\"ItemNotFound\"}"));
		item.put("quantity", newItemQuantity);
		items.add(item);
		carts.updateOne(eq("userID", userID), new Document("$set", new Document("items", items)));
		cart = MongoDBUtils.getUserCart(mongoDB, userID);
		items = (ArrayList<Document>) cart.get("items");
		double newTotalPrice = 0;
		for (Document itm : items)
			newTotalPrice += (double) itm.get("price") * (int) itm.get("quantity");
		carts.updateOne(eq("userID", userID), new Document("$set", new Document("totalPrice", newTotalPrice)));
		cart = MongoDBUtils.getUserCart(mongoDB, userID);
		return makeJSONResponseEnvelope(200, null, new StringBuffer(cart.toJson()));
	}

}
