package com.gboy800.explosiverockets.mixin;

import com.gboy800.explosiverockets.interfaces.SetSourceEntity;
import net.minecraft.client.network.play.IClientPlayNetHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.server.SExplosionPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.IOException;
import java.util.List;

@Mixin(SExplosionPacket.class)
public abstract class MixinExplosionPacket implements IPacket<IClientPlayNetHandler>, SetSourceEntity {

    private Entity source;

    @Override
    public void setSource(Entity source) {
        this.source = source;
    }

    @Override
    public Entity getSource() {
        return source;
    }
}
