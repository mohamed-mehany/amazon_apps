package commands.search;

import java.sql.Connection;
import java.util.Map;

import commands.Command;
import elasticsearch.OrderSearch;



public class SearchOrderByItemNameCmd extends Command implements Runnable {
	public StringBuffer execute(Connection connection, Map<String, Object> map)
			throws Exception {
		StringBuffer strbufResponseJSON = null;
		String itemName = (String) map.get("name");
		String userId = (String) map.get("userId");
		
		strbufResponseJSON = serializeArrayMaptoJSON(OrderSearch.fuzzySearchByItemName(itemName, userId));
		
		return strbufResponseJSON;
	}
}
