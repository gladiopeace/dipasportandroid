package com.dipacommerce.dipasport.views;

import org.json.JSONObject;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dipacommerce.dipasport.DiPaSport;
import com.dipacommerce.dipasport.R;
import com.dipacommerce.dipasport.customer.CustomerInfo;
import com.dipacommerce.dipasport.data.Constants;
import com.dipacommerce.dipasport.database.DatabaseHandler;
import com.dipacommerce.dipasport.network.json.IJSONManager;
import com.dipacommerce.dipasport.network.json.IJSONManager.Callback;

public class FragmentLogin extends DiPaSport<Object, Object>  {
    private View mRootView;
    private Button mLogin;
    private Button mRegister;
    private TextView mPasswordLost;
    private EditText mUserNameInput;
    private EditText mPasswordInput;
    private String mEmailToReset = "";
    
    public FragmentLogin() {
        BuildActionBar();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        UpdateProgressDialogContent();
        BuildActionBar();
    }

    @Override
    public void UpdateProgressDialogContent() {
        sLoading.setMessage(sCtx.getString(R.string.str_login_wait));
        sLoading.RegisterCallbackLoadingTimeOut(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (sCustomer.isLogin()) {
            Intent homepage = new Intent(sCtx, HomePageActivity.class);
            homepage.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(homepage);
        } else {

            mRootView = inflater.inflate(R.layout.fragment_login_page, container, false);
            UpdateViews(mRootView);
        }
        return mRootView;
    }

    @Override
    public void UpdateViews(View rootView) {
        /**
         * Login
         */
        mLogin = (Button) rootView.findViewById(R.id.login_loginbtn);
        if (mLogin != null) {
            mLogin.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    checkLogin();
                    hideKeyboard();
                }
            });
        }

        /**
         * Register
         */
        mRegister = (Button) rootView.findViewById(R.id.login_register);
        if (mRegister != null) {
            mRegister.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    FragmentManager fm = getFragmentManager();
                    fm.beginTransaction().replace(R.id.tab_account, new FragmentRegister()).commit();
                }
            });
        }

        /**
         * Password lost
         */
        mPasswordLost = (TextView) rootView.findViewById(R.id.login_passworddlost);
        if (mPasswordLost != null) {
            mPasswordLost.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    showDialogLostPassword("");
                }
            });
        }

        /**
         * User input
         */
        mUserNameInput = (EditText) rootView.findViewById(R.id.login_user);

        /**
         * Password input
         */
        mPasswordInput = (EditText) rootView.findViewById(R.id.login_password);

        if (Constants.DEBUG_MODE) {
            mUserNameInput.setText("marktestvn@gmail.com");
            mPasswordInput.setText("654321");
        }

        Bundle bundle = getArguments();
        if (bundle != null) {
            String emailRegisted = bundle.getString(CustomerInfo.BUNDLE.Email);
            mUserNameInput.setText(emailRegisted);
        }
    }

    /**
     * 
     * @param rootView
     * @return
     */
    private CustomerInfo getCustomerInfoFromView(View rootView) {
        String username = "";
        String passwd = "";

        if (mUserNameInput != null) {
            username = mUserNameInput.getText().toString();
        }

        if (mPasswordInput != null) {
            passwd = mPasswordInput.getText().toString();
        }

        CustomerInfo customerInfo = new CustomerInfo();
        customerInfo.setEmail(username);
        customerInfo.setPassword(passwd);

        return customerInfo;
    }

    /**
	 * 
	 */
    private void login(CustomerInfo customerInfo) {
        ShowLoading();
        sCustomer.registerCallbackLogin(mCallbackLogin);
        sCustomer.customerLogin(customerInfo);
    }

    /**
	 * 
	 */
    private void checkLogin() {
        CustomerInfo customerInfo = getCustomerInfoFromView(mRootView);
        if (customerInfo.getEmail().length() == 0) {
            Toast.makeText(sCtx, getString(R.string.str_login_username_empty), Toast.LENGTH_LONG).show();
            return;
        }

        if (customerInfo.getPassword().length() == 0) {
            Toast.makeText(sCtx, getString(R.string.str_login_password_empty), Toast.LENGTH_LONG).show();
            return;
        }

        login(customerInfo);
    }

    /**
     * Check login status here
     */
    private IJSONManager.Callback<JSONObject> mCallbackLogin = new IJSONManager.Callback<JSONObject>() {

        @Override
        public void onResults(JSONObject results) {
            if (results == null) {
                Toast.makeText(sCtx, sCtx.getString(R.string.no_network_connection), Toast.LENGTH_SHORT).show();
                return;
            }

            JSONObject ret = results;
            boolean isLogin = sCustomer.loginJSONChecker(ret);
            if (isLogin) {
                String message = "Loggato";// sCustomer.getMessageLogin(results);
                Toast.makeText(sCtx, message, Toast.LENGTH_SHORT).show();
                FragmentManager fm = getFragmentManager();
                fm.beginTransaction().replace(R.id.tab_account, sAccountFragment).commit();
                TabsFragment.setTabSelected(TabsFragment.TAB_HOME_INDEX);
                if (!Constants.DEBUG_MODE) {
                    // clear user info
                    mUserNameInput.setText("");
                    mPasswordInput.setText("");
                }
                sDatabaseHandler = DatabaseHandler.getInstance(sCtx);
                sDatabaseHandler.addEmail(sCustomer.getCustomerInfo());
            } else {
                Toast.makeText(sCtx, getString(R.string.str_login_fail), Toast.LENGTH_LONG).show();
            }
            sLoading.closeDialog();
            
            
        }

        @Override
        public void onErrors(int _errorCode, final String _errorMessage) {
            CloseLoading();
            new Handler().post(new Runnable() {

                @Override
                public void run() {
                    Toast.makeText(sCtx, getString(R.string.no_network_connection), Toast.LENGTH_SHORT).show();

                }
            });
        }

        @Override
        public void onDone() {

        }
    };

    public static void ShowLoading() {
        new Handler().post(new Runnable() {

            @Override
            public void run() {
                sLoading.showDialog();
            }
        });
    }

    public static void CloseLoading() {
        new Handler().post(new Runnable() {

            @Override
            public void run() {
                sLoading.closeDialog();
            }
        });
    }

    @Override
    protected void BuildActionBar() {
        if (sCtx != null) {
            Activity activity = ((Activity) sCtx);
            View mCustomTitle = activity.getLayoutInflater().inflate(R.layout.ctrl_common_title, null);
            ActionBar actionBar = activity.getActionBar();
            if (actionBar != null) {
                ((TextView) mCustomTitle.findViewById(R.id.title_name)).setText(activity.getString(R.string.str_login_title));

                actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
                actionBar.setCustomView(mCustomTitle);
            }
        }
    }

    @Override
    public void UpdateTabChanged(String tabId) {
        BuildActionBar();
        UpdateProgressDialogContent();
    }

    private Callback<JSONObject> mCallbackLostPassword = new Callback<JSONObject>() {

        @Override
        public void onResults(JSONObject results) {
            if (!sCustomer.lostPasswordChecker(results)) {
                Toast.makeText(sCtx, sCtx.getString(R.string.str_lostpassword_fail_msg), Toast.LENGTH_SHORT).show();
                showDialogLostPassword(mEmailToReset);
                return;
            } else {
                String msg = sCtx.getString(R.string.str_lostpassword_msg);// sCustomer.getMessage(results).toString();
                Toast.makeText(sCtx, msg, Toast.LENGTH_LONG).show();
            }

        }

        @Override
        public void onErrors(int _errorCode, String _errorMessage) {
            showDialogLostPassword(mEmailToReset);
        }

        @Override
        public void onDone() {
            sLoading.closeDialog();
        }
    };

    private void showDialogLostPassword(String email) {
        sCustomer.registerCallbackLostPassword(mCallbackLostPassword);

        final AlertDialog.Builder dialog = new AlertDialog.Builder(sCtx);
        dialog.setTitle(sCtx.getString(R.string.str_lostpassword_title));
        dialog.setMessage(sCtx.getString(R.string.str_lostpassword_content));
        final EditText input = new EditText(sCtx);
        input.setHint(R.string.str_register_email_hint);
        input.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        input.setSingleLine(true);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        if (email.length() > 0) {
            input.setText(email);
        }
        dialog.setPositiveButton(sCtx.getString(R.string.str_dialog_ok), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                mEmailToReset = input.getText().toString();
                if (mEmailToReset.length() == 0) {
                    Toast.makeText(sCtx, sCtx.getString(R.string.str_lostpassword_email_empty), Toast.LENGTH_SHORT).show();
                    showDialogLostPassword("");
                    return;
                }
                sLoading.setMessage(sCtx.getString(R.string.str_please_wait));
                sLoading.showDialog();
                sCustomer.customerLostPassword(mEmailToReset);
                dialog.dismiss();
            }
        });

        dialog.setNegativeButton(sCtx.getString(R.string.str_dialog_cancel), null);

        dialog.setView(input);
        dialog.create().show();
    }
    

}
