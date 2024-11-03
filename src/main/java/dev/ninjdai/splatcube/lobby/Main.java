package dev.ninjdai.splatcube.lobby;

import dev.ninjdai.splatcube.lobby.blockhandlers.SignHandler;
import dev.ninjdai.splatcube.lobby.blockhandlers.SkullHandler;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.GameMode;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.event.player.AsyncPlayerConfigurationEvent;
import net.minestom.server.event.player.PlayerChatEvent;
import net.minestom.server.event.player.PlayerSpawnEvent;
import net.minestom.server.extras.velocity.VelocityProxy;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.instance.InstanceManager;
import net.minestom.server.instance.LightingChunk;
import net.minestom.server.instance.anvil.AnvilLoader;

public class Main {
    public static InstanceContainer inkopolisPlaza;
    public static InstanceContainer inkopolisSquare;
    public static InstanceContainer splatville;

    public static void main(String[] args) {
        MinecraftServer minecraftServer = MinecraftServer.init();
        InstanceManager instanceManager = MinecraftServer.getInstanceManager();

        // Register Handlers
        MinecraftServer.getBlockManager().registerHandler(SkullHandler.KEY, SkullHandler::new);
        MinecraftServer.getBlockManager().registerHandler(SignHandler.KEY, SignHandler::new);

        // Load Lobbies
        inkopolisPlaza = instanceManager.createInstanceContainer();
        inkopolisPlaza.setChunkLoader(new AnvilLoader("worlds/inkopolis_plaza"));
        inkopolisPlaza.setChunkSupplier(LightingChunk::new);

        inkopolisSquare = instanceManager.createInstanceContainer();
        inkopolisSquare.setChunkLoader(new AnvilLoader("worlds/inkopolis_square"));
        inkopolisSquare.setChunkSupplier(LightingChunk::new);

        splatville = instanceManager.createInstanceContainer();
        splatville.setChunkLoader(new AnvilLoader("worlds/splatville"));
        splatville.setChunkSupplier(LightingChunk::new);

        // Velocity Setup
        if(System.getenv("PAPER_VELOCITY_SECRET") instanceof String vsecret) {
            VelocityProxy.enable(vsecret);
            System.out.println("v-secret: " + vsecret);
        }

        // Event Handlers
        GlobalEventHandler globalEventHandler = MinecraftServer.getGlobalEventHandler();
        globalEventHandler.addListener(AsyncPlayerConfigurationEvent.class, event -> {
            event.setSpawningInstance(splatville);
            event.getPlayer().setRespawnPoint(new Pos(-9.5, 9, -1.5));
        });
        globalEventHandler.addListener(PlayerSpawnEvent.class, event -> {
            event.getPlayer().setGameMode(GameMode.ADVENTURE);
            event.getPlayer().setAllowFlying(true);

            if (event.getInstance() == splatville) {
                event.getPlayer().lookAt(new Pos(-10, 24, 37));
            } else if (event.getInstance() == inkopolisSquare)
                event.getPlayer().lookAt(new Pos(-222, 9, -782));
            else if (event.getInstance() == inkopolisPlaza) {
                event.getPlayer().lookAt(new Pos(76, 12, -698));
            }
        });
        globalEventHandler.addListener(PlayerChatEvent.class, event -> {
            if (event.getInstance() == splatville) {
                event.getPlayer().setInstance(inkopolisPlaza, new Pos(76.5, 10, -659.5));
            } else if (event.getInstance() == inkopolisPlaza) {
                event.getPlayer().setInstance(inkopolisSquare, new Pos(-221.5, 8, -743.5));
            } else {
                event.getPlayer().setInstance(splatville, new Pos(-9.5, 9, -1.5));
            }
        });

        minecraftServer.start("0.0.0.0", 25565);
    }
}
