package com.stb.log;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.LinkedHashMap;
import java.util.Map;

public class LogUtil {

    public static final String HOST_IP;
    public static final String HOST_NAME;
    public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private LogUtil(){}

    static {
        String hostname= "unknown_host";
        String hostIp="unknown_ip";

        try{
            InetAddress localAddress = InetAddress.getLocalHost();
            hostname = localAddress.getHostName();
            hostIp = localAddress.getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        HOST_IP = hostIp;
        HOST_NAME = hostname;
    }


    public static Map<String, String> extractException(Throwable throwable) {
        Map<String,String> json = new LinkedHashMap<>();
        json.put("class",throwable.getClass().getCanonicalName());
        json.put("message",throwable.getMessage());
        json.put("stacktrace", ExceptionUtils.getStackTrace(throwable));
        return json;
    }
}
