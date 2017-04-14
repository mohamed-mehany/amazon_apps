package commands.order;



import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.eclipsesource.json.JsonObject;

import commands.Command;


public class ViewDeliveredOrdersCmd extends Command implements Runnable {
	
	public StringBuffer execute(Connection connection, Map<String, Object> mapUserData)
			throws Exception {
		
		
		StringBuffer strbufResult;
		CallableStatement sqlProc;
		strbufResult = new StringBuffer();
		try {
			int user_id = (int) mapUserData.get("user_id");
			
			sqlProc = connection.prepareCall("{call view_delivered_orders"+"(?)"+"}");
			sqlProc.setInt(1, Integer.parseInt(""+user_id));
			boolean hadR = sqlProc.execute();
			System.out.println("Boolean : "+ hadR);
			ResultSet nSQLResult = sqlProc.getResultSet();
			while (nSQLResult.next()) {
		       			JsonObject o = new JsonObject();
		       			o.add("id", nSQLResult.getString(1));
		       			o.add("delivery_status", nSQLResult.getString(2));
		       			o.add("payment_status", nSQLResult.getString(3));
		       			o.add("created_at", nSQLResult.getString(4));
		       			o.add("user_id", nSQLResult.getString(5));
		       			o.add("banking_info_id", nSQLResult.getString(6));
		       			o.add("updated_at", nSQLResult.getString(7));
		       			strbufResult.append(o);
	          }
			nSQLResult.close();
			sqlProc.close();
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return strbufResult;
		
	}
}
