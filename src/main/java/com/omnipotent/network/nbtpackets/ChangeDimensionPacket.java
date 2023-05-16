package com.omnipotent.network.nbtpackets;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class ChangeDimensionPacket implements IMessage {
    public static int dimension;

    public ChangeDimensionPacket() {}

    public ChangeDimensionPacket(int dimension){
        this.dimension = dimension;
    }
    @Override
    public void fromBytes(ByteBuf buf) {
        dimension = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(dimension);
    }
    public static class ChangeDimensionHandler implements IMessageHandler<ChangeDimensionPacket, IMessage>{

        @Override
        public IMessage onMessage(ChangeDimensionPacket message, MessageContext ctx) {
            EntityPlayer player = ctx.getServerHandler().player;
            int dimension = ChangeDimensionPacket.dimension;
            player.changeDimension(dimension);
            return null;
        }
    }
}
