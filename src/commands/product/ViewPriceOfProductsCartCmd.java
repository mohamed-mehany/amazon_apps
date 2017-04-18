package commands.product;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.util.Map;

import commands.Command;

public class ViewPriceOfProductsCartCmd extends Command implements Runnable {

	public StringBuffer execute(Connection connection, Map<String, Object> mapUserData)
			throws Exception {
		StringBuffer strbufResult;
		CallableStatement sqlProc;
		Integer product_id, quantity;
		product_id = (Integer) mapUserData.get("product_id");
		quantity = (Integer) mapUserData.get("quantity");
		

		System.out.println();
		System.out.println(product_id);
		System.out.println(quantity);
		sqlProc = connection.prepareCall("{call view_price_products_cart(?,?)}");
		sqlProc.setInt(1, product_id);
		sqlProc.setInt(2, quantity);

		sqlProc.execute();
		strbufResult = makeJSONResponseEnvelope(200, null, null);
		sqlProc.close();

		return strbufResult;
	}
}
