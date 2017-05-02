package commands.user;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Types;
import java.util.Map;

import java.util.HashMap;
import java.util.UUID;

import commands.Command;
import org.boon.primitive.Int;

//name , email,address, date_of_birth, token, gender

public class EditProfileInfoCmd extends Command implements Runnable {

	public StringBuffer execute(Connection connection, Map<String, Object> mapUserData) throws Exception {
		StringBuffer strbufResult= new StringBuffer("");
		CallableStatement sqlProc;
		String strEmail, strName, strAddress, strDateOfBirth, strUserId;
		int id;
		id = (Integer) mapUserData.get("user_id");
		strEmail = (String) mapUserData.get("email");
		strName = (String) mapUserData.get("name");
		strAddress = (String) mapUserData.get("address");
		strDateOfBirth = (String) mapUserData.get("date_of_birth");

		if (strEmail == null || strEmail.trim().length() == 0 || strName == null || strName.trim().length() == 0)
			return null;

		if (!EmailVerifier.verify(strEmail))
			return null;

		sqlProc = connection.prepareCall("{call edit_profile_information(?, ?, ?, ?, ?)}");
		sqlProc.setInt(1, id);
		sqlProc.setString(2, strName);
		sqlProc.setString(3, strEmail);
		sqlProc.setString(4, strAddress);
		sqlProc.setString(5, strDateOfBirth);
		Boolean executed = sqlProc.execute();
		strbufResult.append(changeToJSONFormat(executed + ""));
		// strbufResult = makeJSONResponseEnvelope(200, null, null);
		sqlProc.close();

		return strbufResult;
	}
}
