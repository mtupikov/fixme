package com.core.encoders;


import com.core.messages.MessageExecuteOrReject;
import com.core.messages.MessageTypes;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import java.nio.charset.Charset;

public class ExecuteOrRejectEncoder extends MessageToByteEncoder<MessageExecuteOrReject> {
	private final Charset charset=Charset.forName("UTF-8");

	@Override
	protected void encode(ChannelHandlerContext ctx, MessageExecuteOrReject msg, ByteBuf out) throws Exception {
		out.writeInt(msg.getTypeLength());
		out.writeCharSequence(msg.getMessageType(), charset);
		if (msg.getMessageType().equals(MessageTypes.MESSAGE_EXECUTE.toString()) ||
			msg.getMessageType().equals(MessageTypes.MESSAGE_REJECT.toString())) {
			out.writeInt(msg.getActionLength());
			out.writeCharSequence(msg.getMessageAction(), charset);
			out.writeInt(msg.getId());
			out.writeInt(msg.getMarketId());
			out.writeInt(msg.getChecksumLength());
			out.writeCharSequence(msg.getChecksum(), charset);
		}
	}
}
