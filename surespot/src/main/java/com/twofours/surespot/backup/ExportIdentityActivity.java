package com.twofours.surespot.backup;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPOutputStream;

import android.accounts.AccountManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ForegroundColorSpan;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

import com.twofours.surespot.SurespotApplication;







import com.twofours.surespot.R;
import com.twofours.surespot.common.FileUtils;
import com.twofours.surespot.common.SurespotConstants;
import com.twofours.surespot.common.SurespotLog;
import com.twofours.surespot.common.Utils;
import com.twofours.surespot.identity.IdentityController;
import com.twofours.surespot.network.IAsyncCallback;
import com.twofours.surespot.network.IAsyncCallbackTuple;
import com.twofours.surespot.ui.SingleProgressDialog;
import com.twofours.surespot.ui.UIUtils;

public class ExportIdentityActivity extends SherlockActivity {
    private static final String TAG = "ExportIdentityActivity";
    private List<String> mIdentityNames;
    
    private Spinner mSpinner;

    private TextView mAccountNameDisplay;
    public static final String[] ACCOUNT_TYPE = new String[]{"dummy"};
    private SingleProgressDialog mSpd;
    private SingleProgressDialog mSpdBackupDir;
    private AlertDialog mDialog;
    private Handler mHandler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_export_identity);

        Utils.configureActionBar(this, getString(R.string.identity), getString(R.string.backup), true);
        final String identityDir = FileUtils.getIdentityExportDir().toString();

        final TextView tvPath = (TextView) findViewById(R.id.backupLocalLocation);
        mSpinner = (Spinner) findViewById(R.id.identitySpinner);

        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.sherlock_spinner_item);
        adapter.setDropDownViewResource(R.layout.sherlock_spinner_dropdown_item);
        mIdentityNames = IdentityController.getIdentityNames(this);

        for (String name : mIdentityNames) {
            adapter.add(name);
        }

        mSpinner.setAdapter(adapter);

        String backupUsername = getIntent().getStringExtra("backupUsername");
        getIntent().removeExtra("backupUsername");

        mSpinner.setSelection(adapter.getPosition(backupUsername == null ? IdentityController.getLoggedInUser() : backupUsername));
        mSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                String identityFile = identityDir + File.separator + IdentityController.caseInsensitivize(adapter.getItem(position))
                        + IdentityController.IDENTITY_EXTENSION;
                tvPath.setText(identityFile);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }

        });

        Button exportToSdCardButton = (Button) findViewById(R.id.bExportSd);

        exportToSdCardButton.setEnabled(FileUtils.isExternalStorageMounted());

        exportToSdCardButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v)
            {
                final String user = (String) mSpinner.getSelectedItem();
                exportIdentity(user, SurespotApplication.PW_INSECURE);
            }
        });


    }

    // //////// Local
    private void exportIdentity(String user, String password)
    {
        IdentityController.exportIdentity(ExportIdentityActivity.this, user, password, new IAsyncCallback<String>() {
            @Override
            public void handleResponse(String response) {
                if (response == null) {
                    Utils.makeToast(ExportIdentityActivity.this, getString(R.string.no_identity_exported));
                }
                else {
                    Utils.makeLongToast(ExportIdentityActivity.this, response);
                }

            }
        });
    }

    // //////// DRIVE

    private void chooseAccount(boolean ask) {
    }

    private void removeAccount() {
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
    }

    private void backupIdentityDrive(final boolean firstAttempt) {
    }

    public String ensureDriveIdentityDirectory() {
        String identityDirId = null;
        return identityDirId;
    }

    public boolean updateIdentityDriveFile(String idDirId, String username, byte[] identityData) {
        return false;
    }

/*
    private ChildReference getIdentityFile(String identityDirId, String username) throws IOException {
        ChildReference idFile = null;
        return idFile;
    }
*/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getSupportMenuInflater();
        inflater.inflate(R.menu.menu_help, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
        }
    }


    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        return super.onKeyUp(keyCode, event);
    }


    public void openOptionsMenuDeferred() {
        mHandler.post(new Runnable() {
                          @Override
                          public void run() {
                              openOptionsMenu();
                          }
                      }
        );
    }
}
