package com.stb.log.log4j2;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.stb.log.LogUtil;
import com.stb.log.model.LogJson;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.log4j.helpers.LogLog;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginConfiguration;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.layout.AbstractStringLayout;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;


@Plugin(
        name = "Log4j2Layout",
        category = "Core",
        elementType = "layout",
        printObject = true
)
public class Log4j2Layout extends AbstractStringLayout {

    private final String application;
    private Map<String, String> properties;
    private boolean locationInfo;
    private boolean contextData;

    public Log4j2Layout(boolean locationInfo, boolean contextData, String application, UserProperty[] properties, Charset charset) {
        super(charset);
        this.application = application;
        this.locationInfo = locationInfo;
        this.contextData = contextData;

        if (!ArrayUtils.isEmpty(properties)) {
            this.properties = new HashMap<>();
            int len = properties.length;

            for (int i = 0; i < len; i++) {
                UserProperty property = properties[i];
                this.properties.put(property.getKey(), property.getValue());
            }
        }
    }

    @PluginFactory
    public static Log4j2Layout createLayout(@PluginConfiguration Configuration config,
                                            @PluginAttribute(value = "locationInfo", defaultBoolean = true) boolean locationInfo,
                                            @PluginAttribute(value = "contextData", defaultBoolean = true) boolean contextData,
                                            @PluginAttribute(value = "application") String application,
                                            @PluginAttribute(value = "charset", defaultString = "UTF-8") String charset,
                                            @PluginAttribute(value = "UserProperty") UserProperty[] properties
    ) {
        Charset charsetCopy = Charset.forName(charset);
        LogLog.debug("Creating Log4j2Layout with charset " + charsetCopy);
        return new Log4j2Layout(locationInfo,contextData,application,properties,charsetCopy);
    }

    @Override
    public String toSerializable(LogEvent logEvent) {
        LogJson logJson = new LogJson();
        logJson.setApplication(this.application);
        logJson.setHost(LogUtil.HOST_NAME);
        logJson.setHostIp(LogUtil.HOST_IP);
        logJson.setTimestamp(logEvent.getTimeMillis());
        logJson.setThreadName(logEvent.getThreadName());

        if (logEvent.getLevel() != null) {
            logJson.setLevel(logEvent.getLevel().toString());
        }

        if (logEvent.getThrown() != null) {
            logJson.setException(LogUtil.extractException(logEvent.getThrown()));
        }

        logJson.setProperties(this.properties);
        if (this.contextData) {
            logJson.setContextData(logEvent.getContextData().toMap());
        }
        if (this.locationInfo) {
            logJson.setLocation(extractLocationInfo(logEvent.getSource()));
        }
        try {
            return LogUtil.OBJECT_MAPPER.writeValueAsString(logJson);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Fail to format logging event", e);
        }
    }

    private Map<String, String> extractLocationInfo(StackTraceElement source) {
        Map<String, String> json = new LinkedHashMap<>();
        json.put("line_number", Integer.toString(source.getLineNumber()));
        json.put("class", source.getClassName());
        json.put("method", source.getMethodName());
        return json;
    }
}
