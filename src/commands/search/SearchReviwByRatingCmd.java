package commands.search;

import java.sql.Connection;
import java.util.Map;

import commands.Command;
import elasticsearch.ReviewSearch;

public class SearchReviwByRatingCmd  extends Command implements Runnable {
	public StringBuffer execute(Connection connection, Map<String, Object> map)
			throws Exception {
		StringBuffer strbufResult = null, strbufResponseJSON = null;
		
		String value = (String) map.get("rating_value");
		
		strbufResponseJSON = serializeArrayMaptoJSON(ReviewSearch.searchAllReviewsByValue(value));
		
		strbufResult = makeJSONResponseEnvelope(0, null, strbufResponseJSON);
		return strbufResult;
	}
}
