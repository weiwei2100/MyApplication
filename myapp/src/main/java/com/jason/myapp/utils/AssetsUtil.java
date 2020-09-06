package com.jason.myapp.utils;

import android.content.Context;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by sunjianchao on 17/2/21.
 */
public class AssetsUtil {

    private Context context;

    public AssetsUtil(Context context) {
        this.context = context;
    }

    public String readFile(String file) {

        InputStream is = null;
        String configFileName = file;
        String configJson = null;
        try {
            is = context.getAssets().open(configFileName);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            configJson = new String(buffer);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return configJson;

    }
}
