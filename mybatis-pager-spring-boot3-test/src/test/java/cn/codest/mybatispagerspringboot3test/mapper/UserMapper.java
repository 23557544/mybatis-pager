package cn.codest.mybatispagerspringboot3test.mapper;

import cn.codest.mybatispager.model.Pager;
import cn.codest.mybatispagerspringboot3test.model.User;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface UserMapper {

    List<User> selectPage(@Param("name") String name, Pager<User> pager);

}
