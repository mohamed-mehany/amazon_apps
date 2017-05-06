package commands.product;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.Map;

import com.eclipsesource.json.JsonObject;

import commands.Command;

public class IndexProductsCmd extends Command implements Runnable {

	public StringBuffer execute(Connection connection, Map<String, Object> mapUserData)
			throws Exception {
		CallableStatement sqlProc;
		StringBuffer strbufResult = new StringBuffer("");
		sqlProc = connection.prepareCall("{call all_products"+"()"+"}");
		ResultSet r=sqlProc.executeQuery();
		strbufResult.append(changeToJSONFormat(r));
		sqlProc.close();
		return strbufResult;
		
		
		
	}
}
