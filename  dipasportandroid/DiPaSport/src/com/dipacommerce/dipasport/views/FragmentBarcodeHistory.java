package com.dipacommerce.dipasport.views;

import java.util.List;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

import com.dipacommerce.dipasport.DiPaSport;
import com.dipacommerce.dipasport.R;
import com.dipacommerce.dipasport.barcode.HistoryAdapter;
import com.dipacommerce.dipasport.customer.CustomerManager;

public class FragmentBarcodeHistory extends ListFragment {

    private String customerId = "";
    private HistoryAdapter mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CustomerManager customer = DiPaSport.getCustomerManager();
        customerId = customer.getCustomerInfo().getEmail();
        List<Bundle> list = DiPaSport.getDatabaseHandler().getHistories(customerId);
        mAdapter = new HistoryAdapter(getActivity(), list);
        setListAdapter(mAdapter);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_barcode_history, container, false);
        Button removeAll = (Button) rootView.findViewById(R.id.history_remove_all);
        removeAll.setOnClickListener(OnRemoveAll);
        return rootView;
    }

    private OnClickListener OnRemoveAll = new OnClickListener() {

        @Override
        public void onClick(View v) {

            final AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
            dialog.setMessage(getString(R.string.msg_history_remove_all));
            dialog.setPositiveButton(getString(R.string.str_dialog_ok), new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    DiPaSport.getDatabaseHandler().removeAll(customerId);
                    mAdapter.refresh();
                }
            });
            dialog.setNegativeButton(getString(R.string.str_dialog_cancel), null);
            dialog.create().show();
        }
    };

}
