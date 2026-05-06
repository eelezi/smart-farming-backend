package com.timmk22.smartfarming.service;

import com.timmk22.smartfarming.dto.response.IdNameResponse;
import com.timmk22.smartfarming.model.Crop;
import com.timmk22.smartfarming.repository.CropRepository;
import com.timmk22.smartfarming.service.impl.CropServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CropServiceImplTest {

    @Mock
    private CropRepository cropRepository;

    @InjectMocks
    private CropServiceImpl cropService;

    @Test
    @DisplayName("Should return all crops mapped to IdNameResponse sorted by name")
    void shouldReturnAllCropsMappedToIdNameResponseSortedByName() {
        Crop crop1 = new Crop();
        crop1.setCropId(1L);
        crop1.setName("Apple");

        Crop crop2 = new Crop();
        crop2.setCropId(2L);
        crop2.setName("Tomato");

        when(cropRepository.findAll(Sort.by("name"))).thenReturn(List.of(crop1, crop2));

        List<IdNameResponse> result = cropService.listAll();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).id()).isEqualTo(1L);
        assertThat(result.get(0).name()).isEqualTo("Apple");
        assertThat(result.get(1).id()).isEqualTo(2L);
        assertThat(result.get(1).name()).isEqualTo("Tomato");

        verify(cropRepository).findAll(Sort.by("name"));
    }

    @Test
    @DisplayName("Should return empty list when there are no crops")
    void shouldReturnEmptyListWhenThereAreNoCrops() {
        when(cropRepository.findAll(Sort.by("name"))).thenReturn(List.of());

        List<IdNameResponse> result = cropService.listAll();

        assertThat(result).isEmpty();
        verify(cropRepository).findAll(Sort.by("name"));
    }
}