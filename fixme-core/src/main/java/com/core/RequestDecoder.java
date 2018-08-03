package com.core;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;

import java.nio.charset.Charset;
import java.util.List;

public class RequestDecoder extends ReplayingDecoder<FIXMessage> {
	private final Charset charset = Charset.forName("UTF-8");
	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
		FIXMessage data = new FIXMessage();
		data.setMessageType(in.readCharSequence(in.readInt(), charset).toString());
		data.setId(in.readInt());
		data.setInstrument(in.readCharSequence(in.readInt(), charset).toString());
		data.setMarketId(in.readInt());
		data.setPrice(in.readInt());
		data.setChecksum(in.readCharSequence(in.readInt(), charset).toString());
		out.add(data);
	}
}