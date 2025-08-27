package com.fpt.capstone.tourism.controller.seller;

import com.fpt.capstone.tourism.dto.general.GeneralResponse;
import com.fpt.capstone.tourism.dto.general.PagingDTO;
import com.fpt.capstone.tourism.dto.request.ChangeStatusDTO;
import com.fpt.capstone.tourism.dto.request.RejectRequestDTO;
import com.fpt.capstone.tourism.dto.request.RequestBookingDTO;
import com.fpt.capstone.tourism.dto.request.RequestBookingSummaryDTO;
import com.fpt.capstone.tourism.model.enums.RequestBookingStatus;
import com.fpt.capstone.tourism.service.RequestBookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/seller/request-bookings")
public class SellerRequestBookingController {

    private final RequestBookingService requestBookingService;

    @GetMapping
    // Example request: GET http://localhost:8080/v1/seller/request-bookings?page=0&size=10
    public ResponseEntity<GeneralResponse<PagingDTO<RequestBookingSummaryDTO>>> getRequests(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search) {
        return ResponseEntity.ok(
                requestBookingService.getRequestsByStatus(RequestBookingStatus.PENDING, page, size, search)
        );
    }
    @GetMapping("/{id}")
    public ResponseEntity<GeneralResponse<RequestBookingDTO>> getRequestDetail(@PathVariable Long id) {
        return ResponseEntity.ok(requestBookingService.getRequest(id));
    }

    @PatchMapping("/{id}/approve")
    // Example request: PATCH http://localhost:8080/v1/seller/request-bookings/1/approve
    public ResponseEntity<GeneralResponse<RequestBookingDTO>> approveRequest(@PathVariable Long id) {
        return ResponseEntity.ok(
                requestBookingService.updateStatus(id, new ChangeStatusDTO(RequestBookingStatus.ACCEPTED.name()))
        );
    }
    @PatchMapping("/{id}/reject")
    public ResponseEntity<GeneralResponse<RequestBookingDTO>> rejectRequest(@PathVariable Long id,
                                                                            @RequestBody RejectRequestDTO dto) {
        return ResponseEntity.ok(requestBookingService.rejectRequest(id, dto.getReason()));
    }
}
