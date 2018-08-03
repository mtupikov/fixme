package com.core.decoders;

import com.core.messages.*;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;
import java.nio.charset.Charset;
import java.util.List;

public class Decoder extends ReplayingDecoder<FIXMessage> {
	private final Charset charset = Charset.forName("UTF-8");

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
		System.out.println("decoding");
		FIXMessage msg = new FIXMessage();
		msg.setMessageType(in.readCharSequence(in.readInt(), charset).toString());
		if (msg.getMessageType().equals(MessageTypes.MESSAGE_ACCEPT_CONNECTION.toString())) {
			MessageAcceptConnection io = new MessageAcceptConnection(msg);
			io.setId(in.readInt());
			io.setChecksum(in.readCharSequence(in.readInt(), charset).toString());
			out.add(io);
		} else if (	msg.getMessageType().equals(MessageTypes.MESSAGE_BUY.toString()) ||
				msg.getMessageType().equals(MessageTypes.MESSAGE_SELL.toString())) {
			MessageSellOrBuy io = new MessageSellOrBuy(msg);
			io.setId(in.readInt());
			io.setInstrument(in.readCharSequence(in.readInt(), charset).toString());
			io.setMarketId(in.readInt());
			io.setPrice(in.readInt());
			io.setChecksum(in.readCharSequence(in.readInt(), charset).toString());
			out.add(io);
		} else if (	msg.getMessageType().equals(MessageTypes.MESSAGE_EXECUTE.toString()) ||
				msg.getMessageType().equals(MessageTypes.MESSAGE_REJECT.toString())) {
			MessageExecuteOrReject io = new MessageExecuteOrReject(msg);
			io.setMessageAction(in.readCharSequence(in.readInt(), charset).toString());
			io.setId(in.readInt());
			io.setMarketId(in.readInt());
			io.setChecksum(in.readCharSequence(in.readInt(), charset).toString());
			out.add(io);
		}
	}
}