package com.omnipotent.util;

import com.google.common.collect.Lists;
import com.omnipotent.tools.Kaia;
import net.minecraft.block.Block;
import net.minecraft.block.BlockChest;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.monster.EntityGolem;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntitySnowman;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityBat;
import net.minecraft.entity.passive.EntitySquid;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Enchantments;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.common.FMLCommonHandler;

import java.util.ArrayList;
import java.util.List;

import static com.omnipotent.tools.KaiaConstantsNbt.*;

public class KaiaUtil {
    public static List<Class> antiEntity = new ArrayList();

    public static boolean hasInInventoryKaia(Entity entity) {
        if (!isPlayer(entity)) {
            return false;
        }
        EntityPlayer player = (EntityPlayer) entity;
        if (player.getHeldItem(EnumHand.OFF_HAND).getItem() instanceof Kaia)
            return true;
        for (ItemStack slot : player.inventory.mainInventory) {
            if (slot.getItem() instanceof Kaia) {
                return true;
            }
        }
        return false;
    }

    public static void killArea(EntityLivingBase player) {
        EntityPlayer playerSource = (EntityPlayer) player;
        World world = playerSource.world;
        List<Entity> entities = Lists.newCopyOnWriteArrayList();
        int range = player.getHeldItemMainhand().getTagCompound().getInteger("rangeAttack");
        boolean killAllEntities = player.getHeldItemMainhand().getTagCompound().getBoolean("killAllEntities");
        boolean killFriendEntities = player.getHeldItemMainhand().getTagCompound().getBoolean("killFriendEntities");
        double slope = 0.1;
        for (int dist = 0; dist <= range; dist += 2) {
            AxisAlignedBB bb = playerSource.getEntityBoundingBox();
            Vec3d vec = playerSource.getLookVec();
            vec = vec.normalize();
            bb = bb.grow(slope * dist + 2.0, slope * dist + 0.25, slope * dist + 2.0);
            bb = bb.offset(vec.x * dist, vec.y * dist, vec.z * dist);
            List<Entity> list = world.getEntitiesWithinAABB(Entity.class, bb);
            entities.addAll(list);
        }
        entities.removeIf(entity -> isPlayer(entity) && hasInInventoryKaia((EntityPlayer) entity));
        if (!killFriendEntities) {
            entities.removeIf(entity -> entity instanceof EntityBat || entity instanceof EntitySquid || entity instanceof EntityAgeable || entity instanceof EntityAnimal || entity instanceof EntitySnowman || entity instanceof EntityGolem);
        }
        for (Entity entity : entities) {
            kill(entity, playerSource, killAllEntities);
        }
    }

    public static void kill(Entity entity, EntityPlayer playerSource, boolean killAllEntities) {
        if (entity instanceof EntityLivingBase && !(entity.world.isRemote || entity.isDead || ((EntityLivingBase) entity).getHealth() == 0.0F)) {
            EntityLivingBase entityCreature = (EntityLivingBase) entity;
            DamageSource ds = new AbsoluteOfCreatorDamage(playerSource);
            entityCreature.getCombatTracker().trackDamage(ds, Float.MAX_VALUE, Float.MAX_VALUE);
            entityCreature.attackEntityFrom(ds, Float.MAX_VALUE);
            antiEntity.add(antiEntity.getClass());
            entityCreature.onDeath(ds);
            antiEntity.remove(antiEntity.getClass());
            entityCreature.setHealth(0.0F);
        } else if (entity instanceof EntityPlayer && !hasInInventoryKaia(entity)) {
            EntityPlayer playerEnemie = (EntityPlayer) entity;
            DamageSource ds = new AbsoluteOfCreatorDamage(playerSource);
            playerEnemie.getCombatTracker().trackDamage(ds, Float.MAX_VALUE, Float.MAX_VALUE);
            playerEnemie.setHealth(0.0F);
            if (!playerEnemie.isDead) {
                playerEnemie.attemptTeleport(0, -1000, 0);
                dropAllInventory((EntityPlayer) playerEnemie);
                playerEnemie.onUpdate();
                ((EntityPlayer) playerEnemie).onLivingUpdate();
                playerEnemie.onEntityUpdate();
            }
            playerEnemie.onDeath(ds);
        } else if (killAllEntities) {
            entity.setDead();
        }
    }

    public static boolean withKaiaMainHand(EntityPlayer trueSource) {
        return trueSource.getHeldItemMainhand().getItem() instanceof Kaia;
    }

    public static void decideBreakBlock(EntityPlayerMP player, BlockPos pos) {
        if (getKaiaInMainHand(player).getTagCompound().getInteger(blockBreakArea) > 1) {
            int areaBlock = getKaiaInMainHand(player).getTagCompound().getInteger(blockBreakArea);
            if (!player.world.isRemote && !player.capabilities.isCreativeMode && withKaiaMainHand(player)) {
                if (areaBlock % 2 != 0) {
                    breakBlocksInArea(areaBlock, player, pos);
                }
            }
        } else {
            player.world.spawnEntity(new EntityXPOrb(player.world, pos.getX()+0.5, pos.getY()+0.5,pos.getZ()+0.5, (int) breakBlockIfDropsIsEmpty(player, pos)));
        }
    }

    public static void breakBlocksInArea(int areaBlock, EntityPlayer player, BlockPos centerPos) {
        World world = player.world;
        int startX = centerPos.getX() - areaBlock / 2;
        int endX = centerPos.getX() + areaBlock / 2;
        int startZ = centerPos.getZ() - areaBlock / 2;
        int endZ = centerPos.getZ() + areaBlock / 2;
        int startY = centerPos.getY() - areaBlock / 2;
        int endY = centerPos.getY() + areaBlock / 2;
        float xp = 0f;
        xp+=breakBlockIfDropsIsEmpty((EntityPlayerMP) player, centerPos);
        for (int x = startX; x <= endX; x++) {
            for (int z = startZ; z <= endZ; z++) {
                for (int y = startY; y <= endY; y++) {
                    BlockPos blockPos = new BlockPos(x, y, z);
                    if (!world.isAirBlock(blockPos)) {
                        xp+=breakBlockIfDropsIsEmpty((EntityPlayerMP) player, blockPos);
                    }
                }
            }
        }
        world.spawnEntity(new EntityXPOrb(player.world, centerPos.getX()+0.5, centerPos.getY()+0.5, centerPos.getZ()+0.5, (int) xp));
    }

    public static float breakBlockIfDropsIsEmpty(EntityPlayerMP player, BlockPos pos) {
        IBlockState state = player.world.getBlockState(pos);
        Block block = state.getBlock();
        NonNullList<ItemStack> drops = NonNullList.create();
        int enchLevelFortune = EnchantmentHelper.getEnchantmentLevel(Enchantments.FORTUNE, getKaiaInMainHand(player));
        Float xp = 0f;
        block.getDrops(drops, player.world, pos, state, enchLevelFortune);
        if (drops.isEmpty()) {
            drops.add(block.getPickBlock(state, player.rayTrace(0.0f, 0.0f), player.world, pos, player));
        }
        for (ItemStack dropStack : drops) {
            EntityItem item = new EntityItem(player.world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, dropStack);
            player.world.spawnEntity(item);
        }
        xp+=block.getExpDrop(state, player.world, pos, enchLevelFortune);
        player.addStat(StatList.getBlockStats(block));
        player.world.destroyBlock(pos, false);
        return xp;
    }

    public static void sendMessageToAllPlayers(String message) {
        MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
        List<EntityPlayerMP> players = server.getPlayerList().getPlayers();
        for (EntityPlayerMP player : players) {
            player.sendMessage(new TextComponentString(message));
        }
    }

    public static void dropKaiaOfInventory(ItemStack stack, EntityPlayer player) {
        player.dropItem(stack, false);
        player.inventory.deleteStack(stack);
    }

    public static void createTagCompoundStatusIfNecessary(ItemStack stack) {
        if (stack.getTagCompound() == null) {
            NBTTagCompound status = new NBTTagCompound();
            stack.setTagCompound(status);
        }
    }

    public static void createOwnerIfNecessary(ItemStack stack, Entity entityIn) {
        if (!stack.getTagCompound().hasKey(ownerName)) {
            NBTTagCompound status = stack.getTagCompound();
            status.setString(ownerName, entityIn.getName());
        }
        if (!stack.getTagCompound().hasKey(ownerID)) {
            NBTTagCompound status = stack.getTagCompound();
            status.setString(ownerID, entityIn.getUniqueID().toString());
        }
    }

    public static void dropAllInventory(EntityPlayer player) {
        for (ItemStack item : player.inventory.mainInventory) {
            player.dropItem(true);
        }
    }

    public static boolean theLastAttackIsKaia(EntityPlayer player) {
        return player.getLastDamageSource() != null && player.getLastDamageSource().damageType.equals(new AbsoluteOfCreatorDamage(player).getDamageType());
    }

    public static void clearPlayer(EntityPlayer player) {
        IInventory playerInventory = player.inventory;
        for (int i = 0; i < playerInventory.getSizeInventory(); i++) {
            ItemStack itemStack = playerInventory.getStackInSlot(i);
            if (!itemStack.isEmpty()) {
                playerInventory.removeStackFromSlot(i);
            }
        }
    }

    public static void returnKaiaOfOwner(EntityPlayer player) {
        World world = DimensionManager.getWorld(0);
        if(world == null) return;
        String name = player.getName();
        String uuidPlayer = player.getUniqueID().toString();
        BlockPos pos = new BlockPos(405545454, 0, 28938293);
        TileEntity tileEntity = world.getTileEntity(pos);
        if (tileEntity == null) {
            world.destroyBlock(pos, false);
            world.setBlockState(pos, Blocks.CHEST.getDefaultState());
            TileEntityChest tileEntityChest = (TileEntityChest) DimensionManager.getWorld(0).getTileEntity(pos);
            for (int index = 0; index < tileEntityChest.getSizeInventory(); index++) {
                ItemStack stackInSlot = tileEntityChest.getStackInSlot(index);
                if (!stackInSlot.isEmpty() && stackInSlot.getItem() instanceof Kaia && isOwnerOfKaia(stackInSlot, player) && !stackInSlot.isEmpty()) {
                    player.inventory.addItemStackToInventory(stackInSlot);
                    tileEntityChest.setInventorySlotContents(index, ItemStack.EMPTY);
                }
            }
        } else if (tileEntity.getBlockType() instanceof BlockChest) {
            TileEntityChest tileEntityChest = (TileEntityChest) world.getTileEntity(pos);
            for (int index = 0; index < tileEntityChest.getSizeInventory(); index++) {
                ItemStack stackInSlot = tileEntityChest.getStackInSlot(index);
                if (!stackInSlot.isEmpty() && stackInSlot.getItem() instanceof Kaia && isOwnerOfKaia(stackInSlot, player) && !stackInSlot.isEmpty()) {
                    player.inventory.addItemStackToInventory(stackInSlot);
                    tileEntityChest.setInventorySlotContents(index, ItemStack.EMPTY);
                }
            }
        } else {
            TileEntityChest tileEntityChest = (TileEntityChest) world.getTileEntity(pos);
            for (int index = 0; index < tileEntityChest.getSizeInventory(); index++) {
                ItemStack stackInSlot = tileEntityChest.getStackInSlot(index);
                if (!stackInSlot.isEmpty() && stackInSlot.getItem() instanceof Kaia && isOwnerOfKaia(stackInSlot, player) && !stackInSlot.isEmpty()) {
                    player.inventory.addItemStackToInventory(stackInSlot);
                    tileEntityChest.setInventorySlotContents(index, ItemStack.EMPTY);
                }
            }
        }
    }

    public static ItemStack getKaiaInMainHand(EntityPlayer player) {
        return player.getHeldItemMainhand();
    }

    public static boolean isOwnerOfKaia(ItemStack kaiaStack, EntityPlayer player) {
        String nameOfOwner = kaiaStack.getTagCompound().getString(ownerName);
        String ownerUUID = kaiaStack.getTagCompound().getString(ownerID);
        return nameOfOwner.equals(player.getName()) && ownerUUID.equals(player.getUniqueID().toString());
    }

    public static boolean isPlayer(Entity entity) {
        if (entity instanceof EntityPlayer) {
            return true;
        }
        return false;
    }
}
