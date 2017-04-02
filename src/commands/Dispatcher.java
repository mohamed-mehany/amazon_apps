package commands;

import java.io.BufferedReader;
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

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonObject.Member;
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

	protected void loadCommands(JsonObject settings) throws Exception {
		_htblCommands = new Hashtable<String, Class<?>>();
		JsonObject commands = settings.get("commands").asObject();
		String strActionName, strClassName;
		Iterator<Member> it = commands.iterator();
		while (it.hasNext()) {
			Member member = it.next();
			strActionName = member.getName();
			strClassName = member.getValue().asString();
			Class<?> innerClass = Class.forName(strClassName);
			_htblCommands.put(strActionName, innerClass);
		}
	}

	protected void loadDBConfig(JsonObject settings) throws Exception {
		_htblConfig = new Hashtable<String, String>();
		JsonObject commands = settings.get("dbConfig").asObject();
		Iterator<Member> it = commands.iterator();
		while (it.hasNext()) {
			Member member = it.next();
			_htblConfig.put(member.getName(), member.getValue().asString());
		}

	}

	protected void loadSettings() throws Exception {
		loadDBConfig(JsonObject.readFrom(new FileReader("config/commands.json")));
		loadCommands(JsonObject.readFrom(new FileReader("config/settings.json")));
	}

	protected void loadThreadPool() {
		_threadPoolCmds = Executors.newFixedThreadPool(20);
	}

	public void init() throws Exception {
		loadSettings();
		loadHikari("localhost", _htblConfig.get("dbPortNumber"),
				_htblConfig.get("dbUserName"), _htblConfig.get("dbName"),
				_htblConfig.get("dbPassword"));
		loadThreadPool();
	}
}
