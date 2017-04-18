package commands.product;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.util.Map;


import commands.Command;

public class IndexProductsCmd extends Command implements Runnable {

	public StringBuffer execute(Connection connection, Map<String, Object> mapUserData)
			throws Exception {
		StringBuffer strbufResult;
		CallableStatement sqlProc;
		//System.out.println("d5al elmethod");
		sqlProc = connection.prepareCall("{call all_products()}");
		sqlProc.execute();
		System.out.println("hopaaaaa");
		//System.out.println(sqlProc.getResultSet().getString("name"));
		strbufResult = makeJSONResponseEnvelope(200, null, null);
		sqlProc.close();
		return strbufResult;
	}
}
