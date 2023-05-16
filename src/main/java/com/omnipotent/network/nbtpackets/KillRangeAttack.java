package com.omnipotent.network.nbtpackets;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class KillRangeAttack implements IMessage {
    public static int killRangeAttack;

    public KillRangeAttack() {}

    public KillRangeAttack(int killRangeAttack){
        this.killRangeAttack = killRangeAttack;
    }
    @Override
    public void fromBytes(ByteBuf buf) {
        killRangeAttack = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(killRangeAttack);
    }
    public static class KillRangeAttackHandler implements IMessageHandler<KillRangeAttack, IMessage>{

        @Override
        public IMessage onMessage(KillRangeAttack message, MessageContext ctx) {
            EntityPlayer player = ctx.getServerHandler().player;
            ItemStack kaiaItem = player.getHeldItemMainhand();
            int killRangeAttack = KillRangeAttack.killRangeAttack;
            kaiaItem.getTagCompound().setInteger("rangeAttack", killRangeAttack);
            return null;
        }
    }
}
