package commands.review;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import commands.Command;

public class GetProductRatingsCmd extends Command implements Runnable {

	public StringBuffer execute(Connection connection, Map<String, Object> mapUserData)
			throws Exception {

		CallableStatement getRatings;
		StringBuffer strbufResult = null, strbufResponseJSON;
		int product_id;
		int nSQLResult;

		product_id = (Integer) mapUserData.get("product_id");

		getRatings = connection.prepareCall("{call get_products_reviews" + "(?)" + "}");
		getRatings.setInt(1, product_id);
		ResultSet r = getRatings.executeQuery();
		ResultSetMetaData rsmd = r.getMetaData();

		int columnsNumber = rsmd.getColumnCount();
		while (r.next()) {
			for (int i = 1; i <= columnsNumber; i++) {
				if (i > 1) System.out.print(",  ");
				String columnValue = r.getString(i);
				System.out.print(columnValue + " " + rsmd.getColumnName(i));
			}
			System.out.println("");
		}
		strbufResult = makeJSONResponseEnvelope(200, null, null);

		getRatings.close();

		return strbufResult;

	}
}
