package com.github.rmsy;


import com.github.rmsy.impl.SimpleBackend;
import org.bukkit.plugin.java.JavaPlugin;
import tc.oc.api.backend.BackendManager;

public class PGM extends JavaPlugin {

    public void onDisable() {

    }

    public void onEnable() {
        BackendManager.setBackend(new SimpleBackend());
    }
}
