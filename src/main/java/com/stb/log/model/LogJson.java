package com.stb.log.model;

import java.util.Map;

public class LogJson {

    private String application;
    private String host;
    private String hostIp;
    private String level;
    private String message;
    private String threadName;
    private long timestamp;
    private Map<String,String> exception;
    private Map<String,String> location;
    private Map<String,String> properties;
    private Map<String,String> contextData;

    public String getApplication() {
        return application;
    }

    public void setApplication(String application) {
        this.application = application;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getHostIp() {
        return hostIp;
    }

    public void setHostIp(String hostIp) {
        this.hostIp = hostIp;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getThreadName() {
        return threadName;
    }

    public void setThreadName(String threadName) {
        this.threadName = threadName;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public Map<String, String> getException() {
        return exception;
    }

    public void setException(Map<String, String> exception) {
        this.exception = exception;
    }

    public Map<String, String> getLocation() {
        return location;
    }

    public void setLocation(Map<String, String> location) {
        this.location = location;
    }

    public Map<String, String> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, String> properties) {
        this.properties = properties;
    }

    public Map<String, String> getContextData() {
        return contextData;
    }

    public void setContextData(Map<String, String> contextData) {
        this.contextData = contextData;
    }
}
