package elasticsearch;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Seeds {
	public static void main(String [] args) throws Exception {
		System.out.println("writing..........");
		
		ElasticSearch.loadConf("elasticsearch_ebrahim-elgaml", "localhost", 9300);
		
		///// INSRT ORDER
		Map<String, Object> item1 = new HashMap<String, Object>();
		Map<String, Object> item2 = new HashMap<String, Object>();
		Map<String, Object> item3 = new HashMap<String, Object>();
		Map<String, Object> item4 = new HashMap<String, Object>();
		
		item1.put("id", "1");item1.put("name", "item1");item1.put("product_id", "1");item1.put("price", 5);
		item1.put("product_name", "mobile");item1.put("created_at", new Date(2017, 3, 1));
		
		item2.put("id", "2");item2.put("name", "item2");item2.put("product_id", "2");item2.put("price", 10);
		item2.put("product_name", "mobile");item1.put("created_at", new Date(2017, 2, 1));
		
		item3.put("id", "3");item3.put("name", "item3");item3.put("product_id", "3");item3.put("price", 15);
		item3.put("product_name", "mobile");item1.put("created_at", new Date(2017, 1, 1));
		
		item4.put("id", "4");item4.put("name", "item4");item4.put("product_id", "4");item4.put("price", 20);
		item4.put("product_name", "mobile");item1.put("created_at", new Date(2016, 3, 1));
		
		ArrayList<Map<String, Object>> l1 = new ArrayList<Map<String, Object>>();
		ArrayList<Map<String, Object>> l2 = new ArrayList<Map<String, Object>>();
		l1.add(item1);l1.add(item2);
		l2.add(item3);l2.add(item4);
		
		Map<String, Object> order1 = new HashMap<String, Object>();
		Map<String, Object> order2 = new HashMap<String, Object>();
		order1.put("id", "1");order1.put("user_id", "2");order1.put("items", l1);
		order2.put("id", "2");order2.put("user_id", "2");order2.put("items", l2);
		ElasticSearch.write("order", order1);ElasticSearch.write("order", order2);
		
		///////// INSERT REVIEW
		Map<String, Object> review1 = new HashMap<String, Object>();
		Map<String, Object> review2 = new HashMap<String, Object>();
		
		review1.put("id", "1");review1.put("product_name", "mobile_samsung");review1.put("user_id", "2");
		review1.put("product_id", "1");review1.put("review", "w7sh gedan");review1.put("value", 2);
		
		review2.put("id", "2");review2.put("product_name", "mobile_lenovo");review2.put("user_id", "2");
		review2.put("product_id", "2");review2.put("review", "w7sh bs msh gedan");review2.put("value", 4);
		
		ElasticSearch.write("review", review1);ElasticSearch.write("review", review2);
		
		
		////// INSERT VENDOR
		Map<String, Object> vendor1 = new HashMap<String, Object>();
		Map<String, Object> vendor2 = new HashMap<String, Object>();
		vendor1.put("id", "1");vendor1.put("user_id", "1");vendor1.put("user_name", "user1");
		vendor2.put("id", "1");vendor2.put("user_id", "2");vendor2.put("user_name", "user1");
		
		ElasticSearch.write("vendor", vendor1);ElasticSearch.write("vendor", vendor2);
		
		////// INSERT PRODUCT
		Map<String, Object> product1 = new HashMap<String, Object>();
		Map<String, Object> product2 = new HashMap<String, Object>();
		
		product1.put("id", "1");product1.put("vendor_id", "1");product1.put("vendor_name", "user1");
		product1.put("name", "mobile_samsung");product1.put("description", "mobile by3l2");
		
		product2.put("id", "2");product2.put("vendor_id", "1");product2.put("vendor_name", "user2");
		product2.put("name", "mobile_lenovo");product2.put("description", "mobile byfsl");
		
		ElasticSearch.write("product", product1);ElasticSearch.write("product", product2);
		
		////// INSERT DEPARTMENT
		Map<String, Object> d1 = new HashMap<String, Object>();
		Map<String, Object> d2 = new HashMap<String, Object>();
				
		d1.put("id", "1");d1.put("name", "apple");d1.put("description", "sells mobile phones for apple");
		d2.put("id", "2");d2.put("name", "samsung");d2.put("description", "sells mobile phones for samsung");
		
		ElasticSearch.write("department", d1);ElasticSearch.write("department", d2);
			
			
		////// INSERT PRODUCT_HAS_DEPARTMENT
		Map<String, Object> p_d1 = new HashMap<String, Object>();
		Map<String, Object> p_d2 = new HashMap<String, Object>();
			
		p_d1.put("id", "1");p_d1.put("product_id", "1");p_d1.put("department_id", "1");p_d1.put("product_name", "mobile_leneovo");
		p_d2.put("id", "2");p_d2.put("product_id", "2");p_d2.put("department_id", "1");p_d2.put("product_name", "mobile_samsung");
		
		ElasticSearch.write("product_has_department", p_d1);ElasticSearch.write("product_has_department", p_d2);
		
		////// INSERT ITEM
		ElasticSearch.write("item", item1);ElasticSearch.write("item", item2);
		ElasticSearch.write("item", item3);ElasticSearch.write("item", item4);
		
		Thread.sleep(2000);
		
		////// SEARCH
		System.out.println("searching..........");
		System.out.println(OrderSearch.fuzzySearchByItemName("item", "2"));
		System.out.println(ReviewSearch.searchUserReviews("w7sh", "2"));
		System.out.println(ProductSearch.searchProducts("mobile_"));
		System.out.println(ProductHasDepartment.searchProducts("mobile", "1"));
		System.out.println(DepartmentSearch.searchProducts("*"));
		System.out.println(DepartmentSearch.searchProducts("apple"));
		System.out.println(ItemSearch.searchItemsByName("item", "", ""));
		System.out.println(ItemSearch.searchItemsByName("item", "price", "asc"));
		System.out.println(ItemSearch.searchItemsByName("item", "created_at", "desc"));
		System.out.println(ReviewSearch.searchAllReviewsByValue("2"));
		
	}
}
