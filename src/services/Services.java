package services;

import commands.Receiver;
import controller.Controller;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.util.SelfSignedCertificate;

import java.nio.channels.SocketChannel;
import java.util.Timer;
import java.util.TimerTask;

public final class Services {

	public static final boolean SSL = System.getProperty("ssl") != null;
	public static final int PORT = Integer.parseInt(SSL ? "3030" : "3030");

	protected static Controller _controller;
	protected static Channel channel;
	public static void main(String[] args) throws Exception {

		// Configure SSL.
		final SslContext sslCtx;
		if (SSL) {
			SelfSignedCertificate ssc = new SelfSignedCertificate();
			sslCtx = SslContext.newServerContext(ssc.certificate(), ssc.privateKey());
		} else {
			sslCtx = null;
		}

		// Configure the server.
		EventLoopGroup bossGroup = new NioEventLoopGroup(5);
		EventLoopGroup workerGroup = new NioEventLoopGroup();
		try {
			_controller = new Controller();
			_controller.init();

			Cache.init();
			Cache.loadFromDatabase();

			ServerBootstrap serverBoot = new ServerBootstrap();
			serverBoot.group(bossGroup, workerGroup);
			serverBoot.channel(NioServerSocketChannel.class);
			serverBoot.handler(new LoggingHandler(LogLevel.TRACE));
			serverBoot.childHandler(new ServicesInitializer(sslCtx, _controller));
			channel = serverBoot.bind(PORT).sync().channel();
			runReceivers();
			System.err.println("Services running on  " + (SSL ? "https" : "http")
					+ "://127.0.0.1:" + PORT + '/');
			channel.closeFuture().sync();

		} finally {
			bossGroup.shutdownGracefully();
			workerGroup.shutdownGracefully();
		}
	}
	static class SayHello extends TimerTask

	{
		String tag, ip, key;
		Receiver receive = null;

		public SayHello(String tag, String ip, String key){
			this.tag = tag;
			this.ip = ip;
			this.key = key;
		}
		public void run() {
			receive = new Receiver(tag, ip, key);
			receive.Receive();
		}

	}

	public static void runReceivers() throws Exception{
		Timer timerRatings = new Timer();
		timerRatings.schedule(new SayHello("Ratings", "192.168.1.144", "EXCHANGE_SERVER1"), 0, 500);
		Timer timerMessages = new Timer();
		timerMessages.schedule(new SayHello("Messages", "192.168.1.144", "EXCHANGE_SERVER1"), 0, 500);
		Timer timerUsers = new Timer();
		timerUsers.schedule(new SayHello("Users", "192.168.1.144", "EXCHANGE_SERVER1"), 0, 500);

	}
}