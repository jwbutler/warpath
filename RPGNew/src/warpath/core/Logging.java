package warpath.core;

/**
 * Worst class ever. But this way we can rewrite logging later without having
 * to search for every stupid occurrence.
 * @author jbutler
 * @since September 2016
 */
public class Logging {
  private static final boolean DEBUG_MODE = true;
  public static void debugPrint(String s) {
    if (DEBUG_MODE) {
      System.out.println(s);
    }
  }
  public static void debugPrintf(String format, Object... args) {
    if (DEBUG_MODE) {
      System.out.printf(format, args);
    }
  }
}
