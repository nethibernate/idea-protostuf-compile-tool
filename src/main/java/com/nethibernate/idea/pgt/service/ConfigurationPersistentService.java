package com.nethibernate.idea.pgt.service;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import com.nethibernate.idea.pgt.bean.Config;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author: nethibernate
 */
@State(name = "EgtConfig", storages = {@Storage(file = "$APP_CONFIG$/Egt.xml")})
public class ConfigurationPersistentService implements PersistentStateComponent<Config> {

    private Config config = new Config();

    public static ConfigurationPersistentService getInstance() {
        return ServiceManager.getService(ConfigurationPersistentService.class);
    }

    @Nullable
    @Override
    public Config getState() {
        return config;
    }

    @Override
    public void loadState(@NotNull Config state) {
        XmlSerializerUtil.copyBean(state, config);
    }

    public Config getConfig() {
        return config;
    }
}
