package com.cpy.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cpy.reggie.dto.SetmealDto;
import com.cpy.reggie.entity.Setmeal;

import java.util.List;

public interface SetmealService extends IService<Setmeal> {
    public void saveWithSetmealDish(SetmealDto setmealDto);

    public void removeWithDish(List<Long> ids);

    public void updateStatusByIds(List<Long> ids, int status);

    public SetmealDto queryWithDishes(Long id);

    public void updateWithDishes(SetmealDto setmealDto);
}
