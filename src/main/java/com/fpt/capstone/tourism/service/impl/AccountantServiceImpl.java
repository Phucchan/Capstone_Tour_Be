package com.fpt.capstone.tourism.service.impl;

import com.fpt.capstone.tourism.dto.general.GeneralResponse;
import com.fpt.capstone.tourism.dto.general.PagingDTO;
import com.fpt.capstone.tourism.dto.request.accountatn.CreateBillRequestDTO;
import com.fpt.capstone.tourism.dto.response.accountant.*;
import com.fpt.capstone.tourism.exception.common.BusinessException;
import com.fpt.capstone.tourism.model.User;
import com.fpt.capstone.tourism.model.enums.BookingStatus;
import com.fpt.capstone.tourism.model.enums.PaymentMethod;
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
import org.springframework.security.core.context.SecurityContextHolder;
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


    private User getCurrentUser() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    @Override
    public GeneralResponse<PagingDTO<BookingRefundDTO>> getRefundRequests(String search, BookingStatus status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("start_date").descending());
        Page<Object[]> resultPage = bookingRepository.findRefundRequests(search, status != null ? status.name() : null, pageable);

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
                .filter(pb -> pb.getPaymentType() == PaymentType.RECEIPT)
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
    public GeneralResponse<BookingRefundDetailDTO> approveRefundRequest(Long bookingId) {
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
    public GeneralResponse<BookingRefundDetailDTO> cancelRefundRequest(Long bookingId) {
        Booking booking = bookingRepository.findByIdForUpdate(bookingId)
                .orElseThrow(() -> BusinessException.of(HttpStatus.NOT_FOUND, "Booking not found"));

        if (booking.getBookingStatus() != BookingStatus.CANCEL_REQUESTED) {
            throw BusinessException.of(HttpStatus.BAD_REQUEST, "Booking is not in cancel requested status");
        }

        booking.setBookingStatus(BookingStatus.CONFIRMED);
        bookingRepository.save(booking);

        BookingRefundDetailDTO dto = getRefundRequestDetail(bookingId).getData();
        return new GeneralResponse<>(HttpStatus.OK.value(), "Request cancelled", dto);
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
        Refund refund = refundRepository.findByBooking_Id(bookingId)
                .orElseThrow(() -> BusinessException.of(HttpStatus.BAD_REQUEST, "Refund info not found"));

        String bookingCode = booking.getBookingCode();
        BigDecimal refundAmount = refund.getRefundAmount();
        if (refundAmount == null || refundAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw BusinessException.of(HttpStatus.BAD_REQUEST, "Customer has not paid, cannot create refund bill");
        }

        PaymentBill bill = PaymentBill.builder()
                .billNumber("PB-" + java.util.UUID.randomUUID().toString().substring(0, 8))
                .paymentForType(PaymentForType.CUSTOMER)
                .bookingCode(bookingCode)
                .paidBy(request.getPaidBy())
                .creator(User.builder().id(getCurrentUser().getId()).build())
                .receiverAddress(booking.getUser().getAddress())
                .payTo(request.getPayTo())
                .paymentType(request.getPaymentType())
                .paymentMethod(request.getPaymentMethod())
                .totalAmount(refundAmount)
                .note(request.getNote())
                .build();
        bill.setCreatedAt(request.getCreatedDate());
        bill.setUpdatedAt(request.getCreatedDate());

        PaymentBill savedBill = paymentBillRepository.save(bill);

        PaymentBillItem item = PaymentBillItem.builder()
                .paymentBill(savedBill)
                .content(request.getContent())
                .quantity(1)
                .unitPrice(refundAmount.intValue())
                .discount(0)
                .amount(refundAmount)
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
        String bookingCode = (String) row[1];
        String tourCode = (String) row[2];
        String tourName = (String) row[3];
        String tourType = row[4] != null ? row[4].toString() : null;
        LocalDateTime startDate = row[5] != null ? ((Timestamp) row[5]).toLocalDateTime() : null;
        String status = row[6] != null ? row[6].toString() : null;
        String customerName = (String) row[7];

        return BookingRefundDTO.builder()
                .bookingId(bookingId)
                .bookingCode(bookingCode)
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
    @Transactional(readOnly = true)
    public GeneralResponse<BookingSettlementDTO> getBookingSettlement(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> BusinessException.of(HttpStatus.NOT_FOUND, "Booking not found"));

        List<BookingService> services = bookingServiceRepository.findWithServiceByBookingId(bookingId);

        List<BookingServiceSettlementDTO> serviceDtos = services.stream()
                .filter(bs -> bs.getService() != null)
                .map(bs -> {
                    var service = bs.getService();
                    return BookingServiceSettlementDTO.builder()
                            .serviceId(service != null ? service.getId() : null)
                            .serviceName(service != null ? service.getName() : null)
                            .dayNumber(bs.getDayNumber())
                            .pax(bs.getQuantity())
                            .costPerPax(service != null ? service.getNettPrice() : null)
                            .sellingPrice(service != null ? service.getSellingPrice() : null)
                            .build();
                })
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

        List<PaymentBill> receipts = paymentBillRepository
                .findPaymentBillsByBookingCode(booking.getBookingCode())
                .stream()
                .filter(pb -> pb.getPaymentType() == PaymentType.RECEIPT)
                .toList();
        BigDecimal paidAmount = receipts.stream()
                .map(PaymentBill::getTotalAmount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal amount;
        if (paidAmount.compareTo(BigDecimal.ZERO) == 0) {
            amount = BigDecimal.valueOf(booking.getDepositAmount());
        } else {
            amount = BigDecimal.valueOf(booking.getTotalAmount()).subtract(paidAmount);
        }
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw BusinessException.of(HttpStatus.BAD_REQUEST, "Customer has not paid yet");
        }

        PaymentBill bill = PaymentBill.builder()
                .billNumber("RC-" + java.util.UUID.randomUUID().toString().substring(0, 8))
                .paymentForType(PaymentForType.COMPANY)
                .bookingCode(booking.getBookingCode())
                .paidBy(request.getPaidBy())
                .creator(User.builder().id(getCurrentUser().getId()).build())
                .receiverAddress(booking.getUser().getAddress())
                .payTo(request.getPayTo())
                .paymentType(request.getPaymentType())
                .paymentMethod(request.getPaymentMethod())
                .totalAmount(amount)
                .note(request.getNote())
                .build();
        bill.setCreatedAt(request.getCreatedDate());
        bill.setUpdatedAt(request.getCreatedDate());

        PaymentBill savedBill = paymentBillRepository.save(bill);

        PaymentBillItem item = PaymentBillItem.builder()
                .paymentBill(savedBill)
                .content(request.getContent())
                .quantity(1)
                .unitPrice(amount.intValue())
                .discount(0)
                .amount(amount)
                .paymentBillItemStatus(PaymentBillItemStatus.PAID)
                .build();


        paymentBillItemRepository.save(item);
        savedBill.setItems(List.of(item));

        if (booking.getPaymentMethod() == PaymentMethod.CASH
                && booking.getBookingStatus() == BookingStatus.PENDING) {
            booking.setBookingStatus(BookingStatus.PAID);
            bookingRepository.save(booking);
        }

        BookingSettlementDTO dto = getBookingSettlement(bookingId).getData();
        return new GeneralResponse<>(HttpStatus.OK.value(), "Receipt bill created", dto);
    }

    @Override
    @Transactional
    public GeneralResponse<BookingSettlementDTO> createPaymentBill(Long bookingId, CreateBillRequestDTO request) {
        Booking booking = bookingRepository.findByIdForUpdate(bookingId)
                .orElseThrow(() -> BusinessException.of(HttpStatus.NOT_FOUND, "Booking not found"));

        List<BookingService> services = bookingServiceRepository.findWithServiceByBookingId(bookingId);
        if (services.isEmpty()) {
            throw BusinessException.of(HttpStatus.BAD_REQUEST, "No services found for booking");
        }

        List<PaymentBillItem> items = services.stream().map(bs -> {
            BigDecimal amount = BigDecimal.valueOf(bs.getService().getNettPrice())
                    .multiply(BigDecimal.valueOf(bs.getQuantity()));
            return PaymentBillItem.builder()
                    .content(bs.getService().getName())
                    .quantity(bs.getQuantity())
                    .unitPrice((int) bs.getService().getNettPrice())
                    .discount(0)
                    .amount(amount)
                    .paymentBillItemStatus(PaymentBillItemStatus.PENDING)
                    .build();
        }).toList();

        BigDecimal totalAmount = items.stream()
                .map(PaymentBillItem::getAmount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        PaymentBill bill = PaymentBill.builder()
                .billNumber("PM-" + java.util.UUID.randomUUID().toString().substring(0, 8))
                .paymentForType(PaymentForType.PARTNER)
                .bookingCode(booking.getBookingCode())
                .paidBy(request.getPaidBy())
                .creator(User.builder().id(getCurrentUser().getId()).build())
                .receiverAddress(booking.getUser().getAddress())
                .payTo(request.getPayTo())
                .paymentType(request.getPaymentType())
                .paymentMethod(request.getPaymentMethod())
                .totalAmount(totalAmount)
                .note(request.getNote())
                .build();
        bill.setCreatedAt(request.getCreatedDate());
        bill.setUpdatedAt(request.getCreatedDate());

        PaymentBill savedBill = paymentBillRepository.save(bill);

        items.forEach(i -> i.setPaymentBill(savedBill));
        paymentBillItemRepository.saveAll(items);
        savedBill.setItems(items);

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
    @Override
    @Transactional
    public GeneralResponse<String> markBookingCompleted(Long bookingId) {
        Booking booking = bookingRepository.findByIdForUpdate(bookingId)
                .orElseThrow(() -> BusinessException.of(HttpStatus.NOT_FOUND, "Booking not found"));

        if (booking.getBookingStatus() != BookingStatus.CONFIRMED) {
            throw BusinessException.of(HttpStatus.BAD_REQUEST, "Booking is not in confirmed status");
        }

        booking.setBookingStatus(BookingStatus.COMPLETED);
        bookingRepository.save(booking);
        return new GeneralResponse<>(HttpStatus.OK.value(), "Booking marked as completed", null);
    }


    private PaymentBillListDTO toPaymentBillListDTO(PaymentBill pb) {
        List<PaymentBillItem> items = paymentBillItemRepository.findAllByPaymentBill_Id(pb.getId());

        PaymentBillItemStatus status = PaymentBillItemStatus.PENDING;
        if (items != null && !items.isEmpty()) {
            status = items.get(0).getPaymentBillItemStatus();
        }

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
                .status(status)
                .build();
    }
}