import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.zaxxer.hikari.HikariDataSource;

public class Dispatcher {

	protected Hashtable _htblCommands;
	protected Hashtable<String, String> _htblConfig;
	protected ExecutorService _threadPoolCmds;
	protected HikariDataSource _hikariDataSource;

	public Dispatcher() {
	}


	class AddUserSimpleCmd extends Command implements Runnable {

		public StringBuffer execute(Connection connection,
				Map<String, Object> mapUserData) throws Exception {
			StringBuffer strbufResult;
			CallableStatement sqlProc;
			String strEmail, strPassword, strFirstName, strLastName;
			strEmail = (String) mapUserData.get("email");
			strPassword = (String) mapUserData.get("password");
			strFirstName = (String) mapUserData.get("firstName");
			strLastName = (String) mapUserData.get("lastName");

			if (strEmail == null || strEmail.trim().length() == 0 || strPassword == null
					|| strPassword.trim().length() == 0 || strFirstName == null
					|| strFirstName.trim().length() == 0 || strLastName == null
					|| strLastName.trim().length() == 0)
				return null;

			if (!EmailVerifier.verify(strEmail))
				return null;

			sqlProc = connection.prepareCall("{?=call addUserSimple(?,?,?,?)}");
			sqlProc.registerOutParameter(1, Types.INTEGER);
			sqlProc.setString(2, strEmail);
			sqlProc.setString(3, strPassword);
			sqlProc.setString(4, strFirstName);
			sqlProc.setString(5, strLastName);

			sqlProc.execute();
			strbufResult = makeJSONResponseEnvelope(sqlProc.getInt(1), null, null);
			sqlProc.close();

			return strbufResult;
		}
	}

	class AttemptLoginCmd extends Command implements Runnable {

		public StringBuffer execute(Connection connection,
				Map<String, Object> mapUserData) throws Exception {

			CallableStatement sqlProc;
			StringBuffer strbufResult = null, strbufResponseJSON;
			String strSessionID, strEmail, strPassword, strFirstName, strClientIP;
			int nSQLResult;

			strEmail = ((String) mapUserData.get("email"));
			strPassword = ((String) mapUserData.get("password"));

			if (strEmail == null || strEmail.trim().length() == 0 || strPassword == null
					|| strPassword.trim().length() == 0)
				return null;

			if (!EmailVerifier.verify(strEmail))
				return null;

			strClientIP = _clientHandle.getClientIP();
			strSessionID = UUID.randomUUID().toString();

			sqlProc = connection.prepareCall("{?=call attemptLogin(?,?,?,?)}");
			sqlProc.registerOutParameter(1, Types.INTEGER);
			sqlProc.setString(2, strEmail);
			sqlProc.setString(3, strPassword);
			sqlProc.setString(4, strSessionID);
			sqlProc.setString(5, strClientIP);
			sqlProc.execute();
			nSQLResult = sqlProc.getInt(1);
			sqlProc.close();
			if (nSQLResult >= 0) {
				Cache.addSession(strSessionID, strEmail);
				System.err.println(" adding following session to Cache " + strSessionID);
				Map<String, Object> mapResult = new HashMap<String, Object>();
				mapResult.put("userID", Integer.toString(nSQLResult));
				mapResult.put("sessionID", strSessionID);
				sqlProc = connection.prepareCall("{?=call getUserFirstName(?)}");
				sqlProc.registerOutParameter(1, Types.VARCHAR);
				sqlProc.setInt(2, nSQLResult);
				sqlProc.execute();
				strFirstName = sqlProc.getString(1);
				sqlProc.close();
				mapResult.put("firstName", strFirstName);
				strbufResponseJSON = serializeMaptoJSON(mapResult, null);
				strbufResult = makeJSONResponseEnvelope(0, null, strbufResponseJSON);
			} else
				strbufResult = makeJSONResponseEnvelope(nSQLResult, null, null);

			return strbufResult;
		}
	}

	protected void dispatchRequest(ClientHandle clientHandle, ClientRequest clientRequest)
			throws Exception {

		Command cmd;
		String strAction;
		strAction = clientRequest.getAction();

		Class<?> innerClass = (Class<?>) _htblCommands.get(strAction);
		Class<?> enclosingClass = Class.forName("Dispatcher");
		Object enclosingInstance = enclosingClass.newInstance();
		Constructor<?> ctor = innerClass.getDeclaredConstructor(enclosingClass);
		cmd = (Command) ctor.newInstance(enclosingInstance);
		cmd.init(_hikariDataSource, clientHandle, clientRequest);
		_threadPoolCmds.execute((Runnable) cmd);
	}

	protected void testHikari() {
		String strEmail = "mohamed123@m.com";
		String strPassword = "123";
		String strFirstName = "Hello";
		String strLastName = "World";

		try {
			Connection connection = _hikariDataSource.getConnection();
			CallableStatement sqlProc = connection
					.prepareCall("{?=call addUserSimple(?,?,?,?)}");
			sqlProc.registerOutParameter(1, Types.INTEGER);
			sqlProc.setString(2, strEmail);
			sqlProc.setString(3, strPassword);
			sqlProc.setString(4, strFirstName);
			sqlProc.setString(5, strLastName);

			sqlProc.execute();
			sqlProc.close();

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	protected void loadHikari(String strAddress, String nPort, String strDBName,
			String strUserName, String strPassword) {

		_hikariDataSource = new HikariDataSource();
		_hikariDataSource.setJdbcUrl(
				"jdbc:postgresql://" + strAddress + ":" + nPort + "/" + strDBName);
		_hikariDataSource.setUsername(strUserName);
		_hikariDataSource.setPassword(strPassword);
		// testHikari();
	}

	protected void loadCommands() throws Exception {
		_htblCommands = new Hashtable();
		Properties prop = new Properties();
		InputStream in = getClass().getResourceAsStream("config/commands.properties");
		prop.load(in);
		in.close();
		Enumeration enumKeys = prop.propertyNames();
		String strActionName, strClassName;

		while (enumKeys.hasMoreElements()) {
			strActionName = (String) enumKeys.nextElement();
			strClassName = (String) prop.get(strActionName);
			Class<?> innerClass = Class.forName("Dispatcher$" + strClassName);
			_htblCommands.put(strActionName, innerClass);
		}
	}

	protected void loadConfig() throws Exception {
		_htblConfig = new Hashtable();
		Properties prop = new Properties();
		InputStream in = getClass().getResourceAsStream("config/dbconfig.properties");
		prop.load(in);
		in.close();
		Enumeration enumKeys = prop.propertyNames();
		String strConfigName;

		while (enumKeys.hasMoreElements()) {
			strConfigName = (String) enumKeys.nextElement();
			_htblConfig.put(strConfigName, prop.get(strConfigName).toString());
		}
	}

	protected void loadThreadPool() {
		_threadPoolCmds = Executors.newFixedThreadPool(20);
	}

	public void init() throws Exception {
		loadConfig();
		loadCommands();
		loadHikari("localhost", _htblConfig.get("dbPortNumber"), _htblConfig.get("dbUserName"),
				_htblConfig.get("dbName"), _htblConfig.get("dbPassword"));
		loadThreadPool();
	}
}
