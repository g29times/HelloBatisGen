package config;

import com.alibaba.druid.pool.DruidDataSource;
import com.google.common.collect.Lists;
import io.shardingsphere.api.config.MasterSlaveRuleConfiguration;
import io.shardingsphere.api.config.ShardingRuleConfiguration;
import io.shardingsphere.api.config.TableRuleConfiguration;
import io.shardingsphere.api.config.strategy.InlineShardingStrategyConfiguration;
import io.shardingsphere.shardingjdbc.api.ShardingDataSourceFactory;
import io.shardingsphere.shardingjdbc.api.yaml.YamlShardingDataSourceFactory;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.io.File;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static config.Paths.shardingConfigFilePath;

@Configuration
public class ShardingJdbcConfigurer {

    /**
     * 主从 + 分库分表测试
     * 双库+主从+两表演示
     *
     * @throws Exception
     */
    @Test
    public void test() throws Exception {
        URL url = ShardingJdbcConfigurer.class.getClassLoader()
                .getResource(shardingConfigFilePath);
        DataSource dataSource;
        try {
            File file = new File(url.getFile());
            dataSource = YamlShardingDataSourceFactory.createDataSource(file);
        } catch (Exception e) {
            dataSource = localizeDataSource();
        }

        String sql = "SELECT i.* FROM t_order o JOIN t_order_item i ON o.order_id=i.order_id " +
                "WHERE o.user_id=? AND o.order_id=?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            preparedStatement.setInt(1, 6);
            preparedStatement.setInt(2, 6);
            try (ResultSet rs = preparedStatement.executeQuery()) {
                while (rs.next()) {
                    System.out.println(" -------------------- QUERY RESULT -------------------- ");
                    System.out.println(rs.getInt(1));
                    System.out.println(rs.getInt(2));
                    System.out.println(rs.getInt(3));
                }
            }
        }
        try (Connection conn = dataSource.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            preparedStatement.setInt(1, 7);
            preparedStatement.setInt(2, 7);
            try (ResultSet rs = preparedStatement.executeQuery()) {
                while (rs.next()) {
                    System.out.println(" -------------------- QUERY RESULT -------------------- ");
                    System.out.println(rs.getInt(1));
                    System.out.println(rs.getInt(2));
                    System.out.println(rs.getInt(3));
                }
            }
        }
    }

    /**
     * 配置Sharding数据源
     *
     * @param ds_0
     * @param ds_1
     * @return
     * @throws SQLException
     */
    @Bean
    public DataSource dataSource(@Qualifier("ds_0") DataSource ds_0, @Qualifier("ds_1") DataSource ds_1) throws SQLException {
        // 配置 分片规则
        ShardingRuleConfiguration shardingRuleConfig = new ShardingRuleConfiguration();
        shardingRuleConfig.getTableRuleConfigs().add(configOrderTableRule());
        shardingRuleConfig.getTableRuleConfigs().add(configOrderItemTableRule());
        shardingRuleConfig.getBindingTableGroups().add("t_order, t_order_item");
        // <! 单库配置 ds_0> - <! 多库配置 ds_${user_id % 2}>
        shardingRuleConfig.setDefaultDatabaseShardingStrategyConfig(
                new InlineShardingStrategyConfiguration("user_id",
                        "ds_0"));
        shardingRuleConfig.setDefaultTableShardingStrategyConfig(
                new InlineShardingStrategyConfiguration("order_id",
                        "t_order_${order_id % 2}"));
        Map<String, DataSource> dataSourceMap = new HashMap<>();
        dataSourceMap.put("ds_0", ds_0);
        dataSourceMap.put("ds_1", ds_1);

        // 配置 读写分离规则
        shardingRuleConfig.setMasterSlaveRuleConfigs(configMasterSlaveRule());

        Properties properties = new Properties();
        properties.setProperty("sql.show", Boolean.TRUE.toString());

        try {
            // 方式1 读取yaml
            URL url = ShardingJdbcConfigurer.class.getClassLoader()
                    .getResource(shardingConfigFilePath);
            return YamlShardingDataSourceFactory.createDataSource(new File(url.getFile()));
        } catch (Exception e) {
//            e.printStackTrace();
            // 方式2 获取数据源对象
            return ShardingDataSourceFactory.createDataSource(dataSourceMap,
                    shardingRuleConfig, new ConcurrentHashMap<>(), properties);
        }
    }

    /**
     * 手动(不用配置文件)获取数据源
     *
     * @return
     * @throws SQLException
     */
    public DataSource localizeDataSource() throws SQLException {
        DruidDataSource ds_0 = new DruidDataSource();
        ds_0.setDriverClassName("com.mysql.jdbc.Driver");
        ds_0.setUrl("jdbc:mysql://127.0.0.1:3306/ds_0?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=UTC&rewriteBatchedStatements=true");
        ds_0.setUsername("root");
        ds_0.setPassword("root");
        configDataSource(ds_0);

        DruidDataSource ds_1 = new DruidDataSource();
        ds_1.setDriverClassName("com.mysql.jdbc.Driver");
        ds_1.setUrl("jdbc:mysql://127.0.0.1:3306/ds_1?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=UTC&rewriteBatchedStatements=true");
        ds_1.setUsername("root");
        ds_1.setPassword("root");
        configDataSource(ds_1);
        return dataSource(ds_0, ds_1);
    }

    /**
     * yaml获取数据源
     *
     * @return
     */
    @ConfigurationProperties(prefix = "spring.datasource.ds-0.druid")
    @Bean(name = "ds_0")
    public DataSource dataSource0() {
        return new DruidDataSource();
    }

    @ConfigurationProperties(prefix = "spring.datasource.ds-1.druid")
    @Bean(name = "ds_1")
    public DataSource dataSource1() {
        return new DruidDataSource();
    }

    /**
     * 配置Order表 分表规则
     *
     * @return
     */
    private TableRuleConfiguration configOrderTableRule() {
        // 配置表规则
        TableRuleConfiguration orderTableRuleConfig = new TableRuleConfiguration();
        orderTableRuleConfig.setLogicTable("t_order");
        // <! 单库配置 ds_${0}> - <! 多库配置 ds_${0..1}>
        orderTableRuleConfig.setActualDataNodes("ds_${0}.t_order_${0..1}");
        // 设置主键
        orderTableRuleConfig.setKeyGeneratorColumnName("order_id");
        // 配置分库 + 分表策略
//        orderTableRuleConfig.setDatabaseShardingStrategyConfig(new InlineShardingStrategyConfiguration("user_id", "ds${user_id % 2}"));
//        orderTableRuleConfig.setTableShardingStrategyConfig(new InlineShardingStrategyConfiguration("order_id", "t_order_${order_id % 2}"));
        return orderTableRuleConfig;
    }

    /**
     * 配置OrderItem表 分表规则
     *
     * @return
     */
    private TableRuleConfiguration configOrderItemTableRule() {
        TableRuleConfiguration orderItemTableRuleConfig = new TableRuleConfiguration();
        orderItemTableRuleConfig.setLogicTable("t_order_item");
        // <! 单库配置 ds_${0}> - <! 多库配置 ds_${0..1}>
        orderItemTableRuleConfig.setActualDataNodes("ds_${0}.t_order_item_${[0, 1]}");
        return orderItemTableRuleConfig;
    }

    /**
     * 配置主从库规则
     *
     * @return
     */
    private List<MasterSlaveRuleConfiguration> configMasterSlaveRule() {
        MasterSlaveRuleConfiguration rule1 = new MasterSlaveRuleConfiguration("ds_0",
                "ds_0",
                Arrays.asList("ds_1"/*"ds_0_slave_0", "ds_0_slave_1"*/));
//        MasterSlaveRuleConfiguration rule2 = new MasterSlaveRuleConfiguration("ds_1",
//                "ds_1",
//                Arrays.asList("ds_1_slave_0", "ds_1_slave_1"));
        return Lists.newArrayList(rule1/*, rule2*/);
    }

    /**
     * 连接池参数配置
     *
     * @param druidDataSource
     */
    private void configDataSource(DruidDataSource druidDataSource) {
        // 初始化连接
        druidDataSource.setInitialSize(10);
        // 最小空闲连接
        druidDataSource.setMinIdle(10);
        // 最大活动连接
        druidDataSource.setMaxActive(200);
        // connection-timeout
        druidDataSource.setMaxWait(30000);
        // 每30秒运行一次空闲连接回收器
        druidDataSource.setTimeBetweenEvictionRunsMillis(30000);
        // 池中的连接空闲5分钟后被回收
        druidDataSource.setMinEvictableIdleTimeMillis(300000);
        druidDataSource.setValidationQuery("select 'x'");
        druidDataSource.setTestWhileIdle(true);
        druidDataSource.setTestOnBorrow(false);
        druidDataSource.setTestOnReturn(false);
        druidDataSource.setPoolPreparedStatements(true);
        druidDataSource.setMaxOpenPreparedStatements(200);
        druidDataSource.setUseGlobalDataSourceStat(true);
        try {
            druidDataSource.setFilters("stat,wall,slf4j");
        } catch (SQLException e) {
            System.out.println("druid configuration initialization filter" + e);
        }
    }

}
