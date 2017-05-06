package commands.cart;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bson.Document;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoIterable;
import static com.mongodb.client.model.Filters.eq;

import commands.Command;
import commands.Dispatcher;

public class AddItemToCartCmd extends Command implements Runnable {

	@Override
	public StringBuffer execute(Connection connection, Map<String, Object> mapUserData) throws Exception {
		
		int userID = (int) mapUserData.get("userID");
		int itemID = (int) mapUserData.get("productID");

		CallableStatement getItem = connection.prepareCall("{call getItemInfo(?)}");
		getItem.setInt(1, itemID);
		getItem.execute();

		ResultSet resultSet = getItem.getResultSet();
		if (resultSet.next()) {
			int itemSize = resultSet.getInt("size");
			int itemStock = resultSet.getInt("stock");
			String itemColour = resultSet.getString("colour");
			double itemPrice = resultSet.getDouble("price");
			int itemProductID = resultSet.getInt("product_id");
			int itemCreatedAt = resultSet.getInt("created_at");
			int itemUpdatedAt = resultSet.getInt("updated_at");
			MongoDatabase mongoDB = Dispatcher.getDataBase("Amazon");
			Document cart = MongoDBUtils.getUserCart(mongoDB, userID);
			MongoCollection<Document> carts = mongoDB.getCollection("carts");
			ArrayList<Document> items = (ArrayList<Document>) cart.get("items");
			boolean found = false;
			for (int i = 0; i < items.size(); i++)
				if ((int) items.get(i).get("id") == itemID) {
					found = true;
					break;
				}
			if (!found) {
				Document addedItem = new Document("id", itemID).append("size", itemSize).append("stock", itemStock)
						.append("colour", itemColour).append("price", itemPrice).append("product_id", itemProductID)
						.append("created_at", itemCreatedAt).append("updated_at", itemUpdatedAt).append("quantity", 1);
				items.add(addedItem);
				cart.put("items", items);
				double totalPrice = (double) cart.get("totalPrice");
				carts.updateOne(eq("userID", userID), new Document("$set", new Document("items", items)));
				carts.updateOne(eq("userID", userID),
						new Document("$set", new Document("totalPrice", totalPrice + itemPrice)));
			}
			cart = MongoDBUtils.getUserCart(mongoDB, userID);
			return new StringBuffer("[").append(new StringBuffer(cart.toJson())).append("]");
		}
		return new StringBuffer("[").append("{\"error\":\"msh la2y item\"}").append("]");
		
	}

}
