package com.ou.journal.pojo;

import java.io.Serializable;

import org.thymeleaf.context.Context;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class MailRequest implements Serializable {
    private String to;
    private String subject;
    private String body;
    private Context context;
}
