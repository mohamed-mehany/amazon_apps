package commands.message;


import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import com.eclipsesource.json.JsonObject;

import commands.Command;

public class ViewAllMessagesBetween2UsersCmd extends Command implements Runnable{
	public StringBuffer execute(Connection connection, Map<String, Object> mapUserData)
			throws Exception {
		CallableStatement sqlProc;
		StringBuffer strbufResult = new StringBuffer("");
		int receiverID;
		int senderID;

		receiverID = ((Integer) mapUserData.get("receiver_id"));
		senderID = ((Integer) mapUserData.get("sender_id"));
		sqlProc = connection.prepareCall("{call view_all_messages_between_2_users(?,?)}");
		sqlProc.setInt(1, receiverID);
		sqlProc.setInt(2, senderID);
		sqlProc.execute();
		strbufResult = makeJSONResponseEnvelope(3, null, null);

		ResultSet nSQLResult = sqlProc.getResultSet();
		strbufResult.append(changeToJSONFormat(nSQLResult));
		nSQLResult.close();
		sqlProc.close();

		return strbufResult;
	}


}
