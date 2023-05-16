package com.omnipotent.network;

import com.omnipotent.tools.Kaia;
import com.omnipotent.util.KaiaUtil;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class AbilityPacket implements IMessage {


    @Override
    public void fromBytes(ByteBuf buf) {

    }

    @Override
    public void toBytes(ByteBuf buf) {

    }

    public static class AbilityPacketHandler implements IMessageHandler<AbilityPacket, IMessage> {
        @Override
        public IMessage onMessage(AbilityPacket message, MessageContext ctx) {
            EntityPlayer player = ctx.getServerHandler().player;
            for (ItemStack stack : player.inventory.mainInventory) {
                if (stack.getItem() instanceof Kaia) {
                    KaiaUtil.dropKaiaOfInventory(stack, player);
                }
            }
            return null;
        }
    }
}
