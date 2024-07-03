package com.alan;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import com.alan.pojo.vo.RegdVO;
import org.junit.jupiter.api.Test;
import org.redisson.api.RList;
import org.redisson.api.RedissonClient;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
class DaziFinderApplicationTests {
    @Resource
    private RedissonClient redissonClient;
    /**
     * 测试md5加密
     */
    @Test
    void testMd5() {
        final String SALT = "ysc";
        String originalPsw = "123456";
        String encryptPsw = SecureUtil.md5(SALT+originalPsw);
        System.out.println(encryptPsw);
//        // 测试加密后的密码是否和原密码一致
//        String userPsw = "123456";
//        String EUserPsw = SecureUtil.md5(SALT+userPsw);
//        System.out.println(EUserPsw);
//        System.out.println(encryptPsw.equals(EUserPsw));
    }

    /**
     * 非空测试
     */
    @Test
    void testBlank(){
        boolean blank = StrUtil.hasBlank("/t", "null", "checkPassword");
        System.out.println(blank);
    }
    @Test
    void contextLoads() {
        List<RegdVO> regdVOS=new ArrayList<>();
        regdVOS.add(new RegdVO("2021-1-1",11));
        regdVOS.add(new RegdVO("2021-1-1",11));
        regdVOS.add(new RegdVO("2021-1-1",11));
        regdVOS.forEach(System.out::println);
    }

    /**
     * redisson测试
     */
    @Test
    void testRedisson(){
        //本地内存
        List<String> list =new ArrayList<>();
        list.add("yan");
        System.out.println("list:"+list.get(0));
//        list.remove(0);
        //redis内存
        RList<String> rList = redissonClient.getList("test-list");
//        rList.add("yan");
        System.out.println("rList:"+rList.get(0));
        rList.remove(0);
    }
}
