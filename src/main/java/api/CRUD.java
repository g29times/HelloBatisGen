package api;

import dao.OrderItemMapper;
import dao.OrderMapper;
import model.Order;
import model.OrderItem;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import static config.Paths.batisConfigFilePath;

public class CRUD {

    private SqlSessionFactory sqlSessionFactory;
    private InputStream resourceAsStream;

    @Before
    public void beforeTest() throws IOException {
        // 所在包的路径
        System.out.println(this.getClass().getResource(""));
        // ClassPath根路径
        System.out.println(this.getClass().getResource("/"));
        // ClassPath根路径
        System.out.println(this.getClass().getClassLoader().getResource(""));
        // 无效
        System.out.println(this.getClass().getClassLoader().getResource("/"));

        String resourceFile = batisConfigFilePath;
        // resourceAsStream = Resources.getResourceAsStream(resourceFile);
        URL url = this.getClass().getClassLoader().getResource(resourceFile);
        if (url != null) {
            resourceAsStream = this.getClass().getClassLoader().getResourceAsStream(resourceFile);
            sqlSessionFactory = new SqlSessionFactoryBuilder().build(resourceAsStream);
        }
    }

    @Test
    public void insertTest() {
        SqlSession openSession = sqlSessionFactory.openSession();
        OrderMapper orderMapper = openSession.getMapper(OrderMapper.class);
        OrderItemMapper orderItemMapper = openSession.getMapper(OrderItemMapper.class);

        for (Long i = 2L; i <= 10; i = i + 2) {
            try {
                Order order = new Order();
                order.setOrderId(i);
                order.setStatus("1");
                order.setUserId(i.intValue());
                Integer result = orderMapper.insert(order);
                System.out.println(result);
            } catch (Exception e) {
            }
            try {
                OrderItem orderItem = new OrderItem();
                orderItem.setOrderId(i);
                orderItem.setOrderItemId(i);
                orderItem.setUserId(i.intValue());
                Integer result = orderItemMapper.insert(orderItem);
                System.out.println(result);
            } catch (Exception e) {
            }
        }

        openSession.commit();
        openSession.close();
    }

    @Test
    public void findByIDTest() {
        SqlSession openSession = sqlSessionFactory.openSession();
        OrderMapper mapper = openSession.getMapper(OrderMapper.class);
        Order order = mapper.selectByPrimaryKey(1L);
        System.out.println(order);
    }

}
