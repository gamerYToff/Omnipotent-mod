package com.omnipotent.Event;

import com.omnipotent.tools.Kaia;
import com.omnipotent.util.KaiaUtil;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.item.ItemExpireEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.*;

import static com.omnipotent.util.KaiaUtil.hasInInventoryKaia;


public class UpdateEntity {
    public static final Set<String> entitiesWithKaia = new HashSet<>();
    public static final Set<String> entitiesFlightKaia = new HashSet<>();
    public static ArrayList<EntityLivingBase> mobsNamedMkll = new ArrayList<>();
    private static Map<EntityLivingBase, Integer> timeTeleportation = new HashMap<>();

    @SubscribeEvent
    public void updateAbilities(LivingEvent.LivingUpdateEvent event) {
        if (!(event.getEntity() instanceof EntityPlayer)) {
            defineTimeAndListEasterEggMkll(event);
            return;
        }
        EntityPlayer player = (EntityPlayer) event.getEntityLiving();
        String keyUID = player.getCachedUniqueIdString() + "|" + player.world.isRemote;
        boolean hasKaia = hasInInventoryKaia(player);
        if (hasKaia) {
            if (player.getHealth() < 100) {
                player.isDead = false;
                player.setHealth(Integer.MAX_VALUE);
            }
        }
        try {
            if (!player.getActivePotionEffects().isEmpty() && !player.world.isRemote && hasInInventoryKaia(player)) {
                for (PotionEffect effect : player.getActivePotionEffects()) {
                    Potion effectPotion = effect.getPotion();
                    if (effectPotion.isBadEffect()) {
                        player.removePotionEffect(effectPotion);
                    } else if (effect.getAmplifier() < 250 && effect.getDuration() < Integer.MAX_VALUE) {
                        managerStandartEffectPotions(player, false);
                    }
                }
            } else if (!player.world.isRemote && hasInInventoryKaia(player)) {
                managerStandartEffectPotions(player, false);
            }
        } catch (
                Exception e) {
        }
        if (!entitiesWithKaia.contains(keyUID)) {
            managerStandartEffectPotions(player, true);
        }

        if (hasKaia) {
            entitiesWithKaia.add(keyUID);
            handleKaiaStateChange(player, true);
        }
        if (!hasKaia) {
            entitiesWithKaia.remove(keyUID);
            handleKaiaStateChange(player, false);
        }

    }

    private static void defineTimeAndListEasterEggMkll(LivingEvent.LivingUpdateEvent event) {
        EntityLivingBase entity = event.getEntityLiving();
        if (entity.hasCustomName() && entity.getCustomNameTag().equals("mkll") && !mobsNamedMkll.contains(entity) && !entity.world.isRemote) {
            mobsNamedMkll.add(entity);
            timeTeleportation.put(entity, 1);
        } else if (mobsNamedMkll.contains(entity) && !entity.getCustomNameTag().equals("mkll")) {
            timeTeleportation.remove(entity);
            mobsNamedMkll.remove(entity);
        }
    }

    private static void stripAbilities(EntityLivingBase entity) {
        String key = entity.getCachedUniqueIdString() + "|" + entity.world.isRemote;
        if (entitiesWithKaia.remove(key)) {
            handleKaiaStateChange(entity, false);
        }
    }

    private static void handleKaiaStateChange(EntityLivingBase entity, boolean isNew) {
        String keyUID = entity.getCachedUniqueIdString() + "|" + entity.world.isRemote;
        if (entity instanceof EntityPlayer) {
            EntityPlayer player = ((EntityPlayer) entity);
            if (isNew) {
                player.capabilities.allowFlying = true;
                player.capabilities.setFlySpeed(0.15f);
                entitiesFlightKaia.add(keyUID);
            } else {
                if (!player.capabilities.isCreativeMode && entitiesFlightKaia.contains(keyUID)) {
                    player.capabilities.allowFlying = false;
                    player.capabilities.isFlying = false;
                    entitiesFlightKaia.remove(keyUID);
                }
            }
        }
    }

    private static void managerStandartEffectPotions(EntityPlayer player, boolean removeEffect) {
        if (removeEffect) {
            player.removePotionEffect(MobEffects.NIGHT_VISION);
            player.removePotionEffect(MobEffects.REGENERATION);
            player.removePotionEffect(MobEffects.FIRE_RESISTANCE);
            player.removePotionEffect(MobEffects.SATURATION);
            player.removePotionEffect(MobEffects.WATER_BREATHING);
        } else {
            player.addPotionEffect(new PotionEffect(MobEffects.NIGHT_VISION, Integer.MAX_VALUE, 255));
            player.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, Integer.MAX_VALUE, 255));
            player.addPotionEffect(new PotionEffect(MobEffects.FIRE_RESISTANCE, Integer.MAX_VALUE, 255));
            player.addPotionEffect(new PotionEffect(MobEffects.SATURATION, Integer.MAX_VALUE, 255));
            player.addPotionEffect(new PotionEffect(MobEffects.WATER_BREATHING, Integer.MAX_VALUE, 255));
        }
    }

    @SubscribeEvent
    public static void cancelTimeItem(ItemExpireEvent event) {
        if (event.getEntityItem().getItem().getItem() instanceof Kaia) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent()
    public void entityPickupKaia(EntityItemPickupEvent event) {
        if (event.getEntityPlayer().world.isRemote)
            return;
        if (!(event.getEntity() instanceof EntityPlayer) || !(event.getItem().getItem().getItem() instanceof Kaia))
            return;

        EntityPlayer player = event.getEntityPlayer();
        ItemStack item = event.getItem().getItem();
        if (!item.hasTagCompound())
            return;
        String ownerName = item.getTagCompound().getString("ownerName");
        String ownerID = item.getTagCompound().getString("ownerID");
        if (!player.getName().equals(ownerName) || !player.getUniqueID().toString().equals(ownerID)) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onEntityItemJoinWorld(EntityJoinWorldEvent event) {
        if (event.getEntity() instanceof EntityItem) {
            EntityItem entityItem = (EntityItem) event.getEntity();
            if (!entityItem.getItem().isEmpty() && entityItem.getItem().getItem() instanceof Kaia) {
                entityItem.setEntityInvulnerable(true);
                entityItem.setNoPickupDelay();
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void tickUpdate(TickEvent.WorldTickEvent event) {
        easterEggFunctionMkllVerify();
        /*List<EntityItem> itemsKaiaEntity = event.world.loadedEntityList.stream().filter(entity -> entity instanceof EntityItem && ((EntityItem) entity).getItem().getItem() instanceof Kaia).map(entity -> ((EntityItem) entity)).collect(Collectors.toList());
        List<EntityItem> entitiesKaiaInWorldUpdate = null;
        if (itemsKaiaEntity.isEmpty()) {
            return;
        }
        boolean collectByPlayer = false;
        for (EntityItem entity : itemsKaiaEntity) {
            if (entity.isDead) {
                List<EntityPlayer> players = event.world.playerEntities;
                EntityPlayer playerCollect = null;
                for (EntityPlayer player : event.world.playerEntities) {
                    double distance = player.getDistance(player.posX, player.posY, player.posZ);
                    if (distance < 5.0) {
                        playerCollect = player;
                        break;
                    }
                }
                EntityPlayer playerTarget = playerCollect;
                entitiesKaiaInWorldUpdate = event.world.getLoadedEntityList().stream().filter(entity1 -> entity1 instanceof EntityItem && ((EntityItem) entity1).getItem().getItem() instanceof Kaia && hasInInventoryKaia(playerTarget)).map(entity1 -> ((EntityItem) entity1)).collect(Collectors.toList());
                if (!entitiesKaiaInWorldUpdate.stream().filter(entityItem -> entityItem.getCachedUniqueIdString().equals(entity.getCachedUniqueIdString())).map(entityItem -> ((String) entityItem.getCachedUniqueIdString())).collect(Collectors.toList()).isEmpty()) {
                    collectByPlayer = true;
                }
                if (!collectByPlayer) {
                    entity.isDead = false;
                }
            }
        }*/
    }

    private static void easterEggFunctionMkllVerify() {
        if (!mobsNamedMkll.isEmpty()) {
            for (EntityLivingBase entity : mobsNamedMkll) {
                if (timeTeleportation.get(entity) % 100 == 0) {
                    if (entity.isDead) {
                        mobsNamedMkll.remove(entity);
                        timeTeleportation.remove(entity);
                        return;
                    }
                    entity.attemptTeleport(entity.posX + 10, entity.posY + 3, entity.posZ + 10);
                    entity.world.spawnAlwaysVisibleParticle(EnumParticleTypes.PORTAL.getParticleID(), entity.posX, entity.posY, entity.posZ, 0, 0, 0, new int[0]);
                    entity.world.playSound(null, entity.posX, entity.posY, entity.posZ, SoundEvents.ENTITY_ENDERMEN_TELEPORT, SoundCategory.HOSTILE, 5.0f, 1.0f);
                    int timeTeleportationEntity = timeTeleportation.get(entity);
                    timeTeleportationEntity = 1;
                    timeTeleportation.put(entity, timeTeleportationEntity);
                } else {
                    int timeTeleportationEntity = timeTeleportation.get(entity);
                    timeTeleportationEntity++;
                    timeTeleportation.put(entity, timeTeleportationEntity);
                }
            }
        }
    }
}