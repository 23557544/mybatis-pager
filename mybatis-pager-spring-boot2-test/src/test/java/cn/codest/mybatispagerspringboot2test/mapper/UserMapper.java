package cn.codest.mybatispagerspringboot2test.mapper;

import cn.codest.mybatispager.model.Pager;
import cn.codest.mybatispagerspringboot2test.model.User;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface UserMapper {

    List<User> selectPage(@Param("name") String name, Pager<User> pager);

}
