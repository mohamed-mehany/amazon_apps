package commands;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeoutException;

import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
//import com.sun.scenario.effect.impl.sw.sse.SSEBlend_SRC_OUTPeer;
import io.netty.util.CharsetUtil;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

public class Receiver {

	static class SayHello extends TimerTask {
		Receiver receive =  null;
		public void run() {

			receive = new Receiver("mohsen", "localhost",
					"EXCHANGE_SERVER1");
			receive.Receive();
		}
	}

	public String tag;
	public String exchange_name;
	public String server_ip;
	public Channel channel = null;
	public Connection connection;
	public String queueName;
	public ConnectionFactory factory;
	public  String key;
	public Receiver(String tag, String ip, String key) {
		this.tag = tag;
		server_ip = ip;
		this.key = key;
		getExchange(key);
	}

	public void getExchange(String key) {
		BufferedReader Br = null;
		FileReader Fr = null;
		try {
			Fr = new FileReader("config/config");
			Br = new BufferedReader(Fr);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String in;
		try {
			while ((in = Br.readLine()) != null) {
				String[] tokens = in.split(" ");
				if (tokens[0].equals(key)) {
					exchange_name = tokens[2];
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void connect() {
		 factory = new ConnectionFactory();
		factory.setHost(server_ip);
		factory.setUsername("user");
		factory.setPassword("password");
		// factory.setHost("");

			try {
				connection = factory.newConnection();
				channel = connection.createChannel();
				channel.exchangeDeclare(exchange_name, "direct");
				queueName = channel.queueDeclare(tag, false, false, false, null)
						.getQueue();
				channel.queueBind(queueName, exchange_name, tag);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (TimeoutException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}


	}

	public void Receive() {
		connect();
		{


//			System.out.println("connecting ... 22");
//			System.out.println("Waiting for messages....");
			Consumer consumer = new DefaultConsumer(channel) {
				@Override
				public void handleDelivery(String consumerTag,
										   Envelope envelope, AMQP.BasicProperties properties,
										   byte[] body) throws IOException {
					String message = new String(body, "UTF-8");
					System.out.println(tag + " [x] Received '"
							+ envelope.getRoutingKey() + "':'" + message + "'");
					sendToNetty(message);
				}
			};
			try {
				channel.basicConsume(queueName, true, consumer);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	public void sendToNetty(String message){
		HttpClient httpClient = HttpClientBuilder.create().build();
		try {
			//send request to netty
			HttpPost request = new HttpPost("http://localhost:3030");
			StringEntity params =new StringEntity(message);
			JsonObject result = new JsonObject();
			request.addHeader("content-type", "text/plain");
			request.setEntity(params);
			HttpEntity requestEntity = request.getEntity();
			String requestString = EntityUtils.toString(requestEntity, "UTF-8");
			JsonObject requestJson = JsonObject.readFrom(requestString);
			String receivingQueue = requestJson.get("receivingQueue").asString();
			//get Response from netty
			HttpResponse httpResponse = httpClient.execute(request);
			HttpEntity entity = httpResponse.getEntity();
			String responseString = EntityUtils.toString(entity);
			//put response in jsonArray
			JsonArray responseJson = JsonArray.readFrom(responseString);
			//add it to the result Json
			result.add("data",responseJson);
			//add id to json object
			result.add("id", requestJson.get("requestId"));
			//send to queue
			Sender sender = new Sender(receivingQueue, server_ip, "EXCHANGE_SERVER1");
			sender.send(result.toString());
		}catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			System.out.println("Success! Sent to Netty.");
		}
	}


	public static void main(String[] argv) throws Exception {
		Timer timer = new Timer();
		timer.schedule(new SayHello(), 0, 500);
		System.out.println("STARTING ");


	}
}