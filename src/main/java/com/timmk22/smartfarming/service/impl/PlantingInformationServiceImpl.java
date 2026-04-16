package com.timmk22.smartfarming.service.impl;

import com.timmk22.smartfarming.dto.request.CreatePlantingInformationRequest;
import com.timmk22.smartfarming.dto.request.UpdatePlantingInformationRequest;
import com.timmk22.smartfarming.dto.response.PlantingInformationResponse;
import com.timmk22.smartfarming.enumeration.CurrentStatus;
import com.timmk22.smartfarming.model.Crop;
import com.timmk22.smartfarming.model.PlantingInformation;
import com.timmk22.smartfarming.model.SoilType;
import com.timmk22.smartfarming.model.User;
import com.timmk22.smartfarming.repository.CropRepository;
import com.timmk22.smartfarming.repository.PlantingInformationRepository;
import com.timmk22.smartfarming.repository.SoilTypeRepository;
import com.timmk22.smartfarming.repository.UserRepository;
import com.timmk22.smartfarming.service.PlantingInformationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class PlantingInformationServiceImpl implements PlantingInformationService {

    private final PlantingInformationRepository plantingInformationRepository;
    private final UserRepository userRepository;
    private final CropRepository cropRepository;
    private final SoilTypeRepository soilTypeRepository;

    public PlantingInformationServiceImpl(PlantingInformationRepository plantingInformationRepository,
                                          UserRepository userRepository,
                                          CropRepository cropRepository,
                                          SoilTypeRepository soilTypeRepository) {
        this.plantingInformationRepository = plantingInformationRepository;
        this.userRepository = userRepository;
        this.cropRepository = cropRepository;
        this.soilTypeRepository = soilTypeRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<PlantingInformationResponse> getAllEntries(Long userId) {

        userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));

        return plantingInformationRepository.findByUserUserId(userId).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public PlantingInformationResponse getEntryById(Long entryId, Long userId) {
        PlantingInformation entry = plantingInformationRepository.findById(entryId)
                .orElseThrow(() -> new IllegalArgumentException("Entry not found with ID: " + entryId));

        if (!entry.getUser().getUserId().equals(userId)) {
            throw new IllegalArgumentException("You do not have permission to access this entry");
        }

        return convertToResponse(entry);
    }

    @Override
    public PlantingInformationResponse createEntry(CreatePlantingInformationRequest request, Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));

        Crop crop = cropRepository.findById(request.getCropId())
                .orElseThrow(() -> new IllegalArgumentException("Crop not found with ID: " + request.getCropId()));

        SoilType soilType = soilTypeRepository.findById(request.getSoilTypeId())
                .orElseThrow(() -> new IllegalArgumentException("Soil type not found with ID: " + request.getSoilTypeId()));

        PlantingInformation plantingInformation = new PlantingInformation();
        plantingInformation.setArea(request.getArea());
        plantingInformation.setPlantingDate(request.getPlantingDate());
        plantingInformation.setUser(user);
        plantingInformation.setCrop(crop);
        plantingInformation.setSoilType(soilType);
        plantingInformation.setLatitude(request.getLatitude());
        plantingInformation.setLongitude(request.getLongitude());
        plantingInformation.setLocationName(request.getLocationName());
        plantingInformation.setIrrigationType(request.getIrrigationType());
        plantingInformation.setCurrentStatus(
                request.getCurrentStatus() != null ? request.getCurrentStatus() : CurrentStatus.HEALTHY
        );
        plantingInformation.setExpectedHarvestDate(request.getExpectedHarvestDate());
        plantingInformation.setNotes(request.getNotes());

        PlantingInformation savedEntry = plantingInformationRepository.save(plantingInformation);
        return convertToResponse(savedEntry);
    }

    @Override
    public PlantingInformationResponse updateEntry(Long entryId, UpdatePlantingInformationRequest request, Long userId) {
        PlantingInformation entry = plantingInformationRepository.findById(entryId)
                .orElseThrow(() -> new IllegalArgumentException("Entry not found with ID: " + entryId));

        if (!entry.getUser().getUserId().equals(userId)) {
            throw new IllegalArgumentException("You do not have permission to update this entry");
        }

        Crop crop = cropRepository.findById(request.getCropId())
                .orElseThrow(() -> new IllegalArgumentException("Crop not found with ID: " + request.getCropId()));

        SoilType soilType = soilTypeRepository.findById(request.getSoilTypeId())
                .orElseThrow(() -> new IllegalArgumentException("Soil type not found with ID: " + request.getSoilTypeId()));

        entry.setArea(request.getArea());
        entry.setPlantingDate(request.getPlantingDate());
        entry.setCrop(crop);
        entry.setSoilType(soilType);
        entry.setLatitude(request.getLatitude());
        entry.setLongitude(request.getLongitude());
        entry.setLocationName(request.getLocationName());
        entry.setIrrigationType(request.getIrrigationType());
        entry.setCurrentStatus(request.getCurrentStatus());
        entry.setExpectedHarvestDate(request.getExpectedHarvestDate());
        entry.setNotes(request.getNotes());

        PlantingInformation updatedEntry = plantingInformationRepository.save(entry);
        return convertToResponse(updatedEntry);
    }

    @Override
    public void deleteEntry(Long entryId, Long userId) {
        PlantingInformation entry = plantingInformationRepository.findById(entryId)
                .orElseThrow(() -> new IllegalArgumentException("Entry not found with ID: " + entryId));

        if (!entry.getUser().getUserId().equals(userId)) {
            throw new IllegalArgumentException("You do not have permission to delete this entry");
        }

        plantingInformationRepository.delete(entry);
    }

    private PlantingInformationResponse convertToResponse(PlantingInformation entity) {
        PlantingInformationResponse response = new PlantingInformationResponse();
        response.setPlantingId(entity.getPlantingId());
        response.setArea(entity.getArea());
        response.setPlantingDate(entity.getPlantingDate());
        response.setUserId(entity.getUser().getUserId());
        response.setCropId(entity.getCrop().getCropId());
        response.setCropName(entity.getCrop().getName());
        response.setSoilTypeId(entity.getSoilType().getSoilId());
        response.setSoilTypeName(entity.getSoilType().getName());
        response.setLatitude(entity.getLatitude());
        response.setLongitude(entity.getLongitude());
        response.setLocationName(entity.getLocationName());
        response.setIrrigationType(entity.getIrrigationType());
        response.setCurrentStatus(entity.getCurrentStatus());
        response.setExpectedHarvestDate(entity.getExpectedHarvestDate());
        response.setNotes(entity.getNotes());

        return response;
    }
}
