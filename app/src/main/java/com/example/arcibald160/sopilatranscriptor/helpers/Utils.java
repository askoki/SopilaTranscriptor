package com.example.arcibald160.sopilatranscriptor.helpers;

import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.support.annotation.RequiresApi;

import java.io.File;
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
        float sizeInMB = (float) ((float) sizeInKB / 1024.0);

        // Convert to GB
        float sizeInGB = (float) (sizeInMB / 1024.0);

        if (sizeInGB >= 1.0) {
            String stringGB = String.format("%02.2f GB", sizeInGB);
            return stringGB;
        } else if (sizeInMB >= 1.0) {
            String stringMB = String.format("%02.2f MB", sizeInMB);
            return stringMB;
        }

        return String.valueOf(sizeInKB) + " KB";
    }

    public static String getFileDuration(File file) {
        MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
        try {
            mediaMetadataRetriever.setDataSource(file.getAbsolutePath());
        } catch (RuntimeException e) {
            // file is not ready yet
            return null;
        }
        String durationStr = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        return Utils.formatMiliseconds(Long.parseLong(durationStr));
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public static String getAvailableInternalMemorySize(String prependText) {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());

        long blockSize = stat.getBlockSizeLong();
        long availableBlocks = stat.getAvailableBlocksLong();

        String freeSpaceString = prependText + " " + formatFileSize(availableBlocks * blockSize);
        return freeSpaceString;
    }

    public static boolean renameFile(File from, File to) {
        return from.getParentFile().exists() && from.exists() && from.renameTo(to);
    }
}
