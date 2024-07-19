package cn.codest.mybatispager.interceptor;

import cn.codest.mybatispager.model.Pager;
import com.github.drinkjava2.jdialects.Dialect;
import org.apache.ibatis.binding.MapperMethod;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.springframework.util.CollectionUtils;

import javax.sql.DataSource;
import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

//@Configuration(proxyBeanMethods = false)
@Intercepts({
        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class})
})
public class MyBatisPaginationInterceptor implements Interceptor {

    public MyBatisPaginationInterceptor() {
        Dialect.setGlobalAllowShowSql(Boolean.FALSE);
    }

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        // 查找是否存在分页对象参数，是否需要分页
        Pager<?> pager = findPager(invocation);
        if (Objects.nonNull(pager)) {
            // xml文件中的SQL定义
            MappedStatement mappedStatement = (MappedStatement) invocation.getArgs()[0];
            // mapper层方法参数
            Object methodParams = invocation.getArgs()[1];
            // SQL执行器
            Executor executor = (Executor) invocation.getTarget();
            // SQL定义、SQL参数等
            BoundSql boundSql = mappedStatement.getBoundSql(methodParams);
            // 查询总数据量
            Long count = count(executor, mappedStatement, methodParams, boundSql, (ResultHandler<?>) invocation.getArgs()[3]);
            pager.setTotal(count);
            // 查询分页数据
            if (outOfBounds(count, pager.getNo(), pager.getSize())) {
                pager.setData(Collections.EMPTY_LIST);
                return Collections.EMPTY_LIST;
            }
            List data = selectPageData(pager, executor, mappedStatement, methodParams, boundSql, (ResultHandler<?>) invocation.getArgs()[3]);
            pager.setData(data);
            return data;

        }

        return invocation.proceed();
    }

    protected Boolean outOfBounds(Long count, Integer pageNo, Integer pageSize) {
        return count == 0 || (pageNo - 1)*pageSize >= count;
    }

    /**
     * 获取Mapper层接口方法Pager参数
     * @param invocation
     * @return
     */
    protected Pager<?> findPager(Invocation invocation) {
        if (Objects.isNull(invocation.getArgs()[1])) {  // mapper层接口方法无参数
            return null;
        } else if (invocation.getArgs()[1] instanceof Pager<?>) {   // mapper层接口方法只有Pager参数
            return (Pager<?>) invocation.getArgs()[1];
        } else if(invocation.getArgs()[1] instanceof MapperMethod.ParamMap<?>) {    // mapper层接口方法有多个参数
            MapperMethod.ParamMap params = (MapperMethod.ParamMap) invocation.getArgs()[1];
            return (Pager<?>) params.values().stream()
                    .filter(o -> o instanceof Pager<?>)
                    .findFirst()
                    .orElse(null);
        } else {
            return null;
        }
    }

    /**
     * 获取SQL注入的参数
     * @param boundSql
     * @return
     * @throws NoSuchFieldException
     * @throws IllegalAccessException
     */
    protected Map<String, Object> getSQLParameter(BoundSql boundSql) throws NoSuchFieldException, IllegalAccessException {
        Field additionalParametersField = BoundSql.class.getDeclaredField("additionalParameters");
        additionalParametersField.setAccessible(Boolean.TRUE);
        return (Map<String, Object>) additionalParametersField.get(boundSql);
    }

    protected List selectPageData(Pager pager, Executor executor, MappedStatement mappedStatement, Object methodParams, BoundSql boundSql, ResultHandler<?> resultHandler) throws NoSuchFieldException, IllegalAccessException, SQLException {
        CacheKey pageCacheKey = executor.createCacheKey(mappedStatement, methodParams, RowBounds.DEFAULT, boundSql);
        DataSource dataSource = mappedStatement.getConfiguration().getEnvironment().getDataSource();
        String pageSql = Dialect.guessDialect(dataSource).pagin(pager.getNo(), pager.getSize(), formatSql(boundSql.getSql()));
        BoundSql pageBoundSql = new BoundSql(mappedStatement.getConfiguration(), pageSql, boundSql.getParameterMappings(), methodParams);

        getSQLParameter(boundSql).entrySet().forEach(entry -> pageBoundSql.setAdditionalParameter(entry.getKey(), entry.getValue()));

        return executor.query(mappedStatement, methodParams, RowBounds.DEFAULT, resultHandler, pageCacheKey, pageBoundSql);
    }

    protected String formatSql(String sql) {
        return sql.replace("\r", " ").replace("\n", " ");
    }

    /**
     * 数据量统计
     * @param mappedStatement   XML中的SQL映射
     * @param methodParams      mapper层方法参数
     * @param boundSql          SQL定义
     * @return
     */
    protected Long count(Executor executor, MappedStatement mappedStatement, Object methodParams, BoundSql boundSql, ResultHandler<?> resultHandler) throws NoSuchFieldException, IllegalAccessException, SQLException {
        MappedStatement countMappedStatement = buildMappedStatement(mappedStatement, Long.class);
        CacheKey countCacheKey = executor.createCacheKey(countMappedStatement, methodParams, RowBounds.DEFAULT, boundSql);
        String countSql = String.format("select count(*) from (%s) page_count", boundSql.getSql());
        BoundSql countBoundSql = new BoundSql(mappedStatement.getConfiguration(), countSql, boundSql.getParameterMappings(), methodParams);

        getSQLParameter(boundSql).entrySet().forEach(entry -> countBoundSql.setAdditionalParameter(entry.getKey(), entry.getValue()));

        List<Object> countResult = executor.query(countMappedStatement, methodParams, RowBounds.DEFAULT, resultHandler, countCacheKey, countBoundSql);
        return (Long) CollectionUtils.firstElement(countResult);
    }

    /**
     * MappedStatement构造器
     * @param mappedStatement   XML中的SQL映射对象
     * @param resultType        返回值类型
     * @return
     */
    protected MappedStatement buildMappedStatement(MappedStatement mappedStatement, Class<?> resultType) {

        String countSqlId = String.format("%s_%s", mappedStatement.getId(), "count");

        try {
            return mappedStatement.getConfiguration().getMappedStatement(countSqlId, Boolean.FALSE);
        } catch (Exception e) {
        }

        MappedStatement.Builder builder = new MappedStatement.Builder(
                mappedStatement.getConfiguration(),
                countSqlId,
                mappedStatement.getSqlSource(),
                mappedStatement.getSqlCommandType()
        );

        builder.resource(mappedStatement.getResource())
                .fetchSize(mappedStatement.getFetchSize())
                .statementType(mappedStatement.getStatementType())
                .timeout(mappedStatement.getTimeout())
                .parameterMap(mappedStatement.getParameterMap())
                .resultSetType(mappedStatement.getResultSetType())
                .cache(mappedStatement.getCache())
                .flushCacheRequired(mappedStatement.isFlushCacheRequired())
                .useCache(mappedStatement.isUseCache())
                .resultMaps(Arrays.asList(new ResultMap.Builder(mappedStatement.getConfiguration(), countSqlId, resultType, new ArrayList<>(2)).build()));

        if (null != mappedStatement.getKeyProperties() && mappedStatement.getKeyProperties().length > 0) {
            String keyProperty = Stream.of(mappedStatement.getKeyProperties()).collect(Collectors.joining(","));
            builder.keyProperty(keyProperty);
        }

        MappedStatement countMappedStatement = builder.build();

        mappedStatement.getConfiguration().addMappedStatement(countMappedStatement);

        return countMappedStatement;
    }

}
