package dev.ninjdai.splatcube.lobby.entities;

import lombok.Setter;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.metadata.display.BlockDisplayMeta;
import org.jetbrains.annotations.Nullable;

@Setter
public class Camera extends Entity {
    @Nullable
    private Entity lockedOnEntity;

    public Camera() {
        super(EntityType.BLOCK_DISPLAY);
        BlockDisplayMeta meta = (BlockDisplayMeta) getEntityMeta();
        meta.setHasNoGravity(true);
        meta.setPosRotInterpolationDuration(10);
    }

    public void setPosRotInterpolationDuration(int duration) {
        ((BlockDisplayMeta) getEntityMeta()).setPosRotInterpolationDuration(duration);
    }

    public void cancelLockOnEntity() {
        lockedOnEntity = null;
    }

    @Override
    public void tick(long time) {
        if (lockedOnEntity != null && instance == lockedOnEntity.getInstance()) lookAt(lockedOnEntity);
    }
}
