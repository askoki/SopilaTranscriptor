package com.example.arcibald160.sopilatranscriptor.helpers;

import java.util.concurrent.TimeUnit;

public class Utils {
    /**
     * Function to convert milliseconds time to
     * Timer Format
     * Hours:Minutes:Seconds
     */
    public static String formatMiliseconds(long milliseconds) {

        return  String.format("%02d:%02ds",
                TimeUnit.MILLISECONDS.toMinutes(milliseconds),
                TimeUnit.MILLISECONDS.toSeconds(milliseconds) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(milliseconds)));
    }

    public static String formatFileSize(long sizeInBytes) {
        // Convert the bytes to Kilobytes (1 KB = 1024 Bytes)
        long sizeInKB = sizeInBytes / 1024;

        // Convert the KB to MegaBytes (1 MB = 1024 KBytes)
        long sizeInMB = sizeInKB / 1024;

        if (sizeInMB > 1.0) {
            return String.valueOf(sizeInMB) + " MB";
        }
        return String.valueOf(sizeInKB) + " KB";
    }
}
