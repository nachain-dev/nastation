package org.nastation.common.util;

import java.util.regex.Pattern;

public class RegexpUtil {

    /**
     * ip
     */
    public static final String IP_REGEX_PATTERN = "\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}";

    /**
     * number only
     */
    public static final Pattern NUMBER_PATTERN = Pattern.compile("^[0-9]*$");

    /**
     * email
     */
    public static final Pattern Email_PATTERN = Pattern.compile("^([a-zA-Z0-9._-])+@([a-zA-Z0-9_-])+\\.([a-zA-Z0-9_-])+");


    /**
     * digit and letter
     */
    public static final Pattern NumberAndChar_PATTERN = Pattern.compile("[0-9a-zA-Z]{6,30}$");

    /**
     * digit only
     */
    public static final Pattern Number_PATTERN = Pattern.compile("[0-9][0-9][0-9]*$");

    /**
     * password 8+
     * ^(?=.*\d)(?=.*[a-z])(?=.*[A-Z]).{8,20}$
     * ^(?=.*[A-Za-z])(?=.*\d)(?=.*[@$!%*#?&])[A-Za-z\d@$!%*#?&]{8,}$
     */
    public static final Pattern Password_PATTERN = Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])[A-Za-z0-9@#$%^&*]{8,20}$");

    public static final Pattern WalletName_PATTERN = Pattern.compile("^[a-z0-9A-Z]{1,50}$");


}
