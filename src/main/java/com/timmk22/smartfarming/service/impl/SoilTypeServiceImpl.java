package com.timmk22.smartfarming.service.impl;

import com.timmk22.smartfarming.dto.response.IdNameResponse;
import com.timmk22.smartfarming.repository.SoilTypeRepository;
import com.timmk22.smartfarming.service.SoilTypeService;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class SoilTypeServiceImpl implements SoilTypeService {

    private final SoilTypeRepository soilTypeRepository;

    public SoilTypeServiceImpl(SoilTypeRepository soilTypeRepository) {
        this.soilTypeRepository = soilTypeRepository;
    }

    @Override
    public List<IdNameResponse> listAll() {
        return soilTypeRepository.findAll(Sort.by("name")).stream()
                .map(s -> new IdNameResponse(s.getSoilId(), s.getName()))
                .toList();
    }
}
