package com.cpy.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cpy.reggie.common.BaseContext;
import com.cpy.reggie.common.R;
import com.cpy.reggie.dto.OrdersDto;
import com.cpy.reggie.entity.OrderDetail;
import com.cpy.reggie.entity.Orders;
import com.cpy.reggie.service.OrderDetailService;
import com.cpy.reggie.service.OrdersService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/order")
@Slf4j
public class OrdersController {

    @Autowired
    private OrdersService ordersService;

    @Autowired
    private OrderDetailService orderDetailService;


    /**
     * 分页查询  未编写完
     * 处理的前台请求
     * Request URL: http://localhost:8080/order/page?page=1&pageSize=10
     * Request Method: GET
     * http://localhost:8080/order/page?
     * page=1&pageSize=10&number=123&beginTime=2022-09-06%2000%3A00%3A00&endTime=2022-10-13%2023%3A59%3A59
     */
    @GetMapping("/page")
    public R<Page<Orders>> page(int page, int pageSize, String number, @RequestParam LocalDateTime beginTime, LocalDateTime endTime){
        Page<Orders> pageInfo = new Page<>(page, pageSize);
//        条件构造器
        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(StringUtils.isNotEmpty(number),Orders::getNumber,number);
        queryWrapper.between((beginTime != null && endTime != null), Orders::getOrderTime,beginTime,endTime);
        queryWrapper.orderByAsc(Orders::getOrderTime);
//      进行查询
        ordersService.page(pageInfo,queryWrapper);
        return  R.success(pageInfo);
    }

  /*  Request URL: http://localhost:8080/order/submit
    Request Method: POST*/

    @PostMapping("/submit")
    public R<String> submit(@RequestBody Orders orders){
        ordersService.submit(orders);
        return R.success("submit success");
    }

    /**
     *  * Request URL: http://localhost:8080/order/userPage?page=1&pageSize=1
     *  * Request Method: GET
     *  select Orders and OrderDetail message
     */

    @GetMapping("/userPage")
    public R<Page> userPage(int page, int pageSize){
        Page<Orders> pageInfo = new Page<>(page,pageSize);
//      先获取该用户下的Orders信息
        Long userId = BaseContext.getCurrentId();
//        构造条件
        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Orders::getUserId,userId);
        queryWrapper.orderByAsc(Orders::getOrderTime);
//        查询Page
        ordersService.page(pageInfo, queryWrapper);

        Page<OrdersDto> ordersDtoPage = new Page<>();
//        数据拷贝进需要返回的page
        BeanUtils.copyProperties(pageInfo,ordersDtoPage,"records");
//        拿到原来的records
        List<Orders> ordersList = pageInfo.getRecords();

        List<OrdersDto> ordersDtoList = null;
//        没有Orders就直接返回，有Orders就遍历
        if (ordersList != null) {
            ordersDtoList = ordersList.stream().map(
                    (order) -> {
//                    先将orders中的数据拷贝到ordersDto 中
                        OrdersDto ordersDto = new OrdersDto();
                        BeanUtils.copyProperties(order, ordersDto);
                        Long orderId = order.getId();
                        if (orderId != null) {
//                            查询该order下的 orderDetail
                            LambdaQueryWrapper<OrderDetail> queryWrapper1 = new LambdaQueryWrapper<>();
                            queryWrapper1.eq(OrderDetail::getOrderId, orderId);
                            List<OrderDetail> list = orderDetailService.list(queryWrapper1);
                            ordersDto.setOrderDetails(list);
                        }
                        return ordersDto;
                    }
            ).collect(Collectors.toList());
        }
//        最后封装到一个新的page里面
        ordersDtoPage.setRecords(ordersDtoList);
        return R.success(ordersDtoPage);
    }

//    Request URL: http://localhost:8080/order/page?page=1&pageSize=10
//    Request Method: GET
//    Status Code: 400
//    Remote Address: [::1]:8080

    /*@GetMapping("page")
    public R<Page<Orders>> page(int page, int pageSize){
        Page<Orders> pageInfo = new Page<>(page, pageSize);

        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByAsc(Orders::getOrderTime);

        ordersService.page(pageInfo,queryWrapper);

    }*/

}
