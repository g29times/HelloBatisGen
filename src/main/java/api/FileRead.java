package api;

import config.ShardingJdbcConfigurer;
import org.junit.Test;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import java.io.File;
import java.net.URL;

import static config.Paths.shardingConfigClassPath;
import static config.Paths.shardingConfigFilePath;

public class FileRead {

    @Test
    public void test() {
        try {
            // 寻找配置文件方式1 原生URL
            URL url = ShardingJdbcConfigurer.class.getClassLoader()
                    .getResource(shardingConfigFilePath);
            if (url != null) {
                System.out.println(url.getFile());
                System.out.println(url.getPath());
                File file = new File(url.getFile());
                System.out.println(file);
//                    YamlShardingDataSourceFactory.createDataSource(new File(url.getFile()));
//                    System.out.println(JSON.toJSONString(unmarshal(ShardingJdbcConfigurer.class.getClassLoader()
//                            .getResourceAsStream(originPath))));
            }

            // 寻找配置文件方式2 Spring 的 ResourcePatternResolver
            ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
            if (resolver.getResource(shardingConfigClassPath).exists()) {
                System.out.println(resolver.getResource(shardingConfigClassPath).getFile());
//                System.out.println(JSON.toJSONString(unmarshal(resolver
//                        .getResource(shardingConfigClassPath).getInputStream())));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
