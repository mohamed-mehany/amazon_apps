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


public class ViewUserOrdersCmd extends Command implements Runnable {
	
	public StringBuffer execute(Connection connection, Map<String, Object> mapUserData)
			throws Exception {
		
		
		System.out.println("execute ");
		StringBuffer strbufResult;
		CallableStatement sqlProc;
//		String strEmail, strPassword, strFirstName, strLastName;
//		strEmail = (String) mapUserData.get("email");
//		strPassword = (String) mapUserData.get("password");
//		strFirstName = (String) mapUserData.get("firstName");
//		strLastName = (String) mapUserData.get("lastName");

//		sqlProc = connection.prepareCall("{?=call addUserSimple(?,?,?,?)}");
//		sqlProc.registerOutParameter(1, Types.INTEGER);
//		sqlProc.setString(2, strEmail);
//		sqlProc.setString(3, strPassword);
//		sqlProc.setString(4, strFirstName);
//		sqlProc.setString(5, strLastName);

//		sqlProc.execute();
//		strbufResult = makeJSONResponseEnvelope(sqlProc.getInt(1), null, null);
//		sqlProc.close();

		
		
//		CallableStatement sqlProc;
		strbufResult = new StringBuffer();
		try {
			int user_id = (int) mapUserData.get("user_id");
			
			sqlProc = connection.prepareCall("{call view_orders"+"(?)"+"}");
			sqlProc.setInt(1, Integer.parseInt(""+user_id));
			boolean hadR = sqlProc.execute();
			System.out.println("Boolean : "+ hadR);
			Map<String, Object> mapResult = new HashMap<String, Object>( );
			ResultSet nSQLResult = sqlProc.getResultSet();
			while (nSQLResult.next()) {
					System.out.println(nSQLResult.toString());
		               System.out.println(nSQLResult.getString(1));
		               mapResult.put( "userID", nSQLResult.getString(1) );

		       			JsonObject o = new JsonObject();
		       			o.add("id", nSQLResult.getString(1));
//		       			o.add("delivery_status", nSQLResult.getString(1));
//		       			o.add("payment_status", nSQLResult.getString(1));
//		       			o.add("created_at", nSQLResult.getString(1));
//		       			o.add("user_id", nSQLResult.getString(1));
//		       			o.add("banking_info_id", nSQLResult.getString(1));
//		       			o.add("updated_at", nSQLResult.getString(1));
		       			strbufResult.append(o);
//		               strbufResult.append(nSQLResult.getString(1));
//	               System.out.println(nSQLResult.getString("delivery_status"));
//	               mapResult.put( "userID", nSQLResult.getString("delivery_status") );
	          }
			nSQLResult.close();
			sqlProc.close();
//			if( !nSQLResult.isLast() ){
//				Map<String, Object> mapResult = new HashMap<String, Object>( );
////				mapResult.put( "userID", Integer.toString( nSQLResult)  );
//				mapResult.put( "userID", user_id);
//				return mapResult;
//			}
//			
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return strbufResult;
		
	}
}
