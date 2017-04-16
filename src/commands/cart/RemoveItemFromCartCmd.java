package commands.cart;

import java.sql.Connection;
import java.util.Map;

import commands.Command;

public class RemoveItemFromCartCmd extends Command implements Runnable {

	@Override
	public StringBuffer execute(Connection connection, Map<String, Object> mapUserData) throws Exception {
		int userID = (int) mapUserData.get("userID");
		int itemID = (int) mapUserData.get("itemID");
		
		return null;
	}

}
