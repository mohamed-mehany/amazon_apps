package commands.review;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import commands.Command;

public class GetProductRatingsCmd extends Command implements Runnable {

	public StringBuffer execute(Connection connection, Map<String, Object> mapUserData)
			throws Exception {
		CallableStatement getRatings;
		StringBuffer strbufResult = new StringBuffer("");
		int product_id;
		System.out.println(mapUserData.toString());
		product_id = (Integer) mapUserData.get("product_id");
		getRatings = connection.prepareCall("{call get_products_reviews" + "(?)" + "}");
		getRatings.setInt(1, product_id);
		ResultSet r = getRatings.executeQuery();
		strbufResult.append(changeToJSONFormat(r));
		getRatings.close();

		return strbufResult;

	}
}
