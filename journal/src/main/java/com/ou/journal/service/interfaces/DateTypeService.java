package com.ou.journal.service.interfaces;

import com.ou.journal.pojo.DateType;

public interface DateTypeService {
    DateType retrieve(String typeName) throws Exception;
}
