package ca.dal.distributed.dpg1.Controllers.QueryProcessingModule.Utils;

import ca.dal.distributed.dpg1.Controllers.QueryProcessingModule.Exceptions.QueryExecutionRuntimeException;
import ca.dal.distributed.dpg1.Controllers.QueryProcessingModule.Exceptions.ResourceLockingException;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Ankush Mudgal
 * Resource Lock Manager - Manages the Database/Table level locking for query execution.
 */
public final class ResourceLockManager {

  private ResourceLockManager() {
    // Required private constructor. Cannot be instantiated
  }

  /**
   * The enum - Lock type.
   */
  public enum LockType {
    /**
     * Shared lock type.
     */
    SHARED_LOCK,

    /**
     * Exclusive lock type.
     */
    EXCLUSIVE_LOCK
  }

  /**
   * The constant sharedLockCounter.
   */
  public static int sharedLockCounter = 0;

  /**
   * The constant allLocks.
   */
  public static final Map<String, LockType> allLocks =
      new HashMap<>();

  /**
   * @author Ankush Mudgal
   * Apply shared lock.
   *
   * @param databaseName the database name
   * @param tableName    the table name
   * @throws ResourceLockingException the resource locking exception
   */
  public static void applySharedLock(final String databaseName, final String tableName) throws ResourceLockingException {
    final String databasePath = databaseName + "." + "null";
    final LockType databaseLockTypeApplied = ResourceLockManager.allLocks.get(databasePath);
    if (databaseLockTypeApplied != null) {
      throw new ResourceLockingException ("Database " + databaseName + " locked!");
    }
    final String tablePath = databaseName + "." + tableName;
    final LockType tableLockTypeApplied = ResourceLockManager.allLocks.get(tablePath);
    if (tableLockTypeApplied == null) {
      ResourceLockManager.allLocks.put(tablePath, LockType.SHARED_LOCK);
      ResourceLockManager.sharedLockCounter++;
    }
    if (tableLockTypeApplied == LockType.EXCLUSIVE_LOCK) {
      throw new ResourceLockingException ("Read or write operations cannot be performed on table " + tableName + " at this moment!");
    }
    if (tableLockTypeApplied == LockType.SHARED_LOCK) {
      ResourceLockManager.sharedLockCounter++;
    }
  }

  /**
   * @author Ankush Mudgal
   * Release shared lock.
   *
   * @param databaseName the database name
   * @param tableName    the table name
   */
  public static void releaseSharedLock(final String databaseName, final String tableName) {
    final String tablePath = databaseName + "." + tableName;
    ResourceLockManager.sharedLockCounter--;
    if (ResourceLockManager.sharedLockCounter == 0) {
      ResourceLockManager.allLocks.remove(tablePath);
    }
  }

  /**
   * @author Ankush Mudgal
   * Apply exclusive lock.
   *
   * @param databaseName the database name
   * @param tableName    the table name
   * @throws ResourceLockingException the resource locking exception
   */
  public static void applyExclusiveLock(final String databaseName, final String tableName)  throws ResourceLockingException {
    if (tableName == null) {
      final String databasePath = databaseName + "." + "null";
      final LockType databaseLockTypeApplied = ResourceLockManager.allLocks.get(databasePath);
      if (databaseLockTypeApplied == null) {
        ResourceLockManager.allLocks.put(databasePath, LockType.EXCLUSIVE_LOCK);
      } else {
        throw new ResourceLockingException ("Database " + databaseName + " locked!");
      }
    } else {
      final String tablePath = databaseName + "." + tableName;
      final LockType tableLockTypeApplied = ResourceLockManager.allLocks.get(tablePath);
      if (tableLockTypeApplied == null) {
        ResourceLockManager.allLocks.put(tablePath, LockType.EXCLUSIVE_LOCK);
      } else {
        throw new ResourceLockingException("Table " + tableName + " locked!");
      }
    }
  }

  /**
   * @author Ankush Mudgal
   * Release exclusive lock.
   *
   * @param databaseName the database name
   * @param tableName    the table name
   */
  public static void releaseExclusiveLock(final String databaseName, final String tableName) {
    if (tableName == null) {
      final String databasePath = databaseName + "." + "null";
      ResourceLockManager.allLocks.remove(databasePath);
    } else {
      final String tablePath = databaseName + "." + tableName;
      ResourceLockManager.allLocks.remove(tablePath);
    }
  }
}