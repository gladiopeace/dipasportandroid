package com.dipacommerce.dipasport.views;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dipacommerce.dipasport.DiPaSport;
import com.dipacommerce.dipasport.R;
import com.dipacommerce.dipasport.controls.DialogCallback.OnLoadingTimeOutEvents;
import com.dipacommerce.dipasport.data.Constants;
import com.dipacommerce.dipasport.data.ImageAdapter;
import com.dipacommerce.dipasport.products.IProduct;
import com.dipacommerce.dipasport.utils.Mail;
import com.dipacommerce.dipasport.utils.camera.CameraManager;
import com.dipacommerce.dipasport.utils.camera.ImageObject;

public class FragmentSubmitProduct extends DiPaSport<Object, Object> implements OnLoadingTimeOutEvents {
    private View mRootView;
    private CameraManager mCamera;
    private ImageView mImageProduct;
    public static Bitmap impageProduct;

    private EditText mField1;
    private EditText mField2;
    private EditText mField3;
    private EditText mField4;
    private EditText mField5;
    private EditText mField6;
    private EditText mField7;
    private EditText mField8;

    private static final String Field1 = "f1";
    private static final String Field2 = "f2";
    private static final String Field3 = "f3";
    private static final String Field4 = "f4";
    private static final String Field5 = "f5";
    private static final String Field6 = "f6";
    private static final String Field7 = "f7";
    private static final String Field8 = "f8";

    public FragmentSubmitProduct() {
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mCamera = CameraManager.getInstance();
        // mCamera.registerEventChanged(this);

        BuildActionBar();
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_submitproduct_page, container, false);

        UpdateViews(mRootView);

        return mRootView;
    };

    @Override
    public void UpdateViews(View rootView) {

        View viewLoginRequire = (View) rootView.findViewById(R.id.submit_islogin);
        if (sCustomer.isLogin()) {
            viewLoginRequire.setVisibility(View.GONE);
        } else {
            viewLoginRequire.setVisibility(View.VISIBLE);
        }

        /*
         * mImageProduct = (ImageView)
         * rootView.findViewById(R.id.submit_imageproduct); Bitmap bmp =
         * mCamera.getImageProduct(); if (bmp != null) {
         * mImageProduct.setImageBitmap(bmp); }
         * mImageProduct.setOnClickListener(new OnClickListener() {
         * 
         * @Override public void onClick(View v) { Intent fullscreen = new
         * Intent(sCtx, FullScreenImageActivity.class);
         * fullscreen.putExtra(IProduct.BUNDLE.PRODUCT_IMAGE_PATH, "");
         * fullscreen.putExtra(IProduct.BUNDLE.PRODUCT_CODE, "");
         * startActivity(fullscreen); } });
         */
        Button submit = (Button) rootView.findViewById(R.id.submit_products);
        if (submit != null) {
            submit.setOnClickListener(OnSubmit);
        }

        GridView images = (GridView) rootView.findViewById(R.id.gridview);
        ImageAdapter adapter = new ImageAdapter(sCtx, FragmentHome.mFotos);
        images.setAdapter(adapter);
        images.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                Intent fullscreen = new Intent(sCtx, FullScreenImageActivity.class);
                fullscreen.putExtra(IProduct.BUNDLE.PRODUCT_IMAGE_PATH, "");
                fullscreen.putExtra(IProduct.BUNDLE.PRODUCT_CODE, "");
                fullscreen.putExtra("imgindex", arg2);
                startActivity(fullscreen);
            }
        });

        mField1 = (EditText) rootView.findViewById(R.id.submit_field1);
        mField2 = (EditText) rootView.findViewById(R.id.submit_field2);
        mField3 = (EditText) rootView.findViewById(R.id.submit_field3);
        mField4 = (EditText) rootView.findViewById(R.id.submit_field4);
        mField5 = (EditText) rootView.findViewById(R.id.submit_field5);
        mField6 = (EditText) rootView.findViewById(R.id.submit_field6);
        mField7 = (EditText) rootView.findViewById(R.id.submit_field7);
        mField8 = (EditText) rootView.findViewById(R.id.submit_field8);

        if (Constants.DEBUG_MODE) {
            mField1.setText("aaaa");
            mField4.setText("bbbb");
            mField5.setText("cccc");
        }

    }

    @Override
    public void onDetach() {
        mCamera.clearImageProduct();
        super.onDetach();
    }

    @Override
    protected void BuildActionBar() {
        if (sCtx != null) {
            Activity activity = ((Activity) sCtx);
            View mCustomTitle = activity.getLayoutInflater().inflate(R.layout.ctrl_common_title, null);

            ActionBar actionBar = activity.getActionBar();
            if (actionBar != null) {
                ((TextView) mCustomTitle.findViewById(R.id.title_name)).setText("");

                ImageView logo = (ImageView) mCustomTitle.findViewById(R.id.logo);
                if (logo != null) {
                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                    params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
                    params.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
                    logo.setLayoutParams(params);
                }

                TextView back = (TextView) mCustomTitle.findViewById(R.id.back);
                if (back != null) {
                    back.setVisibility(View.VISIBLE);
                    back.setOnClickListener(new OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            FragmentManager fm = getFragmentManager();
                            fm.beginTransaction().replace(R.id.tab_home, sHomeFragment).commit();
                        }
                    });
                }

                actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
                actionBar.setCustomView(mCustomTitle);

            }
        }
    }

    /**
     * 
     * @param rootView
     */
    private boolean validate(View rootView) {
        boolean flag = true;

        if (mField1.getText().length() == 0) {
            setEditTextHighLight(mField1);
            flag = false;
        } else {
            setEditTextNormal(mField1);
        }

        if (mField2.getText().length() == 0) {
            setEditTextHighLight(mField2);
            flag = false;
        } else {
            setEditTextNormal(mField2);
        }

        if (!sCustomer.isLogin()) {
            if (mField6.getText().length() == 0) {
                setEditTextHighLight(mField6);
                flag = false;
            } else {
                setEditTextNormal(mField6);
            }

            if (mField7.getText().length() == 0) {
                setEditTextHighLight(mField7);
                flag = false;
            } else {
                setEditTextNormal(mField7);
            }

            if (mField8.getText().length() == 0) {
                setEditTextHighLight(mField8);
                flag = false;
            } else {
                setEditTextNormal(mField8);
            }
        }

        if (!flag) {
            Toast.makeText(sCtx, sCtx.getString(R.string.str_required), Toast.LENGTH_LONG).show();
        }
        return flag;
    }

    private Bundle getSubmitInfo() {
        String f1 = mField1.getText().toString();
        String f2 = mField2.getText().toString();
        String f3 = mField3.getText().toString();
        String f4 = mField4.getText().toString();
        String f5 = mField5.getText().toString();

        Bundle bundle = new Bundle();
        bundle.putString(Field1, f1);
        bundle.putString(Field2, f2);
        bundle.putString(Field3, f3);
        bundle.putString(Field4, f4);
        bundle.putString(Field5, f5);
        if (!sCustomer.isLogin()) {
            String f6 = mField6.getText().toString();
            String f7 = mField7.getText().toString();
            String f8 = mField8.getText().toString();
            bundle.putString(Field6, f6);
            bundle.putString(Field7, f7);
            bundle.putString(Field8, f8);
        }

        return bundle;
    }

    /**
     * 
     */
    private OnClickListener OnSubmit = new OnClickListener() {

        @Override
        public void onClick(View v) {
            if (validate(mRootView)) {
                hideKeyboard();
                // Bundle bundle = getSubmitInfo();
                // String s = bundle.getString(Field1);
                sLoading.setMessage(sCtx.getString(R.string.str_please_wait));
                sLoading.showDialog();
                sendEmail();
            }
        }
    };

    private void clearData() {
        mField1.setText("");
        mField2.setText("");
        mField3.setText("");
        mField4.setText("");
        mField5.setText("");
        mField6.setText("");
        mField7.setText("");
        mField8.setText("");
    }

    private String collectData() {
        StringBuilder str = new StringBuilder();
        String f1 = mField1.getText().toString();
        String f2 = mField2.getText().toString();
        String f3 = mField3.getText().toString();
        String f4 = mField4.getText().toString();
        String f5 = mField5.getText().toString();
        String f6 = mField6.getText().toString();
        String f7 = mField7.getText().toString();
        String f8 = mField8.getText().toString();

        str.append("\n\n");
        if (sCustomer.isLogin()) {
            str.append("Customer name: " + sCustomer.getCustomerInfo().getFullname() + "\n");
            str.append("Customer email: " + sCustomer.getCustomerInfo().getEmail() + "\n");
            str.append("Customer telephone: " + sCustomer.getCustomerInfo().getTelephone() + "\n");
        } else {
            str.append("Customer name: " + f6 + "\n");
            str.append("Customer email: " + f7 + "\n");
            str.append("Customer telephone: " + f8 + "\n");
        }
        str.append(sCtx.getString(R.string.str_field1) + " :" + f1 + "\n");
        str.append(sCtx.getString(R.string.str_field2) + " :" + f2 + "\n");
        str.append(sCtx.getString(R.string.str_field3) + " :" + f3 + "\n");
        str.append(sCtx.getString(R.string.str_field4) + " :" + f4 + "\n");
        str.append(sCtx.getString(R.string.str_field5) + " :" + f5 + "\n");

        return str.toString();
    }

    private void sendEmail() {
        new SendMailTask().execute();
    }

    private class SendMailTask extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void... params) {
            final Mail m = new Mail("customer.dipasport@gmail.com", "aM8^M*Sc");
            m.set_subject(sCtx.getString(R.string.str_email_subject));
            m.setBody(collectData());
            m.set_from("customer.dipasport@gmail.com");
            if (Constants.DEBUG_MODE) {
                m.set_to(new String[] { "vngocvan@gmail.com" });
            } else {
                m.set_to(new String[] { Constants.EMAIL_ORDER });
            }

            boolean ret = false;
            try {
                if (FragmentHome.mFotos != null) {
                    for (ImageObject image : FragmentHome.mFotos) {
                        m.addAttachment(image.getFilename(), image.getPath());
                    }
                }
                ret = m.send();
            } catch (Exception e) {
                e.printStackTrace();
            }

            return ret;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (result) {
                Toast.makeText(sCtx, getSendMailStatusOK(), Toast.LENGTH_LONG).show();
                FragmentHome.mFotos.clear();
            } else {
                Toast.makeText(sCtx, "Fail to sent mail, try again", Toast.LENGTH_LONG).show();
            }
            sLoading.closeDialog();
        }
    }

    private String getSendMailStatusOK() {
        String str = String.format("%s %s", sCtx.getString(R.string.str_upload_send_mail_ok), Constants.EMAIL_CONTACT);
        return str;
    }

    @Override
    public void OnLoadingTimeOut() {
        Toast.makeText(sCtx, "Sending...", Toast.LENGTH_LONG).show();
    }
}
