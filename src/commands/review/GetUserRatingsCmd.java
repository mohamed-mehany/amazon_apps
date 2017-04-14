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
		StringBuffer strbufResult = new StringBuffer(""), strbufResponseJSON;
		int user_id;
		int nSQLResult;

		user_id = (Integer) mapUserData.get("user_id");
		
		getRatings = connection.prepareCall("{call get_user_reviews"+"(?)"+"}");
		getRatings.setInt(1, user_id);
		ResultSet r = getRatings.executeQuery();

		ResultSetMetaData rsmd = r.getMetaData();

		int columnsNumber = rsmd.getColumnCount();

		while (r.next()) {
			JsonObject o = new JsonObject();

			for (int i = 1; i <= columnsNumber; i++) {
				o.add(rsmd.getColumnName(i), r.getString(i));
			}

			strbufResult.append(o);
		}
		System.out.println(r.toString());
		r.close();
//		strbufResult = makeJSONResponseEnvelope(getRatings.getInt(1), null, null);
		getRatings.close();

		return strbufResult;

	}
}
