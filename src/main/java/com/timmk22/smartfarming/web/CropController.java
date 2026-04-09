package com.timmk22.smartfarming.web;

import com.timmk22.smartfarming.dto.response.IdNameResponse;
import com.timmk22.smartfarming.service.CropService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/crops")
public class CropController {

    private final CropService cropService;

    public CropController(CropService cropService) {
        this.cropService = cropService;
    }

    @GetMapping
    public ResponseEntity<List<IdNameResponse>> getAll() {
        return ResponseEntity.ok(cropService.listAll());
    }
}
