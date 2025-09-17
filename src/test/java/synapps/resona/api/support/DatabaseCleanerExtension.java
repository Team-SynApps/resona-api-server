package synapps.resona.api.support;

import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

public class DatabaseCleanerExtension implements AfterEachCallback {

  @Override
  public void afterEach(final ExtensionContext context) {
    DatabaseCleaner dataCleaner = (DatabaseCleaner) SpringExtension.getApplicationContext(context)
        .getBean("databaseCleaner");
    dataCleaner.execute();
  }
}