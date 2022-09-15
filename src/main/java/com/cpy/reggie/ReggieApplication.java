package com.cpy.reggie;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;

//  定义为springboot的启动类
@Slf4j  //日志
@SpringBootApplication // 启动类标志
@ServletComponentScan  // 扫描系统内的servlet组件
@EnableTransactionManagement // 增加事务支持
public class ReggieApplication {
    static {
        /*
* 阿里给数据库设置的数据库空闲等待时间是60秒，mysql数据库到了空闲等待时间将关闭空闲的连接，以提升数据库服务器的处理能力。
MySQL的默认空闲等待时间是8小时，就是「wait_timeout」的配置值。如果数据库主动关闭了空闲的连接，而连接池并不知道，还在使用这个连接，就会产生异常。
————] 错误信息：：c.a.druid.pool.DruidAbstractDataSource
* */
        System.setProperty("druid.mysql.usePingMethod","false");
    }

    public static void main(String[] args) {
//        注册这个类
        SpringApplication.run(ReggieApplication.class,args);
        log.info("项目启动成功");

    }
}
