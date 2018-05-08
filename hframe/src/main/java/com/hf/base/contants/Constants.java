package com.hf.base.contants;

import com.hf.base.utils.MapUtils;

import java.util.*;

public class Constants {
    public static final String USER_LOGIN_INFO = "user_login_info";

    public static final String CURRENT_USER_ID = "current_user_id";

    public static final String SUCCESS = "0000000";

    public static final String USER_VARIFIED_FAILED = "9000001";

    public static final String PAGE_DATE_IS_GET = "pageDataIsGet";

    //1:普通商户;2:代理商;3:分公司;10:总部
    public static final int GROUP_TYPE = 3;

    public static final int GROUP_TYPE_COMPANY = 3;

    public static final int GROUP_TYPE_AGENT = 2;

    public static final int GROUP_TYPE_CUSTOMER = 1;

    public static final List<String> bankNames = Arrays.asList("北京银行","东亚银行","中国工商银行","中国光大银行","广发银行","华夏银行","中国建设银行","交通银行","中国民生银行","南京银行","宁波银行","中国农业银行","平安银行","上海银行","上海浦东发展银行","深圳发展银行","兴业银行","中国邮政储蓄银行","招商银行","浙商银行","中国银行","中信银行");

    public static final Integer pageSize = 100;

    public static final String SETTLE_FEE_RATE = "settle_fee_rate";

    public static final List<Map<String,String>> bankCodeList = new ArrayList<>();
    public static final Map<String,String> bankMap = new HashMap<>();

    static {
        bankCodeList.add(MapUtils.buildMap("ICBC","中国工商银行"));
        bankCodeList.add(MapUtils.buildMap("ABC","中国农业银行"));
        bankCodeList.add(MapUtils.buildMap("BOC","中国银行"));
        bankCodeList.add(MapUtils.buildMap("CCB","中国建设银行"));
        bankCodeList.add(MapUtils.buildMap("BOCOM","交通银行"));
        bankCodeList.add(MapUtils.buildMap("CMB","招商银行"));
        bankCodeList.add(MapUtils.buildMap("CEB","光大银行"));
        bankCodeList.add(MapUtils.buildMap("CMBC","民生银行"));
        bankCodeList.add(MapUtils.buildMap("PSBC","中国邮政储蓄银行"));
        bankCodeList.add(MapUtils.buildMap("SPDB","浦发银行"));
        bankCodeList.add(MapUtils.buildMap("CNCB","中信银行"));
        bankCodeList.add(MapUtils.buildMap("PAB","平安银行"));
        bankCodeList.add(MapUtils.buildMap("HXB","华夏银行"));
        bankCodeList.add(MapUtils.buildMap("CIB","兴业银行"));
        bankCodeList.add(MapUtils.buildMap("BOHC","渤海银行"));
        bankCodeList.add(MapUtils.buildMap("BCCB","北京银行"));
        bankCodeList.add(MapUtils.buildMap("GDB","广发银行"));
        bankCodeList.add(MapUtils.buildMap("BOS","上海银行"));
        bankCodeList.add(MapUtils.buildMap("ZSBC","浙商银行"));
        bankCodeList.add(MapUtils.buildMap("NBBC","宁波银行"));
        bankCodeList.add(MapUtils.buildMap("NJBC","南京银行"));
        bankCodeList.add(MapUtils.buildMap("BRCB","北京农村商业银行"));
        bankCodeList.add(MapUtils.buildMap("ZHTLCB","浙江泰隆商业银行"));
        bankCodeList.add(MapUtils.buildMap("BEA","东亚银行"));
        bankCodeList.add(MapUtils.buildMap("HZB","杭州银行"));

        bankMap.put("ICBC","中国工商银行");
        bankMap.put("ABC","中国农业银行");
        bankMap.put("BOC","中国银行");
        bankMap.put("CCB","中国建设银行");
        bankMap.put("BOCOM","交通银行");
        bankMap.put("CMB","招商银行");
        bankMap.put("CEB","光大银行");
        bankMap.put("CMBC","民生银行");
        bankMap.put("PSBC","中国邮政储蓄银行");
        bankMap.put("SPDB","浦发银行");
        bankMap.put("CNCB","中信银行");
        bankMap.put("PAB","平安银行");
        bankMap.put("HXB","华夏银行");
        bankMap.put("CIB","兴业银行");
        bankMap.put("BOHC","渤海银行");
        bankMap.put("BCCB","北京银行");
        bankMap.put("GDB","广发银行");
        bankMap.put("BOS","上海银行");
        bankMap.put("ZSBC","浙商银行");
        bankMap.put("NBBC","宁波银行");
        bankMap.put("NJBC","南京银行");
        bankMap.put("BRCB","北京农村商业银行");
        bankMap.put("ZHTLCB","浙江泰隆商业银行");
        bankMap.put("BEA","东亚银行");
        bankMap.put("HZB","杭州银行");
    }
}
