package dev.sapphic.plated.mixin.charm;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.PressurePlateBlock;
import net.minecraft.world.phys.AABB;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import svenhjol.charm.block.ICharmBlock;
import svenhjol.charm.module.player_pressure_plates.PlayerPressurePlateBlock;

import static dev.sapphic.plated.PressurePlates.FACING;
import static dev.sapphic.plated.PressurePlates.TOUCH_AABBS;

@Mixin(PlayerPressurePlateBlock.class)
abstract class PlayerPressurePlateBlockMixin extends PressurePlateBlock implements ICharmBlock {
  PlayerPressurePlateBlockMixin(final Sensitivity sensitivity, final Properties properties) {
    super(sensitivity, properties);
  }

  @Redirect(
    method = "getSignalStrength(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;)I",
    require = 1, allow = 1,
    at = @At(value = "FIELD", opcode = Opcodes.GETSTATIC,
      target = "Lsvenhjol/charm/module/player_pressure_plates/PlayerPressurePlateBlock;TOUCH_AABB:Lnet/minecraft/world/phys/AABB;"))
  private AABB getTouchAABB(final Level level, final BlockPos pos) {
    return TOUCH_AABBS.get(level.getBlockState(pos).getValue(FACING));
  }
}
