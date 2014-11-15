package com.dipacommerce.dipasport.shoppingcart;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.dipacommerce.dipasport.R;
import com.dipacommerce.dipasport.customer.CustomerManager;
import com.dipacommerce.dipasport.customer.Customers;
import com.dipacommerce.dipasport.products.ProductInfo;
import com.dipacommerce.dipasport.views.AddToQuotationActivity;
import com.dipacommerce.dipasport.views.FragmentQuotation;

public final class AddToQuotationButton extends LinearLayout {

    private LayoutInflater mInflater = null;
    private Button mButtonAddToQuote;

    private ProductInfo mProduct;
    private ICart mShoppingCart;
    private int mProductQuantity = 1;

    private CustomerManager mCustomer;

    public AddToQuotationButton(Context context) {
        super(context);
        initViews();
    }

    public AddToQuotationButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        initViews();
    }

    public AddToQuotationButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initViews();
    }

    public void setProduct(final ProductInfo productOrder) {
        mProduct = productOrder;
    }

    void initViews() {
        mCustomer = Customers.getInstance();
        mShoppingCart = ShoppingCart.getInstance();

        if (mInflater == null) {
            mInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            mInflater.inflate(R.layout.ctrl_add_to_quote_button, this, true);
        }

        mButtonAddToQuote = (Button) findViewById(R.id.add_to_quote);
        if (mButtonAddToQuote != null) {
            if (!mCustomer.isLogin()) {
                mButtonAddToQuote.setVisibility(View.INVISIBLE);
            } else {
                mButtonAddToQuote.setOnClickListener(mAddToQuote);
            }
        }
    }

    private OnClickListener mAddToQuote = new OnClickListener() {

        @Override
        public void onClick(View v) {
            QuotationManager q = Quotation.getInstance();
            if (!q.isExist(mProduct.getCode())) {
                QuotationProduct quote = new QuotationProduct();
                quote.setProductInfo(mProduct);
                quote.setQuantity(1); // as defaults when add to quote
                quote.setComment(""); // no comment
                QuotationManager quotation = Quotation.getInstance();
                quotation.add(quote);

                Intent intent = new Intent(getContext(), AddToQuotationActivity.class);
                intent.putExtra(FragmentQuotation.PRODUCT_ID, mProduct.getEntityID());
                getContext().startActivity(intent);
            } else {
                Toast.makeText(getContext(), getContext().getString(R.string.str_product_existing_incart), Toast.LENGTH_LONG).show();
            }
        }
    };

}
