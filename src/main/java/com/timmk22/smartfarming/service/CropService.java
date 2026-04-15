package com.timmk22.smartfarming.service;

import com.timmk22.smartfarming.dto.response.IdNameResponse;

import java.util.List;

public interface CropService {

    List<IdNameResponse> listAll();
}
