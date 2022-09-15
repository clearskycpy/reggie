package com.cpy.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cpy.reggie.common.R;
import com.cpy.reggie.dto.DishDto;
import com.cpy.reggie.entity.Category;
import com.cpy.reggie.entity.Dish;
import com.cpy.reggie.entity.DishFlavor;
import com.cpy.reggie.service.CategoryService;
import com.cpy.reggie.service.DishFlavorService;
import com.cpy.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/dish")
@Slf4j
public class DishController {

    @Autowired
    private DishService dishService;

    @Autowired
    private DishFlavorService dishFlavorService;

    @Autowired
    private CategoryService categoryService;

    /**
     * 插入具体实现
     * @param dishDto
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto){
        dishService.saveWithDishFlavor(dishDto);
        return R.success("Save Success！");
    }

    /**
     * 菜品信息分页
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name){
//        构造分页器
        Page<Dish> pageInfo = new Page(page,pageSize);
//      为了能够存储 category字段， 原来的dish存储的是id 无法满足要求
        Page<DishDto> dishDtoPage = new Page(page,pageSize);
//        条件构造器
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper();
//        添加查询条件 先判断是否为空
        queryWrapper.like(name != null, Dish::getName,name);
        queryWrapper.orderByDesc(Dish::getUpdateTime);
//        添加排序条件

//        进行查询
        dishService.page(pageInfo,queryWrapper);

//        数据拷贝 不拷贝 list集合records  这个集合由List<DishDto>提供
        BeanUtils.copyProperties(pageInfo,dishDtoPage,"records");
        List<Dish> records = pageInfo.getRecords();

//        使用lambda表达式进行数据处理 将list封装进去
        List<DishDto> list = records.stream().map(
                (item) -> {
                    DishDto dishDto = new DishDto();
                    BeanUtils.copyProperties(item,dishDto);
                    Long categoryId = item.getCategoryId();
                    Category byId = categoryService.getById(categoryId);
                    if (byId != null){
                        dishDto.setCategoryName(byId.getName());
                    }
                    return dishDto;
                }).collect(Collectors.toList());

//        将集合赋值到page里面
        dishDtoPage.setRecords(list);

        return R.success(dishDtoPage);
    }

    /**
     * 根据id查询菜品信息和口味信息
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<DishDto> queryById(@PathVariable Long id){
        DishDto dishDto = dishService.getByIdWithFlavor(id);
        return R.success(dishDto);
    }

    /**
     * update DishMessage and DishFlavor
     * @param dishDto
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto){
        dishService.updateWithDishFlavor(dishDto);
        return R.success("Update Success！");
    }

    /**
     * delete Dishes with DishFlavor
     * @param ids
     * @return
     */
    @DeleteMapping
    public R<String> deleteByIds(@RequestParam List<Long> ids){
        dishService.removeWithDishFlavor(ids);
        return R.success("delete Success");
    }

//    http://localhost:8080/dish/status/1?ids=1413384757047271425,1413385247889891330

    @PostMapping("/status/{status}")
    public R<String> updateStatus(@PathVariable Integer status,@RequestParam List<Long> ids){
//        处理更新请求 -- 单表

        dishService.updateDishStatus(ids,status);
        return R.success("updateStatus success");
    }

//    : http://localhost:8080/dish/list?categoryId=1397844263642378242

    /**
     * 根据条件查询对应的菜品数据
     * @param dish
     * @return
     */
    @GetMapping("/list")
    public R<List<DishDto>> list(Dish dish){
//        构造查询条件
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
//        根据菜品的id分类
        queryWrapper.eq(dish.getCategoryId() != null,Dish::getCategoryId,dish.getCategoryId());
        queryWrapper.like(dish.getName()!= null,Dish::getName,dish.getName());
//        表示该菜品为起售状态
        queryWrapper.eq(Dish::getStatus,1);
//        添加排序条件
        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);

        List<Dish> list = dishService.list(queryWrapper);

        List<DishDto> dishDtoList = list.stream().map(
                (item) -> {
                    DishDto dishDto = new DishDto();
                    BeanUtils.copyProperties(item,dishDto);
                    Long categoryId = item.getCategoryId();
                    Category byId = categoryService.getById(categoryId);
                    if (byId != null){
                        dishDto.setCategoryName(byId.getName());
                    }
//                    菜品id
                    Long id = item.getId();
                    LambdaQueryWrapper<DishFlavor> queryWrapper1 = new LambdaQueryWrapper<>();
                    queryWrapper1.eq(DishFlavor::getDishId,id);
                    List<DishFlavor> list1 = dishFlavorService.list(queryWrapper1);
                    dishDto.setFlavors(list1);
                    return dishDto;
                }).collect(Collectors.toList());
        return R.success(dishDtoList);
    }
}
