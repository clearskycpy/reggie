package com.cpy.reggie.dto;

import com.cpy.reggie.entity.Setmeal;
import com.cpy.reggie.entity.SetmealDish;
import lombok.Data;
import java.util.List;

@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;
}
