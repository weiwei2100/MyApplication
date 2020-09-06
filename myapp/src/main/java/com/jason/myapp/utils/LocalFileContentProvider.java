package com.jason.myapp.utils;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.ParcelFileDescriptor;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * Created by lizhiqi on 15/4/13.
 */
public class LocalFileContentProvider extends ContentProvider {

    private static final String URI_PREFIX = "content://com.meiguan.ipsplayer.utils";

    @Override
    public ParcelFileDescriptor openFile(Uri uri, String mode) throws FileNotFoundException {

        File file = new File(uri.getPath());
        ParcelFileDescriptor parcelFileDescriptor = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY);
        return parcelFileDescriptor;
    }

    @Override
    public AssetFileDescriptor openAssetFile(Uri uri, String mode) throws FileNotFoundException {

        AssetManager am = getContext().getAssets();
        String path = uri.getPath().substring(1);
        String tpath = "/sdcard/Download/" + path;
        File file = new File(tpath);
        if (file.exists()) {
            Uri turi = Uri.parse(URI_PREFIX + tpath);
            return super.openAssetFile(turi, mode);
        }
        return super.openAssetFile(uri, mode);

    }

    @Override
    public boolean onCreate() {
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        throw new UnsupportedOperationException("Not supported by this provider");
    }

    @Override
    public String getType(Uri uri) {
        throw new UnsupportedOperationException("Not supported by this provider");
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        throw new UnsupportedOperationException("Not supported by this provider");
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        throw new UnsupportedOperationException("Not supported by this provider");
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        throw new UnsupportedOperationException("Not supported by this provider");
    }
}
