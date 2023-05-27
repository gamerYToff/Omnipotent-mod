package com.omnipotent.tools;

import com.omnipotent.util.KaiaUtil;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static com.omnipotent.Omnipotent.omnipotentTab;
import static com.omnipotent.tools.KaiaConstantsNbt.*;

public class Kaia extends ItemPickaxe {
    public Kaia() {
        super(EnumHelper.addToolMaterial("kaia", Integer.MAX_VALUE, Integer.MAX_VALUE, Float.MAX_VALUE, Float.MAX_VALUE, Integer.MAX_VALUE));
        setUnlocalizedName("kaia");
        setRegistryName("kaia");
        setCreativeTab(omnipotentTab);
        setDamage(this.getDefaultInstance(), 0);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        tooltip.add("donoverdadeiro");
        tooltip.add("dono");
        super.addInformation(stack, worldIn, tooltip, flagIn);
    }

    @SubscribeEvent
    public void registerTextures(ModelRegistryEvent event) {
        registerTexture();
    }

    public void registerTexture() {
        ModelLoader.setCustomModelResourceLocation(this, 0, new ModelResourceLocation(getRegistryName(), "inventory"));
    }

    @Override
    public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        super.onUpdate(stack, worldIn, entityIn, itemSlot, isSelected);
        if (!(entityIn instanceof EntityPlayer) || worldIn.isRemote) {
            return;
        }
        EntityPlayer player = (EntityPlayer) entityIn;
        KaiaUtil.createTagCompoundStatusIfNecessary(stack);
        KaiaUtil.createOwnerIfNecessary(stack, entityIn);
        String ownerName = stack.getTagCompound().getString(KaiaConstantsNbt.ownerName);
        String ownerID = stack.getTagCompound().getString(KaiaConstantsNbt.ownerID);
        Random x = new Random();
        if(!stack.getTagCompound().hasKey("ench")){
            Map<Enchantment, Integer> enchantments = new HashMap();
            enchantments.put(Enchantments.FORTUNE, 64);
            enchantments.put(Enchantments.FIRE_ASPECT, 64);
            enchantments.put(Enchantments.LOOTING, 64);
            EnchantmentHelper.setEnchantments(enchantments, stack);
        }
        if (!stack.getTagCompound().hasKey(blockBreakArea) || stack.getTagCompound().getInteger(blockBreakArea) < 1) {
            NBTTagCompound status = stack.getTagCompound();
            status.setInteger(blockBreakArea, 1);
        }
        if (!(stack.getTagCompound().hasKey(idLigation))) {
            stack.getTagCompound().setLong(idLigation, x.nextLong());
        }
        if (!stack.getTagCompound().hasKey(killAllEntities)) {
            NBTTagCompound status = stack.getTagCompound();
            status.setBoolean(killAllEntities, false);
        }
        if (!stack.getTagCompound().hasKey(rangeAttack) || stack.getTagCompound().getInteger(rangeAttack) < 1) {
            NBTTagCompound status = stack.getTagCompound();
            status.setInteger(rangeAttack, 1);
        }
        if (!stack.getTagCompound().hasKey(killFriendEntities)) {
            NBTTagCompound status = stack.getTagCompound();
            status.setBoolean(killFriendEntities, true);
        }
        if (!player.getUniqueID().toString().equals(ownerID) || !player.getName().equals(ownerName)) {
            player.world.spawnEntity(new EntityItem(worldIn, player.posX, player.posY, player.posZ + 5, stack));
            player.inventory.deleteStack(stack);
        }
    }

    @Override
    public boolean onEntityItemUpdate(EntityItem entityItem) {
        ItemStack kaiaItem = entityItem.getItem();
        if (entityItem.getPosition().getY() < -5) {
            entityItem.setPosition(entityItem.posX, 150, entityItem.posZ);
            KaiaUtil.sendMessageToAllPlayers("\u00A74O PARADEIRO Y150");
        }
        KaiaUtil.createTagCompoundStatusIfNecessary(kaiaItem);
        if (kaiaItem.getTagCompound().hasKey(PositionKaiaEntityItem)) {
            int[] positionKaiaEntityItems = kaiaItem.getTagCompound().getIntArray(PositionKaiaEntityItem);
            if (positionKaiaEntityItems[0] != entityItem.getPosition().getX())
                kaiaItem.getTagCompound().setIntArray(PositionKaiaEntityItem, new int[]{entityItem.getPosition().getX(), entityItem.getPosition().getY(), entityItem.getPosition().getZ()});
            if (positionKaiaEntityItems[1] != entityItem.getPosition().getY())
                kaiaItem.getTagCompound().setIntArray(PositionKaiaEntityItem, new int[]{entityItem.getPosition().getX(), entityItem.getPosition().getY(), entityItem.getPosition().getZ()});
            if (positionKaiaEntityItems[2] != entityItem.getPosition().getZ())
                kaiaItem.getTagCompound().setIntArray(PositionKaiaEntityItem, new int[]{entityItem.getPosition().getX(), entityItem.getPosition().getY(), entityItem.getPosition().getZ()});
        } else {
            kaiaItem.getTagCompound().setIntArray("PositionKaiaEntityItem", new int[]{entityItem.getPosition().getX(), entityItem.getPosition().getY(), entityItem.getPosition().getZ()});
            int[] positionKaiaEntityItems = kaiaItem.getTagCompound().getIntArray(PositionKaiaEntityItem);
            if (positionKaiaEntityItems[0] != entityItem.getPosition().getX())
                kaiaItem.getTagCompound().setIntArray(PositionKaiaEntityItem, new int[]{entityItem.getPosition().getX(), entityItem.getPosition().getY(), entityItem.getPosition().getZ()});
            if (positionKaiaEntityItems[1] != entityItem.getPosition().getY())
                kaiaItem.getTagCompound().setIntArray(PositionKaiaEntityItem, new int[]{entityItem.getPosition().getX(), entityItem.getPosition().getY(), entityItem.getPosition().getZ()});
            if (positionKaiaEntityItems[2] != entityItem.getPosition().getZ())
                kaiaItem.getTagCompound().setIntArray(PositionKaiaEntityItem, new int[]{entityItem.getPosition().getX(), entityItem.getPosition().getY(), entityItem.getPosition().getZ()});
        }
        BlockPos pos = new BlockPos(405545454, 0, 28938293);
        WorldServer worldServer = DimensionManager.getWorld(0);
        if (worldServer== null) return super.onEntityItemUpdate(entityItem);;
        TileEntityChest chest = (TileEntityChest) worldServer.getTileEntity(pos);
        for (int index = 0; index < chest.getSizeInventory(); index++) {
            ItemStack stackInSlot = chest.getStackInSlot(index);
            if (!stackInSlot.isEmpty() && entityItem.getItem().getTagCompound().getLong(idLigation) == stackInSlot.getTagCompound().getLong(idLigation)) {
                return super.onEntityItemUpdate(entityItem);
            }
        }
        entityItem.setDead();
        return super.onEntityItemUpdate(entityItem);
    }

    @Override
    public boolean onLeftClickEntity(ItemStack stack, EntityPlayer player, Entity entityAttacked) {
        if (!player.world.isRemote && !KaiaUtil.hasInInventoryKaia(entityAttacked)) {
            boolean killAll = player.getHeldItemMainhand().getTagCompound().getBoolean(killAllEntities);
            KaiaUtil.kill(entityAttacked, player, killAll);
        }
        return false;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
        if (!playerIn.world.isRemote) {
            playerIn.world.spawnEntity(new EntityXPOrb(playerIn.world, playerIn.posX, playerIn.posY, playerIn.posZ, Integer.MAX_VALUE / 10000));
        }
        return super.onItemRightClick(worldIn, playerIn, handIn);
    }
}