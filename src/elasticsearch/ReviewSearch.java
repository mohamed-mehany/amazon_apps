package elasticsearch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.common.unit.Fuzziness;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.filter.FilterAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms.Bucket;
import org.elasticsearch.search.aggregations.metrics.avg.Avg;
import org.elasticsearch.search.aggregations.support.ValueType;

public class ReviewSearch {
	private static final String TYPE_NAME = "review";
	
	public static ArrayList<Map<String, Object>> searchUserReviews(String query, String userId) throws Exception {
		QueryBuilder q = QueryBuilders.boolQuery()
		        .should(QueryBuilders.matchQuery("product_name", query).fuzziness(Fuzziness.AUTO))
		        .should(QueryBuilders.matchQuery("review", query).fuzziness(Fuzziness.AUTO))
		        .must(QueryBuilders.matchQuery("user_id", userId));
		SearchResponse s = ElasticSearch.getInstance().client.prepareSearch("amazon")
		        .setTypes(TYPE_NAME)
		        .setQuery(q)
		        .get();
		return ElasticSearch.searchResponseToArray(s);
	}
	
	public static ArrayList<Map<String, Object>> searchAllReviewsByValue(String value) throws Exception {
		SearchResponse s = ElasticSearch.getInstance().client.prepareSearch("amazon")
		        .setTypes(TYPE_NAME)
		        .setQuery(QueryBuilders.matchAllQuery())
		        .addAggregation(
		        		AggregationBuilders.terms("by_product_name").valueType(ValueType.STRING).field("product_name")
		        		.subAggregation(AggregationBuilders.avg("avg_rating").field("value"))
		        		.order(Terms.Order.aggregation("avg_rating", false))
		        		)
		        .get();
		
		Terms agg = s.getAggregations().get("by_product_name");
		ArrayList<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		double min = Double.parseDouble(value);
		for (Terms.Bucket entry : agg.getBuckets()) {
		    Avg avg = entry.getAggregations().get("avg_rating");
		    if(avg.getValue() >= min) {
		    	Map<String, Object> m = new HashMap<String, Object>();
		    	m.put("product_name", entry.getKey());
		    	m.put("rating", avg.getValue());
		    	result.add(m);
		    }
		}
		return result;
	}
}
