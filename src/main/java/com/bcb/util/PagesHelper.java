package com.bcb.util;

import com.github.pagehelper.PageInfo;
import org.springframework.data.domain.Page;

public class PagesHelper {

    public static <T> void copyProperties(Page<T> source, PageInfo<T> dest){
        if(source == null || dest == null){
            return;
        }
        dest.setSize(source.getSize());
        dest.setPages(source.getTotalPages());
        dest.setTotal(source.getTotalElements());
        dest.setList(source.getContent());
    }
}
