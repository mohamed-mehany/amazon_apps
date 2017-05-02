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

public class ViewCartCmd extends Command implements Runnable {

	@Override
	public StringBuffer execute(Connection connection, Map<String, Object> mapUserData) throws Exception {
		int userID = (int) mapUserData.get("userID");
		MongoDatabase mongoDB = Dispatcher.getDataBase("Amazon");
		Document cart = MongoDBUtils.getUserCart(mongoDB, userID);
		return makeJSONResponseEnvelope(200, null, new StringBuffer(cart.toJson()));
	}

}
