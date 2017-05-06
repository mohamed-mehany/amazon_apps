package commands.search;

import java.sql.Connection;
import java.util.Map;

import commands.Command;
import elasticsearch.ReviewSearch;

public class SearchReviewsCmd extends Command implements Runnable {
	public StringBuffer execute(Connection connection, Map<String, Object> map)
			throws Exception {
		StringBuffer strbufResponseJSON = null;
		String query = (String) map.get("query");
		String userId = (String) map.get("userId");
		
		strbufResponseJSON = serializeArrayMaptoJSON(ReviewSearch.searchUserReviews(query, userId));
		
		return strbufResponseJSON;
	}
}
