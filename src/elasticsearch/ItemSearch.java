package elasticsearch;

import java.util.ArrayList;
import java.util.Map;

import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.common.unit.Fuzziness;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;

public class ItemSearch {
	private static final String TYPE_NAME = "item";
	
	public static ArrayList<Map<String, Object>> searchItemsByName(String query, String sort_by, String order) throws Exception {
		QueryBuilder mainQuery;
		if(query.equals("*"))
			mainQuery = QueryBuilders.matchAllQuery();
		else
			mainQuery = QueryBuilders.matchQuery("name", query).fuzziness(Fuzziness.AUTO);
		
		SortBuilder<?> sort = null;
		
		if(!sort_by.equals("")) {
			sort = SortBuilders.fieldSort(sort_by);
			if(order.equals("asc"))
				sort.order(SortOrder.ASC);
			else
				sort.order(SortOrder.DESC);
		}
		
		QueryBuilder q = QueryBuilders.boolQuery()
		        .should(mainQuery)
		        .should(QueryBuilders.matchQuery("product_name", query).fuzziness(Fuzziness.AUTO));
		SearchRequestBuilder s = ElasticSearch.getInstance().client.prepareSearch("amazon")
		        .setTypes(TYPE_NAME)
		        .setQuery(q);
		if(sort != null) s.addSort(sort);
		
		return ElasticSearch.searchResponseToArray(s.get());
	}
}
