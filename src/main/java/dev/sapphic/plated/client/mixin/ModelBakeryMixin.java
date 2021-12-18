/*
 * Copyright 2021 Chloe Dawn
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

package dev.sapphic.plated.client.mixin;

import com.google.common.collect.ObjectArrays;
import com.mojang.datafixers.util.Pair;
import com.mojang.math.Transformation;
import dev.sapphic.plated.PressurePlates;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.block.model.BlockModelDefinition;
import net.minecraft.client.renderer.block.model.MultiVariant;
import net.minecraft.client.renderer.block.model.Variant;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.world.level.block.BasePressurePlateBlock;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Mixin(ModelBakery.class)
@Environment(EnvType.CLIENT)
abstract class ModelBakeryMixin {
  @Unique
  private static final Pattern BLOCK_STATE_KEY_PATTERN =
      Pattern.compile("blockstates/(?<key>.*)[.]json");

  @Unique private static final Direction[] DIRECTIONS = Direction.values();

  @Unique
  private static @Nullable ResourceLocation resolve(final ResourceLocation rl) {
    final Matcher matcher = BLOCK_STATE_KEY_PATTERN.matcher(rl.getPath());

    if (matcher.find()) {
      return ResourceLocation.tryParse(rl.getNamespace() + ':' + matcher.group("key"));
    }

    return null;
  }

  @Unique
  private static Variant rotated(final Variant v, final Transformation transformation) {
    return new Variant(v.getModelLocation(), transformation, v.isUvLocked(), v.getWeight());
  }

  @Unique
  private static String facing(final Direction direction, final String variantString) {
    final String facing = PressurePlates.FACING.getName() + '=' + direction.getName();
    final String[] variants = ObjectArrays.concat(facing, variantString.split("[,]"));

    Arrays.sort(variants);

    return String.join(",", variants);
  }

  @ModifyVariable(
      method =
          "lambda$loadModel$17("
              + "Lnet/minecraft/server/packs/resources/Resource;"
              + ")Lcom/mojang/datafixers/util/Pair;",
      require = 1,
      allow = 1,
      at = @At("RETURN"))
  private Pair<String, BlockModelDefinition> applyPressurePlateRotation(
      final Pair<String, BlockModelDefinition> pair, final Resource resource) {
    if (Registry.BLOCK.get(resolve(resource.getLocation())) instanceof BasePressurePlateBlock) {
      final Map<String, MultiVariant> groups = pair.getSecond().getVariants();

      for (final String variantString : new HashSet<>(groups.keySet())) {
        final List<Variant> variants = groups.remove(variantString).getVariants();

        for (final Direction direction : DIRECTIONS) {
          final List<Variant> rotatedVariants = new ArrayList<>(variants.size());
          final Transformation transformation =
              new Transformation(null, direction.getRotation(), null, null);

          for (final Variant variant : variants) {
            rotatedVariants.add(rotated(variant, transformation));
          }

          groups.put(facing(direction, variantString), new MultiVariant(rotatedVariants));
        }
      }
    }

    return pair;
  }
}
