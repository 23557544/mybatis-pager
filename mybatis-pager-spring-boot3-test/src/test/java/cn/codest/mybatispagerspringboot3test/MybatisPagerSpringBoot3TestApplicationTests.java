package cn.codest.mybatispagerspringboot3test;

import cn.codest.mybatispager.interceptor.MyBatisPaginationInterceptor;
import cn.codest.mybatispager.model.Pager;
import cn.codest.mybatispagerspringboot3test.mapper.UserMapper;
import cn.codest.mybatispagerspringboot3test.model.User;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@SpringBootTest
@MapperScan("cn.codest.mybatispagerspringboot3test.mapper")
class MybatisPagerSpringBoot3TestApplicationTests {

    private static final Logger log = LoggerFactory.getLogger(MybatisPagerSpringBoot3TestApplicationTests.class);

    @Autowired
    private UserMapper userMapper;

    @Test
    void testSelectPageWithoutWhere() {
        Pager<User> pager = Pager.of(1);
        userMapper.selectPage(null, pager);
        log.info("result = [{}]", pager);
    }

    @Test
    void testSelectPageWithWhere() {
        Pager<User> pager = Pager.of(1);
        userMapper.selectPage("郭", pager);
        log.info("result = [{}]", pager);
    }

}
