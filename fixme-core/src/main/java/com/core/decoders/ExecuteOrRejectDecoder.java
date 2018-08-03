package com.core.decoders;

import com.core.messages.MessageExecuteOrReject;
import com.core.messages.MessageTypes;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;

import java.nio.charset.Charset;
import java.util.List;

public class ExecuteOrRejectDecoder extends ReplayingDecoder<MessageExecuteOrReject> {
	private final Charset charset = Charset.forName("UTF-8");

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
		MessageExecuteOrReject msg = new MessageExecuteOrReject();
		msg.setMessageType(in.readCharSequence(in.readInt(), charset).toString());
		if (msg.getMessageType().equals(MessageTypes.MESSAGE_EXECUTE.toString()) ||
			msg.getMessageType().equals(MessageTypes.MESSAGE_REJECT.toString())) {
			msg.setMessageAction(in.readCharSequence(in.readInt(), charset).toString());
			msg.setId(in.readInt());
			msg.setMarketId(in.readInt());
			msg.setChecksum(in.readCharSequence(in.readInt(), charset).toString());
			out.add(msg);
		}
	}
}

