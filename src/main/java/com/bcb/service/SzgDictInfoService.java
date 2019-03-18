package com.bcb.service;

import com.bcb.domain.repository.SzgDictInfoRepository;
import com.bcb.domain.entity.SzgDictInfo;
import com.bcb.util.annotation.TransactionEx;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class SzgDictInfoService{
    @Autowired
    private SzgDictInfoRepository szgDictInfoRepository;

    @TransactionEx
    public Integer addSzgDictInfo(SzgDictInfo szgDictInfo) {
        return szgDictInfoRepository.save(szgDictInfo).getDicId();
    }

    @TransactionEx
    public Integer updateSzgDictInfo(SzgDictInfo szgDictInfo) {
        return szgDictInfoRepository.save(szgDictInfo).getDicId();
    }
}
