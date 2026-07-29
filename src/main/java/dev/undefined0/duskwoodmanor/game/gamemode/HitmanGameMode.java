package dev.undefined0.duskwoodmanor.game.gamemode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.function.UnaryOperator;

import dev.doctor4t.wathe.api.GameMode;
import dev.doctor4t.wathe.cca.GameRoundEndComponent;
import dev.doctor4t.wathe.cca.GameTimeComponent;
import dev.doctor4t.wathe.cca.GameWorldComponent;
import dev.doctor4t.wathe.cca.PlayerShopComponent;
import dev.doctor4t.wathe.game.GameConstants;
import dev.doctor4t.wathe.game.GameFunctions;
import dev.doctor4t.wathe.game.GameFunctions.WinStatus;
import dev.doctor4t.wathe.index.WatheItems;
import dev.doctor4t.wathe.util.AnnounceWelcomePayload;
import dev.undefined0.duskwoodmanor.DuskwoodManor;
import dev.undefined0.duskwoodmanor.ManorUtils;
import dev.undefined0.duskwoodmanor.cca.HitmanDataComponent;
import dev.undefined0.duskwoodmanor.game.ManorRoles;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.entity.player.ItemCooldownManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class HitmanGameMode extends GameMode {
    public HitmanGameMode(Identifier identifier) {
        super(identifier, 15, 4);
    }

    public static final int FAKE_HITMAN_ID = -5;

    private class HitmanInfo {
        public PlayerEntity player;
        public PlayerEntity target;
        public PlayerEntity hunter;

        public HitmanInfo(PlayerEntity hunter, PlayerEntity player, PlayerEntity target) {
            this.player = player;
            this.hunter = hunter;
            this.target = target;
        }
    }

    @Override
    public void initializeGame(ServerWorld serverWorld, GameWorldComponent gameWorldComponent, List<ServerPlayerEntity> players) {
        // technically should never hit but idk apparently some people are?
        if (players.size() < 4) {
            DuskwoodManor.LOGGER.error("less than four players?");
            GameRoundEndComponent.KEY.get(serverWorld).setRoundEndData(serverWorld.getPlayers(), WinStatus.NONE);
            GameFunctions.stopGame(serverWorld);
            return;
        }

        var agents = sortOutAgents(players, serverWorld);
        if (agents.size() == 0) {
            DuskwoodManor.LOGGER.error("Failed to sort out hitman info!!!");
            GameRoundEndComponent.KEY.get(serverWorld).setRoundEndData(serverWorld.getPlayers(), WinStatus.NONE);
            GameFunctions.stopGame(serverWorld);
            return;
        }

        for (ServerPlayerEntity player : players) {
            player.getInventory().clear();
            gameWorldComponent.addRole(player, ManorRoles.HITMAN);

            var hitmanComponent = HitmanDataComponent.KEY.get(player);
            var hitmanInfo = agents.stream().filter(info -> info.player == player).findFirst().get();
            hitmanComponent.setHunter(hitmanInfo.hunter);
            hitmanComponent.setTarget(hitmanInfo.target);

            ItemStack letter = new ItemStack(WatheItems.LETTER);

            letter.set(DataComponentTypes.ITEM_NAME, Text.translatable(letter.getTranslationKey()));
            int letterColor = 0xB57D77;
            letter.apply(DataComponentTypes.LORE, LoreComponent.DEFAULT, component -> {
                List<Text> text = new ArrayList<>();
                UnaryOperator<Style> stylizer = style -> style.withItalic(false).withColor(letterColor);

                String name = ManorUtils.playerName(player);
                if (name.charAt(name.length() - 1) == '\uE780') { // remove ratty supporter icon
                    name = name.substring(0, name.length() - 1);
                }

                text.add(Text.translatable("tip.letter.name", name).styled(style -> style.withItalic(false).withColor(0xFFFFFF)));
                text.add(Text.translatable("tip.letter.hitman.1", ManorUtils.playerName(hitmanInfo.target)).styled(stylizer));
                text.add(Text.translatable("tip.letter.hitman.2", ManorUtils.playerName(hitmanInfo.hunter)).styled(stylizer));
                text.add(Text.translatable("tip.letter.hitman.3", ManorUtils.playerName(hitmanInfo.target), ManorUtils.playerName(hitmanInfo.hunter)).styled(stylizer));
                text.add(Text.translatable("tip.letter.hitman.4").styled(stylizer));

                return new LoreComponent(text);
            });

            player.giveItemStack(letter);

            ItemCooldownManager itemCooldownManager = player.getItemCooldownManager();
            itemCooldownManager.set(WatheItems.DERRINGER, GameConstants.getInTicks(1, 30));
            itemCooldownManager.set(WatheItems.KNIFE, GameConstants.getInTicks(1, 0));

            player.giveItemStack(new ItemStack(WatheItems.CROWBAR));
            player.giveItemStack(new ItemStack(WatheItems.DERRINGER));
            player.giveItemStack(new ItemStack(WatheItems.KNIFE));
        }

        gameWorldComponent.setLooseEndWinner(null);
        for (ServerPlayerEntity player : players) {
            ServerPlayNetworking.send(player, new AnnounceWelcomePayload(FAKE_HITMAN_ID, 0, 0));
        }
    }

    private List<HitmanInfo> sortOutAgents(List<ServerPlayerEntity> players, ServerWorld serverWorld) {
        var ret = new ArrayList<HitmanInfo>(players.size());

        List<ServerPlayerEntity> shuffled = new ArrayList<>(players);
        for (int i = shuffled.size() - 1; i > 0; i--) {
            int j = serverWorld.getRandom().nextInt(i + 1);
            Collections.swap(shuffled, i, j);
        }

        for (int i = 0; i < shuffled.size(); i++) {
            ServerPlayerEntity prev;
            ServerPlayerEntity cur;
            ServerPlayerEntity next;

            if (i == 0) prev = shuffled.get(shuffled.size() - 1);
            else prev = shuffled.get(i - 1);

            if (i == shuffled.size() - 1) next = shuffled.get(0);
            else next = shuffled.get(i + 1);

            cur = shuffled.get(i);

            ret.add(new HitmanInfo(prev, cur, next));
        }

        return ret;
    }

    Function<Long, Integer> HITMAN_PASSIVE_MONEY_TICKER = time -> {
        if (time % GameConstants.getInTicks(0, 1) == 0) {
            return 1;
        }

        return 0;
    };

    @Override
    public void tickServerGameLoop(ServerWorld serverWorld, GameWorldComponent gameWorldComponent) {
        if (!GameTimeComponent.KEY.get(serverWorld).hasTime()) {
            GameRoundEndComponent.KEY.get(serverWorld).setRoundEndData(serverWorld.getPlayers(), WinStatus.TIME);
            GameFunctions.stopGame(serverWorld);
            return;
        }

        int playersAlive = 0;
        ServerPlayerEntity winner = null;
        for (ServerPlayerEntity player : serverWorld.getPlayers()) {
            Integer balanceToAdd = GameConstants.PASSIVE_MONEY_TICKER.apply(serverWorld.getTime());
            if (balanceToAdd > 0) PlayerShopComponent.KEY.get(player).addToBalance(balanceToAdd);

            if (!GameFunctions.isPlayerEliminated(player)) {
                winner = player;
                playersAlive++;
            }
        }

        if (playersAlive <= 1) {
            GameRoundEndComponent.KEY.get(serverWorld).setRoundEndData(serverWorld.getPlayers(), WinStatus.NONE);
            if (playersAlive != 0) GameWorldComponent.KEY.get(serverWorld).setLooseEndWinner(winner.getUuid());
            GameFunctions.stopGame(serverWorld);
            return;
        }
    }
}
