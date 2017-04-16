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
	public static void runReceivers() throws Exception{
		Receiver RatingsReceiver = new Receiver("Ratings", "localhost", "EXCHANGE_SERVER1");
		RatingsReceiver.Receive();


		Receiver MessagesReceiver = new Receiver("Messages", "localhost", "EXCHANGE_SERVER1");
		MessagesReceiver.Receive();

		Receiver UsersReceiver = new Receiver("Users", "localhost", "EXCHANGE_SERVER1");
		UsersReceiver.Receive();

	}
}