package synapps.resona.api.global.config.database;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

// StorageProperties.java
@Configuration
@ConfigurationProperties(prefix = "oci.storage")
@Getter
@Setter
public class StorageProperties {
    private String bucketName;
    private String compartmentId;
    private String namespace;
    private String region;
    private String bufferBucketName;
    private String diskBucketName;
}
