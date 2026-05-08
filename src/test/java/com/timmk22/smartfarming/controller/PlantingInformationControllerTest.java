package com.timmk22.smartfarming.controller;

import com.timmk22.smartfarming.dto.request.CreatePlantingInformationRequest;
import com.timmk22.smartfarming.dto.request.UpdatePlantingInformationRequest;
import com.timmk22.smartfarming.dto.response.PlantingInformationResponse;
import com.timmk22.smartfarming.model.User;
import com.timmk22.smartfarming.service.PlantingInformationService;
import com.timmk22.smartfarming.web.PlantingInformationController;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class PlantingInformationControllerTest {

    @Test
    void getAllEntriesShouldReturnEntriesForUser() {
        PlantingInformationService service = mock(PlantingInformationService.class);
        PlantingInformationController controller = new PlantingInformationController(service);

        User user = buildUser(1L, "dimitar@example.com");

        List<PlantingInformationResponse> entries = List.of(
                mock(PlantingInformationResponse.class),
                mock(PlantingInformationResponse.class)
        );

        when(service.getAllEntries(1L)).thenReturn(entries);

        ResponseEntity<List<PlantingInformationResponse>> response = controller.getAllEntries(user);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).hasSize(2);

        verify(service, times(1)).getAllEntries(1L);
    }

    @Test
    void getAllEntriesShouldReturnNotFoundWhenServiceThrowsException() {
        PlantingInformationService service = mock(PlantingInformationService.class);
        PlantingInformationController controller = new PlantingInformationController(service);

        User user = buildUser(1L, "dimitar@example.com");

        when(service.getAllEntries(1L)).thenThrow(new IllegalArgumentException("User not found"));

        ResponseEntity<List<PlantingInformationResponse>> response = controller.getAllEntries(user);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNull();

        verify(service, times(1)).getAllEntries(1L);
    }

    @Test
    void getEntryByIdShouldReturnEntryWhenFound() {
        PlantingInformationService service = mock(PlantingInformationService.class);
        PlantingInformationController controller = new PlantingInformationController(service);

        User user = buildUser(1L, "dimitar@example.com");
        PlantingInformationResponse entry = mock(PlantingInformationResponse.class);

        when(service.getEntryById(10L, 1L)).thenReturn(entry);

        ResponseEntity<PlantingInformationResponse> response = controller.getEntryById(10L, user);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(entry);

        verify(service, times(1)).getEntryById(10L, 1L);
    }

    @Test
    void getEntryByIdShouldReturnNotFoundWhenServiceThrowsException() {
        PlantingInformationService service = mock(PlantingInformationService.class);
        PlantingInformationController controller = new PlantingInformationController(service);

        User user = buildUser(1L, "dimitar@example.com");

        when(service.getEntryById(10L, 1L)).thenThrow(new IllegalArgumentException("Entry not found"));

        ResponseEntity<PlantingInformationResponse> response = controller.getEntryById(10L, user);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNull();

        verify(service, times(1)).getEntryById(10L, 1L);
    }

    @Test
    void createEntryShouldReturnCreatedWhenRequestIsValid() {
        PlantingInformationService service = mock(PlantingInformationService.class);
        PlantingInformationController controller = new PlantingInformationController(service);

        User user = buildUser(1L, "dimitar@example.com");
        CreatePlantingInformationRequest request = mock(CreatePlantingInformationRequest.class);
        PlantingInformationResponse createdEntry = mock(PlantingInformationResponse.class);

        when(service.createEntry(request, 1L)).thenReturn(createdEntry);

        ResponseEntity<?> response = controller.createEntry(request, user);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isEqualTo(createdEntry);

        verify(service, times(1)).createEntry(request, 1L);
    }

    @Test
    void createEntryShouldReturnBadRequestWhenServiceThrowsException() {
        PlantingInformationService service = mock(PlantingInformationService.class);
        PlantingInformationController controller = new PlantingInformationController(service);

        User user = buildUser(1L, "dimitar@example.com");
        CreatePlantingInformationRequest request = mock(CreatePlantingInformationRequest.class);

        when(service.createEntry(request, 1L)).thenThrow(new IllegalArgumentException("Invalid planting data"));

        ResponseEntity<?> response = controller.createEntry(request, user);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isEqualTo("Invalid planting data");

        verify(service, times(1)).createEntry(request, 1L);
    }

    @Test
    void updateEntryShouldReturnUpdatedEntryWhenRequestIsValid() {
        PlantingInformationService service = mock(PlantingInformationService.class);
        PlantingInformationController controller = new PlantingInformationController(service);

        User user = buildUser(1L, "dimitar@example.com");
        UpdatePlantingInformationRequest request = mock(UpdatePlantingInformationRequest.class);
        PlantingInformationResponse updatedEntry = mock(PlantingInformationResponse.class);

        when(service.updateEntry(10L, request, 1L)).thenReturn(updatedEntry);

        ResponseEntity<?> response = controller.updateEntry(10L, request, user);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(updatedEntry);

        verify(service, times(1)).updateEntry(10L, request, 1L);
    }

    @Test
    void updateEntryShouldReturnNotFoundWhenServiceThrowsException() {
        PlantingInformationService service = mock(PlantingInformationService.class);
        PlantingInformationController controller = new PlantingInformationController(service);

        User user = buildUser(1L, "dimitar@example.com");
        UpdatePlantingInformationRequest request = mock(UpdatePlantingInformationRequest.class);

        when(service.updateEntry(10L, request, 1L)).thenThrow(new IllegalArgumentException("Entry not found"));

        ResponseEntity<?> response = controller.updateEntry(10L, request, user);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isEqualTo("Entry not found");

        verify(service, times(1)).updateEntry(10L, request, 1L);
    }

    @Test
    void deleteEntryShouldReturnNoContentWhenDeleteSucceeds() {
        PlantingInformationService service = mock(PlantingInformationService.class);
        PlantingInformationController controller = new PlantingInformationController(service);

        User user = buildUser(1L, "dimitar@example.com");

        ResponseEntity<?> response = controller.deleteEntry(10L, user);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(response.getBody()).isNull();

        verify(service, times(1)).deleteEntry(10L, 1L);
    }

    @Test
    void deleteEntryShouldReturnNotFoundWhenServiceThrowsException() {
        PlantingInformationService service = mock(PlantingInformationService.class);
        PlantingInformationController controller = new PlantingInformationController(service);

        User user = buildUser(1L, "dimitar@example.com");

        doThrow(new IllegalArgumentException("Entry not found"))
                .when(service).deleteEntry(10L, 1L);

        ResponseEntity<?> response = controller.deleteEntry(10L, user);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isEqualTo("Entry not found");

        verify(service, times(1)).deleteEntry(10L, 1L);
    }

    private User buildUser(Long userId, String email) {
        User user = new User();
        user.setUserId(userId);
        user.setEmail(email);
        user.setName("Dimitar");
        return user;
    }
}