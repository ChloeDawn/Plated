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

package dev.sapphic.plated;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.VoxelShape;

public final class PressurePlates {
  public static final DirectionProperty FACING = BlockStateProperties.FACING;
  public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

  /** {@link VoxelShape} bounds for each orientation of an unpressed (unpowered) plate's shape */
  public static final ImmutableMap<Direction, VoxelShape> AABBS =
      Maps.immutableEnumMap(
          ImmutableMap.<Direction, VoxelShape>builder()
              .put(Direction.DOWN, Block.box(1.0, 15.0, 1.0, 15.0, 16.0, 15.0))
              .put(Direction.UP, Block.box(1.0, 0.0, 1.0, 15.0, 1.0, 15.0))
              .put(Direction.NORTH, Block.box(1.0, 1.0, 15.0, 15.0, 15.0, 16.0))
              .put(Direction.SOUTH, Block.box(1.0, 1.0, 0.0, 15.0, 15.0, 1.0))
              .put(Direction.WEST, Block.box(15.0, 1.0, 1.0, 16.0, 15.0, 15.0))
              .put(Direction.EAST, Block.box(0.0, 1.0, 1.0, 1.0, 15.0, 15.0))
              .build());

  /** {@link VoxelShape} bounds for each orientation of a pressed (powered) plate's shape */
  public static final ImmutableMap<Direction, VoxelShape> PRESSED_AABBS =
      Maps.immutableEnumMap(
          ImmutableMap.<Direction, VoxelShape>builder()
              .put(Direction.DOWN, Block.box(1.0, 15.5, 1.0, 15.0, 16.0, 15.0))
              .put(Direction.UP, Block.box(1.0, 0.0, 1.0, 15.0, 0.5, 15.0))
              .put(Direction.NORTH, Block.box(1.0, 1.0, 15.5, 15.0, 15.0, 16.0))
              .put(Direction.SOUTH, Block.box(1.0, 1.0, 0.0, 15.0, 15.0, 0.5))
              .put(Direction.WEST, Block.box(15.5, 1.0, 1.0, 16.0, 15.0, 15.0))
              .put(Direction.EAST, Block.box(0.0, 1.0, 1.0, 0.5, 15.0, 15.0))
              .build());

  /** {@link AABB} bounds for each orientation of a pressure plate's intersection bounds */
  public static final ImmutableMap<Direction, AABB> TOUCH_AABBS =
      Maps.immutableEnumMap(
          ImmutableMap.<Direction, AABB>builder()
              .put(Direction.DOWN, new AABB(0.125, 0.75, 0.125, 0.875, 1.0, 0.875))
              .put(Direction.UP, new AABB(0.125, 0.0, 0.125, 0.875, 0.25, 0.875))
              .put(Direction.NORTH, new AABB(0.125, 0.125, 0.75, 0.875, 0.875, 1.0))
              .put(Direction.SOUTH, new AABB(0.125, 0.125, 0.0, 0.875, 0.875, 0.25))
              .put(Direction.WEST, new AABB(0.75, 0.125, 0.125, 1.0, 0.875, 0.875))
              .put(Direction.EAST, new AABB(0.0, 0.125, 0.125, 0.25, 0.875, 0.875))
              .build());

  private PressurePlates() {
    throw new UnsupportedOperationException();
  }
}
