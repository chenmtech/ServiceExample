package com.cmtech.android;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;

import com.vise.log.ViseLog;

/**
 * ProjectName:    ServiceExample
 * Package:        com.cmtech.android
 * ClassName:      TestAsyncTask
 * Description:    java类作用描述
 * Author:         作者名
 * CreateDate:     2020/10/27 上午5:32
 * UpdateUser:     更新者
 * UpdateDate:     2020/10/27 上午5:32
 * UpdateRemark:   更新说明
 * Version:        1.0
 */
public class TestAsyncTask extends AsyncTask<Void, Void, Boolean> {
    private final ProgressDialog progressDialog;

    public TestAsyncTask(Context context, String progressStr) {
        if(!TextUtils.isEmpty(progressStr)) {
            progressDialog = new ProgressDialog(context);
            progressDialog.setMessage(progressStr);
            progressDialog.setIndeterminate(false);
            progressDialog.setCancelable(false);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        } else {
            progressDialog = null;
        }
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if(progressDialog != null)
            progressDialog.show();
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);
        ViseLog.e("finish task");
        if(progressDialog != null)
            progressDialog.dismiss();
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }


}
