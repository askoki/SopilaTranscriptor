package com.example.arcibald160.sopilatranscriptor.helpers;

import android.arch.core.util.Function;
import android.content.Context;
import android.content.DialogInterface;
import android.media.MediaMetadataRetriever;
import android.media.MediaScannerConnection;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.EditText;

import com.example.arcibald160.sopilatranscriptor.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.TimeUnit;

import okhttp3.ResponseBody;

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

    public static File getDownloadsDir(Context context) {
        return new File(Environment.getExternalStorageDirectory(), context.getString(R.string.pdf_folder));
    }

    public static boolean writeResponseBodyToDisk(ResponseBody body, Context context, String TAG, String filename) {
        File downloadsDirectory = getDownloadsDir(context);

//        create folder for recordings if it does not exist
        if (!downloadsDirectory.exists()) {
            if (!downloadsDirectory.mkdirs()) {
                Log.d(TAG, "failed to create directory");
            }
        }

        // make it visible on the filesystem
        MediaScannerConnection.scanFile(
                context,
                new String[]{downloadsDirectory.getAbsolutePath()},
                null,
                null
        );

        try {
            File futureMusicPdfFile = new File(
                    downloadsDirectory.getAbsolutePath(),
                    filename + ".pdf"
            );


            InputStream inputStream = null;
            OutputStream outputStream = null;

            try {
                byte[] fileReader = new byte[4096];

                long fileSize = body.contentLength();
                long fileSizeDownloaded = 0;

                inputStream = body.byteStream();
                outputStream = new FileOutputStream(futureMusicPdfFile);

                while (true) {
                    int read = inputStream.read(fileReader);

                    if (read == -1) {
                        break;
                    }

                    outputStream.write(fileReader, 0, read);

                    fileSizeDownloaded += read;

                    Log.d(TAG, "file download: " + fileSizeDownloaded + " of " + fileSize);
                }

                outputStream.flush();

                return true;
            } catch (IOException e) {
                return false;
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }

                if (outputStream != null) {
                    outputStream.close();
                }
            }
        } catch (IOException e) {
            return false;
        }
    }
}
