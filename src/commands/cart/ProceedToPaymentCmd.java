package commands.cart;

import static com.mongodb.client.model.Filters.eq;

import java.sql.Connection;
import java.util.Map;

import org.bson.Document;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import commands.Command;
import commands.Dispatcher;

public class ProceedToPaymentCmd extends Command implements Runnable {

	@Override
	public StringBuffer execute(Connection connection, Map<String, Object> mapUserData) throws Exception {
		int userID = (int) mapUserData.get("userID");
		String creditCardNumber = (String) mapUserData.get("creditCardNumber");
		if (creditCardNumber.equals("111111111111") || creditCardNumber.equals("222222222222") || creditCardNumber.equals("444444444444")) {
			MongoDatabase mongoDB = Dispatcher.getDataBase("Amazon");
			Document cart = MongoDBUtils.getUserCart(mongoDB, userID);
			MongoCollection<Document> carts = mongoDB.getCollection("carts");
			carts.deleteMany(eq("userID", userID));
			return makeJSONResponseEnvelope(200, null, new StringBuffer(cart.toJson()));
		}
		return makeJSONResponseEnvelope(404, null, new StringBuffer("{\"error\":\"creditCardNotFound\"}"));
	}

}
