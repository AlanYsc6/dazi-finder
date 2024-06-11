package com.alan.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;


/**
 * @Author Alan
 * @Date 2024/5/20 09:07
 * @Description
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegdVO implements Serializable {
    private static final long serialVersionUID = 4747428519810136936L;
    //日期
    private String date;
    //注册数
   private Integer regs;

}
