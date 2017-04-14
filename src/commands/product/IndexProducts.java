package commands.user;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.util.Map;

import commands.Command;

public class IndexProducts extends Command implements Runnable {

	public StringBuffer execute(Connection connection, Map<String, Object> mapUserData)
			throws Exception {
		StringBuffer strbufResult;
		CallableStatement sqlProc;

		sqlProc = connection.prepareCall("{call all_products()}");
		sqlProc.execute();
		strbufResult = makeJSONResponseEnvelope(200, null, null);
		sqlProc.close();
		return strbufResult;
	}
}
