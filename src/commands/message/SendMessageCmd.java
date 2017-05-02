package commands.message;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.util.Map;

import commands.Command;

public class SendMessageCmd extends Command implements Runnable {

	public StringBuffer execute(Connection connection, Map<String, Object> mapUserData)
			throws Exception {
		
		CallableStatement sqlProc;
		StringBuffer strbufResult = new StringBuffer("");
		int senderID, receiverID;
		String message;
		
		System.out.println("entered command");
		
		senderID =  ((Integer) mapUserData.get("sender_id"));
		receiverID = ((Integer) mapUserData.get("receiver_id"));
		message = ((String) mapUserData.get("text"));
		
		System.out.println(senderID + " " + receiverID + " " + message);
		
		sqlProc = connection.prepareCall("{call send_message(?,?,?)}");
		sqlProc.setInt(1, senderID);
		sqlProc.setInt(2, receiverID);
		sqlProc.setString(3, message);
		boolean executed = sqlProc.execute();
		System.out.println("procedure executed");
		strbufResult.append(changeToJSONFormat(executed+""));
		sqlProc.close();
		return strbufResult;
	}

}
