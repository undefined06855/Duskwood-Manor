package dev.undefined0.duskwoodmanor.events;

import java.util.List;

import dev.undefined0.duskwoodmanor.DuskwoodManor;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.world.World;

public class UseBlockListener implements UseBlockCallback {
    @Override
    public ActionResult interact(PlayerEntity player, World world, Hand hand, BlockHitResult hitResult) {
        if (player.isCreative() || player.isSpectator()) {
            return ActionResult.PASS;
        }

        var blockData = world.getBlockState(hitResult.getBlockPos());
        var name = blockData.getBlock().getTranslationKey();

        DuskwoodManor.LOGGER.info("right clicked on " + name);

        List<TagKey<Block>> whitelistedTags = List.of(
            TagKey.of(RegistryKeys.BLOCK, Identifier.of("minecraft", "doors")),
            TagKey.of(RegistryKeys.BLOCK, Identifier.of("minecraft", "buttons")),
            TagKey.of(RegistryKeys.BLOCK, Identifier.of("wathe", "vent_hatches"))
        );

        List<String> whitelistedKeys = List.of(
            "block.wathe.food_platter",
            "block.wathe.drink_tray",
            "block.wathe.privacy_glass_panel"
        );

        for (TagKey<Block> tagKey : whitelistedTags) {
            if (blockData.isIn(tagKey)) {
                return ActionResult.PASS;
            }
        }

        for (String key : whitelistedKeys) {
            if (name.equals(key)) {
                return ActionResult.PASS;
            }
        }

        // are we serious
        if (name.contains("wathe") && name.contains("door")) {
            return ActionResult.PASS;
        }

        return ActionResult.FAIL;
    }
}
