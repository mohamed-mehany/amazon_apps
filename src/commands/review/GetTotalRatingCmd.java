package commands.review;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Types;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import commands.Command;

public class GetTotalRatingCmd extends Command implements Runnable {

	public StringBuffer execute(Connection connection, Map<String, Object> mapUserData)
			throws Exception {

		CallableStatement getRatings;
		StringBuffer strbufResult = null, strbufResponseJSON;
		int user_id, product_id;
		int nSQLResult;

		product_id =  (Integer) mapUserData.get("product_id");
		
		getRatings = connection.prepareCall("{call get_total_rating"+"(?,?)"+"}");
		getRatings.setInt(1, (product_id));
		getRatings.registerOutParameter(2, Types.INTEGER);

		getRatings.execute();
//		System.out.println("?????");
		int check = getRatings.getInt(2);

//		System.out.println(check + " ~~~");

		strbufResult = makeJSONResponseEnvelope(3, null, null);
		getRatings.close();

		return strbufResult;

	}
}
