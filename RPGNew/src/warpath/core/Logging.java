package warpath.core;

import java.io.PrintStream;

/**
 * Worst class ever. But this way we can rewrite logging later without having
 * to search for every stupid occurrence.
 * @author jbutler
 * @since September 2016
 */
public class Logging {
  private static final PrintStream stream = System.out;
  private static Level level = Level.DEBUG;

  public enum Level implements Comparable<Level> {
    DEBUG(true, true, true, true),
    INFO(false, true, true, true),
    WARN(false, false, true, true),
    ERROR(false, false, false, true);

    private final boolean isDebug;
    private final boolean isInfo;
    private final boolean isWarn;
    private final boolean isError;

    Level(boolean isDebug, boolean isInfo, boolean isWarn, boolean isError) {
      this.isDebug = isDebug;
      this.isInfo = isInfo;
      this.isWarn = isWarn;
      this.isError = isError;
    }
  }

  public static void level(Level level) {
    Logging.level = level;
  }

  public static void debug(String s) {
    if (level.isDebug) {
      stream.println("[DEBUG] " + s);
    }
  }

  public static void info(String s) {
    if (level.isInfo) {
      stream.println("[INFO] " + s);
    }
  }

  public static void warn(String s) {
    if (level.isWarn) {
      stream.println("[WARN] " + s);
    }
  }

  public static void error(String s) {
    if (level.isError) {
      stream.println("[ERROR] " + s);
    }
  }
}
