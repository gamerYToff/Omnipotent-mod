package com.omnipotent.tools;

import com.omnipotent.Event.UpdateEntity;
import com.omnipotent.Omnipotent;
import com.omnipotent.util.KaiaUtil;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

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
        if (!stack.getTagCompound().hasKey(blockBreakArea) || stack.getTagCompound().getInteger(blockBreakArea) < 1) {
            NBTTagCompound status = stack.getTagCompound();
            status.setInteger(blockBreakArea, 1);
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
        if (entityItem.isDead) {
            if (UpdateEntity.chunkLoadList.contains(entityItem.world.getChunkFromBlockCoords(entityItem.getPosition()))) {
                ForgeChunkManager.Ticket ticket = ForgeChunkManager.requestTicket(Omnipotent.instance, entityItem.world, ForgeChunkManager.Type.NORMAL);
                ForgeChunkManager.unforceChunk(ticket, entityItem.world.getChunkFromBlockCoords(entityItem.getPosition()).getPos());
                UpdateEntity.chunkLoadList.remove(entityItem.world.getChunkFromBlockCoords(entityItem.getPosition()));
            }
        }
        if(entityItem.getPosition().getY()<-5){
            entityItem.setPosition(entityItem.posX, 150, entityItem.posZ);
            KaiaUtil.sendMessageToAllPlayers("\u00A74O PARADEIRO Y150");
        }
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