package com.timmk22.smartfarming.service;

import com.timmk22.smartfarming.dto.request.CreatePlantingInformationRequest;
import com.timmk22.smartfarming.dto.request.UpdatePlantingInformationRequest;
import com.timmk22.smartfarming.dto.response.PlantingInformationResponse;
import com.timmk22.smartfarming.enumeration.CurrentStatus;
import com.timmk22.smartfarming.enumeration.IrrigationType;
import com.timmk22.smartfarming.model.Crop;
import com.timmk22.smartfarming.model.PlantingInformation;
import com.timmk22.smartfarming.model.SoilType;
import com.timmk22.smartfarming.model.User;
import com.timmk22.smartfarming.repository.CropRepository;
import com.timmk22.smartfarming.repository.PlantingInformationRepository;
import com.timmk22.smartfarming.repository.SoilTypeRepository;
import com.timmk22.smartfarming.repository.UserRepository;
import com.timmk22.smartfarming.service.impl.PlantingInformationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PlantingInformationServiceImplTest {

    @Mock
    private PlantingInformationRepository plantingInformationRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CropRepository cropRepository;

    @Mock
    private SoilTypeRepository soilTypeRepository;

    @InjectMocks
    private PlantingInformationServiceImpl plantingInformationService;

    private User user;
    private Crop crop;
    private SoilType soilType;
    private PlantingInformation planting;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setUserId(1L);
        user.setName("Dimitar");
        user.setEmail("dimitar@test.com");
        user.setPassword("123456");

        crop = new Crop();
        crop.setCropId(2L);
        crop.setName("Tomato");

        soilType = new SoilType();
        soilType.setSoilId(3L);
        soilType.setName("Loamy");

        planting = new PlantingInformation();
        planting.setPlantingId(10L);
        planting.setArea(15.5);
        planting.setPlantingDate(LocalDate.of(2026, 5, 1));
        planting.setUser(user);
        planting.setCrop(crop);
        planting.setSoilType(soilType);
        planting.setLatitude(41.63);
        planting.setLongitude(22.47);
        planting.setLocationName("Radovis");
        planting.setIrrigationType(IrrigationType.DRIP);
        planting.setCurrentStatus(CurrentStatus.HEALTHY);
        planting.setExpectedHarvestDate(LocalDate.of(2026, 9, 1));
        planting.setNotes("Healthy crop");
    }

    @Test
    @DisplayName("Should return all entries for existing user")
    void shouldReturnAllEntriesForExistingUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(plantingInformationRepository.findByUserUserId(1L)).thenReturn(List.of(planting));

        List<PlantingInformationResponse> result = plantingInformationService.getAllEntries(1L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getPlantingId()).isEqualTo(10L);
        assertThat(result.get(0).getUserId()).isEqualTo(1L);
        assertThat(result.get(0).getCropName()).isEqualTo("Tomato");
        assertThat(result.get(0).getSoilTypeName()).isEqualTo("Loamy");
    }

    @Test
    @DisplayName("Should throw when user not found in getAllEntries")
    void shouldThrowWhenUserNotFoundInGetAllEntries() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> plantingInformationService.getAllEntries(1L)
        );

        assertThat(ex.getMessage()).isEqualTo("User not found with ID: 1");
        verify(plantingInformationRepository, never()).findByUserUserId(anyLong());
    }

    @Test
    @DisplayName("Should return entry by id when owned by user")
    void shouldReturnEntryByIdWhenOwnedByUser() {
        when(plantingInformationRepository.findById(10L)).thenReturn(Optional.of(planting));

        PlantingInformationResponse result = plantingInformationService.getEntryById(10L, 1L);

        assertThat(result.getPlantingId()).isEqualTo(10L);
        assertThat(result.getUserId()).isEqualTo(1L);
        assertThat(result.getCropId()).isEqualTo(2L);
        assertThat(result.getSoilTypeId()).isEqualTo(3L);
    }

    @Test
    @DisplayName("Should throw when entry not found in getEntryById")
    void shouldThrowWhenEntryNotFoundInGetEntryById() {
        when(plantingInformationRepository.findById(10L)).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> plantingInformationService.getEntryById(10L, 1L)
        );

        assertThat(ex.getMessage()).isEqualTo("Entry not found with ID: 10");
    }

    @Test
    @DisplayName("Should throw when user does not own entry in getEntryById")
    void shouldThrowWhenUserDoesNotOwnEntryInGetEntryById() {
        when(plantingInformationRepository.findById(10L)).thenReturn(Optional.of(planting));

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> plantingInformationService.getEntryById(10L, 99L)
        );

        assertThat(ex.getMessage()).isEqualTo("You do not have permission to access this entry");
    }

    @Test
    @DisplayName("Should create entry successfully")
    void shouldCreateEntrySuccessfully() {
        CreatePlantingInformationRequest request = new CreatePlantingInformationRequest();
        request.setArea(20.0);
        request.setPlantingDate(LocalDate.of(2026, 5, 2));
        request.setCropId(2L);
        request.setSoilTypeId(3L);
        request.setLatitude(41.70);
        request.setLongitude(22.50);
        request.setLocationName("Strumica");
        request.setIrrigationType(IrrigationType.SPRINKLER);
        request.setCurrentStatus(null);
        request.setExpectedHarvestDate(LocalDate.of(2026, 9, 10));
        request.setNotes("New entry");

        PlantingInformation saved = new PlantingInformation();
        saved.setPlantingId(20L);
        saved.setArea(request.getArea());
        saved.setPlantingDate(request.getPlantingDate());
        saved.setUser(user);
        saved.setCrop(crop);
        saved.setSoilType(soilType);
        saved.setLatitude(request.getLatitude());
        saved.setLongitude(request.getLongitude());
        saved.setLocationName(request.getLocationName());
        saved.setIrrigationType(request.getIrrigationType());
        saved.setCurrentStatus(CurrentStatus.HEALTHY);
        saved.setExpectedHarvestDate(request.getExpectedHarvestDate());
        saved.setNotes(request.getNotes());

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(cropRepository.findById(2L)).thenReturn(Optional.of(crop));
        when(soilTypeRepository.findById(3L)).thenReturn(Optional.of(soilType));
        when(plantingInformationRepository.save(any(PlantingInformation.class))).thenReturn(saved);

        PlantingInformationResponse result = plantingInformationService.createEntry(request, 1L);

        assertThat(result.getPlantingId()).isEqualTo(20L);
        assertThat(result.getCurrentStatus()).isEqualTo(CurrentStatus.HEALTHY);

        ArgumentCaptor<PlantingInformation> captor = ArgumentCaptor.forClass(PlantingInformation.class);
        verify(plantingInformationRepository).save(captor.capture());
        assertThat(captor.getValue().getCurrentStatus()).isEqualTo(CurrentStatus.HEALTHY);
    }

    @Test
    @DisplayName("Should throw when user not found in createEntry")
    void shouldThrowWhenUserNotFoundInCreateEntry() {
        CreatePlantingInformationRequest request = new CreatePlantingInformationRequest();
        request.setCropId(2L);
        request.setSoilTypeId(3L);

        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> plantingInformationService.createEntry(request, 1L)
        );

        assertThat(ex.getMessage()).isEqualTo("User not found with ID: 1");
    }

    @Test
    @DisplayName("Should update entry successfully")
    void shouldUpdateEntrySuccessfully() {
        UpdatePlantingInformationRequest request = new UpdatePlantingInformationRequest();
        request.setArea(30.0);
        request.setPlantingDate(LocalDate.of(2026, 6, 1));
        request.setCropId(2L);
        request.setSoilTypeId(3L);
        request.setLatitude(41.80);
        request.setLongitude(22.60);
        request.setLocationName("Updated location");
        request.setIrrigationType(IrrigationType.DRIP);
        request.setCurrentStatus(CurrentStatus.WARNING);
        request.setExpectedHarvestDate(LocalDate.of(2026, 10, 1));
        request.setNotes("Updated notes");

        when(plantingInformationRepository.findById(10L)).thenReturn(Optional.of(planting));
        when(cropRepository.findById(2L)).thenReturn(Optional.of(crop));
        when(soilTypeRepository.findById(3L)).thenReturn(Optional.of(soilType));
        when(plantingInformationRepository.save(any(PlantingInformation.class))).thenAnswer(i -> i.getArgument(0));

        PlantingInformationResponse result = plantingInformationService.updateEntry(10L, request, 1L);

        assertThat(result.getArea()).isEqualTo(30.0);
        assertThat(result.getCurrentStatus()).isEqualTo(CurrentStatus.WARNING);
        assertThat(result.getLocationName()).isEqualTo("Updated location");
    }

    @Test
    @DisplayName("Should throw when updating entry not owned by user")
    void shouldThrowWhenUpdatingEntryNotOwnedByUser() {
        when(plantingInformationRepository.findById(10L)).thenReturn(Optional.of(planting));

        UpdatePlantingInformationRequest request = new UpdatePlantingInformationRequest();

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> plantingInformationService.updateEntry(10L, request, 99L)
        );

        assertThat(ex.getMessage()).isEqualTo("You do not have permission to update this entry");
    }

    @Test
    @DisplayName("Should delete entry successfully")
    void shouldDeleteEntrySuccessfully() {
        when(plantingInformationRepository.findById(10L)).thenReturn(Optional.of(planting));

        plantingInformationService.deleteEntry(10L, 1L);

        verify(plantingInformationRepository).delete(planting);
    }

    @Test
    @DisplayName("Should throw when deleting entry not owned by user")
    void shouldThrowWhenDeletingEntryNotOwnedByUser() {
        when(plantingInformationRepository.findById(10L)).thenReturn(Optional.of(planting));

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> plantingInformationService.deleteEntry(10L, 99L)
        );

        assertThat(ex.getMessage()).isEqualTo("You do not have permission to delete this entry");
        verify(plantingInformationRepository, never()).delete(any());
    }
}