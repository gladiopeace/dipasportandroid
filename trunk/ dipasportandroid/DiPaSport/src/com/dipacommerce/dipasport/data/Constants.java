package com.dipacommerce.dipasport.data;

public class Constants {

    public class Query {
        private static final String HOST_TEST = "http://testdipacommerce.no-ip.org:8000/services/";
        //private static final String HOST_FREE = "http://www.marktestvn123.byethost9.com/services/service.php";
        private static final String HOST_PRIMARY = "http://www.dipacommerce.com/services/";

        private static final String HOST = HOST_PRIMARY;

        public static final String URL_LOGIN = HOST + "service.php?op=login&email=%s&password=%s";
        public static final String URL_REGISTER = HOST + "service.php?op=register&custype=%s&firstname=%s&lastname=%s&email=%s&password=%s&" +
                "company=%s&taxvat=%s&street=%s&city=%s&region=%s&postcode=%s&country=%s&telephone=%s&fax=%s&newsletter=%s&client=%s";
        public static final String URL_LOST_PASSWORD = HOST + "/service.php?op=forgetpass&email_change=%s";

        public static final String URL_HOMEPAGE_CATEGORIES = HOST + "service.php?op=getListCate";
        public static final String URL_HOMEPAGE_PRODUCTS_BY_CATEGORIE = HOST + "service.php?op=listProductBycate&category=%s&page=%d&idUser=%s";

        public static final String URL_HOMEPAGE_PRODUCTS = HOST + "service.php?op=feartureproduct";

        public static final String URL_PRODUCT_DETAIL = HOST + "service.php?op=getViewProduct&entityid=%s&idUser=%s";

        public static final String URL_SEARCH = HOST + "service.php?op=searchPage&qsearch=%s&page=%d&idUser=%s";

        public static final String URL_ORDER = HOST + "service.php?op=checkOut&email=%s&idUse=%s&order=%s";
        public static final String URL_COUPON = HOST + "service.php?op=getcounpon&couponCode=%s";
        public static final String COUPON = "&couponCode=";
        public static final String URL_QUOTATION = HOST + "service.php?op=quote&email=%s&idUse=%s&list=%s";
        
        public static final String URL_REG_PUSH_SERVICE = HOST + "push/index.php/register/add_device?type=regdevice";
    }

    public static final int PRODUCT_FEATURES_WIDTH = 70; // pixels
    public static final int PRODUCT_FEATURES_HEIGHT = 70; // pixels

    public static final int PRODUCT_DETAIL_WIDTH = 120; // pixels
    public static final int PRODUCT_DETAIL_HEIGHT = 120; // pixels

    public static final String DOUBLE_ZERO = ".00";
    public static final int TAX = 22; // percent

    public static final String TAB_HOME = "home";
    public static final String TAB_SEARCH = "search";
    public static final String TAB_ACCOUNT = "account";
    public static final String TAB_LOGIN = "login";
    public static final String TAB_SHOPPINGCART = "shoppingcart";
    public static final String TAB_CONTACT = "contact";

    public static final String BUNDLE_KEYWORD = "Keyword";
    public static final String BUNDLE_CATEGORY = "Category";
    public static final String BUNDLE_INDEX = "SearchIndex";
    
    public static final int REQUEST_TIME_OUT = 40000; // 40s

    // public static final String MAPS_URL =
    // "https://www.google.com/maps/preview?ll=44.972823,9.855574&z=14&t=m&hl=itIT&gl=US&mapclient=embed&q=Strada+Provinciale+Chiusa,+34+29010+Roveleto+PC+Italia&source=newuser-ws";
    public static final String MAPS_URL = "https://www.google.com/maps?t=m&ll=44.972823,9.855574&z=14&q=Strada+Provinciale+Chiusa,+34&output=classic&dg=opt";
    public static final String FACEBOOK_URL = "https://facebook.com/dipasportsrl";
    public static final String FACEBOOK_ID = "245518332169425";

    public static final String TWITTER_URL = "https://twitter.com/dipasport";
    public static final String YOUTUBE_URL = "http://youtube.com/user/dipavideo";
    public static final String EMAIL_ORDER = "order@dipasport.com";
    public static final String EMAIL_INFO = "info@dipasport.com";
    public static final String EMAIL_CONTACT = "contact@dipasport.com";
    public static final String EMAIL_CONTACT_TEST = "marktestvn@gmail.com";

    // Google Maps
    public static final String GOOGLE_MAP_URL = "https://www.google.com/maps/place/44%C2%B058%2718.4%22N+9%C2%B051%2718.1%22E/@44.9717755,9.855024,15z/data=!3m1!4b1!4m2!3m1!1s0x0:0x0";
    public static final float GOOGLE_MAP_LAT = 44.971776f;
    public static final float GOOGLE_MAP_LNG = 9.855024f;
    public static final int GOOGLE_ZOOM_LEVEL = 13;
    
    public static final String DEVICE_PHONE = "phone";
    public static final String DEVICE_TABLET = "tablet";
    
    // Push Message (GCM)
    public static final String SENDER_ID = "633291002426";
    public static final String PUSH_API_KEY = "AIzaSyBWoTZhOqIdiROPE7prUkmgYLxqyR_u8lQ";
    public static final String EXTRA_MESSAGE = "message";
    public static final String PROPERTY_REG_ID = "registration_id";
    
    // Image scale
    public static final int IMAGE_WIDTH = 800;
    public static final int IMAGE_HEIGHT = 600;
    
    /**
     * Switch to false in release mode
     */
    public static final boolean DEBUG_MODE = false;
}
