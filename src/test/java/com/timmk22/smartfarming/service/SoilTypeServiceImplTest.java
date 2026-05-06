package com.timmk22.smartfarming.service;


import com.timmk22.smartfarming.dto.response.IdNameResponse;
import com.timmk22.smartfarming.model.SoilType;
import com.timmk22.smartfarming.repository.SoilTypeRepository;
import com.timmk22.smartfarming.service.impl.SoilTypeServiceImpl;
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
class SoilTypeServiceImplTest {

    @Mock
    private SoilTypeRepository soilTypeRepository;

    @InjectMocks
    private SoilTypeServiceImpl soilTypeService;

    @Test
    @DisplayName("Should return all soil types mapped to IdNameResponse sorted by name")
    void shouldReturnAllSoilTypesMappedToIdNameResponseSortedByName() {
        SoilType soil1 = new SoilType();
        soil1.setSoilId(1L);
        soil1.setName("Clay");

        SoilType soil2 = new SoilType();
        soil2.setSoilId(2L);
        soil2.setName("Loamy");

        when(soilTypeRepository.findAll(Sort.by("name"))).thenReturn(List.of(soil1, soil2));

        List<IdNameResponse> result = soilTypeService.listAll();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).id()).isEqualTo(1L);
        assertThat(result.get(0).name()).isEqualTo("Clay");
        assertThat(result.get(1).id()).isEqualTo(2L);
        assertThat(result.get(1).name()).isEqualTo("Loamy");

        verify(soilTypeRepository).findAll(Sort.by("name"));
    }

    @Test
    @DisplayName("Should return empty list when there are no soil types")
    void shouldReturnEmptyListWhenThereAreNoSoilTypes() {
        when(soilTypeRepository.findAll(Sort.by("name"))).thenReturn(List.of());

        List<IdNameResponse> result = soilTypeService.listAll();

        assertThat(result).isEmpty();
        verify(soilTypeRepository).findAll(Sort.by("name"));
    }
}