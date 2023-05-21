package com.omnipotent.util;

import com.omnipotent.Event.UpdateEntity;
import com.omnipotent.Omnipotent;
import com.omnipotent.tools.Kaia;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.stats.StatList;
import net.minecraft.util.ClassInheritanceMultiMap;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.fml.common.FMLCommonHandler;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import static com.omnipotent.tools.KaiaConstantsNbt.ownerID;
import static com.omnipotent.tools.KaiaConstantsNbt.ownerName;

public class KaiaUtil {
    public static boolean isPlayer(Entity entity) {
        if (entity instanceof EntityPlayer) {
            return true;
        }
        return false;
    }

    public static boolean hasInInventoryKaia(Entity entity) {
        if (!isPlayer(entity)) {
            return false;
        }
        EntityPlayer player = (EntityPlayer) entity;
        boolean result = false;
        if (player.getHeldItem(EnumHand.OFF_HAND).getItem() instanceof Kaia)
            return true;
        for (ItemStack slot : player.inventory.mainInventory) {
            if (slot.getItem() instanceof Kaia) {
                result = true;
                break;
            }
        }
        return result;
    }

    public static void killArea(EntityLivingBase player) {
        EntityPlayer playerSource = (EntityPlayer) player;
        World world = playerSource.world;
        List<Entity> entities = new ArrayList<>();
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
            entities.removeIf(entity -> !(entity instanceof EntityMob) && !(entity instanceof EntityItem));
        }
        for (Entity entity : entities) {
            kill(entity, playerSource, killAllEntities);
        }
    }

    public static void kill(Entity entity, EntityPlayer playerSource, boolean killAllEntities) {
        if (entity instanceof EntityLivingBase) {
            EntityLivingBase entityCreature = (EntityLivingBase) entity;
            DamageSource ds = new AbsoluteOfCreatorDamage(playerSource);
            entityCreature.getCombatTracker().trackDamage(ds, Float.MAX_VALUE, Float.MAX_VALUE);
            entityCreature.setHealth(0.0F);
            entityCreature.onDeath(ds);
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
        if (trueSource.getHeldItemMainhand().getItem() instanceof Kaia)
            return true;
        return false;
    }

    /**
     * este método retorna null caso o jogador não tenha ele, deve ser usado apenas quando se tiver garantia que o jogador
     * tem o item Kaia e quando for necessario pegar uma Kaia que esteja na mainHand antenção este método pode gerar incompatiblidade é preferivel
     * sempre usar o método player.getHeldItemMainhand() ao invés dele.
     *
     * @param player
     * @return
     */
    public static ItemStack getKaiaOfPlayer(EntityPlayer player) {
        if (player.inventory.offHandInventory.get(0).getItem() instanceof Kaia)
            return player.inventory.offHandInventory.get(0).getItem().getDefaultInstance();
        for (ItemStack item : player.inventory.mainInventory) {
            if (item.getItem() instanceof Kaia) {
                return item;
            }
        }
        return null;
    }

    public static void sendMessageToAllPlayers(String message) {
        MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
        List<EntityPlayerMP> players = server.getPlayerList().getPlayers();
        for (EntityPlayerMP player : players) {
            player.sendMessage(new TextComponentString(message));
        }
    }

    public static boolean dropsOfBlockIsEmpty(EntityPlayerMP player, BlockPos pos) {
        IBlockState state = player.world.getBlockState(pos);
        Block block = state.getBlock();
        NonNullList<ItemStack> drops = NonNullList.create();
        block.getDrops(drops, player.world, pos, state, 0);
        if (drops.isEmpty()) {
            return true;
        }
        return false;
    }

    public static void breakBlocksArea(int areaBlock, EntityPlayer player, BlockPos centerPos) {
        int halfArea = areaBlock / 2;
        World world = player.getEntityWorld();
        for (int i = -halfArea; i <= halfArea; i++) {
            for (int j = -halfArea; j <= halfArea; j++) {
                for (int k = -halfArea; k <= halfArea; k++) {
                    BlockPos pos2 = centerPos.add(i, j, k);
                    IBlockState state = world.getBlockState(pos2);
                    Block block = state.getBlock();
                    if (!block.isAir(state, world, pos2)) {
                        if (dropsOfBlockIsEmpty((EntityPlayerMP) player, pos2)) {
                            breakOneBlock((EntityPlayerMP) player, pos2);
                        } else {
                            block.harvestBlock(world, player, pos2, state, null, player.getHeldItemMainhand());
                            world.destroyBlock(pos2, false);
                        }
                    }
                    for (int l = 1; l < areaBlock; l++) {
                        pos2 = centerPos.add(i, j + l, k);
                        state = world.getBlockState(pos2);
                        block = state.getBlock();
                        if (!block.isAir(state, world, pos2)) {
                            if (dropsOfBlockIsEmpty((EntityPlayerMP) player, pos2)) {
                                breakOneBlock((EntityPlayerMP) player, pos2);
                            } else {
                                block.harvestBlock(world, player, pos2, state, null, player.getHeldItemMainhand());
                                world.destroyBlock(pos2, false);
                            }
                        }
                    }
                }
            }
        }
    }

    public static void breakOneBlock(EntityPlayerMP player, BlockPos pos) {
        IBlockState state = player.world.getBlockState(pos);
        Block block = state.getBlock();
        NonNullList<ItemStack> drops = NonNullList.create();
        block.getDrops(drops, player.world, pos, state, 0);
        drops.add(block.getPickBlock(state, player.rayTrace(0.0f, 0.0f), player.world, pos, player));
        for (ItemStack dropStack : drops) {
            EntityItem item = new EntityItem(player.world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, dropStack);
            player.world.spawnEntity(item);
        }
        player.addStat(StatList.getBlockStats(block));
        player.world.destroyBlock(pos, false);
    }

    public static void decideBreakBlock(EntityPlayerMP player, BlockPos pos) {
        if (player.getHeldItemMainhand().getTagCompound().getInteger("areaBloco") > 1) {
            int areaBlock = player.getHeldItemMainhand().getTagCompound().getInteger("areaBloco");
            if (!player.world.isRemote && !player.capabilities.isCreativeMode && withKaiaMainHand(player)) {
                if (areaBlock % 2 != 0) {
                    breakBlocksArea(areaBlock, player, pos);
                }
            }
        } else {
            if (dropsOfBlockIsEmpty(player, pos)) {
                breakOneBlock(player, pos);
            } else {
                player.world.getBlockState(pos).getBlock().harvestBlock(player.world, player, pos, player.world.getBlockState(pos), null, player.getHeldItemMainhand());
                player.world.destroyBlock(pos, false);
            }
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
        if (player.getLastDamageSource() != null && player.getLastDamageSource().damageType.equals(new AbsoluteOfCreatorDamage(player).getDamageType())) {
            return true;
        }
        return false;
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
        World world = player.world;
        String name = player.getName();
        String uuid = player.getUniqueID().toString();
        if (!player.world.isRemote) {
            if (!UpdateEntity.chunkLoadList.isEmpty()) {
                Iterator<ChunkPos> chunkIterator = UpdateEntity.chunkLoadList.iterator();
                while (chunkIterator.hasNext()) {
                    ChunkPos chunkPos = chunkIterator.next();
                    Chunk chunk = world.getChunkFromChunkCoords(chunkPos.x, chunkPos.z);
                    if (!chunk.isLoaded())
                        continue;
                    ClassInheritanceMultiMap<Entity>[] entityLists = chunk.getEntityLists();
                    for (ClassInheritanceMultiMap<Entity> entityMinecraftList : entityLists) {
                        if (entityMinecraftList.isEmpty())
                            continue;
                        List<EntityItem> ListEntity = entityMinecraftList.stream().filter(entity -> entity instanceof EntityItem && ((EntityItem) entity).getItem().getItem() instanceof Kaia).map(entity -> (EntityItem) entity).collect(Collectors.toList());
                        for (EntityItem entity : ListEntity) {
                            ItemStack kaiaStack = ((EntityItem) entity).getItem();
                            if (kaiaStack.hasTagCompound() && isOwnerOfKaia(kaiaStack, player)) {
                                int emptySlot = player.inventory.getFirstEmptyStack();
                                if (emptySlot != -1) {
                                    player.inventory.setInventorySlotContents(emptySlot, kaiaStack);
                                } else {
                                    ItemStack item = player.inventory.getStackInSlot(0);
                                    player.dropItem(item, true);
                                    player.inventory.setInventorySlotContents(0, kaiaStack);
                                }
                                if (Omnipotent.chunkTicker != null) {
                                    ForgeChunkManager.Ticket ticket = Omnipotent.chunkTicker;//ForgeChunkManager.requestTicket(Omnipotent.instance, entity.world, ForgeChunkManager.Type.NORMAL);
                                    ForgeChunkManager.unforceChunk(ticket, chunk.getPos());
                                    chunkIterator.remove();
                                    entity.setDead();
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public static boolean isOwnerOfKaia(ItemStack kaiaStack, EntityPlayer player) {
        String nameOfOwner = kaiaStack.getTagCompound().getString(ownerName);
        String ownerUUID = kaiaStack.getTagCompound().getString(ownerID);
        if (nameOfOwner.equals(player.getName()) && ownerUUID.equals(player.getUniqueID().toString())) {
            return true;
        }
        return false;
    }
}
