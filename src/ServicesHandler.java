
import static io.netty.handler.codec.http.HttpHeaderNames.CONNECTION;
import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_LENGTH;
import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_TYPE;
import static io.netty.handler.codec.http.HttpResponseStatus.BAD_REQUEST;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderUtil;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.CharsetUtil;

public class ServicesHandler extends SimpleChannelInboundHandler<Object> {

	private HttpRequest request;

	/** Buffer that stores the response content */
	protected StringBuilder buf = new StringBuilder();

	protected Controller _controller;

	public ServicesHandler(Controller controller) {
		_controller = controller;
	}

	public void setResponse(StringBuffer strbufResponse) {
		if (strbufResponse == null)
			buf = null;
		else {
			buf.setLength(0);
			buf.append(strbufResponse.toString());
		}
	}

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) {
		ctx.flush();
	}

	@Override
	public boolean acceptInboundMessage(Object msg) throws Exception {
		HttpRequest request;

		request = (HttpRequest) msg;
		if (request.method().compareTo(HttpMethod.POST) == 0)
			return true;
		return false;
	}

	@Override
	protected void messageReceived(ChannelHandlerContext ctx, Object msg) {

		System.err.println(" got a request: " + msg);

		if (msg instanceof HttpRequest) {

			this.request = (HttpRequest) msg;
			HttpRequest request = this.request;
			if (request.method().compareTo(HttpMethod.POST) == 0) {
				// it is a POST -- nice
				try {
					System.err.println(" got a post: " + request.toString());

					HttpPostRequestDecoder postDecoder;
					List<InterfaceHttpData> lst;

					postDecoder = new HttpPostRequestDecoder(request);
					lst = postDecoder.getBodyHttpDatas();
					int i = 1;
					for (InterfaceHttpData temp : lst) {
						System.err.println(i + " " + temp);
						i++;
					}

					_controller.execRequest(new ClientHandle(ctx, request, this));
					synchronized (this) {
						this.wait();
					}

					if (buf != null) {
						System.err.println(" sending back" + buf.toString());
						writeResponse(request, ctx);
					} else {
						System.err.println(" Got a bad request. Closing channel ");
						ctx.close();
					}
				} catch (Exception exp) {
					exp.printStackTrace();
				}
			}
		}
	}

	private static void sendHttpResponse(ChannelHandlerContext ctx, HttpRequest req,
			FullHttpResponse res) {
		// Generate an error page if response getStatus code is not OK (200).
		if (res.status().code() != 200) {
			ByteBuf buf = Unpooled.copiedBuffer(res.status().toString(),
					CharsetUtil.UTF_8);
			res.content().writeBytes(buf);
			buf.release();
			HttpHeaderUtil.setContentLength(res, res.content().readableBytes());
		}

		// Send the response and close the connection if necessary.
		ChannelFuture f = ctx.channel().writeAndFlush(res);
		/*
		 * if (!HttpHeaderUtil.isKeepAlive(req) || res.status().code() != 200) {
		 * f.addListener(ChannelFutureListener.CLOSE); }
		 */
	}

	private boolean writeResponse(HttpObject currentObj, ChannelHandlerContext ctx) {
		// Decide whether to close the connection or not.
		boolean keepAlive = HttpHeaderUtil.isKeepAlive(request);
		// Build the response object.
		FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1,
				currentObj.decoderResult().isSuccess() ? OK : BAD_REQUEST,
				Unpooled.copiedBuffer(buf.toString(), CharsetUtil.UTF_8));

		response.headers().set(CONTENT_TYPE, "text/plain; charset=UTF-8");

		if (keepAlive) {
			// Add 'Content-Length' header only for a keep-alive connection.
			response.headers().setInt(CONTENT_LENGTH, response.content().readableBytes());
			// Add keep alive header as per:
			// -
			// http://www.w3.org/Protocols/HTTP/1.1/draft-ietf-http-v11-spec-01.html#Connection
			response.headers().set(CONNECTION, HttpHeaderValues.KEEP_ALIVE);
		}
		// Write the response.
		ChannelFuture cf = ctx.write(response);
		ctx.flush();
		if (!cf.isSuccess()) {
			System.out.println("Send failed: " + cf.cause());
		}
		return keepAlive;
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		cause.printStackTrace();
		ctx.close();
	}
}
