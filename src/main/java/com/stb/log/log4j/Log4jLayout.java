package com.stb.log.log4j;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.stb.log.LogUtil;
import com.stb.log.model.LogJson;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.spi.LocationInfo;
import org.apache.log4j.spi.LoggingEvent;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * kafka message Layout
 */
public class Log4jLayout extends PatternLayout {

    private String application;
    private Map<String,String> properties;
    private boolean locationInfo = true;
    private boolean contextData = true;

    public String format(LoggingEvent event){
        LogJson logJson = new LogJson();
        logJson.setApplication(this.application);
        logJson.setHost(LogUtil.HOST_NAME);
        logJson.setHostIp(LogUtil.HOST_IP);
        logJson.setTimestamp(event.getTimeStamp());
        logJson.setLevel(event.getLevel().toString());
        logJson.setMessage(super.format(event));

        if(event.getThrowableInformation() != null){
            logJson.setException(LogUtil.extractException(event.getThrowableInformation().getThrowable()));
        }

        logJson.setThreadName(event.getThreadName());
        logJson.setProperties(this.properties);
        if(this.contextData){
            logJson.setContextData(event.getProperties());
        }

        if(this.locationInfo){
            logJson.setLocation(this.extractLocation(event.getLocationInformation()));
        }

        try {
            return LogUtil.OBJECT_MAPPER.writeValueAsString(logJson);
        } catch (JsonProcessingException e) {
           throw new IllegalStateException("Unable to format logging event",e);
        }
    }

    private Map<String, String> extractLocation(LocationInfo locationInformation) {
        Map<String,String> json = new LinkedHashMap<>();
        json.put("line_number",locationInformation.getLineNumber());
        json.put("class",locationInformation.getClassName());
        json.put("method",locationInformation.getMethodName());
        return json;
    }

    public void setUserProperties(String properties){
        String strProps = properties.replace("'","\"")
                .replace("\\","\\\\");

        try {
            this.properties = LogUtil.OBJECT_MAPPER.readValue(strProps,Map.class);
        } catch (IOException e) {
            LogLog.error("Properties not valid, will be excluded from the log",e);
            this.properties = null;
        }
    }

    public String getApplication() {
        return application;
    }

    public void setApplication(String application) {
        this.application = application;
    }

    public Map<String, String> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, String> properties) {
        this.properties = properties;
    }

    public boolean isLocationInfo() {
        return locationInfo;
    }

    public void setLocationInfo(boolean locationInfo) {
        this.locationInfo = locationInfo;
    }

    public boolean isContextData() {
        return contextData;
    }

    public void setContextData(boolean contextData) {
        this.contextData = contextData;
    }
}
