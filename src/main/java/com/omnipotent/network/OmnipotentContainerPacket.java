package com.omnipotent.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import static com.omnipotent.gui.GuiHandler.GuiIDs.ID_MOD;

public class OmnipotentContainerPacket implements IMessage {

    public OmnipotentContainerPacket(){
    }
    @Override
    public void fromBytes(ByteBuf buf) {

    }

    @Override
    public void toBytes(ByteBuf buf) {

    }
    public static class AazominipotentContainerPacketHandler implements IMessageHandler<OmnipotentContainerPacket, IMessage>{

        @Override
        public IMessage onMessage(OmnipotentContainerPacket message, MessageContext ctx) {
            EntityPlayerMP player = ctx.getServerHandler().player;
            player.openGui(this, ID_MOD.ordinal(),player.world , 0, 0, 0);
            return null;
        }
    }
}
