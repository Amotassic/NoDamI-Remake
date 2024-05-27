package com.amotassic.nodami;

import com.amotassic.nodami.config.NodamiConfig;
import com.amotassic.nodami.interfaces.EntityHurtCallback;
import com.amotassic.nodami.interfaces.EntityKnockbackCallback;
import com.amotassic.nodami.interfaces.PlayerAttackCallback;
import com.amotassic.nodami.interfaces.PlayerEntityAccessor;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.kyrptonaught.kyrptconfig.config.ConfigManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NoDamI implements ModInitializer {
    public static final String MODID = "nodami";
    public static final Logger LOGGER = LoggerFactory.getLogger(MODID);
    private final ConfigManager configManager = new ConfigManager.SingleConfigManager(MODID, new NodamiConfig());
    public static NodamiConfig config;

    @Override
    public void onInitialize() {
        LOGGER.info("NoDamI Fabric Edition is starting.");
        configManager.load();
        config = (NodamiConfig) configManager.getConfig("nodamiconfig");
        registerHandlers();
        LOGGER.info("NoDamI: Loading completed.");
    }

    private void registerHandlers() {
        EntityHurtCallback.EVENT.register((entity, source, amount) -> {
            if (entity.getEntityWorld().isClient) {
                return ActionResult.PASS;
            }
            Entity trueSource = source.getAttacker();
            if (config.debugMode && entity instanceof PlayerEntity) {
                String debugSource;
                if (trueSource == null || EntityType.getId(trueSource.getType()) == null) {
                    debugSource = "null";
                } else {
                    debugSource = EntityType.getId(trueSource.getType()).toString();
                }
                String message = String.format("Type of damage received: %s\nAmount: %.3f\nTrue Source (mob id): %s\n",
                        source.getName(), amount, debugSource);
                ((PlayerEntity) entity).sendMessage(Text.literal(message), false);

            }
            if (config.excludePlayers && entity instanceof PlayerEntity) {
                return ActionResult.PASS;
            }
            if (config.excludeAllMobs && !(entity instanceof PlayerEntity)) {
                return ActionResult.PASS;
            }
            Identifier loc = EntityType.getId(entity.getType());
            for (String id : config.dmgReceiveExcludedEntities) {
                if (loc == null)
                    break;
                int starIndex = id.indexOf('*');
                if (starIndex != -1) {
                    if (loc.toString().contains(id.substring(0, starIndex))) {
                        return ActionResult.PASS;
                    }
                } else if (loc.toString().equals(id)) {
                    return ActionResult.PASS;
                }
            }
            for (String dmgType : config.damageSrcWhitelist) {
                if (source.getName().equals(dmgType)) {
                    return ActionResult.PASS;
                }
            }
            for (String id : config.attackExcludedEntities) {
                Entity attacker = source.getAttacker();
                if (attacker == null) break;
                if (loc == null) break;
                int starIndex = id.indexOf('*');
                if (starIndex != -1) {
                    if (EntityType.getId(attacker.getType()).toString().contains(id.substring(0, starIndex))) {
                        return ActionResult.PASS;
                    }
                } else if (EntityType.getId(attacker.getType()).toString().equals(id)) {
                    return ActionResult.PASS;
                }

            }

            entity.timeUntilRegen = config.iFrameInterval;
            return ActionResult.PASS;
        });
        PlayerAttackCallback.EVENT.register((player, target) -> {
            if (player.getEntityWorld().isClient) {
                return ActionResult.PASS;
            }

            if (config.debugMode) {
                String message = String.format("Entity attacked: %s",
                        EntityType.getId(target.getType()));
                player.sendMessage(Text.literal(message), false);
            }

            float str = player.getAttackCooldownProgress(0);
            if (str <= config.attackCancelThreshold) {
                return ActionResult.FAIL;
            }
            if (str <= config.knockbackCancelThreshold) {
                // Don't worry, it's only magic
                PlayerEntityAccessor playerAccessor = (PlayerEntityAccessor) player;
                playerAccessor.noDamI$setSwinging(true);
            }

            return ActionResult.PASS;

        });
        EntityKnockbackCallback.EVENT.register((entity, source, amp, dx, dz) -> {
            if (entity.getEntityWorld().isClient) {
                return ActionResult.PASS;
            }
            if (source != null) {
                // IT'S ONLY MAGIC
                if (source instanceof PlayerEntity player) {
                    PlayerEntityAccessor playerAccessor = (PlayerEntityAccessor) player;
                    if (playerAccessor.noDamI$isSwinging()) {
                        playerAccessor.noDamI$setSwinging(false);
                        return ActionResult.FAIL;
                    }
                }
            }
            return ActionResult.PASS;
        });
        ServerTickEvents.END_SERVER_TICK.register((server) -> {
            for (ServerWorld world : server.getWorlds()) {
                for (PlayerEntity player : world.getPlayers()) {
                    if (config.noAttackCD) {
                        player.lastAttackedTicks = 1145;
                    }
                }
            }
        });
    }
}
