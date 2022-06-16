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
                  "Lsvenhjol/charm/module/player_pressure_plates/PlayerPressurePlateBlock;"
                      + "TOUCH_AABB:"
                      + "Lnet/minecraft/world/phys/AABB;"))
  private AABB getTouchAABB(final Level level, final BlockPos pos) {
    return TOUCH_AABBS.get(level.getBlockState(pos).getValue(FACING));
  }
}
