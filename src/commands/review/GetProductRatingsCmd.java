package commands.review;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.eclipsesource.json.JsonObject;
import commands.Command;

public class GetProductRatingsCmd extends Command implements Runnable {

	public StringBuffer execute(Connection connection, Map<String, Object> mapUserData)
			throws Exception {

		CallableStatement getRatings;
		StringBuffer strbufResult = new StringBuffer(""), strbufResponseJSON;
		int product_id;
		int nSQLResult;

		product_id = (Integer) mapUserData.get("product_id");

		getRatings = connection.prepareCall("{call get_products_reviews" + "(?)" + "}");
		getRatings.setInt(1, product_id);
//		getRatings.execute();
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

		r.close();

		getRatings.close();

		return strbufResult;

	}
}
