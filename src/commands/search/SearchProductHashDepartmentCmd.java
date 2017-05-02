package commands.search;

import java.sql.Connection;
import java.util.Map;

import commands.Command;
import elasticsearch.ProductHasDepartment;

public class SearchProductHashDepartmentCmd extends Command implements Runnable {
	public StringBuffer execute(Connection connection, Map<String, Object> map)
			throws Exception {
		StringBuffer strbufResult = null, strbufResponseJSON = null;
		String productName = (String) map.get("product_name");
		String departmentId = (String) map.get("department_id");
		
		strbufResponseJSON = serializeArrayMaptoJSON(ProductHasDepartment.searchProducts(productName, departmentId));
		
		strbufResult = makeJSONResponseEnvelope(0, null, strbufResponseJSON);
		return strbufResult;
	}
}
