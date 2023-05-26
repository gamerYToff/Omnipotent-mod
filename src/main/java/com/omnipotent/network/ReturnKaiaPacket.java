package com.omnipotent.network;

import com.omnipotent.util.KaiaUtil;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import static com.omnipotent.gui.GuiHandler.GuiIDs.ID_MOD;

public class ReturnKaiaPacket implements IMessage {

    public ReturnKaiaPacket(){
    }
    @Override
    public void fromBytes(ByteBuf buf) {
    }

    @Override
    public void toBytes(ByteBuf buf) {

    }
    public static class ReturnKaiaPacketHandler implements IMessageHandler<ReturnKaiaPacket, IMessage>{
        @Override
        public IMessage onMessage(ReturnKaiaPacket message, MessageContext ctx) {
            KaiaUtil.returnKaiaOfOwner(ctx.getServerHandler().player);
            return null;
        }
    }
}
