package com.hp.mss.hpprint.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.hp.mss.hpprint.R;
import com.hp.mss.hpprint.adapter.PrintPluginAdapter;
import com.hp.mss.hpprint.model.PrintPlugin;
import com.hp.mss.hpprint.util.EventMetricsCollector;
import com.hp.mss.hpprint.util.PrintPluginStatusHelper;
import com.hp.mss.hpprint.util.PrintUtil;

//插件管理的Activity
public class PrintPluginManagerActivity extends AppCompatActivity {
    private static final String TAG = "PRINT_PLUGIN_MANGER_ACTIVITY";

    private Toolbar topToolBar;
    private ListView pluginListView;
    private PrintPluginStatusHelper printPluginStatusHelper;
    private PrintPluginAdapter printPluginAdapter;
    private BroadcastReceiver receiver;
    private IntentFilter intentFilter;
    protected static boolean isVisible = false;
    protected static boolean newPackageInstalled = false;
    Button printBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        final Activity thisActivity = this;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_print_plugin_manger);

        final android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if  (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.x);
            actionBar.setElevation(2);
        }

        //显示可以安装的插件
        pluginListView = (ListView) findViewById(R.id.plugin_manager_list_view);
        printPluginStatusHelper = PrintPluginStatusHelper.getInstance(this);
        printPluginAdapter = new PrintPluginAdapter(this, getprintPluginList());
        pluginListView.setAdapter(printPluginAdapter);
        //listview 中iten 的监听事件
        pluginListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                PrintPlugin plugin = (PrintPlugin) printPluginAdapter.getItem(i);
                if(plugin.getStatus().equals(PrintPlugin.PluginStatus.READY)) {
                    startActivity(new Intent(Settings.ACTION_PRINT_SETTINGS));
                } else if(printPluginStatusHelper != null && printPluginStatusHelper.showBeforeEnableDialog(plugin) ) {
                    //这个表示可以跳转到系统设置里面去开启插件服务
                    displayEnableTipsDialog();
                    //判断插件的状态，是正在安装还是更新状态还是未安装插件状态
                } else if ( printPluginStatusHelper.goToGoogleStore(plugin)) {
                    plugin.goToPlayStoreForPlugin();
                    EventMetricsCollector.postMetricsToHPServer(
                            thisActivity,
                            EventMetricsCollector.PrintFlowEventTypes.SENT_TO_GOOGLE_PLAY_STORE);
                }
            }
        });

        // Continue to print action 继续打印按钮
        printBtn = (Button) findViewById(R.id.print_btn);
        //空白的view
        View listDivider = findViewById(R.id.list_divider);
        //如果 printJobData数据为空，就是没有选择任何打印的东西时，这两个控件就隐藏起来，一般来说都不会出现这个情况，因为即使不选择，也会有默认的
        if (!PrintUtil.hasPrintJob()) {
            //隐藏相关的控件
            printBtn.setVisibility(View.GONE);
            listDivider.setVisibility(View.GONE);
        } else {
            //显示相关的控件
            printBtn.setVisibility(View.VISIBLE);
            listDivider.setVisibility(View.VISIBLE);
            //显示继续打印还是跳过
            printBtn.setText(readyToPrint() ? R.string.continue_to_print : R.string.skip);
            printBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //显示打印数据，跳转到需要打印的界面
                    PrintUtil.readyToPrint(thisActivity);
                }
            });
        }

        // Receiver for broadcast message 通过广播去发送消息
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                //   在接到广播时，可以获取到变化的application的包名，获取包名代码如下：
                String packageName = intent.getData().getEncodedSchemeSpecificPart();
                //判断系统有没有安装指定的打印机服务插件
                if( isAPluginInstalled(packageName) ) {
                    //true
                    if(isVisible) {
                        //显示插件的下载列表
                        newPluginInstalledHandler();
                    } else {
                        newPackageInstalled = true;
                    }
                }
            }
        };

        intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_PACKAGE_ADDED);
        intentFilter.addAction(Intent.ACTION_PACKAGE_INSTALL);
        intentFilter.addDataScheme("package");
        registerReceiver(receiver, intentFilter);

    }

    @Override
    protected void onResume() {
        super.onResume();
        isVisible = true;
        EventMetricsCollector.postMetricsToHPServer(
                this,
                EventMetricsCollector.PrintFlowEventTypes.OPENED_PLUGIN_HELPER);
        newPluginInstalledHandler();
    }

    @Override
    public void onPause()
    {
        super.onPause();
        isVisible = false;
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        getMenuInflater().inflate(R.menu.menu_print_plugin_manager, menu);
        // Inflate the menu; this adds items to the action bar if it is present.
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == android.R.id.home) {
            super.onBackPressed();
            invalidateOptionsMenu();
            return true;
        } else if (id == R.id.action_help) {
            Intent intent = new Intent(this, PrintServicePluginInformation.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    private  PrintPlugin[] getprintPluginList() {
        if(printPluginStatusHelper == null)
            return null;
        return printPluginStatusHelper.getSortedPlugins();
    }

    //判断硬件提供商是不是亚马逊或者有没有更新，如果都没有就返回false
    private boolean readyToPrint() {
        return printPluginStatusHelper.readyToPrint();
    }

    //然后点击button就可以跳转到系统设置里面去开启插件打印服务
    private void displayEnableTipsDialog() {

        final Activity thisActivity = this;
        final Dialog dialog = new Dialog(this);
        //function :取消dialog的标题栏
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_before_enable_tips);

       //插件安装好了之后，会显示一个引导界面。点击这个按钮，就可以去系统设置里面将打印机服务插件功能开启
        Button okBtn = (Button) dialog.findViewById(R.id.dialog_ok_btn);
        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //跳转到系统设置界面
                startActivity(new Intent(Settings.ACTION_PRINT_SETTINGS));
                dialog.dismiss();
                EventMetricsCollector.postMetricsToHPServer(
                        thisActivity,
                        EventMetricsCollector.PrintFlowEventTypes.SENT_TO_PRINT_SETTING);
            }
        });

        Button cancelBtn = (Button) dialog.findViewById(R.id.dialog_cancel_btn);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();

    }

    //主要是判断系统有没有安装插件
    private boolean isAPluginInstalled(String packageName) {
        boolean isInstalled = false;

        for(int i = 0; i < PrintPluginStatusHelper.packageNames.length; i++) {
            //判断这个安装包是否存在
            if( packageName.equals(PrintPluginStatusHelper.packageNames[i]) ) {
                    isInstalled = true;
            }

        }
        return isInstalled;
    }

    //显示所得插件，并且判断是否需要显示那个调到系统设置的dialog
    private void newPluginInstalledHandler() {

        printPluginStatusHelper.updateAllPrintPluginStatus();
        //得到打印机插件服务的类的数组
        PrintPlugin[] sortedList = getprintPluginList();
        //listview控件中显示所有的插件
        printPluginAdapter = new PrintPluginAdapter(this, sortedList);
        pluginListView.setAdapter(printPluginAdapter);
        //一般来说，如果插件没有更新，就是返回false
        printBtn.setText(readyToPrint() ? R.string.continue_to_print : R.string.skip);
        if(newPackageInstalled) {
            //Build.VERSION_CODES.M 表示android 6.0
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) {
                //在这里表示新安装的插件，安装完成之后，就显示这个dialog，然后点击button ，就可以跳转到系统设置里面去打开打印插件服务
                displayEnableTipsDialog();
            }
            newPackageInstalled = false;
        }

    }
}
