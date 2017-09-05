package me.framework.rpc.core.server;

import com.google.common.reflect.Reflection;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import me.framework.rpc.model.MessageRequest;
import me.framework.rpc.model.MessageResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ReflectionUtils;

import java.util.Map;

/**
 * @author paranoidq
 * @since 1.0.0
 */
public class MessageRecvHandler extends ChannelInboundHandlerAdapter {
    private static final Logger logger = LoggerFactory.getLogger(MessageRecvHandler.class);

    private Map<String, Object> handlerMap;

    public MessageRecvHandler(Map<String, Object> handlerMap) {
        this.handlerMap = handlerMap;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        MessageRequest request = (MessageRequest) msg;
        logger.info("Receive MessageRequest：messageId=" + request.getMessageId());
        MessageResponse response = new MessageResponse();
        MessageRecvHandleTask task = new MessageRecvHandleTask(request, response, handlerMap);
        MessageRecvExecutor.submit(task, ctx, request, response);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }
}
