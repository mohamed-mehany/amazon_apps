package commands;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class Sender {
	String tag;
	String exchange_name;
	String server_ip;
	Channel channel = null;
	Connection connection;

	public Sender(String tag, String ip, String key) {
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
		try {
			connection = factory.newConnection();
			channel = connection.createChannel();
			channel.exchangeDeclare(exchange_name, "direct");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TimeoutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void send(String data) {
		connect();
		try {
			channel.basicPublish(exchange_name, tag, null, data.getBytes());
			System.out.println(" [x] Sent '" + tag + "':'" + data + "'");
			channel.close();
			connection.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TimeoutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

//	public static void main(String[] argv) throws java.io.IOException {
//		Sender sender = new Sender("mowafy", "localhost", "EXCHANGE_SERVER1");
//		sender.send("NEW22");
//	}
}