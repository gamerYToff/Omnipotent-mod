package com.omnipotent.network.nbtpackets;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class KillAllEntitiesPacket implements IMessage {
    public static boolean killAllEntitiesPacket;

    public KillAllEntitiesPacket() {}

    public KillAllEntitiesPacket(boolean killAllEntitiesPacket){
        this.killAllEntitiesPacket = killAllEntitiesPacket;
    }
    @Override
    public void fromBytes(ByteBuf buf) {
        killAllEntitiesPacket = buf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeBoolean(killAllEntitiesPacket);
    }
    public static class KillAllEntitiesPacketHandler implements IMessageHandler<KillAllEntitiesPacket, IMessage>{

        @Override
        public IMessage onMessage(KillAllEntitiesPacket message, MessageContext ctx) {
            EntityPlayer player = ctx.getServerHandler().player;
            ItemStack kaiaItem = player.getHeldItemMainhand();
            boolean killAllEntitiesPacket = KillAllEntitiesPacket.killAllEntitiesPacket;
            kaiaItem.getTagCompound().setBoolean("killAllEntities", killAllEntitiesPacket);
            return null;
        }
    }
}
