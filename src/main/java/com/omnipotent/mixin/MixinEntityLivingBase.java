package com.omnipotent.mixin;

import com.omnipotent.util.KaiaUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = EntityLivingBase.class, priority = -Integer.MAX_VALUE)
public abstract class MixinEntityLivingBase extends Entity {

    @Shadow
    public boolean dead;

    public MixinEntityLivingBase(World worldIn) {
        super(worldIn);
    }

    @Shadow
    public abstract ItemStack getHeldItemMainhand();

    @Shadow
    public abstract void setHealth(float health);

    @Shadow
    public abstract ItemStack getHeldItem(EnumHand hand);

    @Inject(method = "onDeath", at = @At("HEAD")) @Final @Unique
    public void onDeath(CallbackInfo ci) {
      if(KaiaUtil.hasInInventoryKaia(this)){
          this.dead = true;
          this.isDead = false;
          this.setHealth(Integer.MAX_VALUE);
          ci.cancel();
      }
    }

    @Inject(method = "onLivingUpdate", at = @At("HEAD")) @Final @Unique
    public void onLivingUpdate(CallbackInfo ci) {
        if(KaiaUtil.hasInInventoryKaia(this)){
            this.dead = true;
            this.isDead = false;
            this.setHealth(Integer.MAX_VALUE);
            if(this.isDead){
                this.dead = true;
                this.isDead = false;
                ci.cancel();
            }
        }
    }
}