package com.core.encoders;

import com.core.messages.*;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import java.nio.charset.Charset;

public class Encoder extends MessageToByteEncoder<FIXMessage> {
	private final Charset charset = Charset.forName("UTF-8");
	@Override
	protected void encode(ChannelHandlerContext ctx, FIXMessage msg, ByteBuf out) throws Exception {
		out.writeInt(msg.getTypeLength());
		out.writeCharSequence(msg.getMessageType(), charset);
		if (msg.getMessageType().equals(MessageTypes.MESSAGE_ACCEPT_CONNECTION.toString())) {
			MessageAcceptConnection io = (MessageAcceptConnection) msg;
			out.writeInt(io.getId());
			out.writeInt(io.getChecksumLength());
			out.writeCharSequence(io.getChecksum(), charset);
		} else if ( msg.getMessageType().equals(MessageTypes.MESSAGE_BUY.toString()) ||
					msg.getMessageType().equals(MessageTypes.MESSAGE_SELL.toString())) {
			MessageSellOrBuy io = (MessageSellOrBuy) msg;
			out.writeInt(io.getId());
			out.writeInt(io.getInstrumentLength());
			out.writeCharSequence(io.getInstrument(), charset);
			out.writeInt(io.getMarketId());
			out.writeInt(io.getPrice());
			out.writeInt(io.getChecksumLength());
			out.writeCharSequence(io.getChecksum(), charset);
		} else if (	msg.getMessageType().equals(MessageTypes.MESSAGE_EXECUTE.toString()) ||
					msg.getMessageType().equals(MessageTypes.MESSAGE_REJECT.toString())) {
			MessageExecuteOrReject io = (MessageExecuteOrReject) msg;
			out.writeInt(io.getActionLength());
			out.writeCharSequence(io.getMessageAction(), charset);
			out.writeInt(io.getId());
			out.writeInt(io.getMarketId());
			out.writeInt(io.getChecksumLength());
			out.writeCharSequence(io.getChecksum(), charset);
		}
	}
}