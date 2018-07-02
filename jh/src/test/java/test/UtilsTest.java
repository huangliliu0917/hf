package test;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hf.base.utils.EpaySignUtil;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.RandomUtils;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.URLDecoder;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class UtilsTest {

    private String prikeyValue = "MIIEpAIBAAKCAQEAtLnOdyl9oKIX/LGyMswd0hr3blb+aS/OawATrroereTXTPrK\n" +
            "6bNJv6tr6KCbCJemuYQgNcgcNbrJe6BAdb5OhxQyMGCaVEw2Y76MxzfAuS2r7lLX\n" +
            "Ni+DmNFtM1ALSbipu6rua9XbDCA03ZJzslavvfeyuaeu7QTOEQGD35IVXIM9bZR5\n" +
            "ONSuJEGlEhCXzyII7ds34R3TE4k/pVKbJ4cqMxTuwb5izL/xsOrV/Ht4eDLan7nN\n" +
            "8zPdVzDCO4nj7t9Ijl7ZnxufdePKusaeTTQjmwAj9MsQptK4Bv81bDoI7SFw03EV\n" +
            "4ZIO7qkf+2pYcrVLUsiebc2yt8mw9/tazYpe/wIDAQABAoIBAQCI2L1PG/rFnJff\n" +
            "P0q5DjhydPrw8SyZx5pdCWTeBI7gjAy/fJQTnC/2073VG2/pdLPJfBPLxagewz53\n" +
            "vyOwRJc/z3olibCyrjbtFkeRPlVPoxayUsYlgJr8J3CxzyWNACh+M3Nv8jJ4nxaI\n" +
            "xLGY0+0lQp9x11gsn1vOIsCRlRNZxbtB6TGH+vzH5vanYkxTByTOYyl4HYpVEQDP\n" +
            "vuVOD2orxSJA1xGYtThNwLxrF39MmDyTP8N15CwRouoLU0omC7+Qq3C+y4Ejox6N\n" +
            "xJBK4DapzAIKy4K3XgMXR1uPqfDlD5f61waeh5UvY8029Kgn8GjXdymy2E86xASr\n" +
            "ohWP1LkZAoGBAO7KFI8awRCCeF1n+jCloNmsfj0FYezS9NLpmYg/CAaFFetG5eUt\n" +
            "RKyoYTt76rjNdGjCtarcjs/xOtZXWBEgxXiKfv6HwVaWAPLTA86FvEOfV9BZuP4X\n" +
            "pCNQv3sno6Z07mPeKRzhemXyzDlPyQxsb4csvePGAaDJ1ek6k0b/MNg9AoGBAMHA\n" +
            "ZCNKVJHuwVZ9Gp5/aj/6IIQBftwHnvbuRPDwPcRrGMALrJp9/gRAKyrrrk+mk/43\n" +
            "7mETuhBa7nK6XcHwiR5TucxLfYIv1t0poGR92zaKZ8QzSQPbkBvWTtFOeAnRQv2i\n" +
            "dAPjMpMPPoFpaJuIRhT/CXrY+uUkMIA8sCyZYkvrAoGAK06/N80UYtgm2Fn5SEVh\n" +
            "zNi59Hs7bWY9PNtdGxbDb9tHRGqRW2VAZUgMimtJAMdSa4WUyS5DQHdxwloJAOI+\n" +
            "rkQAEE2yxO9jsKaQtC4RHPqTRJhhMsQ64qTMdZuU1KW0bqxmLHTAbCkC3QoZXoV0\n" +
            "HMICloLc3Lp+b1ROTbwOsckCgYAtrckuFMkpequ0U1xiP9Hx8WuXE68v+s/8kaJJ\n" +
            "V6qIU2OLa3UvG0M3B1XmEZiQCMrdZZxa4Ma+MmIDRHL0VVxOfRjR1H5rohG7JKQ+\n" +
            "7Pkwu6LJO/ob4bjxBy6f5CsizWZI2/MUM41p5G8tHYffG1rCenpmrx8/xK92nFhA\n" +
            "u4zULwKBgQDX1aPGMlmExas3UEChC9/LVmcRRvOKwcitv6GNe+2O0W6kGH19bHs9\n" +
            "uHzCyAq0uqtrflFX/Eksq+CWCTcug8sH/EPLTIX7RZZtHS/6cpPcNizFswCLBbaR\n" +
            "ee+PJ/NGVyv1yQTb2y0+AQx4VCIrdICrZRd7xINnwPy5aKXujR+qHQ==";

    @Test
    public void testEncrypt() {
        String url = "test123452211";
        String sign = EpaySignUtil.sign(prikeyValue,url);
        System.out.println(sign);
    }

    @Test
    public void readFromFile() throws Exception {
        File file = new File("/Users/tengfei/Downloads/GALAXY_19997867_20180521_001609.txt_19997867_20180521_001609.txt");
        FileInputStream fileInputStream = new FileInputStream(file);
        BufferedReader tmp_reader=new BufferedReader(new InputStreamReader(fileInputStream));
        String minLine = "";
        int minLength = 0;
        String maxLine = "";
        int maxLength = 0;
        int tempLength = 0;
        int count = 0;
        String line = tmp_reader.readLine();
        minLength = line.length();
        minLine = line;
        while (line != null) {
            line = line.replace("lch=push&pushid=139443&msgId=88255504581&","");
            String url = line.substring(line.indexOf("uri")+10,line.indexOf("act%3D2")+7);
            tempLength = url.length();
            if(tempLength > maxLength) {
                maxLength = tempLength;
                maxLine = url;
            }
            if(tempLength <= minLength) {
                minLength = tempLength;
                minLine = url;
            }
            System.out.println(url);
            line = tmp_reader.readLine();
            count++;
        }

        System.out.println("max count:"+count);
        System.out.println("max length:"+maxLength);
        System.out.println("max url:"+maxLine);
        System.out.println("min length:"+minLength);
        System.out.println("min url:"+minLine);
    }

    @Test
    public void testParseStr() {
        String str = "amount=10.00&body=??&channel=qqQr&mchId=512004&outChannelNo=5120045187_P1805210957066342&outTradeNo=5187_P1805210957066342&resultCode=0&returnCode=0&status=02&transTime=20180521095709&sign=E3B3AE30AF09010041A414BB37B8C77B";
        Map<String,String> map = new Gson().fromJson(str,new TypeToken<Map<String,String>>(){}.getType());
        System.out.println(map);
    }

    @Test
    public void testRandom() {
        for(int i=0;i<100;i++) {
            System.out.println(RandomUtils.nextInt(0,3));
        }
    }

    @Test
    public void testMaxInt() {
        System.out.println(Integer.MAX_VALUE);
    }
}
