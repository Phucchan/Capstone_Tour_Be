package com.fpt.capstone.tourism.service.impl;

import com.fpt.capstone.tourism.constants.Constants;
import com.fpt.capstone.tourism.dto.common.ServiceTypeShortDTO;
import com.fpt.capstone.tourism.dto.common.location.LocationShortDTO;
import com.fpt.capstone.tourism.dto.general.GeneralResponse;
import com.fpt.capstone.tourism.dto.general.PagingDTO;
import com.fpt.capstone.tourism.dto.request.ChangeStatusDTO;
import com.fpt.capstone.tourism.dto.request.tourManager.*;
import com.fpt.capstone.tourism.dto.response.UserBasicDTO;
import com.fpt.capstone.tourism.dto.response.tourManager.TourOptionsDTO;
import com.fpt.capstone.tourism.dto.response.tour.TourThemeOptionDTO;
import com.fpt.capstone.tourism.dto.response.tourManager.TourPaxManagerDTO;
import com.fpt.capstone.tourism.dto.response.tourManager.*;
import com.fpt.capstone.tourism.exception.common.BusinessException;
import com.fpt.capstone.tourism.helper.IHelper.TourHelper;
import com.fpt.capstone.tourism.mapper.UserMapper;
import com.fpt.capstone.tourism.mapper.tourManager.TourDayManagerMapper;
import com.fpt.capstone.tourism.mapper.tourManager.TourManagementMapper;
import com.fpt.capstone.tourism.model.Location;
import com.fpt.capstone.tourism.model.User;
import com.fpt.capstone.tourism.model.enums.TourStatus;
import com.fpt.capstone.tourism.model.enums.TourType;
import com.fpt.capstone.tourism.model.partner.PartnerService;
import com.fpt.capstone.tourism.model.tour.*;
import com.fpt.capstone.tourism.repository.LocationRepository;
import com.fpt.capstone.tourism.repository.TourManagementRepository;
import com.fpt.capstone.tourism.repository.partner.PartnerServiceRepository;
import com.fpt.capstone.tourism.repository.partner.ServiceTypeRepository;
import com.fpt.capstone.tourism.repository.tour.TourDayRepository;
import com.fpt.capstone.tourism.repository.tour.TourPaxRepository;
import com.fpt.capstone.tourism.repository.tour.TourScheduleRepository;
import com.fpt.capstone.tourism.repository.tour.TourThemeRepository;
import com.fpt.capstone.tourism.repository.user.UserRepository;
import com.fpt.capstone.tourism.service.LocationService;
import com.fpt.capstone.tourism.specifications.TourSpecification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class TourManagementServiceImpl implements com.fpt.capstone.tourism.service.TourManagementService {

    @Autowired
    private TourManagementRepository tourRepository;
    @Autowired
    private TourManagementMapper tourMapper;
    @Autowired
    private LocationService locationService;
    @Autowired
    private TourThemeRepository tourThemeRepository;
    @Autowired
    private LocationRepository locationRepository;
    @Autowired
    private TourDayRepository tourDayRepository;
    @Autowired
    private TourDayManagerMapper tourDayManagerMapper;
    @Autowired
    private TourPaxRepository tourPaxRepository;
    @Autowired
    private PartnerServiceRepository partnerServiceRepository;
    @Autowired
    private ServiceTypeRepository serviceTypeRepository;
    @Autowired
    private TourScheduleRepository tourScheduleRepository;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TourHelper tourHelper;

    @Override
    public GeneralResponse<PagingDTO<TourResponseManagerDTO>> getListTours(int page, int size, String keyword,
                                                                           TourType tourType, TourStatus tourStatus) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
        Specification<Tour> spec = Specification.where(null);
        if (keyword != null && !keyword.trim().isEmpty()) {
            spec = spec.and(TourSpecification.hasNameLike(keyword));
        }
        if (tourType != null) {
            spec = spec.and(TourSpecification.hasTourType(tourType));
        }
        if (tourStatus != null) {
            spec = spec.and(TourSpecification.hasTourStatus(tourStatus));
        }
        Page<Tour> tours = tourRepository.findAll(spec, pageable);

        List<TourResponseManagerDTO> tourResponseDTOs = tours.getContent().stream()
                .map(tourMapper::toTourResponseDTO)
                .collect(Collectors.toList());
        PagingDTO<TourResponseManagerDTO> pagingDTO = PagingDTO.<TourResponseManagerDTO>builder()
                .page(tours.getNumber())
                .size(tours.getSize())
                .total(tours.getTotalElements())
                .items(tourResponseDTOs)
                .build();
        return GeneralResponse.of(pagingDTO);
    }

    @Override
    public GeneralResponse<TourDetailManagerDTO> createTour(TourCreateManagerRequestDTO requestDTO) {
        Tour tour = new Tour();
        tour.setCode(tourHelper.generateTourCode());
        tour.setName(requestDTO.getName());
        tour.setThumbnailUrl(requestDTO.getThumbnailUrl());
        tour.setDescription(requestDTO.getDescription());
        tour.setTourType(TourType.FIXED);
        tour.setTourStatus(TourStatus.DRAFT);

        Location depart = locationRepository.findById(requestDTO.getDepartLocationId())
                .orElseThrow(() -> BusinessException.of(HttpStatus.NOT_FOUND, "Depart location not found"));
        tour.setDepartLocation(depart);


        if (requestDTO.getTourThemeIds() != null) {

            List<TourTheme> themes = tourThemeRepository.findAllById(requestDTO.getTourThemeIds());
            tour.setThemes(themes);
        }
        Tour savedTour = tourRepository.save(tour);

        if (requestDTO.getDestinationLocationIds() != null) {
            int dayNumber = 1;
            for (Long destId : requestDTO.getDestinationLocationIds()) {
                Location dest = locationRepository.findById(destId)
                        .orElseThrow(() -> BusinessException.of(HttpStatus.NOT_FOUND, "Location not found"));
                TourDay day = new TourDay();
                day.setTour(savedTour);
                day.setDayNumber(dayNumber++);
                day.setLocation(dest);
                // Gán tiêu đề mặc định khi tạo tour
                day.setTitle("Ngày " + (dayNumber - 1) + ": Tham quan " + dest.getName());
                tourDayRepository.save(day);
            }
            savedTour.setDurationDays(dayNumber - 1);
            tourRepository.save(savedTour);
        }

        return GeneralResponse.of(buildDetailDTO(savedTour.getId()), "Tour created successfully");
    }


    @Override
    public GeneralResponse<Object> changeStatus(Long id, ChangeStatusDTO changeStatusDTO) {
        Tour tour = tourRepository.findById(id)
                .orElseThrow(() -> BusinessException.of(HttpStatus.NOT_FOUND, "Tour not found"));
        try {
            TourStatus newStatus = TourStatus.valueOf(changeStatusDTO.getNewStatus());
            tour.setTourStatus(newStatus);
            tourRepository.save(tour);
        } catch (IllegalArgumentException e) {
            throw BusinessException.of(HttpStatus.BAD_REQUEST, "Invalid tour status: " + changeStatusDTO.getNewStatus(), e);
        }
        return GeneralResponse.of((Object) null, "Status updated successfully");
    }

    @Override
    public GeneralResponse<TourDetailOptionsDTO> getTourDetail(Long id) {
        TourDetailManagerDTO detailDto = buildDetailDTO(id);
        TourOptionsDTO optionsDto = getTourOptions().getData();
        TourDetailOptionsDTO response = TourDetailOptionsDTO.builder()
                .detail(detailDto)
                .options(optionsDto)
                .build();
        return GeneralResponse.of(response);
    }

    @Override
    @Transactional // Đảm bảo tất cả các thao tác đều thành công hoặc thất bại cùng nhau
    public GeneralResponse<TourDetailManagerDTO> updateTour(Long id, TourUpdateManagerRequestDTO requestDTO) {
        // 1. Tìm tour cần cập nhật
        Tour tour = tourRepository.findById(id)
                .orElseThrow(() -> BusinessException.of(HttpStatus.NOT_FOUND, "Tour not found"));

        // 2. Cập nhật các trường thông tin cơ bản
        tour.setName(requestDTO.getName());
        tour.setThumbnailUrl(requestDTO.getThumbnailUrl());
        tour.setDescription(requestDTO.getDescription());
        tour.setTourStatus(TourStatus.valueOf(requestDTO.getTourStatus()));

        // 3. Cập nhật điểm khởi hành
        if (requestDTO.getDepartLocationId() != null) {
            Location depart = locationRepository.findById(requestDTO.getDepartLocationId())
                    .orElseThrow(() -> BusinessException.of(HttpStatus.NOT_FOUND, "Location not found"));
            tour.setDepartLocation(depart);
        }

        // 4. Cập nhật danh sách chủ đề (themes) - CÁCH LÀM ĐÚNG CHO ManyToMany
        if (requestDTO.getTourThemeIds() != null) {
            List<TourTheme> newThemes = tourThemeRepository.findAllById(requestDTO.getTourThemeIds());
            tour.getThemes().clear();
            tour.getThemes().addAll(newThemes);
        }

        // 5. Cập nhật danh sách điểm đến (destinations) - CÁCH LÀM ĐÚNG CHO OneToMany với orphanRemoval=true
        if (requestDTO.getDestinationLocationIds() != null) {
            // Xóa các ngày cũ khỏi tour
            tour.getTourDays().clear();

            // Thêm lại các ngày mới
            int dayNumber = 1;
            for (Long destId : requestDTO.getDestinationLocationIds()) {
                Location dest = locationRepository.findById(destId)
                        .orElseThrow(() -> BusinessException.of(HttpStatus.NOT_FOUND, "Location not found"));
                TourDay day = new TourDay();
                day.setTour(tour);
                day.setDayNumber(dayNumber++);
                day.setLocation(dest);
                day.setTitle("Ngày " + (dayNumber - 1) + ": Tham quan " + dest.getName());
                tour.getTourDays().add(day);
            }
            tour.setDurationDays(dayNumber - 1);
        }

        // 6. Lưu lại tour, JPA sẽ tự động xử lý các thay đổi trong collection
        Tour savedTour = tourRepository.save(tour);
        return GeneralResponse.of(buildDetailDTO(savedTour.getId()), "Tour updated successfully");
    }

    @Override
    public GeneralResponse<List<TourDayManagerDTO>> getTourDays(Long tourId) {
        tourRepository.findById(tourId)
                .orElseThrow(() -> BusinessException.of(HttpStatus.NOT_FOUND, "Tour not found"));
        // SỬA LẠI: Gọi phương thức repository mới để tự động lọc ra các ngày đã bị xóa
        List<TourDay> days = tourDayRepository.findByTourIdAndDeletedIsFalseOrderByDayNumberAsc(tourId);

        // Thay vì dùng mapper, chúng ta sẽ tự xây dựng DTO để đảm bảo đủ trường
        List<TourDayManagerDTO> dtos = days.stream().map(day -> {
            Location loc = day.getLocation();
            LocationShortDTO locationDTO = (loc != null) ?
                    new LocationShortDTO(loc.getId(), loc.getName(), loc.getDescription()) : null;

            List<ServiceTypeShortDTO> serviceTypeDTOs = day.getServiceTypes() != null ?
                    day.getServiceTypes().stream()
                            .map(st -> new ServiceTypeShortDTO(st.getId(), st.getCode(), st.getName()))
                            .collect(Collectors.toList()) : Collections.emptyList();

            return TourDayManagerDTO.builder()
                    .id(day.getId()) // <-- Quan trọng nhất
                    .dayNumber(day.getDayNumber())
                    .title(day.getTitle())
                    .description(day.getDescription())
                    .location(locationDTO) // <-- Thêm location
                    .serviceTypes(serviceTypeDTOs)
                    .build();
        }).collect(Collectors.toList());

        return GeneralResponse.of(dtos);
    }

    /**
     * HÀM HELPER MỚI: Dùng để xây dựng DTO chi tiết đúng chuẩn, trả về object {id, name}
     * thay vì chỉ trả về String. Đây là chìa khóa để sửa lỗi.
     */
    private TourDetailManagerDTO buildDetailDTO(Long tourId) {
        Tour tour = tourRepository.findById(tourId)
                .orElseThrow(() -> BusinessException.of(HttpStatus.NOT_FOUND, "Tour not found with id: " + tourId));

        List<TourThemeOptionDTO> themes = tour.getThemes().stream()
                .map(theme -> new TourThemeOptionDTO(theme.getId(), theme.getName()))
                .collect(Collectors.toList());

        // SỬA LẠI: Dùng repository để lấy tour days, thay vì tour.getTourDays()
        List<LocationShortDTO> destinations = tourDayRepository.findByTourIdOrderByDayNumberAsc(tour.getId())
                .stream()
                .map(day -> {
                    Location loc = day.getLocation();
                    return (loc != null) ? new LocationShortDTO(loc.getId(), loc.getName(), loc.getDescription()) : null;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        return TourDetailManagerDTO.builder()
                .id(tour.getId())
                .code(tour.getCode())
                .name(tour.getName())
                .thumbnailUrl(tour.getThumbnailUrl())
                .description(tour.getDescription())
                .tourType(tour.getTourType() != null ? tour.getTourType().name() : null)
                .tourStatus(tour.getTourStatus() != null ? tour.getTourStatus().name() : null)
                .durationDays(tour.getDurationDays())
                .departLocation(new LocationShortDTO(tour.getDepartLocation().getId(),
                        tour.getDepartLocation().getName(),
                        tour.getDepartLocation().getDescription()))
                .destinations(destinations)
                .themes(themes)
                .build();
    }

    @Override
    public GeneralResponse<TourDayManagerDTO> createTourDay(Long tourId, TourDayManagerCreateRequestDTO requestDTO) {
        Tour tour = tourRepository.findById(tourId)
                .orElseThrow(() -> BusinessException.of(HttpStatus.NOT_FOUND, "Tour not found"));

        int nextDayNumber = 1;
        List<TourDay> existing = tourDayRepository.findByTourIdOrderByDayNumberAsc(tourId);
        if (!existing.isEmpty()) {
            nextDayNumber = existing.get(existing.size() - 1).getDayNumber() + 1;
        }

        TourDay day = new TourDay();
        day.setTour(tour);
        day.setDayNumber(nextDayNumber);
        day.setTitle(requestDTO.getTitle());
        day.setDescription(requestDTO.getDescription());

        if (requestDTO.getLocationId() != null) {
            Location location = locationRepository.findById(requestDTO.getLocationId())
                    .orElseThrow(() -> BusinessException.of(HttpStatus.NOT_FOUND, "Location not found"));
            day.setLocation(location);
        }

        if (requestDTO.getServiceTypeIds() != null && !requestDTO.getServiceTypeIds().isEmpty()) {
            day.setServiceTypes(serviceTypeRepository.findAllById(requestDTO.getServiceTypeIds()));
        }

        TourDay saved = tourDayRepository.save(day);
        return GeneralResponse.of(tourDayManagerMapper.toDTO(saved), "Tour day created successfully");
    }

    @Override
    public GeneralResponse<TourDayManagerDTO> updateTourDay(Long tourId, Long dayId, TourDayManagerCreateRequestDTO requestDTO) {
        tourRepository.findById(tourId)
                .orElseThrow(() -> BusinessException.of(HttpStatus.NOT_FOUND, "Tour not found"));

        TourDay day = tourDayRepository.findById(dayId)
                .orElseThrow(() -> BusinessException.of(HttpStatus.NOT_FOUND, Constants.Message.TOUR_DAY_NOT_FOUND));

        if (!day.getTour().getId().equals(tourId)) {
            throw BusinessException.of(HttpStatus.BAD_REQUEST, Constants.Message.TOUR_DAY_NOT_BELONG);
        }

        if (requestDTO.getTitle() != null) day.setTitle(requestDTO.getTitle());
        if (requestDTO.getDescription() != null) day.setDescription(requestDTO.getDescription());

        if (requestDTO.getLocationId() != null) {
            Location location = locationRepository.findById(requestDTO.getLocationId())
                    .orElseThrow(() -> BusinessException.of(HttpStatus.NOT_FOUND, "Location not found"));
            day.setLocation(location);
        }

        if (requestDTO.getServiceTypeIds() != null) {
            day.setServiceTypes(serviceTypeRepository.findAllById(requestDTO.getServiceTypeIds()));
        }

        TourDay saved = tourDayRepository.save(day);
        return GeneralResponse.of(tourDayManagerMapper.toDTO(saved), Constants.Message.TOUR_DAY_UPDATED_SUCCESS);
    }

    @Override
    @Transactional // Đảm bảo tất cả các thao tác trong hàm này đều thành công
    public GeneralResponse<String> deleteTourDay(Long tourId, Long dayId) {
        // 1. Tìm tour cha
        Tour tour = tourRepository.findById(tourId)
                .orElseThrow(() -> BusinessException.of(HttpStatus.NOT_FOUND, "Tour not found"));

        // 2. Tìm ngày cần xóa
        TourDay dayToDelete = tourDayRepository.findById(dayId)
                .orElseThrow(() -> BusinessException.of(HttpStatus.NOT_FOUND, Constants.Message.TOUR_DAY_NOT_FOUND));

        // 3. Kiểm tra xem ngày có thuộc đúng tour không
        if (!dayToDelete.getTour().getId().equals(tourId)) {
            throw BusinessException.of(HttpStatus.BAD_REQUEST, Constants.Message.TOUR_DAY_NOT_BELONG);
        }

        // 4. Thực hiện xóa MỀM (soft delete)
        dayToDelete.softDelete();
        tourDayRepository.save(dayToDelete);

        // 5. Tải lại danh sách các ngày trong tour, đã được sắp xếp theo dayNumber
        // Lấy lại danh sách TẤT CẢ các ngày còn lại (chưa bị xóa mềm), đã được sắp xếp
        List<TourDay> remainingDays = tourDayRepository.findByTourIdAndDeletedIsFalseOrderByDayNumberAsc(tourId);

        // Đánh số lại (re-number) các ngày còn lại
        int currentDayNumber = 1;
        for (TourDay day : remainingDays) {
            day.setDayNumber(currentDayNumber++);
        }

        // Lưu lại danh sách đã được đánh số lại
        tourDayRepository.saveAll(remainingDays);

        // 6. Cập nhật lại thông tin của tour cha
        tour.setDurationDays(remainingDays.size());
        tourRepository.save(tour);

        return GeneralResponse.of(Constants.Message.TOUR_DAY_DELETED_SUCCESS);
    }

    @Override
    public GeneralResponse<List<ServiceBreakdownDTO>> getServiceBreakdown(Long tourId) {
        tourRepository.findById(tourId)
                .orElseThrow(() -> BusinessException.of(HttpStatus.NOT_FOUND, "Tour not found"));

        List<TourDay> days = tourDayRepository.findByTourIdOrderByDayNumberAsc(tourId);
        List<ServiceBreakdownDTO> results = days.stream()
                .flatMap(day -> day.getServices().stream().map(service -> ServiceBreakdownDTO.builder()
                        .dayId(day.getId())
                        .serviceId(service.getId())
                        .dayNumber(day.getDayNumber())
                        .serviceTypeName(service.getServiceType().getName())
                        .partnerName(service.getPartner().getName())
                        .partnerAddress(service.getPartner().getLocation().getName())
                        .nettPrice(service.getNettPrice())
                        .sellingPrice(service.getSellingPrice())
                        .build()))
                .collect(Collectors.toList());

        return GeneralResponse.of(results);
    }

    @Override
    public GeneralResponse<TourDayManagerDTO> addServiceToTourDay(Long tourId, Long dayId, Long serviceId) {
        TourDay day = tourDayRepository.findById(dayId)
                .orElseThrow(() -> BusinessException.of(HttpStatus.NOT_FOUND, Constants.Message.TOUR_DAY_NOT_FOUND));

        if (!day.getTour().getId().equals(tourId)) {
            throw BusinessException.of(HttpStatus.BAD_REQUEST, Constants.Message.TOUR_DAY_NOT_BELONG);
        }

        PartnerService service = partnerServiceRepository.findById(serviceId)
                .orElseThrow(() -> BusinessException.of(HttpStatus.NOT_FOUND, Constants.Message.SERVICE_NOT_FOUND));

        if (day.getServices().contains(service)) {
            throw BusinessException.of(HttpStatus.BAD_REQUEST, Constants.Message.SERVICE_ALREADY_EXISTS);
        }

        day.getServices().add(service);
        TourDay saved = tourDayRepository.save(day);

        TourDayManagerDTO dto = tourDayManagerMapper.toDTO(saved);
        return GeneralResponse.of(dto, Constants.Message.TOUR_DAY_SERVICE_ADDED_SUCCESS);
    }

    @Override
    public GeneralResponse<TourDayManagerDTO> updateServiceInTourDay(Long tourId, Long dayId, Long serviceId, Long newServiceId) {
        TourDay day = tourDayRepository.findById(dayId)
                .orElseThrow(() -> BusinessException.of(HttpStatus.NOT_FOUND, Constants.Message.TOUR_DAY_NOT_FOUND));

        if (!day.getTour().getId().equals(tourId)) {
            throw BusinessException.of(HttpStatus.BAD_REQUEST, Constants.Message.TOUR_DAY_NOT_BELONG);
        }

        PartnerService oldService = partnerServiceRepository.findById(serviceId)
                .orElseThrow(() -> BusinessException.of(HttpStatus.NOT_FOUND, Constants.Message.SERVICE_NOT_FOUND));

        if (!day.getServices().contains(oldService)) {
            throw BusinessException.of(HttpStatus.BAD_REQUEST, Constants.Message.SERVICE_NOT_ASSOCIATED);
        }

        PartnerService newService = partnerServiceRepository.findById(newServiceId)
                .orElseThrow(() -> BusinessException.of(HttpStatus.NOT_FOUND, Constants.Message.SERVICE_NOT_FOUND));

        if (day.getServices().contains(newService) && !newService.getId().equals(oldService.getId())) {
            throw BusinessException.of(HttpStatus.BAD_REQUEST, Constants.Message.SERVICE_ALREADY_EXISTS);
        }

        day.getServices().remove(oldService);
        day.getServices().add(newService);
        TourDay saved = tourDayRepository.save(day);

        TourDayManagerDTO dto = tourDayManagerMapper.toDTO(saved);
        return GeneralResponse.of(dto, Constants.Message.TOUR_DAY_SERVICE_UPDATED_SUCCESS);
    }

    @Override
    public GeneralResponse<TourDayManagerDTO> removeServiceFromTourDay(Long tourId, Long dayId, Long serviceId) {
        TourDay day = tourDayRepository.findById(dayId)
                .orElseThrow(() -> BusinessException.of(HttpStatus.NOT_FOUND, Constants.Message.TOUR_DAY_NOT_FOUND));

        if (!day.getTour().getId().equals(tourId)) {
            throw BusinessException.of(HttpStatus.BAD_REQUEST, Constants.Message.TOUR_DAY_NOT_BELONG);
        }

        PartnerService service = partnerServiceRepository.findById(serviceId)
                .orElseThrow(() -> BusinessException.of(HttpStatus.NOT_FOUND, Constants.Message.SERVICE_NOT_FOUND));

        if (!day.getServices().contains(service)) {
            throw BusinessException.of(HttpStatus.BAD_REQUEST, Constants.Message.SERVICE_NOT_ASSOCIATED);
        }

        day.getServices().remove(service);
        TourDay saved = tourDayRepository.save(day);

        TourDayManagerDTO dto = tourDayManagerMapper.toDTO(saved);
        return GeneralResponse.of(dto, Constants.Message.TOUR_DAY_SERVICE_REMOVED_SUCCESS);
    }


    @Override
    public GeneralResponse<TourPaxManagerDTO> createTourPax(Long tourId, TourPaxManagerCreateRequestDTO requestDTO) {
        Tour tour = tourRepository.findById(tourId)
                .orElseThrow(() -> BusinessException.of(HttpStatus.NOT_FOUND, "Tour not found"));
        if (requestDTO.getMinQuantity() > requestDTO.getMaxQuantity()) {
            throw BusinessException.of(HttpStatus.BAD_REQUEST, Constants.Message.PAX_CONFIG_INVALID_RANGE);
        }
        TourPax pax = new TourPax();
        pax.setTour(tour);
        pax.setMinQuantity(requestDTO.getMinQuantity());
        pax.setMaxQuantity(requestDTO.getMaxQuantity());
        TourPax saved = tourPaxRepository.save(pax);

        TourPaxManagerDTO dto = TourPaxManagerDTO.builder()
                .id(saved.getId())
                .minQuantity(saved.getMinQuantity())
                .maxQuantity(saved.getMaxQuantity())
                .build();

        return GeneralResponse.of(dto, Constants.Message.PAX_CONFIG_CREATE_SUCCESS);
    }

    @Override
    public GeneralResponse<TourPaxManagerDTO> getTourPax(Long tourId, Long paxId) {
        tourRepository.findById(tourId)
                .orElseThrow(() -> BusinessException.of(HttpStatus.NOT_FOUND, "Tour not found"));

        TourPax pax = tourPaxRepository.findById(paxId)
                .orElseThrow(() -> BusinessException.of(HttpStatus.NOT_FOUND, Constants.Message.PAX_CONFIG_NOT_FOUND));

        if (!pax.getTour().getId().equals(tourId)) {
            throw BusinessException.of(HttpStatus.BAD_REQUEST, Constants.Message.PAX_CONFIG_NOT_ASSOCIATED);
        }

        TourPaxManagerDTO dto = TourPaxManagerDTO.builder()
                .id(pax.getId())
                .minQuantity(pax.getMinQuantity())
                .maxQuantity(pax.getMaxQuantity())
                .build();

        return GeneralResponse.of(dto, Constants.Message.PAX_CONFIG_LOAD_SUCCESS);
    }

    @Override
    public GeneralResponse<TourPaxManagerDTO> updateTourPax(Long tourId, Long paxId, TourPaxManagerCreateRequestDTO requestDTO) {
        tourRepository.findById(tourId)
                .orElseThrow(() -> BusinessException.of(HttpStatus.NOT_FOUND, "Tour not found"));

        TourPax pax = tourPaxRepository.findById(paxId)
                .orElseThrow(() -> BusinessException.of(HttpStatus.NOT_FOUND, Constants.Message.PAX_CONFIG_NOT_FOUND));

        if (!pax.getTour().getId().equals(tourId)) {
            throw BusinessException.of(HttpStatus.BAD_REQUEST, Constants.Message.PAX_CONFIG_NOT_ASSOCIATED);
        }

        if (requestDTO.getMinQuantity() > requestDTO.getMaxQuantity()) {
            throw BusinessException.of(HttpStatus.BAD_REQUEST, Constants.Message.PAX_CONFIG_INVALID_RANGE);
        }

        pax.setMinQuantity(requestDTO.getMinQuantity());
        pax.setMaxQuantity(requestDTO.getMaxQuantity());
        TourPax saved = tourPaxRepository.save(pax);

        TourPaxManagerDTO dto = TourPaxManagerDTO.builder()
                .id(saved.getId())
                .minQuantity(saved.getMinQuantity())
                .maxQuantity(saved.getMaxQuantity())
                .build();

        return GeneralResponse.of(dto, Constants.Message.PAX_CONFIG_UPDATE_SUCCESS);
    }

    @Override
    public GeneralResponse<String> deleteTourPax(Long tourId, Long paxId) {
        tourRepository.findById(tourId)
                .orElseThrow(() -> BusinessException.of(HttpStatus.NOT_FOUND, "Tour not found"));

        TourPax pax = tourPaxRepository.findById(paxId)
                .orElseThrow(() -> BusinessException.of(HttpStatus.NOT_FOUND, Constants.Message.PAX_CONFIG_NOT_FOUND));

        if (!pax.getTour().getId().equals(tourId)) {
            throw BusinessException.of(HttpStatus.BAD_REQUEST, Constants.Message.PAX_CONFIG_NOT_ASSOCIATED);
        }

        pax.softDelete();
        tourPaxRepository.save(pax);
        return GeneralResponse.of(Constants.Message.PAX_CONFIG_DELETE_SUCCESS);
    }

    @Override
    public GeneralResponse<TourOptionsDTO> getTourOptions() {
        List<TourThemeOptionDTO> themes = tourThemeRepository.findAll().stream()
                .map(t -> new TourThemeOptionDTO(t.getId(), t.getName()))
                .collect(Collectors.toList());

        List<LocationShortDTO> departures = locationService.getAllDepartures().stream()
                .map(d -> new LocationShortDTO(d.getId(), d.getName(), d.getDescription()))
                .collect(Collectors.toList());
        List<LocationShortDTO> destinations = locationService.getAllDestinations().stream()
                .map(d -> new LocationShortDTO(d.getId(), d.getName(), d.getDescription()))
                .collect(Collectors.toList());

        TourOptionsDTO dto = TourOptionsDTO.builder()
                .themes(themes)
                .departures(departures)
                .destinations(destinations)
                .build();
        return GeneralResponse.of(dto);
    }


    @Override
    public GeneralResponse<List<ServiceTypeShortDTO>> getServiceTypes() {
        try {
            List<ServiceTypeShortDTO> dtos = serviceTypeRepository.findAll().stream()
                    .map(st -> new ServiceTypeShortDTO(st.getId(), st.getCode(), st.getName()))
                    .collect(Collectors.toList());
            return new GeneralResponse<>(HttpStatus.OK.value(), Constants.Message.GET_SERVICE_LIST_SUCCESS, dtos);
        } catch (BusinessException be) {
            throw be;
        } catch (Exception ex) {
            throw BusinessException.of(Constants.Message.GET_SERVICE_LIST_FAIL, ex);
        }
    }
    @Override
    public GeneralResponse<TourScheduleManagerDTO> createTourSchedule(Long tourId, TourScheduleCreateRequestDTO requestDTO) {
        Tour tour = tourRepository.findById(tourId)
                .orElseThrow(() -> BusinessException.of(HttpStatus.NOT_FOUND, "Tour not found"));
        if (tour.getTourStatus() != TourStatus.PUBLISHED) {
            throw BusinessException.of(HttpStatus.BAD_REQUEST, Constants.Message.TOUR_NOT_PUBLISHED);
        }

        TourPax tourPax = tourPaxRepository.findById(requestDTO.getTourPaxId())
                .orElseThrow(() -> BusinessException.of(HttpStatus.NOT_FOUND, Constants.Message.TOUR_PAX_NOT_FOUND));
        if (!tourPax.getTour().getId().equals(tourId)) {
            throw BusinessException.of(HttpStatus.BAD_REQUEST, Constants.Message.TOUR_PAX_MISMATCH);
        }

        User coordinator = userRepository.findById(requestDTO.getCoordinatorId())
                .orElseThrow(() -> BusinessException.of(HttpStatus.NOT_FOUND, Constants.UserExceptionInformation.USER_NOT_FOUND_MESSAGE));

        TourSchedule schedule = new TourSchedule();
        schedule.setTour(tour);
        schedule.setCoordinator(coordinator);
        schedule.setTourPax(tourPax);
        schedule.setDepartureDate(requestDTO.getDepartureDate());
        schedule.setEndDate(requestDTO.getDepartureDate().plusDays(tour.getDurationDays() - 1L));
        schedule.setPublished(false);

        TourSchedule saved = tourScheduleRepository.save(schedule);

        TourScheduleManagerDTO dto = TourScheduleManagerDTO.builder()
                .id(saved.getId())
                .coordinatorId(saved.getCoordinator().getId())
                .tourPaxId(saved.getTourPax().getId())
                .departureDate(saved.getDepartureDate())
                .endDate(saved.getEndDate())
                .build();

        return GeneralResponse.of(dto, Constants.Message.SCHEDULE_CREATED_SUCCESS);
    }
    @Override
    public GeneralResponse<TourScheduleOptionsDTO> getScheduleOptions(Long tourId) {
        Tour tour = tourRepository.findById(tourId)
                .orElseThrow(() -> BusinessException.of(HttpStatus.NOT_FOUND, "Tour not found"));

        List<UserBasicDTO> coordinatorDtos = userRepository.findByRoleName("SERVICE_COORDINATOR").stream()
                .map(userMapper::toUserBasicDTO)
                .collect(Collectors.toList());

        List<TourPaxManagerDTO> paxDtos = tourPaxRepository.findByTourId(tourId).stream()
                .map(p -> TourPaxManagerDTO.builder()
                        .id(p.getId())
                        .minQuantity(p.getMinQuantity())
                        .maxQuantity(p.getMaxQuantity())
                        .build())
                .collect(Collectors.toList());

        List<TourThemeOptionDTO> themeDtos = tour.getThemes().stream()
                .map(t -> new TourThemeOptionDTO(t.getId(), t.getName()))
                .collect(Collectors.toList());

        UserBasicDTO creator = tour.getCreatedBy() != null ? userMapper.toUserBasicDTO(tour.getCreatedBy()) : null;

        TourScheduleOptionsDTO dto = TourScheduleOptionsDTO.builder()
                .tourId(tour.getId())
                .tourName(tour.getName())
                .tourType(tour.getTourType() != null ? tour.getTourType().name() : null)
                .themes(themeDtos)
                .durationDays(tour.getDurationDays())
                .createdDate(tour.getCreatedAt())
                .createdBy(creator)
                .coordinators(coordinatorDtos)
                .tourPaxes(paxDtos)
                .build();

        return GeneralResponse.of(dto, Constants.Message.GET_SCHEDULE_OPTIONS_SUCCESS);
    }
}
