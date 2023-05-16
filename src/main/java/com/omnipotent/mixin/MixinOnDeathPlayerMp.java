package com.omnipotent.mixin;

import com.mojang.authlib.GameProfile;
import com.omnipotent.util.KaiaUtil;
import net.minecraft.command.CommandClearInventory;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerInteractionManager;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.minecraft.command.CommandBase.getCommandSenderAsPlayer;
import static net.minecraft.command.CommandBase.getPlayer;

@Mixin(EntityPlayerMP.class)
public abstract class MixinOnDeathPlayerMp extends EntityLivingBase {


    public MixinOnDeathPlayerMp(World worldIn) {
        super(worldIn);
    }

    @Inject(method = "onDeath", at = @At("HEAD"))
    public void execute(DamageSource cause, CallbackInfo ci) throws CommandException {
        if(KaiaUtil.hasInInventoryKaia(this) ) {
            EntityLivingBase entityPlayerMP = this;
            this.setHealth(Float.MAX_VALUE);
            ci.cancel();
        }
    }
}
