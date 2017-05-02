package commands.cart;

import java.sql.Connection;
import java.util.Map;

import org.bson.Document;

import com.mongodb.client.MongoDatabase;

import commands.Command;
import commands.Dispatcher;

public class ViewTotalPriceCmd extends Command implements Runnable {

	@Override
	public StringBuffer execute(Connection connection, Map<String, Object> mapUserData) throws Exception {
		int userID = (int) mapUserData.get("userID");
		MongoDatabase mongoDB = Dispatcher.getDataBase("Amazon");
		Document cart = MongoDBUtils.getUserCart(mongoDB, userID);
		StringBuffer result = new StringBuffer();
		result.append("{ \"totalPrice\" : ");
		result.append((double) cart.get("totalPrice"));
		result.append('}');
		return makeJSONResponseEnvelope(200, null, result);
	}

}
