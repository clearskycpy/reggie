package com.cpy.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cpy.reggie.common.CustomException;
import com.cpy.reggie.common.FileHandler;
import com.cpy.reggie.dto.SetmealDto;
import com.cpy.reggie.entity.Category;
import com.cpy.reggie.entity.Dish;
import com.cpy.reggie.entity.Setmeal;
import com.cpy.reggie.entity.SetmealDish;
import com.cpy.reggie.mapper.SetmealMapper;
import com.cpy.reggie.service.CategoryService;
import com.cpy.reggie.service.SetmealDishService;
import com.cpy.reggie.service.SetmealService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {
    @Autowired
    private SetmealDishService setmealDishService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private FileHandler fileHandler;
    /**
     * save setmeal and dishes
     * @param setmealDto
     */
    @Override
    @Transactional
    public void saveWithSetmealDish(SetmealDto setmealDto) {
//        save 基本信息
        this.save(setmealDto);

        List<SetmealDish> dishes = setmealDto.getSetmealDishes();
//        这里没有保存setMealId
//        探究一下 雪花算法
//        使用流的方式将SetmealId保存到Dishes中
        dishes = dishes.stream().map((item) -> {
            item.setSetmealId(setmealDto.getId());
            return item;
                }).collect(Collectors.toList());
//        调用setmealDishService 的Save方法
        setmealDishService.saveBatch(dishes);

    }

    @Override
    @Transactional
    public void removeWithDish(List<Long> ids) {
//      查询套餐状态,如果哦还在售卖中则不能删除
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Setmeal::getId,ids).eq(Setmeal::getStatus,1);
        int count = this.count(queryWrapper);
        if (count > 0){
            throw new CustomException("存在套餐还在售卖中");
        }
        //        删除对应的照片
        ids.forEach((id) ->{
            String image = this.getById(id).getImage();
            fileHandler.removeImage(image);
        });
//        批量删除
        this.removeByIds(ids);
//        构造条件
        LambdaQueryWrapper<SetmealDish> queryWrapper1 = new LambdaQueryWrapper<>();
        queryWrapper1.in(SetmealDish::getDishId,ids);
        setmealDishService.remove(queryWrapper1);
    }

    @Override
    @Transactional
    public void updateStatusByIds(List<Long> ids, int status) {
//        先查询状态是否一致
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Setmeal::getStatus,status).in(Setmeal::getId,ids);
        int count = this.count(queryWrapper);
        if (count > 0) {
            throw new CustomException("勾选的套餐状态不一致");
        }
        //        说明可以更新 构造更新条件
        LambdaUpdateWrapper<Setmeal> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(Setmeal::getStatus,status).in(Setmeal::getId,ids);
//        执行更新
        this.update(updateWrapper);
    }

    @Override
    public SetmealDto queryWithDishes(Long id) {
//        查询基本信息
        Setmeal setmeal= this.getById(id);

//        准备返回对象
        SetmealDto setmealDto = new SetmealDto();
        BeanUtils.copyProperties(setmeal,setmealDto);
//        查询对应的菜品信息  构造查询条件
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SetmealDish::getSetmealId,id).orderByDesc(SetmealDish::getUpdateTime);

//        拿到所有的Dish信息
        List<SetmealDish> list = setmealDishService.list(queryWrapper);
//      装填
        setmealDto.setSetmealDishes(list);
//      查询所对的的category
        Category category = categoryService.getById(setmealDto.getCategoryId());

        setmealDto.setCategoryName(category.getName());

        return setmealDto;

    }


    /**
     * update setmeal and Dishes in setmeal
     * LTAI5tCa9SA15Gnpm93GdEzc
     * pKohuWiOodnWEN12abkHjueh78ZEQP
     * @param setmealDto
     */
    @Override
    @Transactional
    public void updateWithDishes(SetmealDto setmealDto) {
//       更新基本信息
        this.updateById(setmealDto);
//        根据id删除SetmealDish表内信息
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SetmealDish::getSetmealId,setmealDto.getId());
        setmealDishService.remove(queryWrapper);
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();

        setmealDishes = setmealDishes.stream().map(
                (temp) ->{
                    temp.setSetmealId(setmealDto.getId());
                    return temp;
                }
        ).collect(Collectors.toList());
//        更新表内信息
        setmealDishService.saveBatch(setmealDishes);
    }
}
