package commands.user;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Types;
import java.util.Map;

import com.eclipsesource.json.JsonObject;

import commands.Command;

//name , email, password, address, date_of_birth, token, gender

public class ViewUserProfileCmd extends Command implements Runnable {

	public StringBuffer execute(Connection connection, Map<String, Object> mapUserData) throws Exception {
		StringBuffer strbufResult= new StringBuffer();
		// String strbufResult;
		PreparedStatement sqlProc;
		String strEmail, strPassword, strName, strAddress, strDateOfBirth, strToken;
		Integer id = (Integer) mapUserData.get("user_id"), gender;

		if (id == null)
			return null;

		sqlProc = connection.prepareStatement("{call view_user(?)}");
		sqlProc.setInt(1, id);
		ResultSet rs = sqlProc.executeQuery();
		while (rs.next()) {
			JsonObject output = new JsonObject();
			strName = rs.getString("name");
			strEmail = rs.getString("email");
			strAddress = rs.getString("address");
			strDateOfBirth = rs.getString("date_of_birth");
			gender = rs.getInt("gender");
			output.add("name", strName);
			output.add("email", strEmail);
			output.add("address", strAddress);
			output.add("date_of_birth", strDateOfBirth);
			output.add("gender",gender);
			System.out.println("name\t: " + rs.getString("name"));
			System.out.println("email\t: " + rs.getString("email"));
			System.out.println("address\t: " + rs.getString("address"));
			System.out.println();
			strbufResult.append(output);
		}
		rs.close();

		// strbufResult = String.valueOf(sqlProc.execute());
		//strbufResult = makeJSONResponseEnvelope(200, null, null);
		sqlProc.close();

		return strbufResult;
	}
}
