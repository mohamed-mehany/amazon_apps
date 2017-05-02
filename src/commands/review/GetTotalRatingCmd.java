package commands.review;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.eclipsesource.json.JsonObject;
import commands.Command;

public class GetTotalRatingCmd extends Command implements Runnable {

	public StringBuffer execute(Connection connection, Map<String, Object> mapUserData)
			throws Exception {

		CallableStatement getRatings;
		StringBuffer strbufResult = new StringBuffer(""), strbufResponseJSON;
		int user_id, product_id;
		int nSQLResult;

		product_id =  (Integer) mapUserData.get("product_id");
		
		getRatings = connection.prepareCall("{call get_total_rating"+"(?,?)"+"}");
		getRatings.setInt(1, (product_id));
		getRatings.registerOutParameter(2, Types.INTEGER);

		getRatings.execute();
		int check = getRatings.getInt(2);
		strbufResult.append(changeToJSONFormat(check+""));
		getRatings.close();

		return strbufResult;

	}
}
