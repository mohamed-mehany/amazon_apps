package commands.user;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.util.Map;

import commands.Command;

public class ViewPriceProductsCart extends Command implements Runnable {

	public StringBuffer execute(Connection connection, Map<String, Object> mapUserData)
			throws Exception {
		Integer product_id, quantity;
		product_id = (Integer) mapUserData.get("product_id");
		quantity = (Integer) mapUserData.get("quantity");
		
		if (quantity == null || product_id == null)
			return null;
		sqlProc = connection.prepareCall("{call view_price_products_cart(?, ?)}");
		sqlProc.setString(1, product_id);
		sqlProc.setString(2, quantity);

		sqlProc.execute();
		strbufResult = makeJSONResponseEnvelope(200, null, null);
		sqlProc.close();

		return strbufResult;
	}
}
