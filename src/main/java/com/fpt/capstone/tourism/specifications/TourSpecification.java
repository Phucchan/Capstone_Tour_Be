package com.fpt.capstone.tourism.specifications;

import com.fpt.capstone.tourism.model.enums.TourStatus;
import com.fpt.capstone.tourism.model.tour.Tour;
import com.fpt.capstone.tourism.model.tour.TourDay;
import com.fpt.capstone.tourism.model.tour.TourPax;
import com.fpt.capstone.tourism.model.tour.TourSchedule;
import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

public class TourSpecification {
    /**
     *
     * Lọc các tour đã được PUBLISHED và không bị xóa.
     */
    public static Specification<Tour> isPublished() {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.and(
                        criteriaBuilder.equal(root.get("tourStatus"), TourStatus.PUBLISHED),
                        criteriaBuilder.isFalse(root.get("deleted"))
                );
    }

    /**
     * Lọc các tour có giá trong một khoảng nhất định.
     */
    public static Specification<Tour> hasPriceInRange(Double minPrice, Double maxPrice) {
        return (root, query, criteriaBuilder) -> {
            if (minPrice == null && maxPrice == null) {
                return null; // Bỏ qua nếu không có giá trị
            }
            // Join với bảng TourPax để lấy giá
            Join<Tour, TourPax> tourPaxJoin = root.join("tourPaxes");

            if (maxPrice == null) {
                return criteriaBuilder.greaterThanOrEqualTo(tourPaxJoin.get("sellingPrice"), minPrice);
            }
            if (minPrice == null) {
                return criteriaBuilder.lessThanOrEqualTo(tourPaxJoin.get("sellingPrice"), maxPrice);
            }
            return criteriaBuilder.between(tourPaxJoin.get("sellingPrice"), minPrice, maxPrice);
        };
    }

    /**
     * Lọc các tour theo điểm khởi hành.
     */
    public static Specification<Tour> hasDepartureLocation(Long departId) {
        return (root, query, criteriaBuilder) ->
                departId == null ? null : criteriaBuilder.equal(root.get("departLocation").get("id"), departId);
    }

    /**
     * Lọc các tour theo điểm đến (dựa trên các ngày trong tour).
     */
    public static Specification<Tour> hasDestination(Long destId) {
        return (root, query, criteriaBuilder) -> {
            if (destId == null) {
                return null;
            }
            // Join với bảng TourDay để lấy điểm đến của từng ngày
            Join<Tour, TourDay> tourDayJoin = root.join("tourDays");
            query.distinct(true); // Đảm bảo không trả về tour trùng lặp
            return criteriaBuilder.equal(tourDayJoin.get("location").get("id"), destId);
        };
    }

    /**
     * Lọc các tour có lịch trình khởi hành vào một ngày cụ thể.
     */
    public static Specification<Tour> hasDepartureDate(LocalDate date) {
        return (root, query, criteriaBuilder) -> {
            if (date == null) {
                return null;
            }
            // Join với bảng TourSchedule để lấy ngày khởi hành
            Join<Tour, TourSchedule> scheduleJoin = root.join("schedules");
            query.distinct(true); // Đảm bảo không trả về tour trùng lặp

            // So sánh phần DATE của trường departureDate (kiểu LocalDateTime) với ngày cung cấp
            return criteriaBuilder.equal(
                    criteriaBuilder.function("DATE", LocalDate.class, scheduleJoin.get("departureDate")),
                    date
            );
        };
    }
}