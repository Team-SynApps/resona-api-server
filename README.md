# atch-backend



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