package com.bhushan.hibishort;

import android.Manifest;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.webkit.DownloadListener;
import android.webkit.URLUtil;
import android.webkit.ValueCallback;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class HibiActivity extends AppCompatActivity {

    WebView webView;
    SharedPreferences shared_pref;

    private static final int MY_PERMISSION_REQUEST_CODE = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hibi);

        checkPermission();

        webView = findViewById(R.id.webview);
        shared_pref = getApplicationContext().getSharedPreferences("user_info",
                                                                   Context.MODE_PRIVATE);

        final String username = shared_pref.getString("student_id", "student_id_not_found");
        final String password = shared_pref.getString("password", "password_not_found");

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setDisplayZoomControls(false);
        webSettings.setSupportZoom(true);
        webSettings.setDefaultTextEncodingName("utf-8");

        webView.loadUrl("https://hib.iiit-bh.ac.in/m-ums-2.0/start/login/?client=iiit");

        final String fail_login1 = "https://hib.iiit-bh.ac.in/m-ums-2.0/start/login/?" +
                                   "client=iiit&mes=UserID_or_Password_Incorrect";
//        final String fail_login2 = "https://hib.iiit-bh.ac.in/Hibiscus/Login/" +
//                              "?client=iiit&mes=UserID_or_Password_or_A_Incorrect";

        final String js = "javascript:document.getElementsByName('uid')[0].value='"+username+
                          "';document.getElementsByName('pwd')[0].value='"+password+
                          "';var str=document.getElementById('txtCaptchaDiv').innerHTML;" +
                          "str=str.slice(0,-7);document.getElementsByName('txtInput')[0]" +
                          ".value=eval(str);document.getElementsByTagName('button')[0].click();";

        webView.setWebViewClient(new WebViewClient() {
            public void onPageFinished(WebView view, String url) {
                final String url_resp = url;
                if(Build.VERSION.SDK_INT >= 19) {
                    view.evaluateJavascript(js, new ValueCallback<String>() {
                        @Override
                        public void onReceiveValue(String s) {
                            Log.d("RECEIVE_STR", s);
                            if(url_resp.equals(fail_login1)) {
                                SharedPreferences.Editor editor = shared_pref.edit();
                                editor.putString("student_id", "");
                                editor.putString("password", "");
                                editor.commit();
                                finish();
                            }
                        }
                    });
                }
            }
        });

        webView.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String s, String s1, String s2, String s3, long l) {
                DownloadManager.Request request = new DownloadManager.Request(Uri.parse(s));
                request.allowScanningByMediaScanner();
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                String filename = URLUtil.guessFileName(s, s2, s3);
                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, filename);
                DownloadManager dManager = (DownloadManager)getSystemService(DOWNLOAD_SERVICE);
                dManager.enqueue(request);
            }
        });
    }

    protected void checkPermission() {
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
            if(checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED) {
                if(shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    // show an alert dialog
                    AlertDialog.Builder builder = new AlertDialog.Builder(HibiActivity.this);
                    builder.setMessage("Write external storage permission is required.");
                    builder.setTitle("Please grant permission");
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            ActivityCompat.requestPermissions(
                                    HibiActivity.this,
                                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                    MY_PERMISSION_REQUEST_CODE
                            );
                        }
                    });
                    builder.setNeutralButton("Cancel",null);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }else {
                    // Request permission
                    ActivityCompat.requestPermissions(
                            HibiActivity.this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            MY_PERMISSION_REQUEST_CODE
                    );
                }
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_BACK:
                    if (webView.canGoBack()) {
                        webView.goBack();
                    }
                    return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_logout) {
            logout();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void logout() {
        SharedPreferences.Editor editor = shared_pref.edit();
        editor.putString("student_id", "");
        editor.putString("password", "");
        editor.commit();
        finish();
    }
}
