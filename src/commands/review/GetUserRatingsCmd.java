package commands.review;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Types;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import commands.Command;

public class GetUserRatingsCmd extends Command implements Runnable {
	public StringBuffer execute(Connection connection, Map<String, Object> mapUserData)
			throws Exception {

		CallableStatement getRatings;
		StringBuffer strbufResult = null, strbufResponseJSON;
		String user_id;
		int nSQLResult;

		user_id = (String) mapUserData.get("user_id");
		
		getRatings = connection.prepareCall("{call get_user_reviews"+"(?)"+"}");
		getRatings.setString(1, user_id);
		ResultSet r = getRatings.executeQuery();

		System.out.println(r.toString());
		strbufResult = makeJSONResponseEnvelope(getRatings.getInt(1), null, null);
		getRatings.close();

		return strbufResult;

	}
}
