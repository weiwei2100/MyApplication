package com.jason.myapp;

import android.Manifest;
import android.annotation.SuppressLint;

import android.os.Environment;
import android.util.Log;
import android.view.View;
import com.blankj.utilcode.util.FileUtils;
import com.tbruyelle.rxpermissions2.RxPermissions;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;

import java.io.File;

public class FileActivity extends BaseActivity<FilePresenter> implements FileView, View.OnClickListener {


    public void wangodng(View view) {
        //初始化权限相关
        initPermission();
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {

    }

    @Override
    protected FilePresenter createPresenter() {
        return null;
    }

    /**
     * 获取布局ID
     *
     * @return
     */
    @Override
    protected int getLayoutId() {
        return R.layout.activity_file;
    }

    /**
     * 数据初始化操作
     */
    @Override
    protected void initData() {

    }

    @Override
    public void onFileSuccess(File file) {
        Log.e("wangdong","下载成功,  保存路径=" + file.getAbsolutePath());
    }

    /**
     * 初始化权限相关
     */
    @SuppressLint("CheckResult")
    private void initPermission() {
        RxPermissions rxPermissions = new RxPermissions(this);
        rxPermissions.request(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
        ).subscribe(new Consumer<Boolean>() {
            @Override
            public void accept(@NonNull Boolean granted) throws Exception {
                if (granted) {
                    String url = "https://bjlzbt.com/upload/default//20190725//c13948258c6ef6a36cbe2d3322b98f5c.mp4";
                    if (FileUtils.createOrExistsDir(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + "mvp_network/")) {
                        mPresenter.downFile(url, Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + "mvp_network/", "ceshi.mp4");
                    }
                } else {
                    showToast("部分权限未获取!!");
                }
            }
        });
    }

}
