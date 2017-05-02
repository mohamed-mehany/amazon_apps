package elasticsearch;
import java.util.Map;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.common.unit.Fuzziness;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import java.util.ArrayList;

public class OrderSearch {
	private static final String TYPE_NAME = "order";
	
	public static ArrayList<Map<String, Object>> fuzzySearchByItemName(String itemName, String userId) throws Exception{
		QueryBuilder q = QueryBuilders.boolQuery()
		        .must(QueryBuilders.matchQuery("items.name", itemName).fuzziness(Fuzziness.AUTO))
		        .must(QueryBuilders.matchQuery("user_id", userId));
		SearchResponse s = ElasticSearch.getInstance().client.prepareSearch("amazon")
		        .setTypes(TYPE_NAME)
		        .setQuery(q)
		        .get();
		return ElasticSearch.searchResponseToArray(s);
	}
}
