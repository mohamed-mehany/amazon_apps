package commands;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;

public class Receiver {

	static class SayHello extends TimerTask {
		Receiver receive =  null;
		public void run() {

			receive = new Receiver("mohsen", "192.168.1.144",
					"EXCHANGE_SERVER1");
			receive.Receive();
		}
	}

	String tag;
	String exchange_name;
	String server_ip;
	Channel channel = null;
	Connection connection;
	String queueName;
	ConnectionFactory factory;
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
					System.out.println(" [x] Received '"
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
//	public static void main(String[] argv) throws Exception {
//		Timer timer = new Timer();
//		timer.schedule(new SayHello(), 0, 500);
//		System.out.println("STARTING ");
//
//
//	}
}