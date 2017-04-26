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
		Document cart = MongoDBUtils.getUserCart(mongoDB, userID);
		MongoCollection<Document> carts = mongoDB.getCollection("carts");
		ArrayList<Document> items = (ArrayList<Document>) cart.get("items");
		boolean found = false;
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
			carts.updateOne(eq("userID", userID), new Document("$set", new Document("totalPrice", totalPrice - ((double) item.get("price") * (int) item.get("quantity")))));
			cart = MongoDBUtils.getUserCart(mongoDB, userID);
			return makeJSONResponseEnvelope(200, null, new StringBuffer(cart.toJson()));
		}
		else
			return makeJSONResponseEnvelope(404, null, new StringBuffer("{\"error\":\"ItemNotFound\"}"));
	}

}
