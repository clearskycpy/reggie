package com.cpy.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cpy.reggie.dto.DishDto;
import com.cpy.reggie.entity.Dish;

import java.util.List;


public interface DishService extends IService<Dish>  {

    /**
     * 完成新增菜品，同时新增好口味表
     * @param dishDto
     */
    public void saveWithDishFlavor(DishDto dishDto);

    public DishDto getByIdWithFlavor(Long id);

    public void updateWithDishFlavor(DishDto dishDto);

    public void removeWithDishFlavor(List<Long> ids);

    public void updateDishStatus(List<Long> ids, Integer status);
}
