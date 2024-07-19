package cn.codest.mybatispager.spring.boot;

import cn.codest.mybatispager.interceptor.MyBatisPaginationInterceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScannerRegistrar;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

import javax.sql.DataSource;

/**
 * Mybatis-Pager自动装配
 * @author changxy
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass({DataSource.class, SqlSessionFactory.class, SqlSessionFactoryBean.class})
@AutoConfigureAfter({DataSourceAutoConfiguration.class})
@AutoConfigureOrder(Ordered.LOWEST_PRECEDENCE + 10)
public class MybatisPaginationAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public MyBatisPaginationInterceptor myBatisPaginationInterceptor() {
        return new MyBatisPaginationInterceptor();
    }

}
