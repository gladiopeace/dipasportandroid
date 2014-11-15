package com.dipacommerce.dipasport.customer;

import com.dipacommerce.dipasport.IDiPaSport;

public class CustomerInfo {
    private String email = "";
    private String password = "";
    private String userId = "";
    private String fullname = "";
    private int groupId = 0;
    private String company = "";
    private String telephone;


    /**
     * 
     */
    public interface BUNDLE {
        public static final String Email = "EMAIL";
    }

    /**
     * 
     */
    public interface Prefs {
        public static final String LOGIN = "login";
        public static final String INFO = "info";
    }

    /**
     * 
     */
    public interface JSON_TAG extends IDiPaSport.JSON_STATUS_TAG {
        public static final String LoginTag = "Login";
        public static final String UserID = "userId";
        public static final String FullName = "user";
        public static final String GroupId = "groupId";
        public static final String Company = REGISTER_INFO.company;
        public static final String Telephone = REGISTER_INFO.telephone;
    }

    /**
     * 
     */
    public interface REGISTER_INFO {
        public static final String customer_type = "customertype";
        public static final String first_name = "firstname";
        public static final String last_name = "lastname";
        public static final String company = "company";
        public static final String taxvat = "taxvat";
        public static final String streetaddress = "street";
        public static final String city = "city";
        public static final String stateprovince = "stateprovince";
        public static final String postalcode = "postalcode";
        public static final String country = "country";
        public static final String telephone = "telephone";
        public static final String fax = "fax";
        public static final String email = "email";
        public static final String password = "password";
        public static final String newsletter = "newsletter";
        public static final String client = "client";
    }
    public String getFullname() {
        return fullname;
    }
    
    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * 
     * @return
     */
    public String getEmail() {
        return email;
    }

    /**
     * 
     */
    public String getEmailWithoutAt() {
        String[] tmp = email.split("@");
        return tmp[0];
    }

    /**
     * 
     * @param name
     */
    public void setEmail(String name) {
        this.email = name;
    }

    /**
     * 
     * @return
     */
    public String getPassword() {
        return password;
    }

    /**
     * 
     * @param password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    public int getGroupId() {
        return groupId;
    }
    
    public boolean priceIsAvailable(){
        boolean g0 = (groupId == CustomerGroup.NOT_LOGGED_IN) ? false: true;
        boolean g16 = (groupId == CustomerGroup.NOPREZZI) ? false: true;
        return (g0 && g16);
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }
    
    
    

}