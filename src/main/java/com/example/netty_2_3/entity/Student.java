package com.example.netty_2_3.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * ClassName: Student
 * <p>
 * Description:Bean:学生
 * <p>
 * Author: Zhao Li
 * <p>
 * Date: 10/29/2021 20:22
 * <p>
 * History:
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Student implements Serializable {

    private Integer age;

    private Integer gender;

    private String name;
}
