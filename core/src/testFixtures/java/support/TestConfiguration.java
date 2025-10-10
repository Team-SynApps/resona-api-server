package support;

import jakarta.persistence.EntityManager;
import javax.sql.DataSource;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.core.MongoTemplate;
import support.database.DatabaseCleaner;

@SpringBootApplication
public class TestConfiguration {
}