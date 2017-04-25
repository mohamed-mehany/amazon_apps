package commands.cart;

import static com.mongodb.client.model.Filters.eq;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
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
		int bankingInfoID = (int) mapUserData.get("bankingInfoID");
		
		CallableStatement getBankingInfo = connection.prepareCall("{call get_banking_info(?)}");
		getBankingInfo.setInt(1, bankingInfoID);
		getBankingInfo.execute();
		ResultSet bankingInfos = getBankingInfo.getResultSet();
		if (!bankingInfos.next())
			makeJSONResponseEnvelope(0, null, new StringBuffer("{\"error\":\"this banking info does not belong to this user\"}"));
		String creditCardNumber = bankingInfos.getString("card_number");
		if (creditCardNumber.equals("111111111111") || creditCardNumber.equals("222222222222") || creditCardNumber.equals("444444444444")) {
			CallableStatement createOrder = connection.prepareCall("{call get_banking_info(?, ?)}");
			createOrder.setInt(1, userID);
			createOrder.setInt(2, bankingInfoID);
			createOrder.execute();
			ResultSet returnFromCreateOrder = getBankingInfo.getResultSet();
			returnFromCreateOrder.next();
			int orderID = returnFromCreateOrder.getInt(1);
			MongoDatabase mongoDB = Dispatcher.getDataBase("Amazon");
			Document cart = MongoDBUtils.getUserCart(mongoDB, userID);
			ArrayList<Document> items = (ArrayList<Document>) cart.get("items");
			for (Document item : items) {
				int itemID = (int) item.get("id");
				int productID = (int) item.get("product_id");
				int count = (int) item.get("quantity");
				CallableStatement addItemToOrder = connection.prepareCall("{call add_item_to_order(?, ?, ?, ?)}");
				addItemToOrder.setInt(1, orderID);
				addItemToOrder.setInt(2, itemID);
				addItemToOrder.setInt(3, productID);
				addItemToOrder.setInt(4, count);
				addItemToOrder.execute();
			}
			MongoCollection<Document> carts = mongoDB.getCollection("carts");
			carts.deleteMany(eq("userID", userID));
			cart = MongoDBUtils.getUserCart(mongoDB, userID);
			return makeJSONResponseEnvelope(200, null, new StringBuffer(cart.toJson()));
		}
		return makeJSONResponseEnvelope(404, null, new StringBuffer("{\"error\":\"creditCardDoesNotEnoughHaveMoney\"}"));
	}

}
