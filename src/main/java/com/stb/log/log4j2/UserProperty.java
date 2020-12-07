package com.stb.log.log4j2;

import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginConfiguration;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;

@Plugin(
        name = "UserProperty",
        category = "Core",
        printObject = true
)
public class UserProperty {
    private final String key;
    private final String value;

    public UserProperty(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "UserProperty{" +
                "key='" + key + '\'' +
                ", value='" + value + '\'' +
                '}';
    }

    @PluginFactory
    public static UserProperty createUserProperty(@PluginConfiguration Configuration configuration,
                                                  @PluginAttribute("key") String key,
                                                  @PluginAttribute("value") String value) {
        return new UserProperty(key, value);
    }

}
