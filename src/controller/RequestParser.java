package controller;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


import org.boon.json.implementation.JsonStringDecoder;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonObject.Member;

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
			Map<String, Object> data = new HashMap<String, Object>();

			String sessionID = body.get("sessionID").asString();
			
			Iterator<Member> temp = jsonData.iterator();

			while(temp.hasNext()){
				Member x = temp.next();
				if (x.getValue().isNumber()){
					data.put(x.getName(), x.getValue().asInt());

				} else if (x.getValue().isString()) {
					data.put(x.getName(), x.getValue().asString());
				}
			}

			ClientRequest clientRequest = new ClientRequest(command, sessionID, data);
			_parseListener.parsingFinished(_clientHandle, clientRequest);
		} catch (Exception exp) {
			_parseListener.parsingFailed(_clientHandle,
					"Exception while parsing JSON object " + exp.toString());
		}
	}

}