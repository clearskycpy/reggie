package com.cpy.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cpy.reggie.common.CustomException;
import com.cpy.reggie.common.FileHandler;
import com.cpy.reggie.dto.DishDto;
import com.cpy.reggie.entity.Dish;
import com.cpy.reggie.entity.DishFlavor;
import com.cpy.reggie.mapper.DishMapper;
import com.cpy.reggie.service.DishFlavorService;
import com.cpy.reggie.service.DishService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {

    @Autowired
    private DishFlavorService dishFlavorService;

    @Autowired
    private FileHandler fileHandler;

    /**
     * 新增具体实现
     * @param dishDto
     */
    @Override
    @Transactional
    public void saveWithDishFlavor(DishDto dishDto) {
//        保存dish内容
        this.save(dishDto); // 此时新增好了之后id就已经赋值了

        Long id= dishDto.getId();
        List<DishFlavor> flavors = dishDto.getFlavors();
//        使用流的方式 lambda 表达式 给参数赋值
        flavors = flavors.stream().map((item) -> {
            item.setDishId(id);
            return item;
        }).collect(Collectors.toList());
//        保存菜品口味
        dishFlavorService.saveBatch(flavors);

    }

    /**
     * 根据id查询菜品基本信息
     * 查询对应的口味信息
     * @param id
     * @return
     */
    @Override
    public DishDto getByIdWithFlavor(Long id) {
//        查询基本信息
        Dish dish = this.getById(id);

//        准备返回对象
        DishDto dishDto = new DishDto();
        BeanUtils.copyProperties(dish,dishDto);
//        查询对应的口味信息
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId,dish.getId());
        List<DishFlavor> flavors = dishFlavorService.list(queryWrapper);
//        给返回对象的口味进行赋值
        dishDto.setFlavors(flavors);

        return dishDto;
    }

    /**
     *实现更新菜品和菜品口味
     * @param dishDto
     */
    @Override
    @Transactional
    public void updateWithDishFlavor(DishDto dishDto) {
//        更新菜品基本信息
        this.updateById(dishDto);
//        清理菜品口味信息
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId,dishDto.getId());
        dishFlavorService.remove(queryWrapper);
//        添加当前新的菜品口味
        List<DishFlavor> flavors = dishDto.getFlavors();
//        这里面没有保存dishId 因为是重新插入的，所以不存在disId

        flavors = flavors.stream().map((item) -> {
            item.setDishId(dishDto.getId());
            return item;
        }).collect(Collectors.toList());
        dishFlavorService.saveBatch(flavors);
    }

    @Override
    @Transactional
    public void removeWithDishFlavor(List<Long> ids) {
//        在删除之前先判断菜品中是否还存在起售中的菜品，如果存在就抛出异常
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Dish::getId,ids).eq(Dish::getStatus,1);
        int count = this.count(queryWrapper);
        if (count > 0)
            throw  new CustomException("存在还在起售的菜品");
//        删除对应的照片
        ids.forEach((id) ->{
            String image = this.getById(id).getImage();
            fileHandler.removeImage(image);
        });
        //        删除该id的Dish
        this.removeByIds(ids);
//        删除菜品下对应的口味数据
//        构造条件
        LambdaQueryWrapper<DishFlavor> queryWrapper1 = new LambdaQueryWrapper<>();
        queryWrapper1.in(DishFlavor::getDishId,ids);
        dishFlavorService.remove(queryWrapper1);

    }

    @Override
    public void updateDishStatus(List<Long> ids, Integer status) {
//        先判断要执行的操作是否全都统一，可能存在选错，也就是查询一下需要更新的Dishes 中是否存在 Status 等于 Status 的
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Dish::getId,ids).eq(Dish::getStatus,status);
        int count = this.count(queryWrapper);
        if (count > 0 ){
            throw new CustomException("选择的菜品状态不一致");
        }
//        说明可以更新 构造更新条件
        LambdaUpdateWrapper<Dish> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(Dish::getStatus,status).in(Dish::getId,ids);
//        执行更新
        this.update(updateWrapper);
    }
}
