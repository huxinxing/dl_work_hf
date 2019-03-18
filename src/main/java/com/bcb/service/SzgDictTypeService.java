package com.bcb.service;

import com.bcb.domain.repository.SzgDictTypeRepository;
import com.bcb.domain.entity.SzgDictType;
import com.bcb.util.annotation.TransactionEx;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class SzgDictTypeService {
    @Autowired
    private SzgDictTypeRepository szgDictTypeRepository;

    @TransactionEx
    public Integer addSzgDictType(SzgDictType szgDictType) {
        return szgDictTypeRepository.save(szgDictType).getId();
    }

    @TransactionEx
    public Integer updateSzgDictType(SzgDictType szgDictType) {
        return szgDictTypeRepository.save(szgDictType).getId();
    }
}
