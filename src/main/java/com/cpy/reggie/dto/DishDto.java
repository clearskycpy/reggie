package com.cpy.reggie.dto;

import com.cpy.reggie.entity.Dish;
import com.cpy.reggie.entity.DishFlavor;
import lombok.Data;
import java.util.ArrayList;
import java.util.List;

@Data
public class DishDto extends Dish {

    private List<DishFlavor> flavors = new ArrayList<>();

    // 保存分类名称
    private String categoryName;

    private Integer copies;
}
