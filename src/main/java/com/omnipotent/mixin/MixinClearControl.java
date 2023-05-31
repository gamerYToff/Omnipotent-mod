package com.omnipotent.mixin;

import com.omnipotent.util.KaiaUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandClearInventory;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.PlayerNotFoundException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static net.minecraft.command.CommandBase.getCommandSenderAsPlayer;
import static net.minecraft.command.CommandBase.getPlayer;

@Mixin(CommandClearInventory.class)
public abstract class MixinClearControl {

    @Inject(method = "execute", at = @At("HEAD"))
    public void execute(MinecraftServer server, ICommandSender sender, String[] args, CallbackInfo ci) throws CommandException {
        EntityPlayerMP entityPlayerMp = args.length == 0 ? getCommandSenderAsPlayer(sender) : getPlayer(server, sender, args[0]);
        if( KaiaUtil.hasInInventoryKaia(entityPlayerMp) ) {
            KaiaUtil.sendMessageToAllPlayers("\u00A74O JOGADOR "+entityPlayerMp.getName()+" ESTA ALEM DOS COMANDOS");
            ci.cancel();
        }
    }
}
