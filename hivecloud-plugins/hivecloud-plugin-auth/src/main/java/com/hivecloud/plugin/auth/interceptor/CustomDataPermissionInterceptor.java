package com.hivecloud.plugin.auth.interceptor;

import com.baomidou.mybatisplus.core.toolkit.PluginUtils;
import com.baomidou.mybatisplus.extension.plugins.inner.InnerInterceptor;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.StringValue;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.sql.SQLException;

@Slf4j
public class CustomDataPermissionInterceptor implements InnerInterceptor {

    @Override
    public void beforeQuery(Executor executor, MappedStatement ms, Object parameter, RowBounds rowBounds, ResultHandler resultHandler, BoundSql boundSql) throws SQLException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return;
        }

        String originalSql = boundSql.getSql();
        String dataPermissionSql = buildDataPermissionSql(originalSql);
        PluginUtils.mpBoundSql(boundSql).sql(dataPermissionSql);
    }

    private String buildDataPermissionSql(String originalSql) {
        try {
            Expression expression = CCJSqlParserUtil.parseCondExpression("dept_id = 'default'");
            AndExpression andExpression = new AndExpression(
                    CCJSqlParserUtil.parseCondExpression("1=1"),
                    expression
            );
            return originalSql + " AND " + andExpression.toString();
        } catch (Exception e) {
            log.warn("Failed to build data permission SQL, using original SQL", e);
            return originalSql;
        }
    }
}