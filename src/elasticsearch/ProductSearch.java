package elasticsearch;

import java.util.ArrayList;
import java.util.Map;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.common.unit.Fuzziness;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

public class ProductSearch {
	private static final String TYPE_NAME = "product";
	
	public static ArrayList<Map<String, Object>> searchProducts(String query) throws Exception{
		QueryBuilder q = QueryBuilders.boolQuery()
		        .should(QueryBuilders.matchQuery("name", query).fuzziness(Fuzziness.AUTO))
		        .should(QueryBuilders.matchQuery("vendor_name", query).fuzziness(Fuzziness.AUTO))
		        .should(QueryBuilders.matchQuery("description", query).fuzziness(Fuzziness.AUTO));
		SearchResponse s = ElasticSearch.getInstance().client.prepareSearch("amazon")
		        .setTypes(TYPE_NAME)
		        .setQuery(q)
		        .get();
		return ElasticSearch.searchResponseToArray(s);
	}
}
