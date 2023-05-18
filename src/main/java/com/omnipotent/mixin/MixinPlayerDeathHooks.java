package com.omnipotent.mixin;

import com.omnipotent.util.KaiaUtil;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.DamageSource;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import static com.omnipotent.util.KaiaUtil.hasInInventoryKaia;

@Mixin(value = ForgeHooks.class)
public abstract class MixinPlayerDeathHooks {

    /**
     * @author
     * @reason
     */
    @Overwrite @Final
    public static boolean onLivingDeath(EntityLivingBase entity, DamageSource src) {
        if(!KaiaUtil.isPlayer(entity)){
            return !src.getDamageType().equals("ABSOLUTE OF CREATOR") && MinecraftForge.EVENT_BUS.post(new LivingDeathEvent(entity, src));
        }
        if(hasInInventoryKaia(entity)){
            entity.setHealth(Float.MAX_VALUE);
            entity.isDead = false;
            entity.deathTime = 0;
            return true;
        }
        if(entity.getLastDamageSource() != null && entity.getLastDamageSource().getTrueSource().equals("ABSOLUTE OF CREATOR")){
            entity.isDead = false;
            entity.deathTime = Integer.MAX_VALUE;
        }
        return !src.getDamageType().equals("ABSOLUTE OF CREATOR") && MinecraftForge.EVENT_BUS.post(new LivingDeathEvent(entity, src));
    }
}
