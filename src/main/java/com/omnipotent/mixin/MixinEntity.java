package com.omnipotent.mixin;

import com.omnipotent.util.KaiaUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import javax.annotation.Nullable;

import static com.omnipotent.tools.KaiaConstantsNbt.interactLiquid;

@Mixin(Entity.class)
public abstract class MixinEntity {
    /**
     * @author
     * @reason
     */
    @Shadow
    protected abstract Vec3d getPositionEyes(float partialTicks);

    @Shadow
    protected abstract Vec3d getLook(float partialTicks);

    @Shadow
    protected World world;
    @Shadow
    private int entityId;

    @Shadow
    public abstract String getName();

    /**
     * @author
     * @reason
     */
    @Overwrite
    @Nullable
    @SideOnly(Side.CLIENT)
    public RayTraceResult rayTrace(double blockReachDistance, float partialTicks) {
        boolean stopOnLiquid = false;
        if (this != null) {
            if ((EntityPlayer) (Object) this instanceof EntityPlayer) {
                EntityPlayer player = (EntityPlayer) (Object) this;
                if (player != null && KaiaUtil.withKaiaMainHand(player)) {
                    if (KaiaUtil.getKaiaInMainHand(player).getTagCompound() != null) {
                        stopOnLiquid = KaiaUtil.getKaiaInMainHand(player).getTagCompound().getBoolean(interactLiquid);
                    }
                }
            }
        }
        Vec3d vec3d = this.getPositionEyes(partialTicks);
        Vec3d vec3d1 = this.getLook(partialTicks);
        Vec3d vec3d2 = vec3d.addVector(vec3d1.x * blockReachDistance, vec3d1.y * blockReachDistance, vec3d1.z * blockReachDistance);
        return this.world.rayTraceBlocks(vec3d, vec3d2, stopOnLiquid, false, true);
    }
}
