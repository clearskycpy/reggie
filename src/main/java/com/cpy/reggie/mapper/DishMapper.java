package com.cpy.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cpy.reggie.entity.Dish;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DishMapper extends BaseMapper<Dish> {
}
