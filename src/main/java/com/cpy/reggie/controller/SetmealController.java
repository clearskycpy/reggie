package com.cpy.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cpy.reggie.common.R;
import com.cpy.reggie.dto.SetmealDto;
import com.cpy.reggie.entity.Setmeal;
import com.cpy.reggie.service.CategoryService;
import com.cpy.reggie.service.SetmealDishService;
import com.cpy.reggie.service.SetmealService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 套餐管理
 */
@RestController
@RequestMapping("/setmeal")
public class SetmealController {
    @Autowired
    CategoryService categoryService;
    @Autowired
    private SetmealDishService setmealDishService;

    @Autowired
    private SetmealService setmealService;

    /**
     * insert
     * @param setmealDto
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody SetmealDto setmealDto) {
        setmealService.saveWithSetmealDish(setmealDto);
        return R.success("save success");
    }


    /**
     * 分页查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page<SetmealDto>> page(int page, int pageSize, String name){
//        分页构造器
        Page<Setmeal> page1 = new Page<>(page,pageSize);
//        条件构造器
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(name != null,Setmeal::getName,name);
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);
        setmealService.page(page1,queryWrapper);

//        将查询到的数据拷贝进新的page里面
        List<Setmeal> records = page1.getRecords();

        List<SetmealDto> list = records.stream().map((item) ->{
            SetmealDto setmealDto = new SetmealDto();
            BeanUtils.copyProperties(item,setmealDto);
            Long categoryId = setmealDto.getCategoryId();
            String categoryName = categoryService.getById(categoryId).getName();
            setmealDto.setCategoryName(categoryName);
            return setmealDto;
        }).collect(Collectors.toList());
        Page<SetmealDto> pageInfo = new Page<>();
        BeanUtils.copyProperties(page1,pageInfo,"records");
        pageInfo.setRecords(list);
        return R.success(pageInfo);
    }

    /**
     * delete setmeals By id
     * @param ids
     * @return
     */
    @DeleteMapping
    public R<String> deleteByIds(@RequestParam List<Long> ids){
        setmealService.removeWithDish(ids);
        return R.success("delete success");
    }
//     localhost:8080/setmeal/status/0?ids=1568618339643367425
    @PostMapping("/status/{status}")
    public R<String> updateStatusByIds(@RequestParam List<Long> ids,@PathVariable int status){
        setmealService.updateStatusByIds(ids,status);
        return R.success("update success");
    }

//    根据id 查询套餐信息

//    Request URL: http://localhost:8080/setmeal/1568618339643367425
//    Request Method: GET
//    Status Code: 404
    @GetMapping("/{id}")
    public R<SetmealDto> querySetmealById(@PathVariable Long id){
        SetmealDto setmealDto = setmealService.queryWithDishes(id);
        return R.success(setmealDto);
    }

//    Request URL: http://localhost:8080/setmeal
//    Request Method: PUT
//    Status Code: 405
    @PutMapping
    public R<String> update(@RequestBody SetmealDto setmealDto){
        setmealService.updateWithDishes(setmealDto);
        return R.success("update success");
    }

//    Request URL: http://localhost:8080/setmeal/list?categoryId=1413342269393674242&status=1
//    Request Method: GET
    /**
     * 根据条件查询套餐数据
     * @param setmeal
     * @return
     */
    @GetMapping("/list")
    public R<List<Setmeal>> list(Setmeal setmeal){
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(setmeal.getCategoryId() != null,Setmeal::getCategoryId,setmeal.getCategoryId());
        queryWrapper.eq(setmeal.getStatus() != null,Setmeal::getStatus,setmeal.getStatus());
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);

        List<Setmeal> list = setmealService.list(queryWrapper);

        return R.success(list);
    }

}
