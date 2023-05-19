package com.omnipotent.network;

import com.omnipotent.network.nbtpackets.*;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public enum NetworkRegister {
    ACESS;

    private SimpleNetworkWrapper channel = NetworkRegistry.INSTANCE.newSimpleChannel("omnipotent");

    private NetworkRegister(){
        int index = 0;
        this.channel.registerMessage(KillPacket.killPacketHandler.class, KillPacket.class, index++, Side.SERVER);
        this.channel.registerMessage(AbilityPacket.AbilityPacketHandler.class, AbilityPacket.class, index++, Side.SERVER);
        this.channel.registerMessage(OmnipotentContainerPacket.AazominipotentContainerPacketHandler.class, OmnipotentContainerPacket.class, index++, Side.SERVER);
        this.channel.registerMessage(AreaBlocoPacket.AreaBlocoPacketHandler.class, AreaBlocoPacket.class, index++, Side.SERVER);
        this.channel.registerMessage(KillRangeAttack.KillRangeAttackHandler.class, KillRangeAttack.class, index++, Side.SERVER);
        this.channel.registerMessage(KillAllEntitiesPacket.KillAllEntitiesPacketHandler.class, KillAllEntitiesPacket.class, index++, Side.SERVER);
        this.channel.registerMessage(KillFriendEntitiesPacket.KillFriendEntitiesPacketHandler.class, KillFriendEntitiesPacket.class, index++, Side.SERVER);
        this.channel.registerMessage(ChangeDimensionPacket.ChangeDimensionHandler.class, ChangeDimensionPacket.class, index++, Side.SERVER);
        this.channel.registerMessage(SummonLightEasterEggPacket.SummonLightEasterEggPacketHandler.class, SummonLightEasterEggPacket.class, index++, Side.SERVER);
        this.channel.registerMessage(ReturnKaiaPacket.ReturnKaiaPacketHandler.class, ReturnKaiaPacket.class, index++, Side.SERVER);
    }
    public void sendToServer(IMessage message){
        channel.sendToServer(message);
    }
}
