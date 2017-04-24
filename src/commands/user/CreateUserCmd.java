package commands.user;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Types;
import java.util.Map;
import java.util.HashMap;
import java.util.UUID;

import commands.Command;
import org.boon.primitive.Int;


//name , email, password, address, date_of_birth, token, gender

public class CreateUserCmd extends Command implements Runnable {
	
	public StringBuffer execute(Connection connection, Map<String, Object> mapUserData)
			throws Exception {
		StringBuffer strbufResult= new StringBuffer("");
		//String strbufResult;
		PreparedStatement sqlProc;
		String strEmail, strPassword, strName, strAddress, strDateOfBirth, strToken;
		int gender;
		strEmail = (String) mapUserData.get("email");
		strPassword = (String) mapUserData.get("password");
		strName = (String) mapUserData.get("name");
		strAddress = (String) mapUserData.get("address");
		strDateOfBirth = (String) mapUserData.get("date_of_birth");
		strToken = (String) mapUserData.get("token");
		gender = (Integer) mapUserData.get("gender");


		if (strEmail == null || strEmail.trim().length() == 0 || strPassword == null
				|| strPassword.trim().length() == 0 || strName == null
				|| strName.trim().length() == 0 )
			return null;
		

		if (!EmailVerifier.verify(strEmail))
			return null;

		sqlProc = connection.prepareCall("{call create_user(?, ?, ?, ?, ?, ?, ?)}");
		sqlProc.setString(1, strName);
		sqlProc.setString(2, strEmail);
		sqlProc.setString(3, strPassword);
		sqlProc.setString(4, strAddress);
		sqlProc.setString(5, strDateOfBirth);
		sqlProc.setString(6, strToken);
		sqlProc.setInt(7, gender);
		Boolean executed = sqlProc.execute();
		strbufResult.append(changeToJSONFormat(executed+""));
		//strbufResult = makeJSONResponseEnvelope(200, null, null);
		sqlProc.close();

		return strbufResult;
	}
}
