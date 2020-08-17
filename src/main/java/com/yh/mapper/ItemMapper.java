package com.yh.mapper;

import com.yh.pojo.Item;
import tk.mybatis.mapper.common.Mapper;


//已经在启动类中配置了MapperScan,所以这里不用再通过注解来注入bean容器了

public interface ItemMapper extends Mapper<Item> {
}
