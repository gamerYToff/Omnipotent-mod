package com.omnipotent.mixin;

import com.omnipotent.util.KaiaUtil;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(EntityPlayerMP.class)
public abstract class MixinOnDeathPlayerMp extends EntityLivingBase {

    public MixinOnDeathPlayerMp(World worldIn) {
        super(worldIn);
    }

    @Inject(method = "onDeath", at = @At("HEAD"))
    public void onDeath(DamageSource cause, CallbackInfo ci) {
        if (KaiaUtil.hasInInventoryKaia(this)) {
            EntityLivingBase entityPlayerMP = this;
            this.setHealth(Float.MAX_VALUE);
            ci.cancel();
        }
    }
}
