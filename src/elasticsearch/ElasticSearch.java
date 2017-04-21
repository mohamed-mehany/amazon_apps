package elasticsearch;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Map;

import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

import commands.Dispatcher;
import services.Services;

public class ElasticSearch {
	public TransportClient client;
	final static String INDEX_NAME = "amazon";
	static String CLUSTER_NAME;
	static String HOST;
	static int PORT;
	private static ElasticSearch elasticSearchClient;
	private ElasticSearch() throws UnknownHostException {
		Settings settings = Settings.builder()
		        .put("cluster.name", CLUSTER_NAME).build();
		client = new PreBuiltTransportClient(settings)
		        .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(HOST), PORT));
	}
	
	public static void loadConf(String clusterName, String host, int port) {
		CLUSTER_NAME = clusterName;
		HOST = host;
		PORT = port;
	}

	public static ElasticSearch getInstance() throws UnknownHostException {
		if(elasticSearchClient != null) return elasticSearchClient;
		elasticSearchClient = new ElasticSearch();
		return elasticSearchClient;
	}
	
	private static XContentBuilder getBuilderFromMap(Map<String, Object> map) throws IOException {
		XContentBuilder builder = jsonBuilder().startObject();
		for(String key: map.keySet()) {
			builder.field(key, map.get(key));
		}
		builder.endObject();
		return builder;
	}
	
	public static boolean write(String type, Map<String, Object> map) throws Exception {
		String id = (String) map.get("id");
		if(id == null) throw new Exception("No ID Found");
		map.remove("id");
		XContentBuilder builder = getBuilderFromMap(map);
		IndexResponse response = getInstance().client.prepareIndex(INDEX_NAME, type, id)
		        .setSource(builder)
		        .get();
		return response.status().getStatus() == 200;
	}
	
	public static GetResponse getById(String type, String id) {
		GetResponse response = elasticSearchClient.client.prepareGet(INDEX_NAME, type, id).get();
		return response;
	}
	
	public static ArrayList<Map<String, Object>> searchResponseToArray(SearchResponse s) {
		ArrayList<Map<String, Object>>  result = new ArrayList<Map<String, Object>> ();
		for(int i = 0; i < s.getHits().getTotalHits(); ++i)
			result.add(s.getHits().getAt(i).getSource());
		return result;
	}


}
