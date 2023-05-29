package com.omnipotent.network;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketInicialization implements IMessage {
    public PacketInicialization() {}
    @Override
    public void fromBytes(ByteBuf buf) {}

    @Override
    public void toBytes(ByteBuf buf) {}
    public static class PacketInicializationHandler implements IMessageHandler<PacketInicialization, IMessage>{
        @Override
        public IMessage onMessage(PacketInicialization message, MessageContext ctx) {
            return null;
        }
    }
}
