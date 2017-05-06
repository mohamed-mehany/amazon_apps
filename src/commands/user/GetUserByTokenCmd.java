package commands.user;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Types;
import java.util.Map;

import java.util.HashMap;
import java.util.UUID;

import commands.Command;
import org.boon.primitive.Int;

//name , email, password, address, date_of_birth, token, gender

public class GetUserByTokenCmd extends Command implements Runnable {

	public StringBuffer execute(Connection connection, Map<String, Object> mapUserData) throws Exception {
		StringBuffer strbufResult= new StringBuffer("");
		PreparedStatement sqlProc;
		String  strToken = (String) mapUserData.get("token");

		if (strToken == null)
			return null;

		sqlProc = connection.prepareStatement("{call get_user(?)}");
		sqlProc.setString(1, strToken);
		ResultSet rs = sqlProc.executeQuery();
		strbufResult.append(changeToJSONFormat(rs));
		rs.close();
		sqlProc.close();
		return strbufResult;
	}
}
