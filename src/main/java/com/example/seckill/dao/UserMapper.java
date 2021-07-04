package com.example.seckill.dao;

import com.example.seckill.pojo.User;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface UserMapper {
    @Select("select * from user where phone = #{phone} ")
    User checkPhone(@Param("phone") String phone);

    @Insert("insert into user(user_name, phone, password, salt, head, login_count, register_date, last_login_date) " +
            "values(#{userName}, #{phone}, #{password}, #{salt}, null, 1, null, null)")
    @SelectKey(keyColumn = "id",keyProperty = "id", resultType = long.class,
            before = false,statement = "select last_insert_id()")
    int insertUser(User user);

}
