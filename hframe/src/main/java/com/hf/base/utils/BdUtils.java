package com.hf.base.utils;

import java.math.BigDecimal;

public class BdUtils {

    public static BigDecimal min(BigDecimal b1,BigDecimal b2) {
        return b1.compareTo(b2)>0?b2:b1;
    }

    public static BigDecimal max(BigDecimal b1,BigDecimal b2) {
        return b1.compareTo(b2)>0?b1:b2;
    }
}
