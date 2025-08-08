package com.fpt.capstone.tourism.service.impl;

import com.fpt.capstone.tourism.dto.general.GeneralResponse;
import com.fpt.capstone.tourism.dto.general.PagingDTO;
import com.fpt.capstone.tourism.dto.request.accountatn.RefundBillRequestDTO;
import com.fpt.capstone.tourism.dto.response.accountant.BookingRefundDTO;
import com.fpt.capstone.tourism.dto.response.accountant.BookingRefundDetailDTO;
import com.fpt.capstone.tourism.dto.response.accountant.RefundBillDTO;
import com.fpt.capstone.tourism.dto.response.accountant.RefundBillItemDTO;
import com.fpt.capstone.tourism.exception.common.BusinessException;
import com.fpt.capstone.tourism.model.User;
import com.fpt.capstone.tourism.model.enums.BookingStatus;
import com.fpt.capstone.tourism.model.enums.PaymentMethod;
import com.fpt.capstone.tourism.model.payment.*;
import com.fpt.capstone.tourism.model.tour.Booking;
import com.fpt.capstone.tourism.repository.RefundRepository;
import com.fpt.capstone.tourism.service.AccountantService;
import com.fpt.capstone.tourism.repository.booking.BookingRepository;
import com.fpt.capstone.tourism.service.payment.PaymentBillItemRepository;
import com.fpt.capstone.tourism.service.payment.PaymentBillRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class AccountantServiceImpl implements AccountantService {

    private final BookingRepository bookingRepository;
    private final RefundRepository refundRepository;
    private final PaymentBillRepository paymentBillRepository;
    private final PaymentBillItemRepository paymentBillItemRepository;

    @Override
    public GeneralResponse<PagingDTO<BookingRefundDTO>> getRefundRequests(String search, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("start_date").descending());
        Page<Object[]> resultPage = bookingRepository.findRefundRequests(search, pageable);

        List<BookingRefundDTO> items = resultPage.getContent().stream()
                .map(this::mapToDto)
                .toList();

        PagingDTO<BookingRefundDTO> pagingDTO = PagingDTO.<BookingRefundDTO>builder()
                .page(resultPage.getNumber())
                .size(resultPage.getSize())
                .total(resultPage.getTotalElements())
                .items(items)
                .build();

        return new GeneralResponse<>(HttpStatus.OK.value(), "Success", pagingDTO);
    }
    @Override
    public GeneralResponse<BookingRefundDetailDTO> getRefundRequestDetail(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> BusinessException.of(HttpStatus.NOT_FOUND, "Booking not found"));

        Refund refund = refundRepository.findByBooking_Id(bookingId).orElse(null);

        List<PaymentBill> payments = paymentBillRepository.findPaymentBillsByBookingCode(booking.getBookingCode());
        BigDecimal totalAmount = BigDecimal.valueOf(booking.getTotalAmount());
        BigDecimal paidAmount = payments.stream()
                .map(PaymentBill::getTotalAmount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal remainingAmount = totalAmount.subtract(paidAmount);

        PaymentBill refundBill = payments.stream()
                .filter(pb -> pb.getPaymentType() == PaymentType.REFUND)
                .findFirst()
                .orElse(null);

        RefundBillDTO refundBillDTO = null;
        if (refundBill != null) {
            List<RefundBillItemDTO> itemDTOs = paymentBillItemRepository
                    .findAllByPaymentBill_Id(refundBill.getId())
                    .stream()
                    .map(i -> RefundBillItemDTO.builder()
                            .content(i.getContent())
                            .unitPrice(i.getUnitPrice())
                            .quantity(i.getQuantity())
                            .discount(i.getDiscount())
                            .amount(i.getAmount())
                            .status(i.getPaymentBillItemStatus())
                            .build())
                    .toList();

            refundBillDTO = RefundBillDTO.builder()
                    .bookingCode(refundBill.getBookingCode())
                    .payTo(refundBill.getPayTo())
                    .paidBy(refundBill.getPaidBy())
                    .createdDate(refundBill.getCreatedAt())
                    .paymentType(refundBill.getPaymentType())
                    .paymentMethod(refundBill.getPaymentMethod())
                    .note(refundBill.getNote())
                    .totalAmount(refundBill.getTotalAmount())
                    .items(itemDTOs)
                    .build();
        }

        var schedule = booking.getTourSchedule();
        var tour = schedule.getTour();
        var user = booking.getUser();

        BookingRefundDetailDTO dto = BookingRefundDetailDTO.builder()
                .bookingId(booking.getId())
                .bookingCode(booking.getBookingCode())
                .tourCode(tour.getCode())
                .tourName(tour.getName())
                .tourType(tour.getTourType() != null ? tour.getTourType().toString() : null)
                .startDate(schedule.getDepartureDate())
                .status(booking.getBookingStatus() != null ? booking.getBookingStatus().toString() : null)
                .customerName(user != null ? user.getFullName() : null)
                .totalAmount(totalAmount)
                .paidAmount(paidAmount)
                .remainingAmount(remainingAmount)
                .refundAmount(refund != null ? refund.getRefundAmount() : null)
                .bankAccountNumber(refund != null ? refund.getBankAccountNumber() : null)
                .bankAccountHolder(refund != null ? refund.getBankAccountHolder() : null)
                .bankName(refund != null ? refund.getBankName() : null)
                .refundBill(refundBillDTO)
                .build();

        return new GeneralResponse<>(HttpStatus.OK.value(), "Success", dto);
    }
    @Override
    @Transactional
    public GeneralResponse<BookingRefundDetailDTO> cancelRefundRequest(Long bookingId) {
        Booking booking = bookingRepository.findByIdForUpdate(bookingId)
                .orElseThrow(() -> BusinessException.of(HttpStatus.NOT_FOUND, "Booking not found"));

        if (booking.getBookingStatus() != BookingStatus.CANCEL_REQUESTED) {
            throw BusinessException.of(HttpStatus.BAD_REQUEST, "Booking is not in cancel requested status");
        }

        booking.setBookingStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);

        BookingRefundDetailDTO dto = getRefundRequestDetail(bookingId).getData();
        return new GeneralResponse<>(HttpStatus.OK.value(), "Cancelled", dto);
    }
    @Override
    @Transactional
    public GeneralResponse<BookingRefundDetailDTO> createRefundBill(Long bookingId, RefundBillRequestDTO request) {
        Booking booking = bookingRepository.findByIdForUpdate(bookingId)
                .orElseThrow(() -> BusinessException.of(HttpStatus.NOT_FOUND, "Booking not found"));

        if (booking.getBookingStatus() != BookingStatus.CANCELLED) {
            throw BusinessException.of(HttpStatus.BAD_REQUEST, "Booking is not in cancelled status");
        }

        // Ensure refund information exists for booking
        refundRepository.findByBooking_Id(bookingId)
                .orElseThrow(() -> BusinessException.of(HttpStatus.BAD_REQUEST, "Refund info not found"));

        String bookingCode = getRefundRequestDetail(bookingId).getData().getBookingCode();

        PaymentBill bill = PaymentBill.builder()
                .billNumber("PB-" + java.util.UUID.randomUUID().toString().substring(0, 8))
                .paymentForType(PaymentForType.CUSTOMER)
                .bookingCode(bookingCode)
                .paidBy(request.getPaidBy())
                .creator(User.builder().id(booking.getUser().getId()).build())
                .receiverAddress(booking.getUser().getAddress())
                .payTo(request.getPayTo())
                .paymentType(request.getPaymentType())
                .paymentMethod(request.getPaymentMethod())
                .totalAmount(request.getAmount())
                .note(request.getNote())
                .build();
        bill.setCreatedAt(request.getCreatedDate());
        bill.setUpdatedAt(request.getCreatedDate());

        PaymentBill savedBill = paymentBillRepository.save(bill);

        PaymentBillItem item = PaymentBillItem.builder()
                .paymentBill(savedBill)
                .content(request.getContent())
                .quantity(request.getQuantity())
                .unitPrice(request.getUnitPrice())
                .discount(request.getDiscount())
                .amount(request.getAmount())
                .paymentBillItemStatus(request.getStatus())
                .build();

        paymentBillItemRepository.save(item);
        savedBill.setItems(List.of(item));

        booking.setBookingStatus(BookingStatus.REFUNDED);
        bookingRepository.save(booking);

        BookingRefundDetailDTO dto = getRefundRequestDetail(bookingId).getData();
        return new GeneralResponse<>(HttpStatus.OK.value(), "Refund bill created", dto);
    }





    private BookingRefundDTO mapToDto(Object[] row) {
        Long bookingId = row[0] != null ? ((Number) row[0]).longValue() : null;
        String tourCode = (String) row[1];
        String tourName = (String) row[2];
        String tourType = row[3] != null ? row[3].toString() : null;
        LocalDateTime startDate = row[4] != null ? ((Timestamp) row[4]).toLocalDateTime() : null;
        String status = row[5] != null ? row[5].toString() : null;
        String customerName = (String) row[6];

        return BookingRefundDTO.builder()
                .bookingId(bookingId)
                .tourCode(tourCode)
                .tourName(tourName)
                .tourType(tourType)
                .startDate(startDate)
                .status(status)
                .customerName(customerName)
                .build();
    }
}