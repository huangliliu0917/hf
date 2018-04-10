package test.cache;

import com.hf.core.biz.service.CacheService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import test.BaseTestCase;

public class CacheTest extends BaseTestCase {
    @Autowired
    private CacheService cacheService;

    @Test
    public void testGetRootPath() {
        System.out.println(cacheService.getRootPath());
    }
}
