package com.cpy.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cpy.reggie.common.CustomException;
import com.cpy.reggie.entity.Category;
import com.cpy.reggie.entity.Dish;
import com.cpy.reggie.entity.Setmeal;
import com.cpy.reggie.mapper.CategoryMapper;
import com.cpy.reggie.service.CategoryService;
import com.cpy.reggie.service.DishService;
import com.cpy.reggie.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService{

    @Autowired
    private DishService dishService;

    @Autowired
    private SetmealService  setmealService;
    /**
     * 根据id删除分类
     * @param id
     */
    @Override
    public void remove(Long id) {
//        查看这个分类是否关联菜品，如果关联了菜品，就抛出一个异常
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dishLambdaQueryWrapper.eq(Dish::getCategoryId,id);
        int countDish = dishService.count(dishLambdaQueryWrapper);
        if (countDish > 0){
//            业务异常
            throw new CustomException("已关联菜品,无法删除");
        }
//        查看这个分类是否关联了套餐，如果已经关联，抛出异常
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealLambdaQueryWrapper.eq(Setmeal::getCategoryId,setmealLambdaQueryWrapper);
        int countSetmeal = setmealService.count(setmealLambdaQueryWrapper);
        if (countSetmeal > 0){
//            业务异常
            throw new CustomException("已关联套餐,无法删除");
        }
//        进行删除业务
        super.removeById(id);
    }
}
