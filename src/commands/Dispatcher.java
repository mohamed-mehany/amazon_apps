package commands;

import java.io.FileReader;
import java.lang.reflect.Constructor;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.mysql.jdbc.Driver;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonObject.Member;
import com.eclipsesource.json.JsonValue;
import com.zaxxer.hikari.HikariDataSource;

import controller.ClientHandle;
import controller.ClientRequest;

public class Dispatcher {

	protected Hashtable<String, Class<?>> _htblCommands;
	protected Hashtable<String, String> _htblConfig;
	protected ExecutorService _threadPoolCmds;
	protected HikariDataSource _hikariDataSource;

	public Dispatcher() {
	}

	public void dispatchRequest(ClientHandle clientHandle, ClientRequest clientRequest)
			throws Exception {

		Command cmd;
		String strAction;
		strAction = clientRequest.getAction();
		Class<?> innerClass = (Class<?>) _htblCommands.get(strAction);
		Constructor<?> ctor = innerClass.getDeclaredConstructor();
		cmd = (Command) ctor.newInstance();
		cmd.init(_hikariDataSource, clientHandle, clientRequest);
		_threadPoolCmds.execute((Runnable) cmd);
	}

	protected void testHikari() {
		String strFirstName = "Hello";
		String strEmail = "mohamed123@m.com";
		String strPassword = "123";
		String strAddress = "World";
		String strDate = "2012.01.01 12:12:12";
		String strToken = "1213142323";
		int gender = 1;
		try {
			Connection connection = _hikariDataSource.getConnection();
			CallableStatement sqlProc = connection
					.prepareCall("{call create_user(?, ?, ?, ?, ?, ?, ?)}");
			
			
			//sqlProc.registerOutParameter(1, Types.INTEGER);
			sqlProc.setString(1, strFirstName);
			sqlProc.setString(2, strEmail);
			sqlProc.setString(3, strPassword);
			sqlProc.setString(4, strAddress);
			sqlProc.setString(5, strDate);
			sqlProc.setString(6, strToken);
			sqlProc.setInt(7, gender);

			sqlProc.execute();
			sqlProc.close();

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	protected void loadHikari(String strAddress, String nPort, String strDBName,
			String strUserName, String strPassword) {

		_hikariDataSource = new HikariDataSource();
		_hikariDataSource.setDriverClassName("com.mysql.jdbc.Driver");
		_hikariDataSource
				.setJdbcUrl("jdbc:mysql://" + strAddress + ":" + nPort + "/" + strDBName);
		_hikariDataSource.setUsername(strUserName);
		_hikariDataSource.setPassword(strPassword);
		testHikari();
	}

	protected void loadCommands() throws Exception {
		_htblCommands = new Hashtable<String, Class<?>>();
		String strActionName, strClassName, strPackageName;
		JsonArray allowedCommands = JsonObject
				.readFrom(new FileReader("config/settings.json")).get("allowedCommands")
				.asArray();
		JsonObject commands = JsonObject.readFrom(new FileReader("config/commands.json"));
		Iterator<JsonValue> it = allowedCommands.iterator();
		while (it.hasNext()) {
			strPackageName = it.next().asString();
			JsonObject commandObj = commands.get(strPackageName).asObject();
			Iterator<Member> it2 = commandObj.iterator();
			while (it2.hasNext()) {
				Member member = it2.next();
				strActionName = member.getName();
				strClassName = member.getValue().asString();
				Class<?> innerClass = Class.forName(strPackageName + '.' + strClassName);
				_htblCommands.put(strActionName, innerClass);
			}
		}
	}

	protected void loadDBConfig() throws Exception {
		_htblConfig = new Hashtable<String, String>();
		JsonObject commands = JsonObject.readFrom(new FileReader("config/settings.json"))
				.get("dbConfig").asObject();
		Iterator<Member> it = commands.iterator();
		while (it.hasNext()) {
			Member member = it.next();
			_htblConfig.put(member.getName(), member.getValue().asString());
		}

	}

	protected void loadSettings() throws Exception {
		loadDBConfig();
		loadCommands();
	}

	protected void loadThreadPool() {
		_threadPoolCmds = Executors.newFixedThreadPool(20);
	}

	public void init() throws Exception {
		loadSettings();
		loadHikari(_htblConfig.get("dbHostName"), _htblConfig.get("dbPortNumber"),
				_htblConfig.get("dbName"), _htblConfig.get("dbUserName"),
				_htblConfig.get("dbPassword"));
		loadThreadPool();
	}
}
