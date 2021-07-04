package com.example.seckill.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class User {
    private int id;
    private String userName;
    private String phone;
    private String password;
    private String salt;
    private String head;
    private int loginCount;
    private Date registerDate;
    private Date lastLoginDate;
}
