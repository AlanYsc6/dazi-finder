package com.alan.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.alan.pojo.domain.Tag;
import com.alan.mapper.TagMapper;
import com.alan.service.TagService;
import org.springframework.stereotype.Service;

/**
* @author Alan
* @description 针对表【tag(标签)】的数据库操作Service实现
* @createDate 2024-06-16 08:17:20
*/
@Service
public class TagServiceImpl extends ServiceImpl<TagMapper, Tag>
    implements TagService{

}




