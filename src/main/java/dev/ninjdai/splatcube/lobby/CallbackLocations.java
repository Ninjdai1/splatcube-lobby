package dev.ninjdai.splatcube.lobby;

import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Player;
import net.minestom.server.instance.Instance;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public enum CallbackLocations {
    SPLATVILLE_METRO(
            Main.splatville,
            new Pos(8.5, 15, 23.5), new Pos(11.5, 18, 28.5),
            (p) -> Cutscenes.SPLATVILLE_ENTER_TRAIN.playThenTeleport(p, Main.inkopolisPlaza, new Pos(76.5, 10, -659.5))
    ),
    INKOPOLIS_PLAZA_METRO(
            Main.inkopolisPlaza,
            new Pos(81.5, 10, -634.5), new Pos(84.5, 12, -632.5),
            (p) -> Cutscenes.INKOPOLIS_PLAZA_ENTER_TRAIN.playThenTeleport(p, Main.splatville, new Pos(7, 16, 26))
    ),

    SPLATVILLE_HUB(
            Main.splatville,
            new Pos(-13, 15, 37), new Pos(-7, 19, 43),
            (player) -> {
                if (!player.hasTag(Tags.hubTag) || !player.getTag(Tags.hubTag)) {
                    player.setTag(Tags.hubTag, true);
                    player.sendMessage("Joining queue...");
                    MinecraftServer.getCommandManager().execute(player, "queue join Splatcube4v4");
                }
            },
            (player) -> {
                player.setTag(Tags.hubTag, false);
                player.sendMessage("Leaving queue");
                MinecraftServer.getCommandManager().execute(player, "queue leave");
            }
    );

    public final Instance instance;
    public final Pos minPos;
    public final Pos maxPos;
    public final Consumer<Player> enterCallback;
    public final Consumer<Player> leaveCallback;

    public boolean playerInArea(Player player) {
        if (player.getInstance() != instance) return false;
        Pos p = player.getPosition();
        return minPos.x() <= p.x() && p.x() <= maxPos.x() && minPos.y() <= p.y() && p.y() <= maxPos.y() && minPos.z() <= p.z() && p.z() <= maxPos.z();
    }

    public boolean posInArea(Instance i, Pos p) {
        if (i != instance) return false;
        return minPos.x() <= p.x() && p.x() <= maxPos.x() && minPos.y() <= p.y() && p.y() <= maxPos.y() && minPos.z() <= p.z() && p.z() <= maxPos.z();
    }

    CallbackLocations(@NotNull Instance instance, @NotNull Pos minPos, @NotNull Pos maxPos, @NotNull Consumer<Player> enterCallback) {
        this.instance = instance;
        this.minPos = minPos;
        this.maxPos = maxPos;
        this.enterCallback = enterCallback;
        this.leaveCallback = (player) -> {};
    }

    CallbackLocations(@NotNull Instance instance, @NotNull Pos minPos, @NotNull Pos maxPos, @Nullable Consumer<Player> enterCallback, @Nullable Consumer<Player> leaveCallback) {
        this.instance = instance;
        this.minPos = minPos;
        this.maxPos = maxPos;
        if (enterCallback==null) this.enterCallback = (player) -> {};
        else this.enterCallback = enterCallback;
        if (leaveCallback==null) this.leaveCallback = (player) -> {};
        else this.leaveCallback = leaveCallback;
    }
}
