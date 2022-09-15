package com.cpy.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cpy.reggie.entity.Employee;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface EmployeeMapper extends BaseMapper<Employee> {
//    由于继承了basMapper所以常见的增删改查等方法也已经继承过来了
}
