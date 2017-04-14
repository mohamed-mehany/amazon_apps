package commands.user;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Types;
import java.util.Map;

import commands.Command;

//name , email, password, address, date_of_birth, token, gender

public class CreateVendorCmd extends Command implements Runnable {
	
	public StringBuffer execute(Connection connection, Map<String, Object> mapUserData)
			throws Exception {
		StringBuffer strbufResult;
		//String strbufResult;
		PreparedStatement sqlProc;
		String strEmail, strPassword, strName, strAddress, strDateOfBirth, strToken;
		Integer gender;
		strEmail = (String) mapUserData.get("email");
		strPassword = (String) mapUserData.get("password");
		strName = (String) mapUserData.get("name");
		strAddress = (String) mapUserData.get("address");
		strDateOfBirth = (String) mapUserData.get("date_of_birth");
		strToken = (String) mapUserData.get("token");
		gender = (Integer) mapUserData.get("strGender");


		if (strEmail == null || strEmail.trim().length() == 0 || strPassword == null
				|| strPassword.trim().length() == 0 || strName == null
				|| strName.trim().length() == 0 || strToken == null
				|| strToken.trim().length() == 0|| gender == null)
			return null;

		if (!EmailVerifier.verify(strEmail))
			return null;

		sqlProc = connection.prepareStatement("{call create_vendor(?, ?, ?, ?, ?, ?, ?)}");
		sqlProc.setString(1, strName);
		sqlProc.setString(2, strEmail);
		sqlProc.setString(3, strPassword);
		sqlProc.setString(4, strAddress);
		sqlProc.setString(5, strDateOfBirth);
		sqlProc.setString(6, strToken);
		sqlProc.setInt(7, gender);
		sqlProc.executeQuery();
		 
		//strbufResult =  String.valueOf(sqlProc.execute());
		strbufResult = makeJSONResponseEnvelope(((CallableStatement) sqlProc).getInt(1), null, null);
		sqlProc.close();

		return strbufResult;
	}
}
