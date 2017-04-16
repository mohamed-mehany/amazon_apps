package commands.cart;

import static com.mongodb.client.model.Filters.eq;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Map;

import org.bson.Document;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoIterable;

import commands.Command;
import commands.Dispatcher;

public class RemoveItemFromCartCmd extends Command implements Runnable {

	@Override
	public StringBuffer execute(Connection connection, Map<String, Object> mapUserData) throws Exception {
		int userID = (int) mapUserData.get("userID");
		int itemID = (int) mapUserData.get("itemID");
		MongoDatabase mongoDB = Dispatcher.getDataBase("Amazon");
		MongoIterable<String> collections = mongoDB.listCollectionNames();
		boolean found = false;
		for (String s : collections)
			if (s.equals("carts"))
				found = true;
		if (!found)
			mongoDB.createCollection("carts");
		MongoCollection<Document> carts = mongoDB.getCollection("carts");
		Document cart = carts.find(eq("userID", userID)).first();
		if (cart == null) {
			cart = new Document("userID", userID).append("items", new ArrayList<Document>());
			carts.insertOne(cart);
		}
		ArrayList<Document> items = (ArrayList<Document>) cart.get("items");
		found = false;
		Document item = null;
		for (int i = 0; i < items.size(); i++)
			if ((int) items.get(i).get("id") == itemID) {
				item = items.remove(i);
				found = true;
				break;
			}
		carts.updateOne(eq("userID", userID), new Document("$set", new Document("items", items)));
		if (found) {
			double totalPrice = (double) cart.get("totalPrice");
			carts.updateOne(eq("userID", userID), new Document("$set", new Document("totalPrice", totalPrice + (double) item.get("price"))));
			return makeJSONResponseEnvelope(200, null, null);
		}
		else
			return makeJSONResponseEnvelope(404, null, new StringBuffer("{\"error\":\"ItemNotFound\"}"));
	}

}
