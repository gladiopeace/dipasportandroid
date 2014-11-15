package com.dipacommerce.dipasport.customer;


public abstract class Customers {

    private static CustomerManager sCustomerAction;

    public static CustomerManager getInstance() {

        if (sCustomerAction == null) {
            sCustomerAction = new CustomerActionsImp();
        }

        return sCustomerAction;
    }

}
