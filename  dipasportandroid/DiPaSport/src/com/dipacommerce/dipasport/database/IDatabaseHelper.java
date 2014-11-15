package com.dipacommerce.dipasport.database;

import java.util.ArrayList;

import com.dipacommerce.dipasport.customer.CustomerInfo;

interface IDatabaseHelper {
    // TABLE EMAIL

    /**
     * Add new an email
     * 
     * @param customer
     */
    public void addEmail(CustomerInfo customer);

    /**
     * Delete an email
     * 
     * @param customer
     */
    public void deleteEmail(CustomerInfo customer);

    /**
     * Delete all emails in the table
     */
    public void deleteAllEmail();

    /**
     * Get the list of mails in table
     * 
     * @return
     */
    public ArrayList<CustomerInfo> getEmails();

    /**
     * Check an email already in the table
     * 
     * @param customer
     * @return
     */
    public boolean isEmailExist(CustomerInfo customer);

    


}
