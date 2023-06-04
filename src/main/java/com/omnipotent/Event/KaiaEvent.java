package com.omnipotent.Event;

import com.omnipotent.network.KillPacket;
import com.omnipotent.network.NetworkRegister;
import com.omnipotent.network.SummonLightEasterEggPacket;
import com.omnipotent.tools.KaiaConstantsNbt;
import com.omnipotent.util.KaiaUtil;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import static com.omnipotent.util.KaiaUtil.*;

public class KaiaEvent {
    @SubscribeEvent
    public void playerAttack(PlayerInteractEvent.LeftClickEmpty event) {
        EntityPlayer player = event.getEntityPlayer();
        ItemStack heldItemMainhand = player.getHeldItemMainhand();
        if (withKaiaMainHand(player)) {
            if (heldItemMainhand.getTagCompound().getInteger("rangeAttack") > 1) {
                NetworkRegister.ACESS.sendToServer(new KillPacket());
            }
        } else if (heldItemMainhand.getItem().equals(Item.getItemById(258)) && !heldItemMainhand.isItemDamaged()) {
            NetworkRegister.ACESS.sendToServer(new SummonLightEasterEggPacket());
        }
    }

    @SubscribeEvent(receiveCanceled = true, priority = EventPriority.LOWEST)
    public void killEvent(LivingDeathEvent event) {
        if (isPlayer(event.getEntityLiving())) {
            EntityPlayer player = (EntityPlayer) event.getEntityLiving();
            if (hasInInventoryKaia(player)) {
                player.setHealth(Integer.MAX_VALUE);
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent(receiveCanceled = true, priority = EventPriority.LOWEST)
    public void attackEvent(LivingAttackEvent event) {
        if (isPlayer(event.getEntity())) {
            EntityPlayer player = (EntityPlayer) event.getEntity();
            if (hasInInventoryKaia(player)) {
                player.setHealth(Integer.MAX_VALUE);
                event.setCanceled(true);
                NBTTagCompound tagCompoundOfKaia = getKaiaInInventory(player).getTagCompound();
                if (tagCompoundOfKaia.getBoolean(KaiaConstantsNbt.counterAttack)) {
                    if(!isPlayer(event.getSource().getTrueSource()) || (isPlayer(event.getSource().getTrueSource()) && !hasInInventoryKaia(event.getSource().getTrueSource())) ){
                        KaiaUtil.kill(event.getSource().getTrueSource(), player, tagCompoundOfKaia.getBoolean(KaiaConstantsNbt.killAllEntities));
                    }
                }
            }
        }
    }

    @SubscribeEvent(receiveCanceled = true, priority = EventPriority.LOWEST)
    public void damageHurt(LivingHurtEvent event) {
        if (isPlayer(event.getEntity()) && hasInInventoryKaia((EntityPlayer) event.getEntity())) {
            event.setCanceled(true);
        } else if (isPlayer(event.getSource().getTrueSource()) && withKaiaMainHand((EntityPlayer) event.getSource().getTrueSource())) {
            event.setCanceled(false);
        }
    }

    @SubscribeEvent
    public void playerClickBlock(PlayerInteractEvent.LeftClickBlock event) {
        EntityPlayer entityPlayer = event.getEntityPlayer();
        if (withKaiaMainHand(entityPlayer) && !entityPlayer.world.isRemote && entityPlayer instanceof EntityPlayerMP && !entityPlayer.capabilities.isCreativeMode) {
            decideBreakBlock((EntityPlayerMP) entityPlayer, event.getPos());
        }
    }
}
