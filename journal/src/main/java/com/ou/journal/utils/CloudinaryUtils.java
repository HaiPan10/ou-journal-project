package com.ou.journal.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CloudinaryUtils {
    public static Matcher parseUrl(String imageUrl) {
        Pattern pattern = Pattern.compile(".*/v\\d+/(.*?)(\\.\\w+)$");
        return pattern.matcher(imageUrl);
    }
    
    public static String getPublicId(String imageUrl) {
        Matcher matcher = parseUrl(imageUrl);
        if (matcher.find()) {
            return matcher.group(1);
        }        
        return null;
    }

    public static String getImageType(String imageUrl) {
        Matcher matcher = parseUrl(imageUrl);
        if (matcher.find()) {
            return matcher.group(2).substring(1);
        }        
        return null;
    }
}
