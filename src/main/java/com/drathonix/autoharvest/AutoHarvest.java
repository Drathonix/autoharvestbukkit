package com.drathonix.autoharvest;

import org.bukkit.plugin.java.JavaPlugin;

public final class AutoHarvest extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        getServer().getPluginManager().registerEvents(new HarvestHandler(), this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
