package dev.ninjdai.splatcube.lobby;

import dev.ninjdai.splatcube.lobby.entities.Camera;
import dev.ninjdai.splatcube.lobby.entities.PlaybackPlayer;
import lombok.Getter;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.Player;
import net.minestom.server.instance.Instance;
import net.minestom.server.tag.Tag;
import net.minestom.server.timer.TaskSchedule;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

@Getter
public enum Cutscenes {
    INKOPOLIS_PLAZA_ENTER_TRAIN((player, camera) -> {
        player.teleport(new Pos(-5, 12, -619.3));

        Entity fakePlayer = createFakePlayer(player);
        fakePlayer.setInstance(Main.inkopolisPlaza, new Pos(-5, 12, -619.3, -180, 0));
        fakePlayer.setAutoViewable(false);
        fakePlayer.addViewer(player);

        camera.setInstance(Main.inkopolisPlaza, new Pos(57, 16.5, -629.5, -39, 27));
        //camera.setLockedOnEntity(fakePlayer);
        player.spectate(camera);

        AtomicInteger steps = new AtomicInteger();
        fakePlayer.scheduler().scheduleTask(() -> {
            fakePlayer.setVelocity(new Vec(3, 0, 0));
            steps.getAndIncrement();
            if (steps.get() == 8) {
                fakePlayer.remove();
                return TaskSchedule.stop();
            }
            return TaskSchedule.tick(5);
        }, TaskSchedule.nextTick());
    }, 10),
    SPLATVILLE_ENTER_TRAIN((player, camera) -> {
        camera.setInstance(Main.splatville, new Pos(16.5, 17, 20, 33, 33));
        player.spectate(camera);

        Entity fakePlayer = createFakePlayer(player);

        fakePlayer.setInstance(Main.splatville, new Pos(11.5, 16, 24.5, -90, 45));
        AtomicInteger steps = new AtomicInteger();
        fakePlayer.scheduler().scheduleTask(() -> {
            fakePlayer.setVelocity(new Vec(3, 0, 0));
            steps.getAndIncrement();
            if (steps.get() == 8) fakePlayer.setVelocity(new Vec(0, 0, -8));
            if (steps.get() == 20) {
                fakePlayer.remove();
                return TaskSchedule.stop();
            }
            return TaskSchedule.tick(5);
        }, TaskSchedule.nextTick());
    }, 4);

    public static Entity createFakePlayer(Player player) {
        Entity fakePlayer;
        if (player.getSkin() != null) {
            fakePlayer = new PlaybackPlayer(player.getUsername(), player.getSkin().textures(), player.getSkin().signature());
        } else {
            fakePlayer = new PlaybackPlayer(player.getUsername(), null, null);
        }
        fakePlayer.setNoGravity(false);
        return fakePlayer;
    }
    
    public final void playThenTeleport(Player p, Instance targetInstance, Pos targetPos) {
        if (p.hasTag(Tags.cutsceneTag)) return;
        p.setTag(Tags.cutsceneTag, true);
        p.setInvisible(true);
        Camera camera = new Camera();
        
        animation.accept(p, camera);
        p.scheduler().scheduleTask(() -> {
            p.setInstance(targetInstance, targetPos);
            camera.remove();
            p.removeTag(Tags.cutsceneTag);
            return TaskSchedule.stop();
        }, TaskSchedule.seconds(duration));
    }

    private final BiConsumer<Player, Camera> animation;
    private final int duration;
    Cutscenes(BiConsumer<Player, Camera> animation, int duration) {
        this.animation = animation;
        this.duration = duration;
    }
}
