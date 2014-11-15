package com.dipacommerce.dipasport.views;

import android.app.ActionBar;
import android.app.Activity;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dipacommerce.dipasport.DiPaSport;
import com.dipacommerce.dipasport.R;
import com.dipacommerce.dipasport.customer.CustomerInfo;
import com.dipacommerce.dipasport.data.Constants;
import com.dipacommerce.dipasport.utils.Mail;
import com.dipacommerce.dipasport.utils.camera.CameraManager;
import com.dipacommerce.dipasport.utils.camera.ICamera.CaptureEventChanged;

public class FragmentSearchRequeted extends DiPaSport<Object, Object> implements OnClickListener {

    private TextView mHeaderText;

    private View mInfoRequested;
    private EditText mPartRequested;
    private EditText mTypeOfVehicle;
    private EditText mModelOfVehicle;
    private EditText mBrandOfPart;
    private EditText mPartNumber;
    private EditText mFaultOfVehicle;
    private EditText mFaultDetected;
    private EditText mAddInfo;

    private View mContactDetail;
    private EditText mContactName;
    private EditText mContactSurname;
    private EditText mContactCompany;
    private EditText mContactPhone;
    private EditText mContactEmail;

    private Button mSubmit;

    private Button mTakePicture1;
    private Button mTakePicture2;
    private Button mTakePicture3;

    private ImageView mImageView1;
    private ImageView mImageView2;
    private ImageView mImageView3;

    private CameraManager mCamera;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        BuildActionBar();

        mCamera = CameraManager.getInstance();
        mCamera.setAutoCallback(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_searchrequest, container, false);
        UpdateViews(rootView);
        return rootView;
    }

    @Override
    public void UpdateViews(View rootView) {

        mHeaderText = (TextView) rootView.findViewById(R.id.search_header_status);

        mInfoRequested = (View) rootView.findViewById(R.id.search_information_requested);
        mPartRequested = (EditText) rootView.findViewById(R.id.search_part_requested);
        mTypeOfVehicle = (EditText) rootView.findViewById(R.id.search_typeof_vehicle);
        mModelOfVehicle = (EditText) rootView.findViewById(R.id.search_modelof_vehicle);
        mBrandOfPart = (EditText) rootView.findViewById(R.id.search_brandof_part);
        mPartNumber = (EditText) rootView.findViewById(R.id.search_part_numbers);
        mFaultOfVehicle = (EditText) rootView.findViewById(R.id.search_faultof_vehicle);
        mFaultDetected = (EditText) rootView.findViewById(R.id.search_fault_detected);
        mAddInfo = (EditText) rootView.findViewById(R.id.search_add_info);

        mContactDetail = (View) rootView.findViewById(R.id.search_contact_detail);
        mContactName = (EditText) rootView.findViewById(R.id.search_contact_name);
        mContactSurname = (EditText) rootView.findViewById(R.id.search_contact_surname);
        mContactCompany = (EditText) rootView.findViewById(R.id.search_contact_company);
        mContactPhone = (EditText) rootView.findViewById(R.id.search_contact_phone);
        mContactEmail = (EditText) rootView.findViewById(R.id.search_contact_email);

        mSubmit = (Button) rootView.findViewById(R.id.search_requested_submit);
        mSubmit.setOnClickListener(OnSubmitSearchRequested);

        if (sCustomer.isLogin()) {
            mContactDetail.setVisibility(View.GONE);
        } else {
            mContactDetail.setVisibility(View.VISIBLE);
        }

        if (Constants.DEBUG_MODE) {
            mPartRequested.setText("test 1");
            mTypeOfVehicle.setText("test 2");
            mModelOfVehicle.setText("test 3");
            mBrandOfPart.setText("test 4");
            mPartNumber.setText("test 5");
            mFaultOfVehicle.setText("test 6");
            mFaultDetected.setText("test 7");
            mAddInfo.setText("test 8");
            mContactName.setText("test 9");
            mContactSurname.setText("test 10");
            mContactCompany.setText("test 11");
            mContactPhone.setText("test 12");
            mContactEmail.setText("test 13");
        }

        mTakePicture1 = (Button) rootView.findViewById(R.id.search_picture_1);
        mTakePicture2 = (Button) rootView.findViewById(R.id.search_picture_2);
        mTakePicture3 = (Button) rootView.findViewById(R.id.search_picture_3);

        mTakePicture1.setOnClickListener(this);
        mTakePicture2.setOnClickListener(this);
        mTakePicture3.setOnClickListener(this);

        mImageView1 = (ImageView) rootView.findViewById(R.id.search_imageview_picture_1);
        mImageView2 = (ImageView) rootView.findViewById(R.id.search_imageview_picture_2);
        mImageView3 = (ImageView) rootView.findViewById(R.id.search_imageview_picture_3);

        super.UpdateViews(rootView);
    }

    @Override
    protected void BuildActionBar() {
        Activity activity = ((Activity) sCtx);
        View mCustomTitle = activity.getLayoutInflater().inflate(R.layout.ctrl_search_requested_title, null);

        ActionBar actionBar = activity.getActionBar();
        if (actionBar != null) {
            ((TextView) mCustomTitle.findViewById(R.id.search_title_name)).setText("");

            ImageView logo = (ImageView) mCustomTitle.findViewById(R.id.search_logo);
            if (logo != null) {
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
                params.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
                logo.setLayoutParams(params);
            }

            TextView back = (TextView) mCustomTitle.findViewById(R.id.search_back);
            if (back != null) {
                back.setVisibility(View.VISIBLE);
                back.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        FragmentManager fm = getFragmentManager();
                        fm.beginTransaction().replace(R.id.tab_search, sSearchFragment).commit();
                    }
                });
            }

            actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
            actionBar.setCustomView(mCustomTitle);

        }
    }

    private boolean validate(ViewGroup rootView) {
        boolean flag = true;

        if (mPartRequested.getText().length() == 0) {
            setEditTextHighLight(mPartRequested);
            flag = false;
        } else {
            setEditTextNormal(mPartRequested);
        }

        if (mTypeOfVehicle.getText().length() == 0) {
            setEditTextHighLight(mTypeOfVehicle);
            flag = false;
        } else {
            setEditTextNormal(mTypeOfVehicle);
        }

        if (mModelOfVehicle.getText().length() == 0) {
            setEditTextHighLight(mModelOfVehicle);
            flag = false;
        } else {
            setEditTextNormal(mModelOfVehicle);
        }

        if (mBrandOfPart.getText().length() == 0) {
            setEditTextHighLight(mBrandOfPart);
            flag = false;
        } else {
            setEditTextNormal(mBrandOfPart);
        }

        if (mPartNumber.getText().length() == 0) {
            setEditTextHighLight(mPartNumber);
            flag = false;
        } else {
            setEditTextNormal(mPartNumber);
        }

        if (mFaultOfVehicle.getText().length() == 0) {
            setEditTextHighLight(mFaultOfVehicle);
            flag = false;
        } else {
            setEditTextNormal(mFaultOfVehicle);
        }

        if (!sCustomer.isLogin()) {
            if (mContactEmail.getText().length() == 0) {
                setEditTextHighLight(mContactEmail);
                flag = false;
            } else {
                setEditTextNormal(mContactEmail);
            }

            if (mContactSurname.getText().length() == 0) {
                setEditTextHighLight(mContactSurname);
                flag = false;
            } else {
                setEditTextNormal(mContactSurname);
            }

            if (mContactCompany.getText().length() == 0) {
                setEditTextHighLight(mContactCompany);
                flag = false;
            } else {
                setEditTextNormal(mContactCompany);
            }

            if (mContactPhone.getText().length() == 0) {
                setEditTextHighLight(mContactPhone);
                flag = false;
            } else {
                setEditTextNormal(mContactPhone);
            }

            if (mContactEmail.getText().length() == 0) {
                setEditTextHighLight(mContactEmail);
                flag = false;
            } else {
                setEditTextNormal(mContactEmail);
            }
        }

        if (!flag) {
            Toast.makeText(sCtx, sCtx.getString(R.string.str_required), Toast.LENGTH_LONG).show();
        }

        return flag;
    }

    private String getCollectSubmit() {
        StringBuilder body = new StringBuilder();
        CustomerInfo customerInfo = null;
        if (sCustomer.isLogin()) {
            customerInfo = sCustomer.getCustomerInfo();
        }

        String partrequest = mPartRequested.getText().toString();
        String typeofvehicle = mTypeOfVehicle.getText().toString();
        String modelofvehicle = mModelOfVehicle.getText().toString();
        String brandofpart = mBrandOfPart.getText().toString();
        String partnumbers = mPartNumber.getText().toString();
        String faultofvehicle = mFaultOfVehicle.getText().toString();
        String faultdetect = mFaultDetected.getText().toString();
        String addinfo = mAddInfo.getText().toString();
        String name = mContactName.getText().toString();
        String surname = mContactSurname.getText().toString();
        String company = mContactCompany.getText().toString();
        String phone = mContactPhone.getText().toString();
        String email = mContactEmail.getText().toString();

        body.append("\n\n");
        if (customerInfo != null) {
            body.append(String.format("%s: %s\n", sCtx.getString(R.string.str_register_fname_hint), customerInfo.getFullname()));
            body.append(String.format("%s: %s\n", sCtx.getString(R.string.str_search_company), customerInfo.getCompany()));
            body.append(String.format("%s: %s\n", sCtx.getString(R.string.str_register_telephone), customerInfo.getTelephone()));
            body.append(String.format("%s: %s\n", sCtx.getString(R.string.str_register_email_hint), customerInfo.getEmail()));
        } else {
            body.append(String.format("%s: %s %s\n", sCtx.getString(R.string.str_register_fname_hint), name, surname));
            body.append(String.format("%s: %s\n", sCtx.getString(R.string.str_search_company), company));
            body.append(String.format("%s: %s\n", sCtx.getString(R.string.str_register_telephone), phone));
            body.append(String.format("%s: %s\n", sCtx.getString(R.string.str_register_email_hint), email));
        }

        body.append(String.format("%s: %s\n", sCtx.getString(R.string.str_search_part_requested), partrequest));
        body.append(String.format("%s: %s\n", sCtx.getString(R.string.str_search_typeof_vehicle), typeofvehicle));
        body.append(String.format("%s: %s\n", sCtx.getString(R.string.str_search_modelof_vehicle), modelofvehicle));
        body.append(String.format("%s: %s\n", sCtx.getString(R.string.str_search_brandof_part), brandofpart));
        body.append(String.format("%s: %s\n", sCtx.getString(R.string.str_search_part_numbers), partnumbers));
        body.append(String.format("%s: %s\n", sCtx.getString(R.string.str_search_faultof_vehicle), faultofvehicle));
        body.append(String.format("%s: %s\n", sCtx.getString(R.string.str_search_fault_codes_descriptions), faultdetect));
        body.append(String.format("%s: %s\n", sCtx.getString(R.string.str_search_additional_info), addinfo));

        return body.toString();
    }

    private OnClickListener OnSubmitSearchRequested = new OnClickListener() {

        @Override
        public void onClick(View v) {
            if (validate((ViewGroup) v.getParent())) {
                sLoading.setMessage(sCtx.getString(R.string.str_please_wait));
                sLoading.showDialog();
                new SendMailTask().execute();
            }
        }
    };

    private class SendMailTask extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void... params) {
            final Mail m = new Mail("customer.dipasport@gmail.com", "aM8^M*Sc");
            m.set_subject(sCtx.getString(R.string.str_email_subject));
            m.setBody(getCollectSubmit());
            m.set_from("customer.dipasport@gmail.com");
            if (Constants.DEBUG_MODE) {
                m.set_to(new String[] { "vngocvan@gmail.com" });
            } else {
                m.set_to(new String[] { Constants.EMAIL_ORDER });
            }

            boolean ret = false;
            try {
                Object name1 = mImageView1.getTag();
                Object path1 = mTakePicture1.getTag();

                Object name2 = mImageView2.getTag();
                Object path2 = mTakePicture2.getTag();

                Object name3 = mImageView3.getTag();
                Object path3 = mTakePicture3.getTag();

                if (name1 != null && path1 != null) {
                    m.addAttachment(name1.toString(), path1.toString());
                }

                if (name2 != null && path2 != null) {
                    m.addAttachment(name2.toString(), path2.toString());
                }

                if (name3 != null && path3 != null) {
                    m.addAttachment(name3.toString(), path3.toString());
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
                mInfoRequested.setVisibility(View.GONE);
                mSubmit.setVisibility(View.GONE);

                if (!sCustomer.isLogin()) {
                    mContactDetail.setVisibility(View.GONE);
                }

                String status = String.format("%s<br>%s<br><b>%s</b>", getString(R.string.str_search_request_msg1), getString(R.string.str_search_request_msg2), getString(R.string.str_search_request_msg3));
                mHeaderText.setText(Html.fromHtml(status));
                Toast.makeText(sCtx, getSendMailStatusOK(), Toast.LENGTH_LONG).show();
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
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
        case R.id.search_picture_1:
            mCamera.registerEventChanged(OnPictureOne);
            break;

        case R.id.search_picture_2:
            mCamera.registerEventChanged(OnPictureTwo);
            break;

        case R.id.search_picture_3:
            mCamera.registerEventChanged(OnPictureThree);
            break;
        }

        mCamera.openCamera();
    }

    private CaptureEventChanged OnPictureOne = new CaptureEventChanged() {

        @Override
        public void OnCaptureSuccess(Bitmap bmp) {
            mImageView1.setVisibility(View.VISIBLE);
            mImageView1.setImageBitmap(bmp);
            String path = mCamera.getImageFile().getPath();
            String filename = mCamera.getFileName();

            mImageView1.setTag(filename);
            mTakePicture1.setTag(path);
        }
    };

    private CaptureEventChanged OnPictureTwo = new CaptureEventChanged() {

        @Override
        public void OnCaptureSuccess(Bitmap bmp) {
            mImageView2.setVisibility(View.VISIBLE);
            mImageView2.setImageBitmap(bmp);
            String path = mCamera.getImageFile().getPath();
            String filename = mCamera.getFileName();

            mImageView2.setTag(filename);
            mTakePicture2.setTag(path);
        }
    };

    private CaptureEventChanged OnPictureThree = new CaptureEventChanged() {

        @Override
        public void OnCaptureSuccess(Bitmap bmp) {
            mImageView3.setVisibility(View.VISIBLE);
            mImageView3.setImageBitmap(bmp);
            String path = mCamera.getImageFile().getPath();
            String filename = mCamera.getFileName();

            mImageView3.setTag(filename);
            mTakePicture3.setTag(path);
        }
    };

}
