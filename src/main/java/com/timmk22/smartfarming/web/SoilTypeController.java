package com.timmk22.smartfarming.web;

import com.timmk22.smartfarming.dto.response.IdNameResponse;
import com.timmk22.smartfarming.service.SoilTypeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/soil-types")
public class SoilTypeController {

    private final SoilTypeService soilTypeService;

    public SoilTypeController(SoilTypeService soilTypeService) {
        this.soilTypeService = soilTypeService;
    }

    @GetMapping
    public ResponseEntity<List<IdNameResponse>> getAll() {
        return ResponseEntity.ok(soilTypeService.listAll());
    }
}
