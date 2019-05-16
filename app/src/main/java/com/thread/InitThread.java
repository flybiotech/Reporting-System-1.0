package com.thread;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.activity.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * ��ʼ���̣߳��������raw��Դ���Ƶ�SD������ȷĿ¼�£�����cfg.ini�ļ��ж�ȡ������IP�Ͷ˿ں�
 * @author HeLinyu
 *
 */
public class InitThread extends Thread {
    private Context mContext;

    public InitThread(Context context) {
        mContext = context;
    }

    @Override
    public void run() {
        copyRawToSD();

    }

    /**
     * �������raw��Դ���Ƶ�SD������ȷĿ¼��
     */
    private void copyRawToSD() {
        boolean isSdReady = checkSd();

        if(isSdReady) {
            Log.d("Init", "SD Card is Ready.");
            File sdPath = Environment.getExternalStorageDirectory();		//��ȡSD����Ŀ¼
            String dstPathStr = sdPath.getPath() + "/southMedical";					//Ŀ��·��
            File dstPath = new File(dstPathStr);

            if(!dstPath.exists()) {
                if(!dstPath.mkdirs()) {
                    return;
                }
            }

            int[] inputFileIds = {
                    R.raw.soundkeypress,
                    R.raw.soundsnapshot
            };

            String[] outputFileNames = {
                    "soundkeypress.wav",
                    "soundsnapshot.wav"
            };

            for(int i = 0; i < inputFileIds.length; i++) {
                try {
                    String fileName = outputFileNames[i];

                    File outFile = new File(dstPath, fileName);
                    if(outFile.exists()) continue;

                    InputStream in = mContext.getResources().openRawResource(inputFileIds[i]);
                    OutputStream out = new FileOutputStream(outFile);

                    // Transfer bytes from in to out
                    byte[] buf = new byte[1024];
                    int len;
                    while ((len = in.read(buf)) > 0) {
                        out.write(buf, 0, len);
                    }
                    out.flush();
                    in.close();
                    out.close();
                }
                catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else {
//			acty.initMsgHandler.sendEmptyMessage(Login.ERROR_SD);
        }
    }



    /**
     * ��ȡSD��״̬
     * @return true-SD���Ѱ�װ��false-SD��δ��װ
     */
    private boolean checkSd() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    /**
     * ����ַ����Ƿ��ʾ�Ϸ���IP��ַ
     * @param str ������ַ���
     * @return true-�Ϸ���IP��ַ, false-�Ƿ���IP��ַ
     */
    private boolean checkIp(String str) {
        boolean result = false;

        String ip;
        String port;
        int portVal;

        int colonPos = str.indexOf(":");
        if(colonPos != -1)
        {
            ip = str.substring(0, colonPos);
            port = str.substring(colonPos + 1);
            portVal = Integer.parseInt(port);
            Log.d("checkIp: IP", ip);
            Log.d("checkIp: port", port);
            Log.d("checkIp: portVal", portVal + "");
        } else {
            ip = str;
            port = "";
            portVal = 0;
        }

        String regExp = "^(([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\\.){3}([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])$";
        Pattern p = Pattern.compile(regExp);
        Matcher m = p.matcher(ip);
        if(m.matches() && (0 <= portVal && portVal <=65535)) {
            result = true;
        }
        return result;
    }

    /**
     * ����ַ����Ƿ��ʾ�Ϸ�������
     * @param str ������ַ���
     * @return true-�Ϸ�������, false-�Ƿ�������
     */
    private boolean checkDomainName(String str) {
        boolean result = false;

        String ip;
        String port;
        int portVal;

        int colonPos = str.indexOf(":");
        if(colonPos != -1)
        {
            ip = str.substring(0, colonPos);
            port = str.substring(colonPos + 1);
            portVal = Integer.parseInt(port);
            Log.d("checkDomainName: IP", ip);
            Log.d("checkDomainName: port", port);
            Log.d("checkDomain: portVal", ""+portVal);
        } else {
            ip = str;
            port = "";
            portVal = 0;
        }

        String regExp = "^(([a-zA-Z0-9]|[a-zA-Z0-9][a-zA-Z0-9\\-]*[a-zA-Z0-9])\\.)*([A-Za-z0-9]|[A-Za-z0-9][A-Za-z0-9\\-]*[A-Za-z0-9])$";
        Pattern p = Pattern.compile(regExp);
        Matcher m = p.matcher(ip);
        if(m.matches() && (0 <= portVal && portVal <=65535)) {
            result = true;
        }
        return result;
    }

    private boolean checkPort(String port) {
        boolean result = false;

        int portVal = Integer.parseInt(port);
        if(0 <= portVal && portVal <= 65535) {
            result = true;
        }
        return result;
    }
}
