package commands.cart;

import static com.mongodb.client.model.Filters.eq;

import java.util.ArrayList;

import org.bson.Document;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoIterable;

public class MongoDBUtils {
	public static Document getUserCart(MongoDatabase mongoDB, int userID) {
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
			cart = new Document("userID", userID).append("items", new ArrayList<Document>()).append("totalPrice", 0.0);
			carts.insertOne(cart);
		}
		return cart;
	}
}
