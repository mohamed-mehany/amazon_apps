package commands.user;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.util.Map;
import commands.Command;


public class AddUserSimpleCmd extends Command implements Runnable {

	public StringBuffer execute(Connection connection, Map<String, Object> mapUserData)
			throws Exception {
		StringBuffer strbufResult;
		CallableStatement sqlProc;
		String strEmail, strPassword, strFirstName, strLastName, strAddress, strDate,
				strToken;
		strEmail = (String) mapUserData.get("email");
		strPassword = (String) mapUserData.get("password");
		strFirstName = (String) mapUserData.get("firstName");
		strLastName = (String) mapUserData.get("lastName");
		strAddress = (String) mapUserData.get("address");
		strDate = (String) mapUserData.get("date");
		strToken = (String) mapUserData.get("token");
		int gender = (Integer) mapUserData.get("gender");
		if (strEmail == null || strEmail.trim().length() == 0 || strPassword == null
				|| strPassword.trim().length() == 0 || strFirstName == null
				|| strFirstName.trim().length() == 0 || strLastName == null
				|| strLastName.trim().length() == 0)
			return null;

		if (!EmailVerifier.verify(strEmail))
			return null;
		sqlProc = connection.prepareCall("{call create_user(?, ?, ?, ?, ?, ?, ?)}");

		// sqlProc.registerOutParameter(1, Types.INTEGER);
		sqlProc.setString(1, strFirstName);
		sqlProc.setString(2, strEmail);
		sqlProc.setString(3, strPassword);
		sqlProc.setString(4, strAddress);
		sqlProc.setString(5, strDate);
		sqlProc.setString(6, strToken);
		sqlProc.setInt(7, gender);

		sqlProc.execute();
		strbufResult = makeJSONResponseEnvelope(200, null, null);
		sqlProc.close();

		return strbufResult;
	}
}
