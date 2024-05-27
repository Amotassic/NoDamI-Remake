package com.amotassic.nodami.config;

import blue.endless.jankson.Comment;
import net.kyrptonaught.kyrptconfig.config.AbstractConfigFile;

public class NodamiConfig implements AbstractConfigFile {

    @Comment("How many ticks of i-frames does an entity get when damaged, from 0 (default), to 2^31-1 (nothing can take damage)\n实体受到伤害时获得多少刻伤害免疫，可以设置为从0到 2^31-1 之间任意一个数")
    public int iFrameInterval = 0;

    @Comment("No attack cooldown, like bedrock edition      取消攻击冷却")
    public boolean noAttackCD = false;

    @Comment("Are players excluded from this mod (if true, players will always get 10 ticks of i-frames on being damaged)\n设置为true时，所有玩家在受到伤害时依然会获得伤害免疫")
    public boolean excludePlayers = false;

    @Comment("Are all mobs excluded from this mod (if true, all mobs will always get 10 ticks of i-frames on being damaged)\n设置为true时，所有怪物在受到伤害时依然会获得伤害免疫")
    public boolean excludeAllMobs = false;

    @Comment("If true, turns on feature which sends a message when a player receives damage, containing information such as the name of the source and the quantity. Use this to find the name of the source you need to whitelist, or the id of the mob you want to exclude.\ndebug模式。设置为true时，会在受到伤害时收到一条包含伤害来源及伤害量的信息，以便于添加白名单")
    public boolean debugMode = false;

    @Comment("How weak a player's attack can be before it gets nullified, from 0 (0%, cancels multiple attacks on the same tick) to 1 (100%, players cannot attack), or -0.1 (disables this feature)\n设置攻击冷却等于或低于百分之多少时无法造成伤害，从0（取消同一帧的多次攻击）到1（完全无法造成伤害），或者是-0.1（禁用此功能）")
    public float attackCancelThreshold = 0.1f;

    @Comment("How weak a player's attack can be before the knockback gets nullified, from 0 (0%, cancels multiple attacks on the same tick) to 1 (100%, no knockback), or -0.1 (disables this feature)\n设置攻击冷却等于或低于百分之多少时无法造成击退，从0（取消同一帧的多次击退）到1（无法造成击退），或者是-0.1（禁用此功能）")
    public float knockbackCancelThreshold = 0.75f;

    @Comment("List of entities that need to give i-frames on attacking\n攻击时会给予被攻击目标伤害免疫的实体列表")
    public String[] attackExcludedEntities = new String[] {"minecraft:slime", "minecraft:magma_cube", "twilightforest:maze_slime"};

    @Comment("List of entities that need to receive i-frames on receiving attacks or relies on i-frames\n受到攻击时依然会获得伤害免疫的实体列表")
    public String[] dmgReceiveExcludedEntities = new String[] {};

    @Comment("List of damage sources that need to give i-frames on doing damage (ex: lava)\n造成伤害时依然会给予伤害免疫的伤害来源列表")
    public String[] damageSrcWhitelist = new String[] {"inFire", "lava", "sweetBerryBush", "cactus", "lightningBolt", "inWall", "hotFloor"};
}
