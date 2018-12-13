//package config;
//
//
//import com.fenji.read.mc.task.constant.ProjectConstant;
//import com.github.pagehelper.PageInterceptor;
//import org.apache.ibatis.plugin.Interceptor;
//import org.apache.ibatis.session.SqlSessionFactory;
//import org.mybatis.spring.SqlSessionFactoryBean;
//import org.mybatis.spring.SqlSessionTemplate;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
//import org.springframework.core.io.support.ResourcePatternResolver;
//import org.springframework.jdbc.datasource.DataSourceTransactionManager;
//import tk.mybatis.spring.mapper.MapperScannerConfigurer;
//
//import javax.sql.DataSource;
//import java.util.Properties;
//
//
//@Configuration
//public class MybatisConfigurer {
//
//    @Bean(name = "sqlSessionFactoryBean")
//    public SqlSessionFactory sqlSessionFactoryBean(DataSource dataSource) throws Exception {
//        SqlSessionFactoryBean factory = new SqlSessionFactoryBean();
//        factory.setDataSource(dataSource);
//        factory.setTypeAliasesPackage(ProjectConstant.MODEL_PACKAGE);
//
//        //配置分页插件，详情请查阅官方文档
//        PageInterceptor pageHelper = new PageInterceptor();
//        Properties properties = new Properties();
//        // properties.setProperty("dialect","mysql");    //配置mysql数据库的方言
//        properties.setProperty("pageSizeZero", "true");//分页尺寸为0时查询所有纪录不再执行分页
//        properties.setProperty("reasonable", "false");//分页参数合理化（true:页码<=0 查询第一页，页码>=总页数查询最后一页）
//        properties.setProperty("supportMethodsArguments", "true");//支持通过 Mapper 接口参数来传递分页参数
//        pageHelper.setProperties(properties);
//
//        //添加插件
//        factory.setPlugins(new Interceptor[]{pageHelper});
//        //添加XML目录
//        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
//        factory.setMapperLocations(resolver.getResources("classpath:mapper/*.xml"));
//        factory.setConfigLocation(resolver.getResource("classpath:config/mybatis.xml"));
//        return factory.getObject();
//    }
//
//    @Bean(name = "shardSqlSessionTemplate")
//    public SqlSessionTemplate shardSqlSessionTemplate(
//            @Qualifier("sqlSessionFactoryBean") SqlSessionFactory sqlSessionFactory) {
//        return new SqlSessionTemplate(sqlSessionFactory);
//    }
//
//    @Bean(name = "shardTransactionManager")
//    public DataSourceTransactionManager shardTransactionManager(DataSource dataSource) {
//        return new DataSourceTransactionManager(dataSource);
//    }
//
//    @Bean
//    public MapperScannerConfigurer mapperScannerConfigurer() {
//        MapperScannerConfigurer mapperScannerConfigurer = new MapperScannerConfigurer();
//        mapperScannerConfigurer.setSqlSessionFactoryBeanName("sqlSessionFactoryBean");
//        mapperScannerConfigurer.setBasePackage(ProjectConstant.MAPPER_PACKAGE);
//
//        //配置通用Mapper，详情请查阅官方文档
//        Properties properties = new Properties();
//        properties.setProperty("mappers", ProjectConstant.MAPPER_INTERFACE_REFERENCE);
//        properties.setProperty("notEmpty", "false");//insert、update是否判断字符串类型!='' 即 test="str != null"表达式内是否追加 and str != ''
//        properties.setProperty("IDENTITY", "MYSQL");
//        mapperScannerConfigurer.setProperties(properties);
//
//        return mapperScannerConfigurer;
//    }
//
//}
