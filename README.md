# atch-backend

## File Upload
This project use oci-bucket to upload files. Uses Client Oracle-JavaSDK/3.43.1. Links below are official documents.
- [Java SDK](https://docs.oracle.com/en-us/iaas/Content/API/SDKDocs/javasdk.htm)
- [Copy Object official Docs](https://docs.oracle.com/en-us/iaas/api/#/en/objectstorage/20160918/Object/CopyObject)
- [Put Object official Docs](https://docs.oracle.com/en-us/iaas/api/#/en/objectstorage/20160918/Object/PutObject)


## Logging
This project use log4j2 to log. The logs are stored to file, and mongoDB database.

To log, I recommend using log4j libraries rather than slfj.
```java
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ExampleClass{
    private final Logger logger = LogManager.getLogger(ExampleClass.class);

    private void loggingExample() {
        logger.info("log example");    
    }
}
```

To see further configuration, see `resources/log4j2-spring.xml`.


- this repository use diceware from [diceware](https://github.com/biddster/diceware)