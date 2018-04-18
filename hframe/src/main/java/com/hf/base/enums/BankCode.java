package com.hf.base.enums;

public enum BankCode {
    ICBC("中国工商银行"),
    ABC("中国农业银行"),
    BOC("中国银行"),
    CCB("中国建设银行"),
    CMBC("民生银行"),
    CMB("招商银行"),
    HXB("华夏银行"),
    CEB("中国光大银行"),
    CNCB("中信银行"),
    BEA("东亚银行");

    private String bank;

    BankCode(String bank) {
        this.bank = bank;
    }

    public String getBank() {
        return bank;
    }

    public static BankCode parse(String code) {
        for(BankCode bankCode:BankCode.values()) {
            if(bankCode.name().equals(code)) {
                return bankCode;
            }
        }
        return null;
    }

}
