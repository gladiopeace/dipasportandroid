package com.dipacommerce.dipasport.views;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.json.JSONObject;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.dipacommerce.dipasport.DiPaSport;
import com.dipacommerce.dipasport.R;
import com.dipacommerce.dipasport.customer.CustomerInfo;
import com.dipacommerce.dipasport.data.Constants;
import com.dipacommerce.dipasport.data.CountryAdapter;
import com.dipacommerce.dipasport.network.json.IJSONManager.Callback;
import com.dipacommerce.dipasport.utils.MapUtils;

public class FragmentRegister extends DiPaSport<Object, Object> {

    /**
     * Required
     */
    private Spinner mCustomerType;

    /**
     * Required
     */
    private EditText mFirstName;

    /**
     * Required
     */
    private EditText mLastName;

    /**
     * Required
     */
    private EditText mCompany;

    /**
     * Required
     */
    private EditText mTaxVAT;

    /**
     * Required
     */
    private EditText mStreetAddress;

    /**
     * Required
     */
    private EditText mCity;

    private EditText mStateProvince;

    /**
     * Required
     */
    private EditText mZipPostalCode;

    /**
     * Required. Italy as default
     */
    private Spinner mCountry;

    /**
     * Required
     */
    private EditText mTelephone;
    private EditText mFax;

    /**
     * Required
     */
    private EditText mEmail;

    /**
     * Required
     */
    private EditText mPassword;

    /**
     * Required
     */
    private EditText mPasswordConfirm;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_register_page, container, false);

        UpdateViews(rootView);
        return rootView;
    }

    @Override
    public void UpdateViews(final View rootView) {
        sLoading.RegisterCallbackLoadingTimeOut(this);
        final CheckBox privacyoption = (CheckBox)rootView.findViewById(R.id.register_privacy_option);
        //final CheckBox newsletter = (CheckBox)rootView.findViewById(R.id.register_add_newsletter);
        
        final Button register = (Button) rootView.findViewById(R.id.register_ok);
        register.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                boolean privacy = privacyoption.isChecked();
                
                if (validate(rootView)) {
                    if(!privacy){
                        privacyoption.setTextColor(getResources().getColor(R.color.privacy_highlight));
                        Toast.makeText(sCtx, "Selezionare una opzione.", Toast.LENGTH_SHORT).show();
                        return;
                    }else{
                        privacyoption.setTextColor(getResources().getColor(android.R.color.black));
                    }
                    
                    UpdateProgressDialogContent();
                    
                    //boolean subscriber = newsletter.isChecked();
                    //String ns = subscriber ? "YES" : "NO";
                    String client = getString(R.string.register_by_client);
                    
                    sLoading.showDialog();
                    sCustomer.registerCallbackReg(mCallbackRegister);
                    Bundle bundle = getRegisterInfo(rootView);
                    bundle.putString(CustomerInfo.REGISTER_INFO.newsletter, "YES");
                    bundle.putString(CustomerInfo.REGISTER_INFO.client, client);
                    sCustomer.customerRegister(bundle);
                }
            }
        });

        Button cancel = (Button) rootView.findViewById(R.id.register_cancel);
        cancel.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                FragmentManager fm = getFragmentManager();
                fm.beginTransaction().replace(R.id.tab_account, sLoginFragment).commit();
            }
        });
        
        // Privacy
        TextView privacy = (TextView)rootView.findViewById(R.id.register_privacy_links);
        privacy.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                final Intent intent=new Intent(Intent.ACTION_VIEW,Uri.parse(getResources().getString(R.string.str_register_privacy_url)));
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY|Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                startActivity(intent);
            }
        });

        mCustomerType = (Spinner) rootView.findViewById(R.id.register_customer_type);
        mFirstName = (EditText) rootView.findViewById(R.id.register_fname);
        mLastName = (EditText) rootView.findViewById(R.id.register_lname);
        mCompany = (EditText) rootView.findViewById(R.id.register_company);
        mTaxVAT = (EditText) rootView.findViewById(R.id.register_taxvat);
        mStreetAddress = (EditText) rootView.findViewById(R.id.register_street);
        mCity = (EditText) rootView.findViewById(R.id.register_city);
        mStateProvince = (EditText) rootView.findViewById(R.id.register_state_province);
        mZipPostalCode = (EditText) rootView.findViewById(R.id.register_zip_postcode);
        mCountry = (Spinner) rootView.findViewById(R.id.register_country);
        mTelephone = (EditText) rootView.findViewById(R.id.register_telephone);
        mFax = (EditText) rootView.findViewById(R.id.register_fax);
        mEmail = (EditText) rootView.findViewById(R.id.register_email);
        mPassword = (EditText) rootView.findViewById(R.id.register_passwd);
        mPasswordConfirm = (EditText) rootView.findViewById(R.id.register_passwd_confirm);

        if (Constants.DEBUG_MODE) {
            mCustomerType.setSelection(0);
            mFirstName.setText("van");
            mLastName.setText("vo");
            mCompany.setText("my company");
            mTaxVAT.setText("112233");
            mStreetAddress.setText("Ho Chi Minh");
            mCity.setText("Ho Chi Minh");
            mStateProvince.setText("Q12");
            mZipPostalCode.setText("70000");

            mTelephone.setText("0123456789");
            mFax.setText("9876543210");
            mEmail.setText("vn001@gmail.com");
            mPassword.setText("123456");
            mPasswordConfirm.setText("123456");
        }

        Map<String, String> countries = getCountries();
        CountryAdapter adapter = new CountryAdapter(sCtx, countries);
        mCountry.setAdapter(adapter);

        int countryIndex = getCountryIndex(Locale.getDefault().getCountry());
        mCountry.setSelection(countryIndex);

    }

    @Override
    public void UpdateProgressDialogContent() {
        sLoading.setMessage(sCtx.getString(R.string.str_please_wait));
    }

    /**
     * 
     * @param rootView
     * @return
     */
    private Bundle getRegisterInfo(View rootView) {

        String customerType = mCustomerType.getSelectedItem().toString();
        String fname = mFirstName.getText().toString();
        String lname = mLastName.getText().toString();
        String company = mCompany.getText().toString();
        String taxvat = mTaxVAT.getText().toString();
        String street = mStreetAddress.getText().toString();
        String city = mCity.getText().toString();
        String state = mStateProvince.getText().toString();
        String postalCode = mZipPostalCode.getText().toString();
        String country_id = mCountry.getSelectedItem().toString();
        String telephone = mTelephone.getText().toString();
        String fax = mFax.getText().toString();

        String email = mEmail.getText().toString();
        String password = mPassword.getText().toString();
        // String password_confirm = mPasswordConfirm.getText().toString();

        Bundle bundle = new Bundle();
        bundle.putString(CustomerInfo.REGISTER_INFO.customer_type, customerType);
        bundle.putString(CustomerInfo.REGISTER_INFO.first_name, fname);
        bundle.putString(CustomerInfo.REGISTER_INFO.last_name, lname);
        bundle.putString(CustomerInfo.REGISTER_INFO.company, company);
        bundle.putString(CustomerInfo.REGISTER_INFO.taxvat, taxvat);
        bundle.putString(CustomerInfo.REGISTER_INFO.streetaddress, street);
        bundle.putString(CustomerInfo.REGISTER_INFO.city, city);
        bundle.putString(CustomerInfo.REGISTER_INFO.stateprovince, state);
        bundle.putString(CustomerInfo.REGISTER_INFO.postalcode, postalCode);
        bundle.putString(CustomerInfo.REGISTER_INFO.country, country_id);
        bundle.putString(CustomerInfo.REGISTER_INFO.telephone, telephone);
        bundle.putString(CustomerInfo.REGISTER_INFO.fax, fax);
        bundle.putString(CustomerInfo.REGISTER_INFO.email, email);
        bundle.putString(CustomerInfo.REGISTER_INFO.password, password);

        return bundle;
    }

    private boolean validate(View rootView) {
        boolean flag = true;

        if (mFirstName.getText().length() == 0) {
            setEditTextHighLight(mFirstName);
            flag = false;
        } else {
            setEditTextNormal(mFirstName);
        }

        if (mLastName.getText().length() == 0) {
            setEditTextHighLight(mLastName);
            flag = false;
        } else {
            setEditTextNormal(mLastName);
        }

        if (mCompany.getText().length() == 0) {
            setEditTextHighLight(mCompany);
            flag = false;
        } else {
            setEditTextNormal(mCompany);
        }

        if (mTaxVAT.getText().length() == 0) {
            setEditTextHighLight(mTaxVAT);
            flag = false;
        } else {
            setEditTextNormal(mTaxVAT);
        }

        if (mStreetAddress.getText().length() == 0) {
            setEditTextHighLight(mStreetAddress);
            flag = false;
        } else {
            setEditTextNormal(mStreetAddress);
        }

        if (mCity.getText().length() == 0) {
            setEditTextHighLight(mCity);
            flag = false;
        } else {
            setEditTextNormal(mCity);
        }

        if (mZipPostalCode.getText().length() == 0) {
            setEditTextHighLight(mZipPostalCode);
            flag = false;
        } else {
            setEditTextNormal(mZipPostalCode);
        }

        if (mTelephone.getText().length() == 0) {
            setEditTextHighLight(mTelephone);
            flag = false;
        } else {
            setEditTextNormal(mTelephone);
        }

        if (mEmail.getText().length() == 0) {
            setEditTextHighLight(mEmail);
            flag = false;
        } else {
            setEditTextNormal(mEmail);
        }

        if (mPassword.getText().length() == 0) {
            this.setEditTextHighLightPassword(mPassword);
            flag = false;
        } else {
            this.setEditTextNormalPassword(mPassword);
        }

        if (mPasswordConfirm.getText().length() == 0) {
            this.setEditTextHighLightPassword(mPasswordConfirm);
            flag = false;
        } else {
            this.setEditTextNormalPassword(mPasswordConfirm);
        }

        if (!flag) {
            Toast.makeText(sCtx, sCtx.getString(R.string.str_required), Toast.LENGTH_LONG).show();
        } else {
            String passwd = mPassword.getText().toString();
            String passwdconfirm = mPasswordConfirm.getText().toString();

            // Check password match
            if ((passwd.length() + passwdconfirm.length() > 1) && !passwd.equals(passwdconfirm)) {
                this.setEditTextHighLightPassword(mPasswordConfirm);
                Toast.makeText(sCtx, sCtx.getString(R.string.str_register_password_confirm_not_match), Toast.LENGTH_SHORT).show();
                return false;
            } else {
                this.setEditTextNormalPassword(mPasswordConfirm);
            }

            // Check password lenght
            if (passwd.length() < 6 && passwdconfirm.length() < 6) {
                this.setEditTextHighLightPassword(mPassword);
                this.setEditTextHighLightPassword(mPasswordConfirm);
                Toast.makeText(sCtx, sCtx.getString(R.string.str_register_password_length_min), Toast.LENGTH_LONG).show();
                return false;
            } else {
                this.setEditTextNormalPassword(mPassword);
                this.setEditTextNormalPassword(mPasswordConfirm);
            }
        }

        return flag;
    }

    private Callback<JSONObject> mCallbackRegister = new Callback<JSONObject>() {

        @Override
        public void onResults(JSONObject results) {
            if (results == null)
                return;
            String msg = sCustomer.getMessage(results).toString();
            Toast.makeText(sCtx, msg, Toast.LENGTH_SHORT).show();
            if (sCustomer.registerChecker(results)) {
                Toast.makeText(sCtx, msg, Toast.LENGTH_LONG).show();

                FragmentManager fm = getFragmentManager();
                Bundle bundle = new Bundle();
                sLoginFragment.setArguments(bundle);
                fm.beginTransaction().replace(R.id.tab_account, sLoginFragment).commit();
            }
        }

        @Override
        public void onErrors(int _errorCode, String _errorMessage) {
            Toast.makeText(sCtx, sCtx.getString(R.string.str_register_fail), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onDone() {
            sLoading.closeDialog();
        }
    };

    private Map<String, String> getCountries() {
        Map<String, String> countries = new HashMap<String, String>();
        for (String iso : Locale.getISOCountries()) {
            Locale l = new Locale("", iso);
            countries.put(iso, l.getDisplayCountry());
        }

        countries = MapUtils.sortByValue(countries);
        return countries;
    }

    private int getCountryIndex(String country_code) {
        Set<String> countries = getCountries().keySet();
        int selection = 0;
        for (String key : countries) {
            if (key.equals(country_code)) {
                return selection;
            }
            ++selection;
        }
        return selection;
    }

    private void setEditTextHighLightPassword(EditText t) {
        super.setEditTextHighLight(t);
        t.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
    }

    private void setEditTextNormalPassword(EditText t) {
        super.setEditTextNormal(t);
        t.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
    }

}
