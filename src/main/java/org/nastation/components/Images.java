package org.nastation.components;

import com.vaadin.flow.component.html.Image;

/**
 * @author John | NaChain
 * @since 01/05/2022 10:57
 */
public class Images {

    public static final String S3_URL = "https://nn-app-files.s3.us-east-2.amazonaws.com";

    public static Image nac_144x144() {
        return new Image(S3_URL + "/images/nac_144x144.png", "");
    }

    public static Image nac_72x72() {
        return new Image(S3_URL + "/images/nac_72x72.png", "");
    }

    public static Image nac_48x48() {
        return new Image(S3_URL + "/images/nac_48x48.png", "");
    }

    public static Image nomc_72x72() {
        return new Image(S3_URL + "/images/nomc_72x72.png", "");
    }

    public static Image usdn_72x72() {
        return new Image(S3_URL + "/images/usdn_72x72.png", "");
    }

    public static Image token_common_72x72() {
        return new Image(S3_URL + "/images/token_common_72x72.png", "");
    }

    public static Image warn() {
        return new Image(S3_URL + "/images/icons/icons-warn.png", "");
    }

    public static Image success() {
        return new Image(S3_URL + "/images/icons/icons-success.png", "");
    }

    public static Image unlock() {
        return new Image(S3_URL + "/images/icons/icons-unlock.png", "");
    }
}
