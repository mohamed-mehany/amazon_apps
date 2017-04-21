package commands.message;


import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import com.eclipsesource.json.JsonObject;

import commands.Command;

public class ViewAllMessagesBetween2UsersCmd extends Command implements Runnable{
	@Override
	public StringBuffer execute(Connection connection, Map<String, Object> mapUserData) throws Exception {
		// TODO Auto-generated method stub
		CallableStatement sqlProc;
		StringBuffer strbufResult = null, strbufResponseJSON;
		int receiverID;
		int senderID;
		
		try {
			
			receiverID = ((Integer) mapUserData.get("receiver_id"));
			senderID = ((Integer) mapUserData.get("sender_id"));
			sqlProc = connection.prepareCall("{call view_all_messages_between_2_users(?,?)}");
			sqlProc.setInt(1, receiverID);
			sqlProc.setInt(2, senderID);
			sqlProc.execute();
			strbufResult = makeJSONResponseEnvelope(3, null, null);
			
			
			ResultSet nSQLResult = sqlProc.getResultSet();
			while (nSQLResult.next()) {
		       			JsonObject o = new JsonObject();
		       			o.add("name", nSQLResult.getString(2));
		       			o.add("message", nSQLResult.getString(1));
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
