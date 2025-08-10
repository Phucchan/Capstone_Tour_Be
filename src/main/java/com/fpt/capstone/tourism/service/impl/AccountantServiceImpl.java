package com.fpt.capstone.tourism.service.impl;

import com.fpt.capstone.tourism.dto.general.GeneralResponse;
import com.fpt.capstone.tourism.dto.general.PagingDTO;
import com.fpt.capstone.tourism.dto.request.accountatn.CreateBillRequestDTO;
import com.fpt.capstone.tourism.dto.response.accountant.*;
import com.fpt.capstone.tourism.exception.common.BusinessException;
import com.fpt.capstone.tourism.model.User;
import com.fpt.capstone.tourism.model.enums.BookingStatus;
import com.fpt.capstone.tourism.model.payment.*;
import com.fpt.capstone.tourism.model.tour.Booking;
import com.fpt.capstone.tourism.model.tour.BookingService;
import com.fpt.capstone.tourism.repository.RefundRepository;
import com.fpt.capstone.tourism.repository.booking.BookingServiceRepository;
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
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@RequiredArgsConstructor
public class AccountantServiceImpl implements AccountantService {

    private final BookingRepository bookingRepository;
    private final RefundRepository refundRepository;
    private final PaymentBillRepository paymentBillRepository;
    private final PaymentBillItemRepository paymentBillItemRepository;
    private final BookingServiceRepository bookingServiceRepository;

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
    public GeneralResponse<BookingRefundDetailDTO> createRefundBill(Long bookingId, CreateBillRequestDTO request) {
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
                .paymentBillItemStatus(PaymentBillItemStatus.PENDING)
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
    @Override
    public GeneralResponse<PagingDTO<BookingListDTO>> getBookings(String search, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<Booking> bookingPage = StringUtils.hasText(search)
                ? bookingRepository.findByTourSchedule_Tour_NameContainingIgnoreCase(search, pageable)
                : bookingRepository.findAll(pageable);

        AtomicInteger counter = new AtomicInteger(1);
        List<BookingListDTO> items = bookingPage.getContent().stream()
                .map(b -> BookingListDTO.builder()
                        .stt(counter.getAndIncrement())
                        .bookingId(b.getId())
                        .tourName(b.getTourSchedule().getTour().getName())
                        .startDate(b.getTourSchedule().getDepartureDate())
                        .endDate(b.getTourSchedule().getEndDate())
                        .tourType(b.getTourSchedule().getTour().getTourType().name())
                        .duration(b.getTourSchedule().getTour().getDurationDays())
                        .status(b.getBookingStatus() != null ? b.getBookingStatus().name() : null)
                        .build())
                .toList();

        PagingDTO<BookingListDTO> paging = PagingDTO.<BookingListDTO>builder()
                .page(bookingPage.getNumber())
                .size(bookingPage.getSize())
                .total(bookingPage.getTotalElements())
                .items(items)
                .build();

        return new GeneralResponse<>(HttpStatus.OK.value(), "Success", paging);
    }
    @Override
    public GeneralResponse<BookingSettlementDTO> getBookingSettlement(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> BusinessException.of(HttpStatus.NOT_FOUND, "Booking not found"));

        List<BookingService> services = bookingServiceRepository.findWithServiceByBookingId(bookingId);

        List<BookingServiceSettlementDTO> serviceDtos = services.stream()
                .map(bs -> BookingServiceSettlementDTO.builder()
                        .serviceName(bs.getService().getName())
                        .dayNumber(bs.getDayNumber())
                        .pax(bs.getQuantity())
                        .costPerPax(bs.getService().getNettPrice())
                        .sellingPrice(bs.getService().getSellingPrice())
                        .build())
                .toList();

        List<PaymentBill> bills = paymentBillRepository.findPaymentBillsByBookingCode(booking.getBookingCode());

        List<PaymentBillListDTO> receiptBills = bills.stream()
                .filter(pb -> pb.getPaymentType() == PaymentType.RECEIPT)
                .map(this::toPaymentBillListDTO)
                .toList();

        List<PaymentBillListDTO> paymentBills = bills.stream()
                .filter(pb -> pb.getPaymentType() == PaymentType.PAYMENT)
                .map(this::toPaymentBillListDTO)
                .toList();

        List<PaymentBillListDTO> refundBills = bills.stream()
                .filter(pb -> pb.getPaymentType() == PaymentType.REFUND)
                .map(this::toPaymentBillListDTO)
                .toList();

        var schedule = booking.getTourSchedule();

        BookingSettlementDTO dto = BookingSettlementDTO.builder()
                .bookingId(booking.getId())
                .bookingCode(booking.getBookingCode())
                .tourName(schedule.getTour().getName())
                .startDate(schedule.getDepartureDate())
                .endDate(schedule.getEndDate())
                .tourType(schedule.getTour().getTourType().name())
                .duration(schedule.getTour().getDurationDays())
                .status(booking.getBookingStatus() != null ? booking.getBookingStatus().name() : null)
                .services(serviceDtos)
                .receiptBills(receiptBills)
                .paymentBills(paymentBills)
                .refundBills(refundBills)
                .build();

        return new GeneralResponse<>(HttpStatus.OK.value(), "Success", dto);
    }
    @Override
    @Transactional
    public GeneralResponse<BookingSettlementDTO> createReceiptBill(Long bookingId, CreateBillRequestDTO request) {
        Booking booking = bookingRepository.findByIdForUpdate(bookingId)
                .orElseThrow(() -> BusinessException.of(HttpStatus.NOT_FOUND, "Booking not found"));

        PaymentBill bill = PaymentBill.builder()
                .billNumber("RC-" + java.util.UUID.randomUUID().toString().substring(0, 8))
                .paymentForType(PaymentForType.COMPANY)
                .bookingCode(booking.getBookingCode())
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
                .paymentBillItemStatus(PaymentBillItemStatus.PENDING)
                .build();

        paymentBillItemRepository.save(item);
        savedBill.setItems(List.of(item));

        BookingSettlementDTO dto = getBookingSettlement(bookingId).getData();
        return new GeneralResponse<>(HttpStatus.OK.value(), "Receipt bill created", dto);
    }

    @Override
    @Transactional
    public GeneralResponse<BookingSettlementDTO> createPaymentBill(Long bookingId, CreateBillRequestDTO request) {
        Booking booking = bookingRepository.findByIdForUpdate(bookingId)
                .orElseThrow(() -> BusinessException.of(HttpStatus.NOT_FOUND, "Booking not found"));

        PaymentBill bill = PaymentBill.builder()
                .billNumber("PM-" + java.util.UUID.randomUUID().toString().substring(0, 8))
                .paymentForType(PaymentForType.PARTNER)
                .bookingCode(booking.getBookingCode())
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
                .paymentBillItemStatus(PaymentBillItemStatus.PENDING)
                .build();

        paymentBillItemRepository.save(item);
        savedBill.setItems(List.of(item));

        BookingSettlementDTO dto = getBookingSettlement(bookingId).getData();
        return new GeneralResponse<>(HttpStatus.OK.value(), "Payment bill created", dto);
    }
    @Override
    @Transactional
    public GeneralResponse<String> markBillPaid(Long billId) {
        PaymentBill bill = paymentBillRepository.findById(billId)
                .orElseThrow(() -> BusinessException.of(HttpStatus.NOT_FOUND, "Bill not found"));
        List<PaymentBillItem> items = paymentBillItemRepository.findAllByPaymentBill_Id(billId);
        if (items.stream().anyMatch(i -> i.getPaymentBillItemStatus() != PaymentBillItemStatus.PENDING)) {
            throw BusinessException.of(HttpStatus.BAD_REQUEST, "Bill is not in pending status");
        }
        items.forEach(i -> i.setPaymentBillItemStatus(PaymentBillItemStatus.PAID));
        paymentBillItemRepository.saveAll(items);
        return new GeneralResponse<>(HttpStatus.OK.value(), "Bill marked as paid", null);
    }


    private PaymentBillListDTO toPaymentBillListDTO(PaymentBill pb) {
        return PaymentBillListDTO.builder()
                .billId(pb.getId())
                .billNumber(pb.getBillNumber())
                .bookingCode(pb.getBookingCode())
                .payTo(pb.getPayTo())
                .paidBy(pb.getPaidBy())
                .createdDate(pb.getCreatedAt())
                .paymentType(pb.getPaymentType())
                .paymentMethod(pb.getPaymentMethod())
                .totalAmount(pb.getTotalAmount())
                .build();
    }
}