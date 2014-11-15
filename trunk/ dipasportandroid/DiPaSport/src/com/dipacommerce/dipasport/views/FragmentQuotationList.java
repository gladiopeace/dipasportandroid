package com.dipacommerce.dipasport.views;

import org.json.JSONObject;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.dipacommerce.dipasport.DiPaSport;
import com.dipacommerce.dipasport.R;
import com.dipacommerce.dipasport.network.json.IJSONManager.Callback;
import com.dipacommerce.dipasport.shoppingcart.QuotationAdapter;
import com.dipacommerce.dipasport.shoppingcart.QuotationProduct;
import com.dipacommerce.dipasport.shoppingcart.ShopImp.ShoppingCartChanged;

public class FragmentQuotationList extends DiPaSport<Object, Object> implements ShoppingCartChanged {

    private ListView mListQuotation;
    private QuotationAdapter<QuotationProduct> mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_quotation_list, container, false);
        UpdateViews(rootView);
        return rootView;
    }

    @Override
    public void UpdateViews(View rootView) {
        mAdapter = new QuotationAdapter<QuotationProduct>(sCtx, R.layout.ctrl_product_add_to_cart, sQuotation.getItems());
        mListQuotation = (ListView) rootView.findViewById(R.id.quotation_list_to_sent_contact);
        mListQuotation.setAdapter(mAdapter);
        sQuotation.registerCartUpdated(this);

        Button submit = (Button) rootView.findViewById(R.id.quotation_sent_contact_button);
        submit.setOnClickListener(OnSubmitQuotation);
    }

    private OnClickListener OnSubmitQuotation = new OnClickListener() {

        @Override
        public void onClick(View v) {
            Toast.makeText(sCtx, sCtx.getString(R.string.str_quotation_msg_sending_quote), Toast.LENGTH_LONG).show();
            sQuotation.registerCallbackQuote(mCallbackQuote);
            sQuotation.quote(sCustomer.getCustomerInfo(), sQuotation.getItems());
        }
    };

    @Override
    public void onCartUpdated() {
        mAdapter.refreshData(sQuotation.getItems());
    }

    private Callback<JSONObject> mCallbackQuote = new Callback<JSONObject>() {

        @Override
        public void onResults(JSONObject results) {
            if (results == null)
                return;
            // String json = results.toString();
            showDialogQuote();

        }

        @Override
        public void onErrors(int _errorCode, String _errorMessage) {

        }

        @Override
        public void onDone() {

        }
    };

    private void showDialogQuote() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(sCtx);
        dialog.setMessage(sCtx.getString(R.string.str_quotation_msg_success));
        dialog.setPositiveButton(sCtx.getString(R.string.str_dialog_ok), null);
        dialog.create().show();
    }
}
