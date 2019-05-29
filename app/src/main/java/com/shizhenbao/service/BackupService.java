package com.shizhenbao.service;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.shizhenbao.pop.SystemSet;
import com.shizhenbao.util.BackupsUtils;
import com.shizhenbao.util.Const;
import com.shizhenbao.util.OneItem;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPClientConfig;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.litepal.LitePal;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.TimeZone;

public class BackupService  extends Service {

    private BackupsUtils backupsUtils;
    public BackupService(){
        backupsUtils = new BackupsUtils(Const.context);
    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        new UploadTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,"");
        return super.onStartCommand(intent, flags, startId);
    }


    private class UploadTask extends AsyncTask<String,Void,String>{
        @Override
        protected String doInBackground(String... strings) {
            backupsUtils.localBackup();
            return null;
        }
    }

}
