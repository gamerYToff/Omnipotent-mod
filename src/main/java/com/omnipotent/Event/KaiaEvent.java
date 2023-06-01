package com.omnipotent.Event;

import com.omnipotent.network.KillPacket;
import com.omnipotent.network.NetworkRegister;
import com.omnipotent.network.SummonLightEasterEggPacket;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
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
            } else if (event.getSource().getTrueSource() != null && isPlayer(event.getSource().getTrueSource()) && withKaiaMainHand((EntityPlayer) event.getSource().getTrueSource())) {
                EntityPlayer entityTarget = (EntityPlayer) event.getEntityLiving();
                if (event.isCanceled()) {
                    event.setCanceled(false);
                    entityTarget.setHealth(0.0f);
                    entityTarget.inventory.dropAllItems();
                    if (entityTarget.getHeldItemOffhand() != null) {
                        ItemStack item = entityTarget.getHeldItemOffhand().copy();
                        entityTarget.setHeldItem(EnumHand.OFF_HAND, null);
                        player.world.spawnEntity(new EntityItem(player.world, player.posX, player.posY, player.posZ, item));
                    }
                }
                event.setCanceled(false);
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
            } else if (isPlayer(event.getSource().getTrueSource()) && withKaiaMainHand((EntityPlayer) event.getSource().getTrueSource())) {
                event.setCanceled(false);
                event.getEntityLiving().setHealth(0.0f);
            }
        }
    }

    @SubscribeEvent(receiveCanceled = true, priority = EventPriority.LOWEST)
    public void damageEvent(LivingAttackEvent event) {
        if (isPlayer(event.getEntityLiving())) {
            EntityPlayer player = (EntityPlayer) event.getEntityLiving();
            if (hasInInventoryKaia(player)) {
                player.setHealth(Integer.MAX_VALUE);
                event.setCanceled(true);
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

    @SubscribeEvent(receiveCanceled = true, priority = EventPriority.LOWEST)
    public void attackLiving(LivingAttackEvent event) {
        if (isPlayer(event.getEntity()) && hasInInventoryKaia((EntityPlayer) event.getEntity())) {
            event.setCanceled(true);
        } else if (isPlayer(event.getSource().getTrueSource()) && withKaiaMainHand((EntityPlayer) event.getSource().getTrueSource())) {
            event.setCanceled(false);
            event.getEntityLiving().setHealth(0.0f);
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
