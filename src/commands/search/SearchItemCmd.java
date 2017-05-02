package commands.search;

import java.sql.Connection;
import java.util.Map;

import commands.Command;
import elasticsearch.ItemSearch;

public class SearchItemCmd  extends Command implements Runnable {
	public StringBuffer execute(Connection connection, Map<String, Object> map)
			throws Exception {
		StringBuffer strbufResponseJSON = null;
		
		String query = (String) map.get("query");
		String sort_by = (String) map.get("sort_by");
		String order = (String) map.get("order");
		
		strbufResponseJSON = serializeArrayMaptoJSON(ItemSearch.searchItemsByName(query, sort_by, order));
		
		return strbufResponseJSON;
	}
}
