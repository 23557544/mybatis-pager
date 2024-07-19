package cn.codest.mybatispagerspringboot2test;

import cn.codest.mybatispager.model.Pager;
import cn.codest.mybatispagerspringboot2test.mapper.UserMapper;
import cn.codest.mybatispagerspringboot2test.model.User;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@MapperScan("cn.codest.mybatispagerspringboot2test.mapper")
class MybatisPagerSpringBoot2TestApplicationTests {

	private static final Logger log = LoggerFactory.getLogger(MybatisPagerSpringBoot2TestApplicationTests.class);

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
