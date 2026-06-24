package dev.undefined0.duskwoodmanor.mixin;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.spongepowered.asm.mixin.Mixin;

import com.google.common.collect.Lists;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;

import dev.doctor4t.wathe.Wathe;
import dev.doctor4t.wathe.cca.MapVariablesWorldComponent;
import dev.doctor4t.wathe.entity.FirecrackerEntity;
import dev.doctor4t.wathe.entity.NoteEntity;
import dev.doctor4t.wathe.entity.PlayerBodyEntity;
import dev.doctor4t.wathe.game.GameFunctions;
import dev.doctor4t.wathe.index.WatheEntities;
import dev.undefined0.duskwoodmanor.DuskwoodManor;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.component.ComponentMap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.decoration.painting.PaintingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtDouble;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.world.ChunkTicketType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.Mutable;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3d;

// man do i hate this

@Mixin(GameFunctions.class)
public class GameFunctionsResetMixin {
    private record BlockEntityInfo(NbtCompound nbt, ComponentMap components) {}

    private record BlockInfo(BlockPos pos, BlockState state, BlockEntityInfo blockEntityInfo) {}

    // tldr the orig wathe one SUCKS and takes a crazy amount of time on a map this large so i (gpt) rewrote it which i
    // feel so so bad about and im so sorry but i will rewrite this by hand eventually
    @SuppressWarnings("deprecation")
    private static boolean origWatheTryResetTrain(ServerWorld serverWorld) {
        if (serverWorld.getServer().getOverworld().equals(serverWorld)) {
            MapVariablesWorldComponent areas = MapVariablesWorldComponent.KEY.get(serverWorld);
            BlockPos backupMinPos = BlockPos.ofFloored(areas.getResetTemplateArea().getMinPos());
            BlockPos backupMaxPos = BlockPos.ofFloored(areas.getResetTemplateArea().getMaxPos());
            BlockBox backupTrainBox = BlockBox.create(backupMinPos, backupMaxPos);
            BlockPos trainMinPos = BlockPos.ofFloored(areas.getResetTemplateArea().offset(Vec3d.of(areas.getResetPasteOffset())).getMinPos());
            BlockPos trainMaxPos = trainMinPos.add(backupTrainBox.getDimensions());
            BlockBox trainBox = BlockBox.create(trainMinPos, trainMaxPos);

            if (serverWorld.isRegionLoaded(backupMinPos, backupMaxPos) && serverWorld.isRegionLoaded(trainMinPos, trainMaxPos)) {
                int volume = backupTrainBox.getBlockCountX() * backupTrainBox.getBlockCountY() * backupTrainBox.getBlockCountZ();
                List<BlockInfo> list = Lists.newArrayListWithCapacity(volume);
                List<BlockInfo> list2 = Lists.newArrayListWithCapacity(volume);
                List<BlockInfo> list3 = Lists.newArrayListWithCapacity(volume);
                BlockPos blockPos5 = new BlockPos(
                    trainBox.getMinX() - backupTrainBox.getMinX(), trainBox.getMinY() - backupTrainBox.getMinY(), trainBox.getMinZ() - backupTrainBox.getMinZ()
                );
                Mutable backupPos = new BlockPos.Mutable();
                Mutable targetPos = new BlockPos.Mutable();

                for (int k = backupTrainBox.getMinZ(); k <= backupTrainBox.getMaxZ(); k++) {
                    for (int l = backupTrainBox.getMinY(); l <= backupTrainBox.getMaxY(); l++) {
                        for (int m = backupTrainBox.getMinX(); m <= backupTrainBox.getMaxX(); m++) {
                            backupPos.set(m, l, k);
                            targetPos.set(m + blockPos5.getX(), l + blockPos5.getY(), k + blockPos5.getZ());
                            BlockState blockState = serverWorld.getBlockState(backupPos);

                            BlockEntity blockEntity = serverWorld.getBlockEntity(backupPos);
                            if (blockEntity != null) {
                                BlockEntityInfo blockEntityInfo = new BlockEntityInfo(
                                    blockEntity.createComponentlessNbt(serverWorld.getRegistryManager()), blockEntity.getComponents()
                                );
                                list2.add(new BlockInfo(targetPos.toImmutable(), blockState, blockEntityInfo));
                            } else if (!blockState.isOpaqueFullCube(serverWorld, backupPos) && !blockState.isFullCube(serverWorld, backupPos)) {
                                list3.add(new BlockInfo(targetPos.toImmutable(), blockState, null));
                            } else {
                                list.add(new BlockInfo(targetPos.toImmutable(), blockState, null));
                            }
                        }
                    }
                }

                List<BlockInfo> list4 = Lists.newArrayListWithCapacity(list.size() + list2.size() + list3.size());
                list4.addAll(list);
                list4.addAll(list2);
                list4.addAll(list3);

                int mx = 0;

                for (BlockInfo blockInfo2 : list4) {
                    if (serverWorld.setBlockState(blockInfo2.pos, blockInfo2.state, Block.NOTIFY_LISTENERS)) {
                        mx++;
                    }
                }

                for (BlockInfo blockInfo2x : list2) {
                    BlockEntity blockEntity4 = serverWorld.getBlockEntity(blockInfo2x.pos);
                    if (blockInfo2x.blockEntityInfo != null && blockEntity4 != null) {
                        blockEntity4.readComponentlessNbt(blockInfo2x.blockEntityInfo.nbt, serverWorld.getRegistryManager());
                        blockEntity4.setComponents(blockInfo2x.blockEntityInfo.components);
                        blockEntity4.markDirty();
                    }

                    serverWorld.setBlockState(blockInfo2x.pos, blockInfo2x.state, Block.NOTIFY_LISTENERS);
                }

                serverWorld.getBlockTickScheduler().scheduleTicks(serverWorld.getBlockTickScheduler(), backupTrainBox, blockPos5);
                if (mx == 0) {
                    Wathe.LOGGER.info("Train reset failed: No blocks copied. Queueing another attempt.");
                    return true;
                }
            } else {
                Wathe.LOGGER.info("Train reset failed: Clone positions not loaded. Queueing another attempt.");
                return true;
            }

            // discard all player bodies and items
            for (PlayerBodyEntity body : serverWorld.getEntitiesByType(WatheEntities.PLAYER_BODY, playerBodyEntity -> true)) {
                body.discard();
            }
            for (ItemEntity item : serverWorld.getEntitiesByType(EntityType.ITEM, playerBodyEntity -> true)) {
                item.discard();
            }
            for (FirecrackerEntity entity : serverWorld.getEntitiesByType(WatheEntities.FIRECRACKER, entity -> true))
                entity.discard();
            for (NoteEntity entity : serverWorld.getEntitiesByType(WatheEntities.NOTE, entity -> true))
                entity.discard();


            Wathe.LOGGER.info("Train reset successful.");
            return false;
        }
        return false;
    }

    private static boolean tryCopyEntitiesAndTrainOnce(ServerWorld serverWorld) {
        MapVariablesWorldComponent areas = MapVariablesWorldComponent.KEY.get(serverWorld);
        BlockPos backupMinPos = BlockPos.ofFloored(areas.getResetTemplateArea().getMinPos());
        BlockPos backupMaxPos = BlockPos.ofFloored(areas.getResetTemplateArea().getMaxPos());
        BlockBox backupTrainBox = BlockBox.create(backupMinPos, backupMaxPos);
        BlockPos trainMinPos = BlockPos.ofFloored(areas.getResetTemplateArea().offset(Vec3d.of(areas.getResetPasteOffset())).getMinPos());
        BlockPos trainMaxPos = trainMinPos.add(backupTrainBox.getDimensions());
        BlockBox trainBox = BlockBox.create(trainMinPos, trainMaxPos);

        Box backupTrainBox2 = new Box(
            backupTrainBox.getMinX(),
            backupTrainBox.getMinY(),
            backupTrainBox.getMinZ(),
            backupTrainBox.getMaxX() + 1,
            backupTrainBox.getMaxY() + 1,
            backupTrainBox.getMaxZ() + 1
        );

        Box trainBox2 = new Box(
            trainBox.getMinX(),
            trainBox.getMinY(),
            trainBox.getMinZ(),
            trainBox.getMaxX() + 1,
            trainBox.getMaxY() + 1,
            trainBox.getMaxZ() + 1
        );

        int entitiesKilled = 0;
        var entitiesToKill = serverWorld.getOtherEntities(null, trainBox2, entity -> !(entity instanceof PlayerEntity));
        DuskwoodManor.LOGGER.info("{} entities to kill...", entitiesToKill.size());
        for (Entity entity : entitiesToKill) {
            entity.discard();
            entitiesKilled++;
        }

        DuskwoodManor.LOGGER.info("killed {} entities", entitiesKilled);

        int entitiesCopied = 0;
        var entitiesToCopy = serverWorld.getOtherEntities(null, backupTrainBox2, entity -> !(entity instanceof PlayerEntity));
        DuskwoodManor.LOGGER.info("{} entities to copy...", entitiesToCopy.size());
        for (Entity entity : entitiesToCopy) {
            var newPos = entity.getPos().add(Vec3d.of(areas.getResetPasteOffset()));
            if (entity instanceof PaintingEntity) {
                newPos = newPos.add(0.0, -1.0, 0.0);
            }

            var copy = entity.getType().create(serverWorld);
            var oldNbt = copy.writeNbt(new NbtCompound());
            var nbt = entity.writeNbt(new NbtCompound());

            nbt.put("UUID", oldNbt.get("UUID"));
            NbtList nbtNewPos = new NbtList();
            nbtNewPos.add(NbtDouble.of(newPos.x));
            nbtNewPos.add(NbtDouble.of(newPos.y));
            nbtNewPos.add(NbtDouble.of(newPos.z));
            nbt.put("Pos", nbtNewPos);
            if (entity instanceof PaintingEntity) {
                nbt.putInt("TileX", nbt.getInt("TileX") + areas.getResetPasteOffset().getX());
                nbt.putInt("TileY", nbt.getInt("TileY") + areas.getResetPasteOffset().getY());
                nbt.putInt("TileZ", nbt.getInt("TileZ") + areas.getResetPasteOffset().getZ());
            }

            copy.readNbt(nbt);
            serverWorld.spawnEntity(copy);
            entitiesCopied++;
        }

        if (entitiesCopied == 0) {
            DuskwoodManor.LOGGER.info("copied no entities, retrying");
            return true;
        }

        DuskwoodManor.LOGGER.info("copied {} entities", entitiesCopied);

        long startTime = System.currentTimeMillis();
        boolean ret = origWatheTryResetTrain(serverWorld);
        long endTime = System.currentTimeMillis();

        DuskwoodManor.LOGGER.info("wathe tryReset took {} seconds", (endTime - startTime) / 1000f);

        if (ret) {
            DuskwoodManor.LOGGER.warn("wathe wanted reset, ignoring!");
        }

        return false;
    }

    private static void tryCopyEntitiesAndTrain(ServerWorld serverWorld, Runnable onSuccess, int count) {
        if (count > 3) {
            DuskwoodManor.LOGGER.warn("copying failed >3 times so we're just going to die i guess");
            DuskwoodManor.LOGGER.warn("hehe >3 is like an evil face because we ARE evil!!!");
            onSuccess.run();
            return;
        }

        delayAndThenRun(() -> {
            var ret = tryCopyEntitiesAndTrainOnce(serverWorld);
            if (ret) {
                DuskwoodManor.LOGGER.info("queueing another attempt...");
                tryCopyEntitiesAndTrain(serverWorld, onSuccess, count + 1);
            }
            else onSuccess.run();
        }, 50, serverWorld);
    }

    private static void delayAndThenRun(Runnable callback, int delay, ServerWorld serverWorld) {
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.schedule(() -> {
            serverWorld.getServer().execute(() -> {
                callback.run();
            });
        }, delay, TimeUnit.MILLISECONDS);
    }

    @WrapMethod(method = "tryResetTrain")
    private static boolean tryResetTrain(ServerWorld serverWorld, Operation<Boolean> original) {
        if (!serverWorld.getServer().getOverworld().equals(serverWorld)) {
            return original.call(serverWorld);
        }

        long resetTrainStartTime = System.currentTimeMillis();

        DuskwoodManor.LOGGER.info(
            "Loaded entities in world: {}",
            serverWorld.iterateEntities().spliterator().getExactSizeIfKnown()
        );

        MapVariablesWorldComponent areas = MapVariablesWorldComponent.KEY.get(serverWorld);
        BlockPos backupMinPos = BlockPos.ofFloored(areas.getResetTemplateArea().getMinPos());
        BlockPos backupMaxPos = BlockPos.ofFloored(areas.getResetTemplateArea().getMaxPos());
        BlockBox backupTrainBox = BlockBox.create(backupMinPos, backupMaxPos);
        BlockPos trainMinPos = BlockPos.ofFloored(areas.getResetTemplateArea().offset(Vec3d.of(areas.getResetPasteOffset())).getMinPos());
        BlockPos trainMaxPos = trainMinPos.add(backupTrainBox.getDimensions());

        int chunksLoaded = 0;

        // HOW DO I LOAD THESE CHUNKS ??????????????????

        for (int x = Math.floorDiv(backupMinPos.getX(), 16); x <= Math.floorDiv(backupMaxPos.getX(), 16); x++) {
            for (int z = Math.floorDiv(backupMinPos.getZ(), 16); z <= Math.floorDiv(backupMaxPos.getZ(), 16); z++) {
                serverWorld.setChunkForced(x, z, true);
                serverWorld.getChunkManager().addTicket(ChunkTicketType.PLAYER, new ChunkPos(x, z), 2, new ChunkPos(x, z));
                chunksLoaded++;
            }
        }

        for (int x = Math.floorDiv(trainMinPos.getX(), 16); x <= Math.floorDiv(trainMaxPos.getX(), 16); x++) {
            for (int z = Math.floorDiv(trainMinPos.getZ(), 16); z <= Math.floorDiv(trainMaxPos.getZ(), 16); z++) {
                serverWorld.setChunkForced(x, z, true);
                serverWorld.getChunkManager().addTicket(ChunkTicketType.PLAYER, new ChunkPos(x, z), 2, new ChunkPos(x, z));
                chunksLoaded++;
            }
        }

        DuskwoodManor.LOGGER.info(
            "Loaded entities in world: {}",
            serverWorld.iterateEntities().spliterator().getExactSizeIfKnown()
        );

        DuskwoodManor.LOGGER.info("force loaded {} chunks, waiting a few ticks...", chunksLoaded);

        delayAndThenRun(() -> {
            tryCopyEntitiesAndTrain(serverWorld, () -> {
                for (int x = Math.floorDiv(backupMinPos.getX(), 16); x <= Math.floorDiv(backupMaxPos.getX(), 16); x++) {
                    for (int z = Math.floorDiv(backupMinPos.getZ(), 16); z <= Math.floorDiv(backupMaxPos.getZ(), 16); z++) {
                        serverWorld.setChunkForced(x, z, false);
                        serverWorld.getChunkManager().removeTicket(ChunkTicketType.PLAYER, new ChunkPos(x, z), 1, new ChunkPos(x, z));
                    }
                }

                for (int x = Math.floorDiv(trainMinPos.getX(), 16); x <= Math.floorDiv(trainMaxPos.getX(), 16); x++) {
                    for (int z = Math.floorDiv(trainMinPos.getZ(), 16); z <= Math.floorDiv(trainMaxPos.getZ(), 16); z++) {
                        serverWorld.setChunkForced(x, z, false);
                        serverWorld.getChunkManager().removeTicket(ChunkTicketType.PLAYER, new ChunkPos(x, z), 1, new ChunkPos(x, z));
                    }
                }

                DuskwoodManor.LOGGER.info("unloaded chunks");

                long resetTrainEndTime = System.currentTimeMillis();

                DuskwoodManor.LOGGER.info("entire tryResetTrain took {} seconds", (resetTrainEndTime - resetTrainStartTime) / 1000f);
            }, 0);
        }, 200, serverWorld);

        return false;
    }
}
