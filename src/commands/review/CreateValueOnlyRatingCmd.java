package commands.review;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Types;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import commands.Command;

public class CreateValueOnlyRatingCmd extends Command implements Runnable {

	public StringBuffer execute(Connection connection, Map<String, Object> mapUserData)
			throws Exception {

		CallableStatement createReview;
		StringBuffer strbufResult = null, strbufResponseJSON;
		int user_id, product_id, value;
		String review, strClientIP;
		int nSQLResult;

		product_id = ((Integer) mapUserData.get("product_id"));
		value = (Integer) mapUserData.get("value");
		user_id = (Integer) mapUserData.get("user_id");
		
		createReview = connection.prepareCall("{call rate_product"+"(?,?,?)"+"}");
		createReview.setInt(1,user_id);
		createReview.setInt(2,product_id);
		createReview.setInt(3,value);
		createReview.execute();

		strbufResult = makeJSONResponseEnvelope(200, null, null);
		createReview.close();

		return strbufResult;
	}
}
