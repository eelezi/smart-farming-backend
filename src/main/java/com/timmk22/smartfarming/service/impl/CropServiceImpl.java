package com.timmk22.smartfarming.service.impl;

import com.timmk22.smartfarming.dto.response.IdNameResponse;
import com.timmk22.smartfarming.repository.CropRepository;
import com.timmk22.smartfarming.service.CropService;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class CropServiceImpl implements CropService {

    private final CropRepository cropRepository;

    public CropServiceImpl(CropRepository cropRepository) {
        this.cropRepository = cropRepository;
    }

    @Override
    public List<IdNameResponse> listAll() {
        return cropRepository.findAll(Sort.by("name")).stream()
                .map(c -> new IdNameResponse(c.getCropId(), c.getName()))
                .toList();
    }
}
