package com.dipacommerce.dipasport.database;

import java.util.ArrayList;

import android.os.Bundle;

public interface IBarcodeHistory {
    /**
     * 
     * @param history
     */
    public void addHistory(Bundle history, String customerId);

    /**
     * 
     * @param history
     * @param customerId
     */
    public int updateHistory(Bundle history, String customerId);

    /**
     * 
     * @return
     */
    public Bundle getHistory(String code, String customerId);

    /**
     * 
     * @return
     */
    public ArrayList<Bundle> getHistories(String customerId);

    /**
     * 
     * @param bundle
     */
    public void removeHistory(Bundle bundle, String customerId);

    public void removeHistory(String customerId);

    /**
     * 
     */
    public void removeAll(String customerId);
}
