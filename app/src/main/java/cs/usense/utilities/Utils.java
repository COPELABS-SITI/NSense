/**
 * @version 2.0
 * COPYRIGHTS COPELABS/ULHT, LGPLv3.0, LGPLv3.0, date (e.g. 22-04-2016)
 * Class is part of the NSense application. Provides generic methods like print to a file.
 * @author Miguel Tavares (COPELABS/ULHT),
 */

package cs.usense.utilities;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import cs.usense.db.NSenseSQLiteHelper;

public abstract class Utils {

    private static final String TAG = Utils.class.getSimpleName();

    public static final String EMPTY_STRING = "";

    private static final String EXPERIMENT_FOLDER_NAME = "Experiment";

    private static final String ZIP_FOLDER_NAME = "ZIP";

    private static final String ROOT_PATH = Environment.getExternalStorageDirectory() + File.separator;

    private static final String EXPERIMENT_PATH = ROOT_PATH + EXPERIMENT_FOLDER_NAME;

    private static final String ZIP_PATH = ROOT_PATH + ZIP_FOLDER_NAME;


    /**
     * This method checks if the folder already exists. Creates the folder if doesn't exists.
     */
    private static void createFolder(String folderPath) {
        File folder = new File(folderPath);
        if (!folder.exists())
            folder.mkdirs();
    }

    /**
     * This method stores messages to a file
     * @param fileName File to be written
     * @param data Data to be written
     */
    public static void appendLogs(String fileName, String data) {
        appendLogs(fileName, new ArrayList<>(Arrays.asList(DateUtils.getTimeNowTimeSeriesFormat(), data)));
    }

    /**
     * This method stores messages to a file
     * @param fileName File to be written
     * @param data Data to be written
     */
    public static void appendLogs(String fileName, String[] data) {
        ArrayList<String> dataTransformed = new ArrayList<>();
        Collections.addAll(dataTransformed, data);
        appendLogs(fileName, dataTransformed);
    }

    public static Uri getCsvFileUri(String fileName) {
        return Uri.fromFile(new File(EXPERIMENT_PATH, fileName +".txt"));
    }

    public static String getBluetoothName() {
        return BluetoothAdapter.getDefaultAdapter().getName();
    }

    public static boolean isEmailValid(String email) {
        return email != null && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public static void zipExperimentDir() throws IOException {
        createFolder(ZIP_PATH);
        File dirObj = new File(EXPERIMENT_PATH);
        String zipFileName = getZipFileName();
        ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipFileName));
        Log.i(TAG, "Creating : " + zipFileName);
        addDir(dirObj, out);
        out.close();
    }

    private static void addDir(File dirObj, ZipOutputStream out) throws IOException {
        File[] files = dirObj.listFiles();
        byte[] tmpBuf = new byte[1024];
        for (File file : files) {
            if (file.isDirectory()) {
                addDir(file, out);
                continue;
            }
            FileInputStream in = new FileInputStream(file.getAbsolutePath());
            Log.i(TAG, " Adding: " + file.getAbsolutePath());
            out.putNextEntry(new ZipEntry(file.getAbsolutePath()));
            for(int len; (len = in.read(tmpBuf)) > 0; out.write(tmpBuf, 0, len));
            out.closeEntry();
            in.close();
        }
    }

    private static String getZipFileName() {
        StringBuilder sb = new StringBuilder();
        sb.append(ZIP_PATH).append(File.separator);
        sb.append(getBluetoothName()).append("##");
        sb.append(DateUtils.getTimeNowAsString().replace("/", "-").replace(" ", ""));
        sb.append(".zip");
        return sb.toString();
    }

    public static File mostRecentFile() {
        File fl = new File(ZIP_PATH);
        File[] files = fl.listFiles(new FileFilter() {
            public boolean accept(File file) {
                return file.isFile();
            }
        });
        long lastMod = Long.MIN_VALUE;
        File choice = null;
        for (File file : files) {
            if (file.lastModified() > lastMod) {
                choice = file;
                lastMod = file.lastModified();
            }
        }
        return choice;
    }

    /**
     * This method stores messages to a file
     * @param fileName File to be written
     * @param data Data to be written
     */
    private static void appendLogs(String fileName, ArrayList<String> data) {
        try {
            createFolder(EXPERIMENT_PATH);
            File logFile = new File(EXPERIMENT_PATH, fileName + ".txt");
            if (!logFile.exists()) {
                logFile.createNewFile();
            } else if (fileName.contains("ReportItem")){
                if(logFile.lastModified() + 2000 < DateUtils.getTimeNowAsLong()) {
                    logFile.delete();
                    logFile.createNewFile();
                }
            }
            BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, true));
            for(String text : data) {
                buf.write(text);
                buf.write(";");
            }
            buf.newLine();
            buf.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static double computeEMA(double previousValue, double currentValue, double factor) {
        return (factor * previousValue) + (1 - factor) * currentValue;
    }

    public static void dbBackup(Context context) {
        try {
            File sd = new File(EXPERIMENT_PATH);
            if (sd.canWrite()) {
                String DB_PATH = context.getFilesDir().getAbsolutePath().replace("files", "databases") + File.separator;
                String dbFileName = NSenseSQLiteHelper.DATABASE_NAME;
                String dbBackupFileName = "databaseBackup.db";
                File currentDB = new File(DB_PATH, dbFileName);
                File backupDB = new File(sd, dbBackupFileName);
                if (currentDB.exists()) {
                    FileChannel src = new FileInputStream(currentDB).getChannel();
                    FileChannel dst = new FileOutputStream(backupDB).getChannel();
                    dst.transferFrom(src, 0, src.size());
                    src.close();
                    dst.close();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}