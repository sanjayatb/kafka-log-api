# kafka-log-api
Kafka Log4j Appender which post log messages to given kafka topic

### Sample Usage as a log4j appender
```
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE log4j:configuration SYSTEM "log4j.dtd">
<log4j:configuration debug="true" xmlns:log4j='http://jakarta.apache.org/log4j/'>

    <appender name="console" class="org.apache.log4j.ConsoleAppender">
        <param name="Target" value="System.out"/>
        <layout class="org.apache.log4j.PatternLayout">
            <param name="ConversionPattern" value="%d{yyyy-MM-dd HH:mm:ss} %-5p %c{1}:%L - %m%n"/>
        </layout>
    </appender>

    <appender name="fileAppender" class="org.apache.log4j.RollingFileAppender">
        <param name="File" value="demoApplication.log"/>
        <layout class="org.apache.log4j.PatternLayout">
            <param name="ConversionPattern" value="%d{yyyy-MM-dd HH:mm:ss} %-5p %c{1}:%L - %m%n"/>
        </layout>
    </appender>

    <appender name="kafka" class="com.stb.log.log4j.KafkaLog4jAppender">
        <layout class="com.stb.log.log4j.Log4jLayout">
            <param name="Application" value="TEST-APP"/>
            <param name="Level" value="%-5p"/>
            <param name="Properties" value="{'user':'${user.name}','dir':'${user.dir}'}}"/>
            <param name="ConversionPattern" value="%d{yyyy-MM-dd HH:mm:ss} %-5p %c{1}:%L - %m%n"/>
        </layout>
        <param name="BrokerList" value="broker1:6667,broker2:6667"/>
        <param name="Topic" value="log_topic"/>
        <param name="blockingSend" value="false"/>
        <param name="securityProtocol" value="SASL_PLAINTEXT"/>
        <param name="saslKeberosServiceName" value="kafka"/>
    </appender>

    <root>
        <priority value="INFO"/>
        <appender-ref ref="console"/>
        <appender-ref ref="fileAppender"/>
        <appender-ref ref="kafka"/>
    </root>

</log4j:configuration>
```
