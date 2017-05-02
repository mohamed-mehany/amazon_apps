package commands.cart;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.util.Map;

import commands.Command;

public class CreateBankingInfoCmd extends Command implements Runnable {

	@Override
	public StringBuffer execute(Connection connection, Map<String, Object> mapUserData) throws Exception {
		String cardNumber = (String) mapUserData.get("cardNumber");
		String cardHolder = (String) mapUserData.get("cardHolder");
		String provider = (String) mapUserData.get("provider");
		String type = (String) mapUserData.get("type");
		int userID = (int) mapUserData.get("userID");
		
		CallableStatement createBankingInfo = connection.prepareCall("{call create_banking_info(?, ?, ?, ?, ?)}");
		createBankingInfo.setString(1, cardNumber);
		createBankingInfo.setString(2, cardHolder);
		createBankingInfo.setString(3, provider);
		createBankingInfo.setString(4, type);
		createBankingInfo.setInt(5, userID);
		createBankingInfo.execute();
		return new StringBuffer("[").append("{\"res\":\"ok\"}").append("]");
	}

}
