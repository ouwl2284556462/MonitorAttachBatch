package com.owl.monitorattachbatch.config;
 
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.mybatis.spring.boot.autoconfigure.ConfigurationCustomizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
 
import javax.sql.DataSource;
 

@Configuration
@MapperScan(basePackages = {"com.owl.monitorattachbatch.mapper.sqlserver"}, sqlSessionTemplateRef = "sqlServerSqlSessionTemplate")
public class SqlServerSqlSessionTplConfig {
 
    @Value("${mybatis.mapper-locations}")
    private String mapper_location;

 
    private Logger logger = LoggerFactory.getLogger(SqlServerSqlSessionTplConfig.class);
 
    /**
     * 自定义sqlSessionFactory配置（因为没有用到MybatisAutoConfiguration自动配置类，需要手动配置）
     * @param dataSource
     * @return
     * @throws Exception
     */
    @Bean
    @Primary
    public SqlSessionFactory sqlServerSqlSessionFactory(@Qualifier("sqlserver") DataSource dataSource) throws Exception {
        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
        bean.setDataSource(dataSource);
        //如果重写了 SqlSessionFactory 需要在初始化的时候手动将 mapper 地址 set到 factory 中，否则会报错：
        //org.apache.ibatis.binding.BindingException: Invalid bound statement (not found)
        bean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources(mapper_location));
        return bean.getObject();
    }
 
    /**
     * SqlSessionTemplate 是 SqlSession接口的实现类，是spring-mybatis中的，实现了SqlSession线程安全
     *
     * @param sqlSessionFactory
     * @return
     */
    @Bean
    @Primary
    public SqlSessionTemplate sqlServerSqlSessionTemplate(@Qualifier("sqlServerSqlSessionFactory") SqlSessionFactory sqlSessionFactory) {
        SqlSessionTemplate template = new SqlSessionTemplate(sqlSessionFactory);
        return template;
    }
}