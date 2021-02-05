package com.owl.monitorattachbatch.config;


import java.util.Properties;


import com.alibaba.druid.filter.stat.StatFilter;
import com.alibaba.druid.pool.xa.DruidXADataSource;
import com.alibaba.druid.support.http.StatViewServlet;
import com.alibaba.druid.support.http.WebStatFilter;
import com.alibaba.druid.wall.WallConfig;
import com.alibaba.druid.wall.WallFilter;
import com.atomikos.icatch.jta.UserTransactionImp;
import com.atomikos.icatch.jta.UserTransactionManager;
import com.microsoft.sqlserver.jdbc.SQLServerXADataSource;
import org.apache.ibatis.plugin.Interceptor;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jta.atomikos.AtomikosDataSourceBean;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;

import com.github.pagehelper.PageHelper;
import org.springframework.transaction.jta.JtaTransactionManager;

import javax.sql.DataSource;
import javax.transaction.UserTransaction;

@Configuration
/**
 * MVC配置
 */
public class DbConfig {

    @Autowired
    private Environment env;

    @Value("${spring.datasource.type}")
    private String dataSourceType;




    /**
     * 配置主数据源，多数据源中必须要使用@Primary指定一个主数据源
     * 其次DataSource里用的是DruidXADataSource ，而后注册到AtomikosDataSourceBean并且返回
     *
     * @return
     */
    @Primary
    @Bean(name = "sqlserver")
    public DataSource sqlserver() {
        SQLServerXADataSource ds = new SQLServerXADataSource();
        ds.setURL(env.getProperty("spring.datasource.druid.sqlserver.url"));
        ds.setUser(env.getProperty("spring.datasource.druid.sqlserver.username"));
        ds.setPassword(env.getProperty("spring.datasource.druid.sqlserver.password"));
        AtomikosDataSourceBean atomikosDataSourceBean = new AtomikosDataSourceBean();
        atomikosDataSourceBean.setXaDataSource(ds);
        atomikosDataSourceBean.setUniqueResourceName("sqlserver");
        return atomikosDataSourceBean;
    }


    /**
     * 配置次数据源
     * 其次DataSource里用的是DruidXADataSource ，而后注册到AtomikosDataSourceBean并且返回
     *
     * @return
     */
    @Bean(name = "mysql")
    public DataSource mysql() {
        AtomikosDataSourceBean  ds = new AtomikosDataSourceBean();
        Properties prop = build(env, "spring.datasource.druid.mysql.");
        ds.setXaDataSourceClassName(dataSourceType);
        ds.setPoolSize(5);
        ds.setXaProperties(prop);
        ds.setUniqueResourceName("mysql");
        return ds;
    }


    private Properties build(Environment env, String prefix) {
        Properties prop = new Properties();
        prop.put("url", env.getProperty(prefix + "url"));
        prop.put("username", env.getProperty(prefix + "username"));
        prop.put("password", env.getProperty(prefix + "password"));
        prop.put("driverClassName", env.getProperty(prefix + "driverClassName", ""));
        prop.put("initialSize", env.getProperty(prefix + "initialSize", Integer.class));
        prop.put("maxActive", env.getProperty(prefix + "maxActive", Integer.class));
        prop.put("minIdle", env.getProperty(prefix + "minIdle", Integer.class));
        prop.put("maxWait", env.getProperty(prefix + "maxWait", Integer.class));
        prop.put("poolPreparedStatements", env.getProperty(prefix + "poolPreparedStatements", Boolean.class));
        prop.put("maxPoolPreparedStatementPerConnectionSize",
                env.getProperty(prefix + "maxPoolPreparedStatementPerConnectionSize", Integer.class));
        prop.put("maxPoolPreparedStatementPerConnectionSize",
                env.getProperty(prefix + "maxPoolPreparedStatementPerConnectionSize", Integer.class));
        prop.put("validationQuery", env.getProperty(prefix + "validationQuery"));
        prop.put("validationQueryTimeout", env.getProperty(prefix + "validationQueryTimeout", Integer.class));
        prop.put("testOnBorrow", env.getProperty(prefix + "testOnBorrow", Boolean.class));
        prop.put("testOnReturn", env.getProperty(prefix + "testOnReturn", Boolean.class));
        prop.put("testWhileIdle", env.getProperty(prefix + "testWhileIdle", Boolean.class));
        prop.put("timeBetweenEvictionRunsMillis",
                env.getProperty(prefix + "timeBetweenEvictionRunsMillis", Integer.class));
        prop.put("minEvictableIdleTimeMillis", env.getProperty(prefix + "minEvictableIdleTimeMillis", Integer.class));
        prop.put("filters", env.getProperty(prefix + "filters"));

        return prop;
    }


    @Bean
    /**
     * 分页插件配置
     *
     * @return
     */
    public PageHelper pageHelper() {
        // 分页插件
        PageHelper pageHelper = new PageHelper();
        Properties properties = new Properties();
        // 超过页数仍然可以查出数据。如：如果pageNum>pageSize,会查询最后一页的数据
        properties.setProperty("reasonable", "true");
        // 如果参数中有pageNum，pageSize分页参数，则会自动分页
        properties.setProperty("supportMethodsArguments", "true");
        // 是否返回PageInfo类，有三个选项：
        // always:总是返回PageInfo类型
        // check:检查返回类型是否为PageInfo，是则返回，否则返回Page
        // none:返回Page
        properties.setProperty("returnPageInfo", "check");
        // 用于从Map或ServletRequest中取值
        properties.setProperty("params", "count=countSql");
        pageHelper.setProperties(properties);

        // 添加插件
        new SqlSessionFactoryBean().setPlugins(new Interceptor[]{pageHelper});
        return pageHelper;
    }


    @Bean
    public ServletRegistrationBean druidServlet() {
        ServletRegistrationBean servletRegistrationBean = new ServletRegistrationBean(new StatViewServlet(), "/druid/*");

        //控制台管理用户，加入下面2行 进入druid后台就需要登录
        //servletRegistrationBean.addInitParameter("loginUsername", "admin");
        //servletRegistrationBean.addInitParameter("loginPassword", "admin");
        return servletRegistrationBean;
    }

    @Bean
    public FilterRegistrationBean filterRegistrationBean() {
        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
        filterRegistrationBean.setFilter(new WebStatFilter());
        filterRegistrationBean.addUrlPatterns("/*");
        filterRegistrationBean.addInitParameter("exclusions", "*.js,*.gif,*.jpg,*.png,*.css,*.ico,/druid/*");
        filterRegistrationBean.addInitParameter("profileEnable", "true");
        return filterRegistrationBean;
    }

    @Bean
    public StatFilter statFilter(){
        StatFilter statFilter = new StatFilter();
        statFilter.setLogSlowSql(true); //slowSqlMillis用来配置SQL慢的标准，执行时间超过slowSqlMillis的就是慢。
        statFilter.setMergeSql(true); //SQL合并配置
        statFilter.setSlowSqlMillis(1000);//slowSqlMillis的缺省值为3000，也就是3秒。
        return statFilter;
    }

    @Bean
    public WallFilter wallFilter(){
        WallFilter wallFilter = new WallFilter();
        //允许执行多条SQL
        WallConfig config = new WallConfig();
        config.setMultiStatementAllow(true);
        wallFilter.setConfig(config);
        return wallFilter;
    }

}
