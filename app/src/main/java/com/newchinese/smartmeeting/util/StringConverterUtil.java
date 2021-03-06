package com.newchinese.smartmeeting.util;

import org.greenrobot.greendao.converter.PropertyConverter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Description:   用于GreenDao数据库存储List集合转换
 * author         xulei
 * Date           2017/8/19
 */

public class StringConverterUtil implements PropertyConverter<List<String>, String> {
    @Override
    public List<String> convertToEntityProperty(String databaseValue) {
        if (databaseValue == null) {
            return null;
        } else {
            if (!",".equals(databaseValue)) {
                String[] databaseValues = databaseValue.split(",");
                List<String> list = new ArrayList<>();
                for (String value : databaseValues) {
                    list.add(value);
                }
                return list;
            } else {
                return null;
            }
        }
    }

    @Override
    public String convertToDatabaseValue(List<String> entityProperty) {
        if (entityProperty == null) {
            return null;
        } else {
            StringBuilder sb = new StringBuilder();
            for (String link : entityProperty) {
                sb.append(link);
                sb.append(",");
            }
            return sb.toString();
        }
    }
}
