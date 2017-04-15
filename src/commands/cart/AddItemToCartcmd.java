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

import commands.Command;
import commands.Dispatcher;

public class AddItemToCartcmd extends Command implements Runnable {

	@Override
	public StringBuffer execute(Connection connection, Map<String, Object> mapUserData) throws Exception {
		String userToken = (String) mapUserData.get("authorization"); // not
																		// sure
																		// if
																		// this
																		// is
																		// it's
																		// name
		int itemID = (int) mapUserData.get("itemID");

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
			MongoIterable<String> collections = mongoDB.listCollectionNames();
			boolean found = false;
			for (String s : collections)
				if (s.equals("carts"))
					found = true;
			if (!found)
				mongoDB.createCollection("carts");
			DBCollection carts = (DBCollection) mongoDB.getCollection("carts");
			BasicDBObject query = new BasicDBObject();
			query.put("userToken", userToken);
			DBCursor cursor = carts.find(query);
			DBObject cart;
			if (!cursor.hasNext()) {
				cart = new BasicDBObject("userToken", userToken).append("items", new ArrayList<DBObject>());
				carts.insert(cart);
			}
			cursor = carts.find(query);
			cart = (DBObject) cursor.next();
			DBObject addedItem = new BasicDBObject("id", itemID).append("size", itemSize)
					.append("stock", itemStock).append("colour", itemColour).append("price", itemPrice)
					.append("product_id", itemProductID).append("created_at", itemCreatedAt)
					.append("updated_at", itemUpdatedAt);
			ArrayList<DBObject> items = (ArrayList<DBObject>) cart.get("items");
			items.add(addedItem);
			cart.put("items", items);
			carts.updateMulti(query, cart);
		}
		return makeJSONResponseEnvelope(200, null, null);
	}

}
