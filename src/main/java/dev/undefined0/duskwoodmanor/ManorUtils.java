package dev.undefined0.duskwoodmanor;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;

public class ManorUtils {
    public static String playerName(PlayerEntity player) {
        if (player == null) return "(?????)";

        Text displayName = player.getDisplayName();
        return displayName != null ? displayName.getString() : player.getName().getString();
    }
}
