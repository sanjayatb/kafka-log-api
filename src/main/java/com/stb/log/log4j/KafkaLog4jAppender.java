package com.stb.log.log4j;

import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.config.ConfigException;
import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.spi.LoggingEvent;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/***
 * Appender for kafka
 */
public class KafkaLog4jAppender extends AppenderSkeleton {

    private String brokerList;
    private String topic;
    private String compressionType;
    private String securityProtocol;
    private String sslTruststoreLocation;
    private String sslTruststorePassword;
    private String sslKeystoreType;
    private String sslKeystoreLocation;
    private String sslKeystorePassword;
    private String sslKerberosServiceName;
    private String clientJassConf;
    private int retries;
    private int requiredNumAcks = 2147483647;
    private boolean blockingSend;
    private Producer<byte[], byte[]> producer;

    public String getBrokerList() {
        return brokerList;
    }

    public void setBrokerList(String brokerList) {
        this.brokerList = brokerList;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getCompressionType() {
        return compressionType;
    }

    public void setCompressionType(String compressionType) {
        this.compressionType = compressionType;
    }

    public String getSecurityProtocol() {
        return securityProtocol;
    }

    public void setSecurityProtocol(String securityProtocol) {
        this.securityProtocol = securityProtocol;
    }

    public String getSslTruststoreLocation() {
        return sslTruststoreLocation;
    }

    public void setSslTruststoreLocation(String sslTruststoreLocation) {
        this.sslTruststoreLocation = sslTruststoreLocation;
    }

    public String getSslTruststorePassword() {
        return sslTruststorePassword;
    }

    public void setSslTruststorePassword(String sslTruststorePassword) {
        this.sslTruststorePassword = sslTruststorePassword;
    }

    public String getSslKeystoreType() {
        return sslKeystoreType;
    }

    public void setSslKeystoreType(String sslKeystoreType) {
        this.sslKeystoreType = sslKeystoreType;
    }

    public String getSslKeystoreLocation() {
        return sslKeystoreLocation;
    }

    public void setSslKeystoreLocation(String sslKeystoreLocation) {
        this.sslKeystoreLocation = sslKeystoreLocation;
    }

    public String getSslKeystorePassword() {
        return sslKeystorePassword;
    }

    public void setSslKeystorePassword(String sslKeystorePassword) {
        this.sslKeystorePassword = sslKeystorePassword;
    }

    public String getSslKerberosServiceName() {
        return sslKerberosServiceName;
    }

    public void setSslKerberosServiceName(String sslKerberosServiceName) {
        this.sslKerberosServiceName = sslKerberosServiceName;
    }

    public String getClientJassConf() {
        return clientJassConf;
    }

    public void setClientJassConf(String clientJassConf) {
        this.clientJassConf = clientJassConf;
    }

    public int getRetries() {
        return retries;
    }

    public void setRetries(int retries) {
        this.retries = retries;
    }

    public int getRequiredNumAcks() {
        return requiredNumAcks;
    }

    public void setRequiredNumAcks(int requiredNumAcks) {
        this.requiredNumAcks = requiredNumAcks;
    }

    public Producer<byte[], byte[]> getProducer() {
        return producer;
    }

    public void setProducer(Producer<byte[], byte[]> producer) {
        this.producer = producer;
    }

    @Override
    public void activateOptions() {
        Properties properties = new Properties();
        if (StringUtils.isNotBlank(this.brokerList)) {
            properties.put("bootstrap.servers", this.brokerList);
        }

        if (properties.isEmpty()) {
            throw new ConfigException("Bootstrap server properties should be defined");
        } else if (StringUtils.isBlank(topic)) {
            throw new ConfigException("Topic must be defined");
        } else {
            setKafkaProperties(properties);
        }
    }

    private void setKafkaProperties(Properties props) {
        if (StringUtils.isNotBlank(compressionType)) {
            props.put("compression.type", this.compressionType);
        }

        if (this.requiredNumAcks != 2147483647) {
            props.put("acks", Integer.toString(this.requiredNumAcks));
        }

        if (this.retries > 0) {
            props.put("retries", this.retries);
        }

        if (StringUtils.isNotBlank(this.securityProtocol)) {
            props.put("security.protocol", this.securityProtocol);
        }
        if (StringUtils.contains(this.securityProtocol, "SSL") &&
                StringUtils.isNotBlank(this.sslTruststoreLocation) &&
                StringUtils.isNotBlank(this.sslTruststorePassword)) {

            props.put("ssl.truststore.location", this.sslTruststoreLocation);
            props.put("ssl.truststore.password", this.sslTruststorePassword);
            if (StringUtils.isNotBlank(this.sslKeystoreType)
                    && StringUtils.isNotBlank(this.sslKeystoreLocation)
                    && StringUtils.isNotBlank(this.sslKeystorePassword)) {
                props.put("ssi.keystore.type", this.sslKeystoreType);
                props.put("ss1.keystore.location", this.sslKeystoreLocation);
                props.put("ss1.keystore.password", this.sslKeystorePassword);
            }
        }
        if (StringUtils.contains(this.securityProtocol, "SASL") &&
                StringUtils.isNotBlank(this.sslKerberosServiceName) &&
                StringUtils.isNotBlank(this.clientJassConf)) {
            props.put("ssi.keberos.service.name", this.sslKerberosServiceName);
            props.put("ss1.jaas.config", this.clientJassConf);
        }

        props.put("key.serializer", ByteArraySerializer.class.getName());
        props.put("value.serializer", ByteArraySerializer.class.getName());
        this.producer = this.getKafkaProducer(props);
        LogLog.debug("Kafka producer created with "+this.brokerList);
        LogLog.debug("Logs posted to topic "+this.topic);
    }

    private Producer<byte[],byte[]> getKafkaProducer(Properties props) {
        return new KafkaProducer<>(props);
    }

    @Override
    protected void append(LoggingEvent event) {
        String message = this.subAppend(event);
        LogLog.debug("[" + new Date(event.getTimeStamp()) + "]" + message);

        ProducerRecord<byte[], byte[]> newRecord = new ProducerRecord<>(topic, message.getBytes(StandardCharsets.UTF_8));

        if (this.blockingSend) {
            Future<RecordMetadata> response = producer.send(newRecord);

            try {
                response.get();
            } catch (InterruptedException e) {
                LogLog.error("Error Starting application", e);
                Thread.currentThread().interrupt();
            } catch (ExecutionException e) {
                throw new IllegalArgumentException(e);
            }
        } else {
            CompletableFuture.runAsync(() ->
                    this.producer.send(newRecord, (metadata, exception) -> {
                        if (exception != null) {
                            LogLog.error("Unable to write to kafka in appender [" + getName() + "]", exception);
                        }
                    }));
        }
    }

    private String subAppend(LoggingEvent event) {
        return this.layout == null ? event.getRenderedMessage() : this.layout.format(event);
    }

    @Override
    public void close() {
        if (!this.closed) {
            this.closed = true;
            this.producer.close();
        }
    }

    @Override
    public boolean requiresLayout() {
        return true;
    }
}
