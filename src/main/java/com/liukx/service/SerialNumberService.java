package com.liukx.service;

/**
 * Created by liukx on 2020/3/18 0018.
 */
public interface SerialNumberService {

    String getSystemSerialNumber(String prefix, String dateStr, int suffixLength);

}
