package commands.user;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Types;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import commands.Command;
import org.boon.primitive.Int;
import services.Cache;

public class LogoutCmd extends Command implements Runnable {

	public StringBuffer execute(Connection connection, Map<String, Object> mapUserData)
			throws Exception {

		CallableStatement sqlProc;
		StringBuffer strbufResult = new StringBuffer("");
		String strToken;
		Integer id;

		strToken = ((String) mapUserData.get("token"));
		id = ((int) mapUserData.get("id"));

		if (strToken == null || strToken.trim().length() == 0 || id == null)
			return null;

		sqlProc = connection.prepareCall("{call user_logout(?,?)}");
		sqlProc.setString(1, strToken);
		sqlProc.setInt(2, id);
		Boolean executed = sqlProc.execute();
		strbufResult.append(changeToJSONFormat(executed + ""));
		sqlProc.close();
		//strbufResult = makeJSONResponseEnvelope(200, null, null);
		return strbufResult;
	}
}
