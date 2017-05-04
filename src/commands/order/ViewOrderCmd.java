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


public class ViewOrderCmd extends Command implements Runnable {
	
	public StringBuffer execute(Connection connection, Map<String, Object> mapUserData)
			throws Exception {
		
		
		StringBuffer strbufResult;
		CallableStatement sqlProc;
		strbufResult = new StringBuffer();
		try {
			int user_id = (int) mapUserData.get("order_id");
			
			sqlProc = connection.prepareCall("{call view_order"+"(?)"+"}");
			sqlProc.setInt(1, Integer.parseInt(""+user_id));
			boolean hadR = sqlProc.execute();
			System.out.println("Boolean : "+ hadR);
			Map<String, Object> mapResult = new HashMap<String, Object>( );
			ResultSet nSQLResult = sqlProc.getResultSet();
   			strbufResult.append("[");
			while (nSQLResult.next()) {
					System.out.println(nSQLResult.toString());
		               System.out.println(nSQLResult.getString(1));
		               mapResult.put( "userID", nSQLResult.getString(1) );

		       			JsonObject o = new JsonObject();
		       			o.add("id", nSQLResult.getString(1));
		       			o.add("delivery_status", nSQLResult.getString(2));
		       			o.add("payment_status", nSQLResult.getString(3));
		       			o.add("created_at", nSQLResult.getString(4));
		       			o.add("user_id", nSQLResult.getString(5));
		       			o.add("banking_info_id", nSQLResult.getString(6));
		       			o.add("updated_at", nSQLResult.getString(7));
		       			strbufResult.append(o+", ");
	          }
   			strbufResult.delete(strbufResult.length()-2, strbufResult.length());
   			strbufResult.append("]");
			nSQLResult.close();
			sqlProc.close();
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return strbufResult;
		
	}
}
