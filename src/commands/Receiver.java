package commands;

import com.rabbitmq.client.*;
import com.rabbitmq.client.Channel;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class Receiver {

	String tag;
	String exchange_name;
	String server_ip;
	Channel channel = null;
	Connection connection;
	String queueName;
	boolean received;
	String messageReceived;

	public Receiver(String tag, String ip, String key) {
		this.tag = tag;
		server_ip = ip;
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
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost(server_ip);
		factory.setUsername("user");
		factory.setPassword("password");
//		factory.setHost("");

		try {
			connection = factory.newConnection();
			channel = connection.createChannel();
			channel.exchangeDeclare(exchange_name, "direct");
			queueName = channel.queueDeclare(tag, false, false, false, null).getQueue();
			channel.queueBind(queueName, exchange_name, tag);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TimeoutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void Receive() throws java.net.URISyntaxException, java.lang.InterruptedException{
		System.out.println("connecting ..." + tag );
		connect();
		System.out.println("Waiting for messages....");
		Consumer consumer = new DefaultConsumer(channel) {
			@Override
			public void handleDelivery(String consumerTag, Envelope envelope,
									   AMQP.BasicProperties properties, byte[] body)
					throws IOException {
				String message = new String(body, "UTF-8");
				System.out.println(" [x] Received '" + envelope.getRoutingKey()
						+ "':'" + message + "'");
//				messageReceived = message;
//				JsonObject json = JsonObject.readFrom(message);
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


	public void sendToNetty(String message){
		HttpClient httpClient = HttpClientBuilder.create().build();
		try {

			HttpPost request = new HttpPost("http://localhost:3030");
			StringEntity params =new StringEntity(message);
			request.addHeader("content-type", "text/plain");
			request.setEntity(params);
			HttpResponse response = httpClient.execute(request);

		}catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			System.out.println("Success! Sent to Netty.");
		}
	}


}