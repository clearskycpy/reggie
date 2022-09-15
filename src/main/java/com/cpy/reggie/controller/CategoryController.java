package com.cpy.reggie.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cpy.reggie.common.R;
import com.cpy.reggie.entity.Category;
import com.cpy.reggie.entity.Dish;
import com.cpy.reggie.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 分类管理
 */
@RestController
@RequestMapping("/category")
@Slf4j
public class CategoryController {
    @Autowired
    private CategoryService categoryService;
    /**
     * 新增分类
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody Category category){
        categoryService.save(category);
        return R.success("新增分类成功");
    }

    /**
     * 分页查询
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize){
//        构造分页器
        Page<Category> pageInfo = new Page(page,pageSize);
//        条件构造器
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper();
//        添加查询条件 先判断是否为空
//        添加排序条件
        queryWrapper.orderByAsc(Category::getSort);
        categoryService.page(pageInfo,queryWrapper);
        return R.success(pageInfo);
    }

    /**
     * 删除分类
     * @param ids
     * @return
     */
    @DeleteMapping
    public R<String> delete(Long ids){
        categoryService.remove(ids);
        /**
         * 调用自定义service层方法
         */
        return R.success("分类信息删除成功");
    }

    /**
     *
     * @param category
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody Category category){
        categoryService.updateById(category);
        return R.success("update success");
    }

    /**
     * save Dish query Category list by type

     * @param category
     * @return
     */
    @GetMapping("/list")
    public R<List<Category>> list(Category category){
//             * 因为前台传递的使用的是get的方式，所以可以直接使用Category接收
//        条件构造器
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
//        查询条件，如果type不为空，既eq
        queryWrapper.eq(category.getType() != null,Category::getType,category.getType());
//        返回数据的进行排序
        queryWrapper.orderByAsc(Category::getSort).orderByDesc(Category::getUpdateTime);
        List<Category> list = categoryService.list(queryWrapper);
        return R.success(list);
    }
}
