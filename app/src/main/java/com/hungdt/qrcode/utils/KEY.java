package com.hungdt.qrcode.utils;

public interface KEY {
    String RESULT_TEXT = "text";
    String TYPE_SCAN_CAMERA = "Scan Camera";
    String TYPE_SCAN_GALLERY = "Scan Image";
    String TYPE_GENERATE = "Generate Code";
    String RESULT_TYPE_CODE = "result type code";
    String RESULT_TYPE_TEXT = "result type text";
    String TYPE_CREATE = "type";
    String TYPE_VIEW = "type view";
    String SAVED = "Saved";
    String HISTORY = "History";
    String FAVORITE = "Favorite";
    String CODE_ID = "Code id";
    String TYPE_RESULT = "type result";
    String DELETE = "delete";
    String UPDATE = "update";
    String CAMERA = "camera";
    String STORAGE = "storage";
    String TYPE = "type";
    String TYPE_DETAIL = "type detail";
    String LINK_PATTERN = "^(http:\\/\\/www\\.|https:\\/\\/www\\.|http:\\/\\/|https:\\/\\/)?[a-z0-9]+([\\-\\.]{1}[a-z0-9]+)*\\.[a-z]{2,5}(:[0-9]{1,5})?(\\/.*)?$";
    String PHONE_PATTERN = "^[+]*[(]{0,1}[0-9]{1,4}[)]{0,1}[-\\s\\./0-9]*$";
    String EMAIL_PATTERN = "^(Email:|email:|EMAIL:)?\\s*[a-z][a-z0-9_\\.]{5,32}@[a-z0-9]{2,}(\\.[a-z0-9]{2,4}){1,2}\\s*\\n.*\\s*\\n.*\\s*$";
    String ADDRESS_PATTERN = "^\\d+\\s[A-z]+\\s[A-z]+$";
    String WIFI_PATTERN = "^(WIFI:S:)[0-9A-Za-z]*(;T:)(WPA|WEP|nopass)(;P:)[0-9A-Za-z]*(;H:)(true|false);;$";
    String SMS_PATTERN = "^.*[+]*[(]{0,1}[0-9]{1,4}[)]{0,1}[-\\s\\./0-9]*(:|\\n)?.*$";
    String CALENDAR_PATTERN = "^\\s*(BEGIN:)[A-Z]*(\\n\\s*.*)*\\n(\\s)*END:[A-Z]*(\\s)*\\n*(\\s)*$";

    String GOOD = "Good";
    String LINK = "Link";
    String PHONE = "Phone";
    String EMAIL = "Email";
    String ADDRESS = "Address";
    String WIFI = "Wifi";
    String CALENDER = "Calender";
    String SMS = "SMS";
    String TEXT = "Text";
}
