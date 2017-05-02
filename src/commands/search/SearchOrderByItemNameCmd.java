package commands.search;

import java.sql.Connection;
import java.util.Map;

import commands.Command;
import elasticsearch.OrderSearch;



public class SearchOrderByItemNameCmd extends Command implements Runnable {
	public StringBuffer execute(Connection connection, Map<String, Object> map)
			throws Exception {
		StringBuffer strbufResult = null, strbufResponseJSON = null;
		String itemName = (String) map.get("name");
		String userId = (String) map.get("userId");
		
		strbufResponseJSON = serializeArrayMaptoJSON(OrderSearch.fuzzySearchByItemName(itemName, userId));

		strbufResult = makeJSONResponseEnvelope(0, null, strbufResponseJSON);
		return strbufResult;
	}
}
