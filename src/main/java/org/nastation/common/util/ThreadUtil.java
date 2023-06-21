package org.nastation.common.util;

/**
 * @author John | NaChain
 * @since 10/13/2021 0:55
 */
public class ThreadUtil {

    public static void sleepSeconds(int seconds) {

        try {
            Thread.sleep(seconds * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void sleepMillis(int millis) {

        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
