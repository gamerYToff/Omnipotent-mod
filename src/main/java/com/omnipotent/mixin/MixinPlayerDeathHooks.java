package com.omnipotent.mixin;

import com.omnipotent.util.KaiaUtil;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.DamageSource;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import static com.omnipotent.util.KaiaUtil.hasInInventoryKaia;

@Mixin(value = ForgeHooks.class, priority = -Integer.MAX_VALUE)
public abstract class MixinPlayerDeathHooks {

    /**
     * @author
     * @reason
     */
    @Overwrite
    public static boolean onLivingDeath(EntityLivingBase entity, DamageSource src) {
        if(hasInInventoryKaia(entity)){
            entity.setHealth(Float.MAX_VALUE);
            entity.isDead = false;
            return true;
        }
        return !src.getDamageType().equals("ABSOLUTE OF CREATOR") && MinecraftForge.EVENT_BUS.post(new LivingDeathEvent(entity, src));
    }
}
