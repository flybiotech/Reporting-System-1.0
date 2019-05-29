package com.shizhenbao.service;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.util.Log;

import com.application.MyApplication;
import com.shizhenbao.pop.SystemSet;
import com.shizhenbao.util.BackupsUtils;
import com.shizhenbao.util.Const;
import com.shizhenbao.util.DeleteUtils;
import com.shizhenbao.util.Item;
import com.shizhenbao.util.OneItem;
import com.shizhenbao.util.RecoveryUtils;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPClientConfig;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.litepal.LitePal;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.TimeZone;

public class RecoverService extends Service {

    private RecoveryUtils recoveryUtils;
    public RecoverService(){

        recoveryUtils = new RecoveryUtils(MyApplication.getmContext());

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        new DownloadTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,"");
        return super.onStartCommand(intent, flags, startId);
    }

    private class DownloadTask extends AsyncTask<String,Void,String> {
        @Override
        protected String doInBackground(String... strings) {

            recoveryUtils.inquiryRecoverDialog();

            return null;
        }
    }

}
