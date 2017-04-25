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
		
		CallableStatement createBaningInfo = connection.prepareCall("{call create_banking_info(?, ?, ?, ?, ?)}");
		createBaningInfo.setString(1, cardNumber);
		createBaningInfo.setString(2, cardHolder);
		createBaningInfo.setString(3, provider);
		createBaningInfo.setString(4, type);
		createBaningInfo.setInt(5, userID);
		createBaningInfo.execute();
		return makeJSONResponseEnvelope(200, null, null);
	}

}
