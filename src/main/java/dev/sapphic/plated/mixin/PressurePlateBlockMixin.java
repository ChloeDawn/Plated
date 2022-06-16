/*
 * Copyright 2022 Chloe Dawn
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package dev.sapphic.plated.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BasePressurePlateBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.PressurePlateBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.AABB;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static dev.sapphic.plated.PressurePlates.FACING;
import static dev.sapphic.plated.PressurePlates.TOUCH_AABBS;
import static dev.sapphic.plated.PressurePlates.WATERLOGGED;

@Mixin(PressurePlateBlock.class)
abstract class PressurePlateBlockMixin extends BasePressurePlateBlock {
  PressurePlateBlockMixin(final Properties properties) {
    super(properties);
  }

  @ModifyArg(
      method =
          "<init>("
              + "Lnet/minecraft/world/level/block/PressurePlateBlock$Sensitivity;"
              + "Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;"
              + ")V",
      require = 1,
      allow = 1,
      at =
          @At(
              value = "INVOKE",
              opcode = Opcodes.INVOKEVIRTUAL,
              target =
                  "Lnet/minecraft/world/level/block/PressurePlateBlock;"
                      + "registerDefaultState("
                      + "Lnet/minecraft/world/level/block/state/BlockState;"
                      + ")V"))
  private BlockState setDefaultFacing(final BlockState state) {
    return state.setValue(FACING, Direction.UP).setValue(WATERLOGGED, false);
  }

  @Redirect(
      method =
          "getSignalStrength("
              + "Lnet/minecraft/world/level/Level;"
              + "Lnet/minecraft/core/BlockPos;"
              + ")I",
      require = 1,
      allow = 1,
      at =
          @At(
              value = "FIELD",
              opcode = Opcodes.GETSTATIC,
              target =
                  "Lnet/minecraft/world/level/block/PressurePlateBlock;"
                      + "TOUCH_AABB:"
                      + "Lnet/minecraft/world/phys/AABB;"))
  private AABB getTouchAABB(final Level level, final BlockPos pos) {
    return TOUCH_AABBS.get(level.getBlockState(pos).getValue(FACING));
  }

  @Inject(
      method =
          "createBlockStateDefinition("
              + "Lnet/minecraft/world/level/block/state/StateDefinition$Builder;"
              + ")V",
      at = @At("TAIL"))
  private void addProperties(
      final StateDefinition.Builder<Block, BlockState> builder, final CallbackInfo ci) {
    builder.add(FACING, WATERLOGGED);
  }
}
