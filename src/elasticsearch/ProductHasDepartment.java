package elasticsearch;

import java.util.ArrayList;
import java.util.Map;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.common.unit.Fuzziness;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

public class ProductHasDepartment {
	private static final String TYPE_NAME = "product_has_department";

	public static ArrayList<Map<String, Object>> searchProducts(String query, String departmentId) throws Exception{
		QueryBuilder q = QueryBuilders.boolQuery()
		        .should(QueryBuilders.matchQuery("product_name", query).fuzziness(Fuzziness.AUTO))
		        .must(QueryBuilders.matchQuery("department_id", departmentId));
		SearchResponse s = ElasticSearch.getInstance().client.prepareSearch("amazon")
		        .setTypes(TYPE_NAME)
		        .setQuery(q)
		        .get();
		return ElasticSearch.searchResponseToArray(s);
	}
}
