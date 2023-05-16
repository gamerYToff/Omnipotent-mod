package com.omnipotent.network.nbtpackets;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class KillFriendEntitiesPacket implements IMessage {
    public static boolean killFriendEntities;

    public KillFriendEntitiesPacket() {}

    public KillFriendEntitiesPacket(boolean killFriendEntities){
        this.killFriendEntities = killFriendEntities;
    }
    @Override
    public void fromBytes(ByteBuf buf) {
        killFriendEntities = buf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeBoolean(killFriendEntities);
    }
    public static class KillFriendEntitiesPacketHandler implements IMessageHandler<KillFriendEntitiesPacket, IMessage>{

        @Override
        public IMessage onMessage(KillFriendEntitiesPacket message, MessageContext ctx) {
            EntityPlayer player = ctx.getServerHandler().player;
            ItemStack kaiaItem = player.getHeldItemMainhand();
            boolean killFriendEntities = KillFriendEntitiesPacket.killFriendEntities;
            kaiaItem.getTagCompound().setBoolean("killFriendEntities", killFriendEntities);
            return null;
        }
    }
}
