package commands.search;

import java.sql.Connection;
import java.util.Map;

import commands.Command;
import elasticsearch.DepartmentSearch;

public class SearchDepartmentCmd extends Command implements Runnable {
	public StringBuffer execute(Connection connection, Map<String, Object> map)
			throws Exception {
		StringBuffer strbufResult = null, strbufResponseJSON = null;
		String query = (String) map.get("query");
		
		strbufResponseJSON = serializeArrayMaptoJSON(DepartmentSearch.searchProducts(query));
		
		strbufResult = makeJSONResponseEnvelope(0, null, strbufResponseJSON);
		return strbufResult;
	}
}
