package commands.review;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Types;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import commands.Command;
import org.boon.primitive.Int;

public class CreateRatingCmd extends Command implements Runnable {

	public StringBuffer execute(Connection connection, Map<String, Object> mapUserData)
			throws Exception {

		CallableStatement createReview;
		StringBuffer strbufResult = new StringBuffer("");
		int user_id, product_id, value;
		String review;
		
		product_id = ((Integer) mapUserData.get("product_id"));
		value = ((Integer) mapUserData.get("value"));
		review = (String) mapUserData.get("review");
		user_id = (Integer) mapUserData.get("user_id");
		
		createReview = connection.prepareCall("{call create_review"+"(?,?,?,?)"+"}");
		createReview.setInt(1, value);
		createReview.setInt(2, user_id);
		createReview.setInt(3, product_id);
		createReview.setString(4, review);
		Boolean executed = createReview.execute();
		strbufResult.append(changeToJSONFormat(executed+""));
		createReview.close();

		return strbufResult;

	}
}
