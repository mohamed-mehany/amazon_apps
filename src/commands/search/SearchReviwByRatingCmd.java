package commands.search;

import java.sql.Connection;
import java.util.Map;

import commands.Command;
import elasticsearch.ReviewSearch;

public class SearchReviwByRatingCmd  extends Command implements Runnable {
	public StringBuffer execute(Connection connection, Map<String, Object> map)
			throws Exception {
		StringBuffer strbufResponseJSON = null;
		
		String value = (String) map.get("rating_value");
		
		strbufResponseJSON = serializeArrayMaptoJSON(ReviewSearch.searchAllReviewsByValue(value));
		
		return strbufResponseJSON;
	}
}
