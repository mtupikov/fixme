package com.core;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import java.nio.charset.Charset;

public class ResponseEncoder extends MessageToByteEncoder<FIXMessage> {
	private final Charset charset = Charset.forName("UTF-8");
	@Override
	protected void encode(ChannelHandlerContext ctx, FIXMessage msg, ByteBuf out) throws Exception {
		out.writeInt(msg.getTypeLength());
		out.writeCharSequence(msg.getMessageType(), charset);
		out.writeInt(msg.getId());
		out.writeInt(msg.getInstrumentLength());
		out.writeCharSequence(msg.getInstrument(), charset);
		out.writeInt(msg.getMarketId());
		out.writeInt(msg.getPrice());
		out.writeInt(msg.getChecksumLength());
		out.writeCharSequence(msg.getChecksum(), charset);
	}
}