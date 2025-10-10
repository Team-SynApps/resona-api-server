package support.database;

import java.util.Arrays;

public enum SystemTableName {
  INFORMATION_SCHEMA,
  SYSTEM_,
  ENUM_,
  IN_,
  CONSTANTS,
  RIGHTS,
  ROLES,
  SESSIONS,
  SESSION_STATE,
  SETTINGS,
  USERS,
  SYNONYMS,
  QUERY_STATISTICS,
  LOCKS,
  INDEXES,
  INDEX_COLUMNS;

  public static boolean matches(String tableName) {
    String upperTableName = tableName.toUpperCase();
    return Arrays.stream(values())
        .anyMatch(systemTable -> upperTableName.startsWith(systemTable.name()));
  }
}
