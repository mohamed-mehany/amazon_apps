package commands.message;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.util.Map;

import commands.Command;

public class SendMessageCmd extends Command implements Runnable {

	@Override
	public StringBuffer execute(Connection connection, Map<String, Object> mapUserData) throws Exception {
		// TODO Auto-generated method stub
		
		CallableStatement sqlProc;
		StringBuffer strbufResult = null, strbufResponseJSON;
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
		sqlProc.execute();
		System.out.println("procedure executed");
		strbufResult = makeJSONResponseEnvelope(3, null, null);
		sqlProc.close();
		return strbufResult;
	}

}
