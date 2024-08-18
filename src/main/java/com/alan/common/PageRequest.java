package com.alan.common;

import lombok.Data;
import java.io.Serializable;

@Data
public class PageRequest implements Serializable {

    private static final long serialVersionUID = -3072754472046827384L;

    /**
     * 页面大小
     */
    protected int pageSize = 10;

    /**
     * 当前页码
     */
    protected int pageNum = 1;

}

