package test;

import java.util.Map;
import java.util.TreeMap;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hf.core.biz.service.CacheService;
import com.hfb.merchant.code.sercret.CertUtil;
import com.hfb.merchant.code.util.ResponseUtil;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class HfbCallBackTest extends BaseTestCase {
    @Autowired
    private CacheService cacheService;

    @Test
    public void testCallBack() {
        String msg = "{\"YUL1\":\"\",\"YUL2\":\"\",\"YUL3\":\"\",\"amount\":\"100\",\"channelNo\":\"05\",\"ext1\":\"\",\"ext2\":\"\",\"merchantNo\":\"S20180412032071\",\"paySerialNo\":\"805149792128519380992\",\"remark\":\"测试,测试\",\"rtnCode\":\"0000\",\"rtnMsg\":\"交易成功\",\"settDate\":\"20180514\",\"sign\":\"AH/wFBGO8/YRnQW9fvxhYLTDKkwWjx8WyoHz2daylr4s5c4Jx/a9X64urBOLAZDmcLb3GsWvftUzqTiOqdwubTefELuy0RjoBoBDt1ZImiuBup0UXk8hdXRXhLtnXPAI0sMux5irA04NZOnk02BwKJD2P6IVh5WHcz9ilEwSIzQ\\u003d\",\"tranCode\":\"YS1003\",\"tranFlow\":\"5132_5932489163697196032\",\"version\":\"v1\"}";
        Map<String,String> map = new Gson().fromJson(msg,new TypeToken<Map<String,String>>(){}.getType());
        TreeMap<String, String> transMap = new TreeMap<String, String>();
        for(String key:map.keySet()) {
            if(StringUtils.isEmpty(map.get(key))) {
                continue;
            }
            transMap.put(key,map.get(key));
        }

        // 私钥文件路径
        String privateKey = cacheService.getRootPath()+"/certs/CS20180409031247_20180411131244514.pfx";
        // 公钥文件路径
        String publicKey = cacheService.getRootPath()+"/certs/SS20180409031247_20180411131244514.cer";
        // 密钥密码
        String KeyPass = "408916";
        // 加密工具类的创建
        CertUtil certUtil = new CertUtil(publicKey, privateKey, KeyPass, true);
        try {
            Map<String,String> resultMap = ResponseUtil.parseResponse(transMap, certUtil);
            System.out.println(new Gson().toJson(resultMap));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
