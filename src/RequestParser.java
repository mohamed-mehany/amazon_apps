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
			
		} catch (Exception exp) {
			_parseListener.parsingFailed(_clientHandle,
					"Exception while parsing JSON object " + exp.toString());
		}
	}

}