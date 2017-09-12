package me.framework.rpc.api;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandler;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.*;
import me.framework.rpc.api.ability.AbilityDetailProvider;
import me.framework.rpc.logger.AppLoggerInject;
import org.slf4j.Logger;

/**
 * @author paranoidq
 * @since 1.0.0
 */
public class ApiEchoHandler extends ChannelInboundHandlerAdapter {

    @AppLoggerInject
    private static Logger logger;

    private static final String CONTENT_TYPE = "Content-Type";
    private static final String CONTENT_LENGTH = "Content-Length";
    private static final String CONNECTION = "Connection";
    private static final String KEEP_ALIVE = "Keep-Alive";

    public ApiEchoHandler() {
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof HttpRequest) {
            HttpRequest request = (HttpRequest) msg;
            AbilityDetailProvider provider = new AbilityDetailProvider();
            byte[] content = provider.listAbilityDetail(true).toString().getBytes();
            FullHttpResponse response = new DefaultFullHttpResponse(
                HttpVersion.HTTP_1_1, HttpResponseStatus.OK, Unpooled.wrappedBuffer(content));
            response.headers().set(CONTENT_TYPE, "text/html");
            response.headers().set(CONTENT_LENGTH, response.content());
            response.headers().set(CONNECTION, KEEP_ALIVE);
            ctx.write(response);
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("ApiEchoHandler处理异常", cause);
        ctx.close();
    }
}
