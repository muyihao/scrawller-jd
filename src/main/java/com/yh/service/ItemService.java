package com.yh.service;


import com.yh.mapper.ItemMapper;
import com.yh.pojo.Item;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ItemService {

    @Autowired
    private ItemMapper itemMapper;


    public List<Item> findAll(Item item) {
        List<Item> items = itemMapper.select(item);
        return items;
    }


    @Transactional
    public void save(Item item) {
        this.itemMapper.insertSelective(item);
    }

}
