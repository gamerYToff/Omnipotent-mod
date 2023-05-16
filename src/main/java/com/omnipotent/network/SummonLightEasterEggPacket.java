package com.omnipotent.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class SummonLightEasterEggPacket implements IMessage {

    public SummonLightEasterEggPacket() {
    }

    @Override
    public void fromBytes(ByteBuf buf) {

    }

    @Override
    public void toBytes(ByteBuf buf) {

    }

    public static class SummonLightEasterEggPacketHandler implements IMessageHandler<SummonLightEasterEggPacket, IMessage> {
        @Override
        public IMessage onMessage(SummonLightEasterEggPacket message, MessageContext ctx) {
            EntityPlayerMP player = ctx.getServerHandler().player;
            ItemStack heldItemMainhand = player.getHeldItemMainhand();
            if (heldItemMainhand.getItem().equals(Item.getItemById(258)) && !heldItemMainhand.isItemDamaged()) {
                EntityLightningBolt light = null;
                if (player.isSneaking()) {
                    BlockPos blockPos = player.rayTrace(100, 0).getBlockPos();
                    light = new EntityLightningBolt(player.world, blockPos.getX(), blockPos.getY(), blockPos.getZ(), false);
                    player.world.addWeatherEffect(light);
                } else {
                    light = new EntityLightningBolt(player.world, player.posX, player.posY, player.posZ, false);
                    player.world.addWeatherEffect(light);
                }
            }
            return null;
        }
    }
}
