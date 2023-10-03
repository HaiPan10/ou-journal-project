package com.ou.journal.components;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.Formatter;
import org.springframework.stereotype.Component;

@Component
public class DateFormatter implements Formatter<Date>{

    @Autowired
    private SimpleDateFormat simpleDateFormat;

    @Override
    public String print(Date object, Locale locale) {
        System.out.println("[DEBUG] - Calling print method");
        System.out.println("[DEBUG] - Date: " + simpleDateFormat.format(object));
        return simpleDateFormat.format(object);
    }

    @Override
    public Date parse(String text, Locale locale) throws ParseException {
        System.out.println("[DEBUG] - Calling parse method");
        return simpleDateFormat.parse(text);
    }
    
}
