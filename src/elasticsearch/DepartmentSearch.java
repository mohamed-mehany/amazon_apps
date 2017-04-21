package elasticsearch;

import java.util.ArrayList;
import java.util.Map;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.common.unit.Fuzziness;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

public class DepartmentSearch {
	private static final String TYPE_NAME = "department";
	
	public static ArrayList<Map<String, Object>> searchProducts(String query) throws Exception{
		QueryBuilder q = null;
		if(query.equals("*"))
			q = QueryBuilders.boolQuery().should(QueryBuilders.matchAllQuery());
		else
			q = QueryBuilders.boolQuery()
	        .should(QueryBuilders.matchQuery("name", query).fuzziness(Fuzziness.AUTO));
		
		SearchResponse s = ElasticSearch.getInstance().client.prepareSearch("amazon")
		        .setTypes(TYPE_NAME)
		        .setQuery(q)
		        .get();
		return ElasticSearch.searchResponseToArray(s);
	}
}
