package com.example.moveonotes.Services;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

import com.example.moveonotes.R;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class getServer {
    private static final String TAG = "Helper";

    public static String getConfigValue(Context context, String name) {

        try {
            Resources resources = context.getResources();
            InputStream rawResource = resources.openRawResource(R.raw.config);
            Properties properties = new Properties();
            properties.load(rawResource);
            return properties.getProperty(name);
        } catch (Resources.NotFoundException e) {
            Log.e(TAG, "Unable to find the config file: " + e.getMessage());
        } catch (IOException e) {
            Log.e(TAG, "Failed to open config file.");
        }

        return null;
    }
}
