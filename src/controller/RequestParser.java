package controller;

import java.util.HashMap;
import java.util.Map;

public class RequestParser implements Runnable {

	protected ParseListener _parseListener;
	protected ClientHandle _clientHandle;

	public RequestParser(ParseListener parseListener, ClientHandle clientHandle) {
		_parseListener = parseListener;
		_clientHandle = clientHandle;
	}

	public void run() {
		try {
			// CALL DISPATCHER
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("email", "mohamead121212@m.com");
			data.put("password", "johnpass");
			data.put("firstName", "mohamed");
			data.put("lastName", "mostafa");
			data.put("address", "world");
			data.put("date", "2012.01.01 12:12:12");
			data.put("token", "1213132322");
			data.put("gender", 1);

			ClientRequest clientRequest = new ClientRequest("addUserSimple", "121", data);
			_parseListener.parsingFinished(_clientHandle, clientRequest);
		} catch (Exception exp) {
			_parseListener.parsingFailed(_clientHandle,
					"Exception while parsing JSON object " + exp.toString());
		}
	}

}