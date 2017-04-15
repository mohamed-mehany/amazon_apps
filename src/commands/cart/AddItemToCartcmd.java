package commands.cart;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Map;

import com.mongodb.client.MongoDatabase;

import commands.Command;
import commands.Dispatcher;

public class AddItemToCartcmd extends Command implements Runnable {

	@Override
	public StringBuffer execute(Connection connection, Map<String, Object> mapUserData) throws Exception {
		String userToken = (String) mapUserData.get("authorization"); // not sure if this is it's name
		int itemID = (int) mapUserData.get("itemID");
		String query = "SELECT * FROM item WHERE id = " + itemID;
		Statement statement = connection.createStatement();
		ResultSet resultSet = statement.executeQuery(query);
		if (resultSet.next()) {
			int itemSize = resultSet.getInt("size");
			int itemStock = resultSet.getInt("stock");
			String itemColour = resultSet.getString("colour");
			double itemPrice = resultSet.getDouble("price");
			int itemProductID = resultSet.getInt("product_id");
			int itemCreatedAt = resultSet.getInt("created_at");
			int itemUpdatedAt = resultSet.getInt("updated_at");
			MongoDatabase mongoDB = Dispatcher.getDataBase("Amazon");
			
		}
		return null;
	}

}
