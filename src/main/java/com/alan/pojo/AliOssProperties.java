package com.alan.pojo;

import com.aliyun.oss.common.auth.EnvironmentVariableCredentialsProvider;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "sky.alioss")
@Data
public class AliOssProperties {
    private String endpoint;
    private String bucketName;
    private String folderName;
    private EnvironmentVariableCredentialsProvider credentialsProvider;
}
