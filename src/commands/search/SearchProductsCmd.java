package commands.search;

import java.sql.Connection;
import java.util.Map;

import commands.Command;
import elasticsearch.ProductSearch;

public class SearchProductsCmd extends Command implements Runnable {
	public StringBuffer execute(Connection connection, Map<String, Object> map)
			throws Exception {
		StringBuffer strbufResponseJSON = null;
		String query = (String) map.get("query");
		
		strbufResponseJSON = serializeArrayMaptoJSON(ProductSearch.searchProducts(query));
		
		return strbufResponseJSON;
	}
}
