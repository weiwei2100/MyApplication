package com.jason.myapp.utils;

import android.content.Context;

/**
 * Created by qiuzi on 15/6/17.
 */
public class ImageLoaderUtil {

    public static void init(Context context) {
//        if(ImageLoader.getInstance().isInited()) {
//            return;
//        }
//        File imageLoaderCacheFile = new File(Environment.getExternalStorageDirectory() + "/cache");
//        if(!imageLoaderCacheFile.exists()) {
//            imageLoaderCacheFile.mkdirs();
//        }
//        // 初始化ImageLoader
//        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
//                .cacheInMemory(true).imageScaleType(ImageScaleType.EXACTLY)
//                .cacheOnDisk(true).bitmapConfig(Bitmap.Config.RGB_565).build();
//        ImageLoaderConfiguration configuration = new ImageLoaderConfiguration.Builder(context)
//                .threadPoolSize(3).memoryCache(new WeakMemoryCache())
//                .threadPriority(Thread.NORM_PRIORITY - 2)
//                .denyCacheImageMultipleSizesInMemory()
//                .diskCache(new UnlimitedDiskCache(imageLoaderCacheFile))
//                .diskCacheFileNameGenerator(new Md5FileNameGenerator())
//                .tasksProcessingOrder(QueueProcessingType.LIFO)
//                .memoryCacheSizePercentage(13)
//                .defaultDisplayImageOptions(defaultOptions).build();
//        ImageLoader.getInstance().init(configuration);
    }
}
