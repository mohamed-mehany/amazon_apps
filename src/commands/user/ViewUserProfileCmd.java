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

public class ViewUserProfileCmd extends Command implements Runnable {

	public StringBuffer execute(Connection connection, Map<String, Object> mapUserData) throws Exception {
		StringBuffer strbufResult= new StringBuffer("");
		// String strbufResult;
		PreparedStatement sqlProc;
		String strEmail, strPassword, strName, strAddress, strDateOfBirth, strToken;
		Integer id = (Integer) mapUserData.get("user_id"), gender;

		if (id == null)
			return null;

		sqlProc = connection.prepareStatement("{call view_user(?)}");
		sqlProc.setInt(1, id);
		ResultSet rs = sqlProc.executeQuery();
		strbufResult.append(changeToJSONFormat(rs));
		rs.close();
		sqlProc.close();
		return strbufResult;
	}
}
