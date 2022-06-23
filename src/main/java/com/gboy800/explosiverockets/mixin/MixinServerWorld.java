package com.gboy800.explosiverockets.mixin;

import com.gboy800.explosiverockets.interfaces.SetSourceEntity;
import com.google.common.collect.Lists;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.play.server.SExplosionPacket;
import net.minecraft.profiler.IProfiler;
import net.minecraft.util.DamageSource;
import net.minecraft.util.RegistryKey;
import net.minecraft.world.*;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.ISpawnWorldInfo;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Supplier;

@Mixin(ServerWorld.class)
public abstract class MixinServerWorld extends World implements ISeedReader, net.minecraftforge.common.extensions.IForgeWorldServer, SetSourceEntity {

    @Shadow
    @Final
    private List<ServerPlayerEntity> players;

    private MixinServerWorld(ISpawnWorldInfo p_i241925_1_, RegistryKey<World> p_i241925_2_, DimensionType p_i241925_3_, Supplier<IProfiler> p_i241925_4_, boolean p_i241925_5_, boolean p_i241925_6_, long p_i241925_7_) {
        super(p_i241925_1_, p_i241925_2_, p_i241925_3_, p_i241925_4_, p_i241925_5_, p_i241925_6_, p_i241925_7_);
    }

    @Inject(method = "explode", at = @At(value = "HEAD"), cancellable = true)
    public void explodeWithFirework(@Nullable Entity pExploder, @Nullable DamageSource pDamageSource, @Nullable ExplosionContext pContext, double pX, double pY, double pZ, float pSize, boolean pCausesFire, Explosion.Mode pMode, CallbackInfoReturnable<Explosion> cir) {
        Explosion explosion = new Explosion(this, pExploder, pDamageSource, pContext, pX, pY, pZ, pSize, pCausesFire, pMode);
        if (net.minecraftforge.event.ForgeEventFactory.onExplosionStart(this, explosion)) cir.setReturnValue(explosion);
        explosion.explode();
        explosion.finalizeExplosion(false);
        if (pMode == Explosion.Mode.NONE) {
            explosion.clearToBlow();
        }

        for(ServerPlayerEntity serverplayerentity : this.players) {
            if (serverplayerentity.distanceToSqr(pX, pY, pZ) < 4096.0D) {
                SExplosionPacket explosionPacket = new SExplosionPacket(pX, pY, pZ, pSize, explosion.getToBlow(), explosion.getHitPlayers().get(serverplayerentity));
                ((SetSourceEntity)explosionPacket).setSource(pExploder);
                serverplayerentity.connection.send(explosionPacket);
            }
        }

        cir.setReturnValue(explosion);
    }

}
