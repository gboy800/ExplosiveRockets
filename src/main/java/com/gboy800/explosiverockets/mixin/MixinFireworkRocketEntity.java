package com.gboy800.explosiverockets.mixin;

import com.gboy800.explosiverockets.interfaces.NoDamageExplosion;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.IRendersAsItem;
import net.minecraft.entity.projectile.FireworkRocketEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FireworkRocketEntity.class)
public abstract class MixinFireworkRocketEntity extends ProjectileEntity implements IRendersAsItem, NoDamageExplosion {

    @Shadow
    @Final
    private static DataParameter<ItemStack> DATA_ID_FIREWORKS_ITEM;

    private MixinFireworkRocketEntity(EntityType<? extends FireworkRocketEntity> p_i50164_1_, World p_i50164_2_) {
        super(p_i50164_1_, p_i50164_2_);
    }

    @Inject(method = "explode", at = @At(value = "HEAD"))
    public void explodeBlocks(CallbackInfo ci) {
        this.level.broadcastEntityEvent(this, (byte)17);
        ItemStack itemstack = this.entityData.get(DATA_ID_FIREWORKS_ITEM);
        CompoundNBT compoundnbt = itemstack.isEmpty() ? null : itemstack.getTagElement("Fireworks");
        ListNBT listnbt = compoundnbt != null ? compoundnbt.getList("Explosions", 10) : null;
        if (listnbt != null && !listnbt.isEmpty() && !this.level.isClientSide()) {
            this.level.explode((FireworkRocketEntity)(Object) this, null, null,
                    this.position.x, this.position.y, this.position.z,
                    4.0F, true, Explosion.Mode.BREAK);
        }
        this.dealExplosionDamage();
        this.remove();
    }

    @Shadow
    private void dealExplosionDamage() {
        // Whatever
    }
}
