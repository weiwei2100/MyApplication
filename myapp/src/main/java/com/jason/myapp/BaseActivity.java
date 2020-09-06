package com.jason.myapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;


/**
 * File descripition: activity基类
 * <p>
 *
 * @author lp
 * @date 2018/5/16
 */
public abstract class BaseActivity<P extends BasePresenter> extends AppCompatActivity implements BaseView {
    protected final String TAG = this.getClass().getSimpleName();
    public Context mContext;
    protected P mPresenter;

    protected abstract P createPresenter();

    private LoadingDialog loadingDialog;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(getLayoutId());
        mPresenter = createPresenter();


        this.initData();
    }

    /**
     * 获取布局ID
     *
     * @return
     */
    protected abstract int getLayoutId();

    /**
     * 数据初始化操作
     */
    protected abstract void initData();



    /**
     * 封装toast方法（自行定制实现）
     *
     * @param str
     */
    public void showToast(String str) {
        Toast.makeText(this,str,0).show();
    }

    public void showLongToast(String str) {
        Toast.makeText(this,str,1).show();
    }

    @Override
    public void showError(String msg) {
        showToast(msg);
    }

    /**
     * 返回所有状态  除去指定的值  可设置所有（根据需求）
     *
     * @param model
     */
    @Override
    public void onErrorCode(BaseModel model) {
        if (model.getError_code() == 10000000) {
            //处理些后续逻辑   如果某个页面不想实现  子类重写这个方法  将super去掉  自定义方法
//            App.put();
//            startActivity(LoginActivity.class);
        }
    }

    @Override
    public void showLoading() {
        showLoadingDialog();
    }

    @Override
    public void hideLoading() {
        dissMissDialog();
    }

    public void showLoadingDialog() {
        showLoadingDialog("加载中...");
    }

    /**
     * 加载  黑框...
     */
    public void showLoadingDialog(String msg) {
        if (loadingDialog == null) {
            loadingDialog = new LoadingDialog(this);
        }
        loadingDialog.setMessage(msg);
        if (!loadingDialog.isShowing()) {
            loadingDialog.show();
        }
    }

    /**
     * 消失  黑框...
     */
    public void dissMissDialog() {
        if (loadingDialog != null) {
            loadingDialog.dismiss();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPresenter != null) {
            mPresenter.detachView();
        }
        if (loadingDialog != null) {
            loadingDialog.dismiss();
        }
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
        if (mPresenter != null) {
            mPresenter.detachView();
        }
    }


    @Override
    public void showProgress() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        }

        if (!progressDialog.isShowing()) {
            progressDialog.show();
        }
    }

    @Override
    public void hideProgress() {
        if (progressDialog != null) {

            progressDialog.dismiss();
        }
    }

    @Override
    public void onProgress(int progress) {
        if (progressDialog != null) {
            progressDialog.setProgress(progress);
        }
    }

    /**
     * [页面跳转]
     *
     * @param clz
     */
    public void startActivity(Class<?> clz) {
        startActivity(clz, null);
    }


    /**
     * [携带数据的页面跳转]
     *
     * @param clz
     * @param bundle
     */
    public void startActivity(Class<?> clz, Bundle bundle) {
        Intent intent = new Intent();
        intent.setClass(this, clz);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        startActivity(intent);
    }

    /**
     * [含有Bundle通过Class打开编辑界面]
     *
     * @param cls
     * @param bundle
     * @param requestCode
     */
    public void startActivityForResult(Class<?> cls, Bundle bundle, int requestCode) {
        Intent intent = new Intent();
        intent.setClass(this, cls);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        startActivityForResult(intent, requestCode);
    }










}
