package com.dipacommerce.dipasport;

public interface IDiPaSport {
    public interface JSON_STATUS_TAG {
        public static final String ERROR_CODE = "ErrorCode";
        public static final String DATA = "DATA";
        public static final String MESSAGE = "Message";
    }

    public enum CODE {
        OK(0),
        ERROR(1);

        private int value;

        private CODE(int v) {
            value = v;
        }

        public int getStatusCode() {
            return value;
        }
    }

    interface STATUS_CODE {
        public static final CODE OK = CODE.OK;
        public static final CODE ERROR = CODE.ERROR;
    }

    public interface Prefs {
        public static final String KEYWORD = "keyword";
        public static final String INDEX_SEARCH = "search";
        public static final String INDEX_SEARCH_CATEGORY = "searchcat";
    }

    public abstract interface OnRequestCallback<T, RE> {
        public RE getData(final T t);

        public CharSequence getMessage(final T t);

        public CODE getStatus(final T t);
    }

    public static final int SEARCH_METHODS = 100;

}
