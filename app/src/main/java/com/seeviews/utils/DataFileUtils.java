package com.seeviews.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.os.AsyncTaskCompat;
import android.util.Log;

import com.google.gson.Gson;
import com.seeviews.SeeviewApplication;
import com.seeviews.model.internal.BaseModel;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

/**
 * Created by Jan-Willem on 30-11-2016.
 */

public class DataFileUtils {

    private static final String TAG = "DataFileUtils";

    private static final String NAME_MODEL = "cache";

    private static void write(Context c, String fileName, String data) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(c.openFileOutput(fileName, Context.MODE_PRIVATE));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        } catch (Exception e) {
            Log.e(TAG, "File write failed: " + e.toString());
        }
    }

    private static String readFromFile(Context c, String fileName) {
        String ret = "";

        try {
            InputStream inputStream = c.openFileInput(fileName);

            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ((receiveString = bufferedReader.readLine()) != null)
                    stringBuilder.append(receiveString);

                inputStream.close();
                ret = stringBuilder.toString();
            }
        } catch (FileNotFoundException e) {
            Log.e(TAG, "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e(TAG, "Can not read file: " + e.toString());
        }

        return ret;
    }

    public static void getData(final Context c, @NonNull final SeeviewApplication.DataListener dataListener) {
        AsyncTaskCompat.executeParallel(new AsyncTask<Void, Void, BaseModel>() {
            @Override
            protected BaseModel doInBackground(Void... voids) {
                try {
                    return new Gson().fromJson(readFromFile(c, NAME_MODEL), BaseModel.class);
                } catch (Exception e) {
                    Log.e(TAG, "getData error " + e.getLocalizedMessage());
                    return null;
                }
            }

            @Override
            protected void onPostExecute(BaseModel baseModel) {
                super.onPostExecute(baseModel);
                if (baseModel == null)
                    dataListener.onDataError(new Exception("Unable to read data from file"));
                else
                    dataListener.onDataLoaded(baseModel);
            }
        });
    }

    public static void writeData(final Context c, final BaseModel model) {
        AsyncTaskCompat.executeParallel(new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                write(c, NAME_MODEL, new Gson().toJson(model));
                return null;
            }
        });
    }

    public static void burnEverything(final Context c, final Runnable runOnFinish) {
        AsyncTaskCompat.executeParallel(new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                write(c, NAME_MODEL, "");
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                if (runOnFinish != null)
                    runOnFinish.run();
            }
        });
    }

    public static String getVideoThumbnailLocation(String videoPath) {
        File videoFile = new File(videoPath);
        if (videoFile.exists()) {
            Bitmap thumb = ThumbnailUtils.createVideoThumbnail(videoFile.getAbsolutePath(), MediaStore.Video.Thumbnails.MINI_KIND);
            if (thumb == null)
                return "";

            File thumbnailFile = new File(videoFile.getParent(), videoFile.getName() + "_thumbnail.jpg");

            FileOutputStream out = null;
            try {
                out = new FileOutputStream(thumbnailFile);
                thumb.compress(Bitmap.CompressFormat.JPEG, 80, out); // bmp is your Bitmap instance
                // PNG is a lossless format, the compression factor (100) is ignored
                return thumbnailFile.getAbsolutePath();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (out != null) {
                        out.close();
                    }
                    return thumbnailFile.getAbsolutePath();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return "";
    }

    public static boolean eraseFile(File f) {
        if (f != null && f.exists()) {
            if (f.isFile())
                return f.delete();
            else {
                for (File child : f.listFiles()) {
                    if (!eraseFile(child))
                        return false;
                }
                return f.delete();
            }
        }
        return false;
    }
}
