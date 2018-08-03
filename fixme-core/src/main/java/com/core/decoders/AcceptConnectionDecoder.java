package com.core.decoders;

import com.core.messages.MessageAcceptConnection;
import com.core.messages.MessageTypes;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;

import java.nio.charset.Charset;
import java.util.List;

public class AcceptConnectionDecoder extends ReplayingDecoder<MessageAcceptConnection> {
	private final Charset charset = Charset.forName("UTF-8");

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
		MessageAcceptConnection msg = new MessageAcceptConnection();
		msg.setMessageType(in.readCharSequence(in.readInt(), charset).toString());
		if (msg.getMessageType().equals(MessageTypes.MESSAGE_ACCEPT_CONNECTION.toString())) {
			msg.setId(in.readInt());
			msg.setChecksum(in.readCharSequence(in.readInt(), charset).toString());
			out.add(msg);
			System.out.println("Receiving: " + msg);
		}
	}
}
