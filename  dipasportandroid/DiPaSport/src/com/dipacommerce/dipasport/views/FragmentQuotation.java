package com.dipacommerce.dipasport.views;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.dipacommerce.dipasport.DiPaSport;
import com.dipacommerce.dipasport.R;
import com.dipacommerce.dipasport.shoppingcart.QuotationAdapter;
import com.dipacommerce.dipasport.shoppingcart.QuotationProduct;
import com.dipacommerce.dipasport.shoppingcart.ShopImp.ShoppingCartChanged;

public class FragmentQuotation extends DiPaSport<Object, Object> implements ShoppingCartChanged {

    public static final String PRODUCT_ID = "product_id";

    private ListView mQuote;
    private EditText mComment;
    private QuotationAdapter<QuotationProduct> mAdapter;
    private String mProductId = "";
    private QuotationProduct mCurrentProduct;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_quotation, container, false);

        UpdateViews(rootView);

        return rootView;
    }

    @Override
    public void UpdateViews(View rootView) {
        Bundle bundle = getArguments();
        if (bundle != null) {
            mProductId = bundle.getString(PRODUCT_ID);
            sQuotation.registerCartUpdated(this);
            mCurrentProduct = sQuotation.getItem(mProductId).get(0);
            
            mAdapter = new QuotationAdapter<QuotationProduct>(sCtx, R.layout.ctrl_product_add_to_cart, sQuotation.getItem(mProductId));
            mQuote = (ListView) rootView.findViewById(R.id.quotation_content);
            mQuote.setAdapter(mAdapter);

            mComment = (EditText) rootView.findViewById(R.id.quotation_comment);
            Button submit = (Button) rootView.findViewById(R.id.quotation_submit);
            submit.setOnClickListener(OnAddToQuotation);
        }
    }

    @Override
    protected void BuildActionBar() {
    }

    public ListView getList() {
        return mQuote;
    }

    public String getComment() {
        return mComment.getText().toString();
    }

    // Add the product with comment to quotation list.
    private OnClickListener OnAddToQuotation = new OnClickListener() {

        @Override
        public void onClick(View v) {
            String c = mComment.getText().toString();
            mCurrentProduct.setComment(c);            
            sQuotation.update(mCurrentProduct, sCustomer.getCustomerInfo().getUserId());
            String msg = sCtx.getString(R.string.str_quotation_added_product);
            Toast.makeText(sCtx, msg, Toast.LENGTH_LONG).show();
            getActivity().finish();
        }
    };

    @Override
    public void onCartUpdated() {
        mAdapter.refreshData(sQuotation.getItem(mProductId));
    }

}
