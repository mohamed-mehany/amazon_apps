package controller;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import org.boon.json.implementation.JsonStringDecoder;

import com.eclipsesource.json.JsonObject;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.HttpContent;
import io.netty.util.CharsetUtil;

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
			HttpContent cc = (HttpContent) (_clientHandle._httpRequest);
			ByteBuf content = cc.content();
			JsonObject body = JsonObject.readFrom(content.toString(CharsetUtil.UTF_8));
			JsonObject jsonData = body.get("data").asObject();
			String command = body.get("command").asString();
			String sessionID = body.get("sessionID").asString();
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("email", jsonData.get("email").asString());
			data.put("password", jsonData.get("password").asString());
			data.put("firstName", jsonData.get("firstName").asString());
			data.put("lastName", jsonData.get("lastName").asString());
			data.put("address", jsonData.get("address").asString());
			data.put("date", jsonData.get("date").asString());
			data.put("token", jsonData.get("token").asString());
			data.put("gender", jsonData.get("gender").asInt());
			ClientRequest clientRequest = new ClientRequest(command, sessionID, data);
			_parseListener.parsingFinished(_clientHandle, clientRequest);
		} catch (Exception exp) {
			_parseListener.parsingFailed(_clientHandle,
					"Exception while parsing JSON object " + exp.toString());
		}
	}

}