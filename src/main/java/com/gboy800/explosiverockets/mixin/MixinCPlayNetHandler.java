package com.gboy800.explosiverockets.mixin;

import com.gboy800.explosiverockets.interfaces.SetSourceEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.play.ClientPlayNetHandler;
import net.minecraft.client.network.play.IClientPlayNetHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.FireworkRocketEntity;
import net.minecraft.network.PacketThreadUtil;
import net.minecraft.network.play.server.SExplosionPacket;
import net.minecraft.util.concurrent.ThreadTaskExecutor;
import net.minecraft.world.Explosion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetHandler.class)
public abstract class MixinCPlayNetHandler implements IClientPlayNetHandler, SetSourceEntity {

    @Shadow
    private Minecraft minecraft;

    @Inject(method = "handleExplosion", at = @At(value = "HEAD"), cancellable = true)
    public void newHandleExplosion(SExplosionPacket pPacket, CallbackInfo ci) {
        PacketThreadUtil.ensureRunningOnSameThread(pPacket, this, this.minecraft);
        Explosion explosion = new Explosion(this.minecraft.level, ((SetSourceEntity) pPacket).getSource(), pPacket.getX(), pPacket.getY(), pPacket.getZ(), pPacket.getPower(), pPacket.getToBlow());
        if (((SetSourceEntity) pPacket).getSource() instanceof FireworkRocketEntity) {
            explosion.finalizeExplosion(false);
        }
        else {
            explosion.finalizeExplosion(true);
        }
        this.minecraft.player.setDeltaMovement(this.minecraft.player.getDeltaMovement().add((double)pPacket.getKnockbackX(), (double)pPacket.getKnockbackY(), (double)pPacket.getKnockbackZ()));
        ci.cancel();
    }

}
