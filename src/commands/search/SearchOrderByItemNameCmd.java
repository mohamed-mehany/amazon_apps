package commands.search;

import java.sql.Connection;
import java.util.Map;

import commands.Command;
import elasticsearch.ElasticSearch;
import elasticsearch.OrderSearch;



public class SearchOrderByItemNameCmd extends Command implements Runnable {
	public StringBuffer execute(Connection connection, Map<String, Object> map)
			throws Exception {
		StringBuffer strbufResult = null, strbufResponseJSON = null;
		String itemName = (String) map.get("name");
		String userId = (String) map.get("userId");
		for(Map<String, Object> i : OrderSearch.fuzzySearchByItemName(itemName, userId)) {
			if(strbufResponseJSON == null)
				strbufResponseJSON = serializeMaptoJSON(i, null);
			else
				strbufResponseJSON.append(serializeMaptoJSON(i, null));
		}
		strbufResult = makeJSONResponseEnvelope(0, null, strbufResponseJSON);
		return strbufResult;
	}
}
