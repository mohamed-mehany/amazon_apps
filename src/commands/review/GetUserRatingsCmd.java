package commands.review;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.eclipsesource.json.JsonObject;
import commands.Command;

public class GetUserRatingsCmd extends Command implements Runnable {
	public StringBuffer execute(Connection connection, Map<String, Object> mapUserData)
			throws Exception {

		CallableStatement getRatings;
		StringBuffer strbufResult = new StringBuffer("");
		int user_id;

		user_id = (Integer) mapUserData.get("user_id");
		
		getRatings = connection.prepareCall("{call get_user_reviews"+"(?)"+"}");
		getRatings.setInt(1, user_id);
		ResultSet r = getRatings.executeQuery();
		strbufResult.append(changeToJSONFormat(r));
		getRatings.close();

		return strbufResult;

	}
}
