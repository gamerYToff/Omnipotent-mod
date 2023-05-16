package com.omnipotent.network.nbtpackets;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class AreaBlocoPacket implements IMessage {
    public static int areaBloco;

    public AreaBlocoPacket() {}

    public AreaBlocoPacket(int areaBloco){
        this.areaBloco = areaBloco;
    }
    @Override
    public void fromBytes(ByteBuf buf) {
        areaBloco = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(areaBloco);
    }
    public static class AreaBlocoPacketHandler implements IMessageHandler<AreaBlocoPacket, IMessage>{

        @Override
        public IMessage onMessage(AreaBlocoPacket message, MessageContext ctx) {
            EntityPlayer player = ctx.getServerHandler().player;
            ItemStack kaiaItem = player.getHeldItemMainhand();
            int areaBloco = AreaBlocoPacket.areaBloco;
            kaiaItem.getTagCompound().setInteger("areaBloco", areaBloco);
            return null;
        }
    }
}
