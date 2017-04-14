package commands.user;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.util.Map;

import commands.Command;

public class SortProducts extends Command implements Runnable {

	public StringBuffer execute(Connection connection, Map<String, Object> mapUserData)
			throws Exception {
		StringBuffer strbufResult;
		CallableStatement sqlProc;
		int sorting_method;

		sorting_method = (Integer) mapUserData.get("sorting_method");

		sqlProc = connection.prepareCall("{call sort_products(?)}");
		sqlProc.setString(1, sorting_method);

		sqlProc.execute();
		strbufResult = makeJSONResponseEnvelope(200, null, null);
		sqlProc.close();
		return strbufResult;
	}
}
