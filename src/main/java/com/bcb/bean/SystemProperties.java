package com.bcb.bean;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Data
@Component
//@PropertySource(value = "classpath:resource.yml")
@ConfigurationProperties(prefix = "sys")
public class SystemProperties {
    private String certUrl;
    private String midUrl;

    private String seaHttpOnlineUrl;
    private String seaHttpInnerUrl;
    private String seafileUrl;
    private String seafileUserName;
    private String seafilePassword;
    private String seafileRepoId;

    private String telegramURL;
    private Long telegramChatId;

    private String fastdfsIP;
    private Integer fastdfsPort;
    private Integer fastdfsG_tracker_http_port;
    private Integer fastdfsG_network_timeout;
    private Integer fastdfsG_connect_timeout;
    private String fastdfsNginx;
    private Integer fastdfsSize;
}
