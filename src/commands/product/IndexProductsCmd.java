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
		StringBuffer strbufResult = new StringBuffer(""), strbufResponseJSON;
		System.out.println("he2");
		sqlProc = connection.prepareCall("{call all_products"+"()"+"}");
		
		
		ResultSet r=sqlProc.executeQuery();
		ResultSetMetaData rsmd = r.getMetaData();
		int columnsNumber = rsmd.getColumnCount();
		
		
		while (r.next()) {
			JsonObject o = new JsonObject();

			for (int i = 1; i <= columnsNumber; i++) {
				o.add(rsmd.getColumnName(i), r.getString(i));
				System.out.println(r.getString(i));
			}

			strbufResult.append(o);
		}
		r.close();
		sqlProc.close();
		return strbufResult;
		
		
		
	}
}
