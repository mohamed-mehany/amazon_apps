package commands.search;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.common.unit.Fuzziness;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

import commands.Command;
import elasticsearch.ElasticSearch;
import elasticsearch.OrderSearch;
import elasticsearch.ReviewSearch;



public class SearchOrderByItemNameCmd extends Command implements Runnable {
	public StringBuffer execute(Connection connection, Map<String, Object> map)
			throws Exception {
		StringBuffer strbufResult = null, strbufResponseJSON = null;
		String itemName = (String) map.get("name");
		String userId = (String) map.get("userId");
		
		strbufResponseJSON = serializeArrayMaptoJSON(OrderSearch.fuzzySearchByItemName(itemName, userId));

		strbufResult = makeJSONResponseEnvelope(0, null, strbufResponseJSON);
		return strbufResult;
	}
	
//	public static void main(String [] args) throws Exception {
//		ElasticSearch.loadConf("elasticsearch_ebrahim-elgaml", "localhost", 9300);
//		System.out.println(OrderSearch.fuzzySearchByItemName("item", "2"));
//		Map<String, Object> map = new HashMap<String, Object>();
//		map.put("id", "1");
//		map.put("product_name", "mobile_samsung");
//		map.put("userId", "2");
//		map.put("productId", "1");
//		map.put("review", "w7sh gedan");
//		map.put("value", "2");
//		ElasticSearch.write("review", map);
//		
//		Map<String, Object> map2 = new HashMap<String, Object>();
//		map2.put("id", "2");
//		map2.put("product_name", "mobile_lenovo");
//		map2.put("userId", "2");
//		map2.put("productId", "2");
//		map2.put("review", "w7sh bs msh gedan");
//		map2.put("value", "4");
//		ElasticSearch.write("review", map2);
//		QueryBuilder q = QueryBuilders.boolQuery()
//		        .should(QueryBuilders.matchQuery("product_name", "w7sh").fuzziness(Fuzziness.AUTO))
//		        .should(QueryBuilders.matchQuery("review", "w7sh").fuzziness(Fuzziness.AUTO))
//		        .must(QueryBuilders.matchQuery("userId", 2));
//		SearchResponse s = ElasticSearch.getInstance().client.prepareSearch("amazon")
//		        .setTypes("review")
//		        .setQuery(q)
//		        .get();
//		System.out.println(s);
//	}
}
