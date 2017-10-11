package com.jimei.k3wise_mobile;

import android.app.Activity;
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.jimei.k3wise_mobile.BO.LoginUser;
import com.jimei.k3wise_mobile.Component.AudioPlayer;
import com.jimei.k3wise_mobile.Component.BaseAppCompatActivity;
import com.jimei.k3wise_mobile.Util.KingdeeK3WiseWebServiceHelper;
import com.jimei.k3wise_mobile.Component.ProgressView;
import com.jimei.k3wise_mobile.Component.WebserviceTask;
import com.jimei.k3wise_mobile.Util.PreferencesHelper;
import com.jimei.k3wise_mobile.Util.ShowDialog;

import org.json.JSONObject;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends BaseAppCompatActivity  /* implements LoaderCallbacks<Cursor>*/ {

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;
    // UI references.
    private AutoCompleteTextView mUserView;
    private EditText mPasswordView;
    private ProgressView mProgressView;
    private View mLoginFormView;
    private Button mSettingButton;

    private long exitTime = 0;

    private void exit() {
        if ((System.currentTimeMillis() - exitTime) > 2000) {
            Toast.makeText(getApplicationContext(), "再按一次退出程序",
                    Toast.LENGTH_SHORT).show();
            exitTime = System.currentTimeMillis();
        } else {
            finish();
            System.exit(0);
        }
    }

    @Override
    public void onBackPressed() {
        exit();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_login;
    }

    @Override
    protected boolean isShowBacking() {
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AudioPlayer.buildSoundPool(LoginActivity.this);

        // Set up the login form.
        mUserView = (AutoCompleteTextView) findViewById(R.id.user_number);
        mUserView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_NEXT) {
                    mPasswordView.requestFocus();
                    mPasswordView.selectAll();
                    return true;
                }
                return false;
            }
        });

        mPasswordView = (EditText) findViewById(R.id.user_password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_ACTION_DONE) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        CheckBox mShowPwdCheckBox = (CheckBox) findViewById(R.id.show_password);
        mShowPwdCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    mPasswordView.setInputType(InputType.TYPE_CLASS_TEXT);
                } else {
                    mPasswordView.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }
            }
        });

        Button mUserSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mUserSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mSettingButton = (Button) findViewById(R.id.system_setting_button);
        mSettingButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, SettingsActivity.class);
                startActivity(intent);
                mPasswordView.setText("");
                mUserView.requestFocus();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);

        mProgressView = (ProgressView) findViewById(R.id.login_progress);

        mUserView.setText(PreferencesHelper.Get(this,"LoginUser"));

        if(mUserView.getText().toString().equals("")){
            mUserView.requestFocus();
        }
        else{
            mPasswordView.requestFocus();
        }

        IntentFilter filter = new IntentFilter();
        filter.addAction("exitApp");    //只有持有相同的action的接受者才能接收此广播
        registerReceiver(new ReceiveBroadCast(), filter);
    }

    @Override
    protected void onDestroy() {
        if(mAuthTask!=null){
            if(!mAuthTask.isCancelled()){
                mAuthTask.cancel(true);
            }
        }

        super.onDestroy();
        AudioPlayer.Release();
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mUserView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String user = mUserView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(password)/* && !isPasswordValid(password)*/) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(user)) {
            mUserView.setError(getString(R.string.error_invalid_user));
            focusView = mUserView;
            cancel = true;
        }/* else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }*/

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);

            JSONObject jParas = new JSONObject();
            try {
                jParas.put("number", user);
                jParas.put("password", password);
            }catch (Exception ex) {
                ShowDialog.ExceptionDialog(LoginActivity.this,ex.getMessage());
                showProgress(false);
                return;
            }
            mAuthTask = new UserLoginTask(LoginActivity.this, "Login", jParas);
            mAuthTask.execute();
        }
    }

    private void showProgress(boolean show) {
        mProgressView.Show(mLoginFormView, show);
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends WebserviceTask {

        UserLoginTask(Context context, String method, JSONObject Paras) {
            super(context, method, Paras);
        }

        @Override
        protected void onPostExecute(final Message msg) {

            mAuthTask = null;
            showProgress(false);

            switch (msg.what) {
                case KingdeeK3WiseWebServiceHelper.INVOKE_SUCCESS:
                    try {
                        JSONObject jsonObject = new JSONObject(msg.obj.toString());
                        LoginUser.Id = jsonObject.getInt("id");
                        LoginUser.Number = jsonObject.getString("number");
                        LoginUser.Name = jsonObject.getString("name");
                    } catch (Exception ex) {
                        ShowDialog.ExceptionDialog(LoginActivity.this, ex.getMessage());
                        return;
                    }

                    PreferencesHelper.Save(LoginActivity.this, "LoginUser", LoginUser.Number);

                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    break;
                case KingdeeK3WiseWebServiceHelper.INVOKE_NULL:
                    ShowDialog.WarningDialog(LoginActivity.this, "用户或密码错误");
                    break;
                case KingdeeK3WiseWebServiceHelper.INVOKE_BUSINESS_EXCEPTION:
                    ShowDialog.WarningDialog(LoginActivity.this, msg.obj.toString());
                    break;
                case KingdeeK3WiseWebServiceHelper.INVOKE_EXCEPTION:
                    ShowDialog.ExceptionDialog(LoginActivity.this, msg.obj.toString());
                    break;
            }

            super.onPostExecute(msg);
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }

    public class ReceiveBroadCast extends BroadcastReceiver
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            LoginActivity.this.finish();
            System.exit(0);
        }
    }
}

