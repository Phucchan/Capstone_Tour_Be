package com.fpt.capstone.tourism.service.impl;

import com.fpt.capstone.tourism.constants.Constants;
import com.fpt.capstone.tourism.dto.common.PartnerServiceShortDTO;
import com.fpt.capstone.tourism.dto.common.ServiceTypeShortDTO;
import com.fpt.capstone.tourism.dto.common.location.LocationShortDTO;
import com.fpt.capstone.tourism.dto.general.GeneralResponse;
import com.fpt.capstone.tourism.dto.general.PagingDTO;
import com.fpt.capstone.tourism.dto.request.ChangeStatusDTO;
import com.fpt.capstone.tourism.dto.request.PartnerServiceCreateDTO;
import com.fpt.capstone.tourism.dto.request.tourManager.*;
import com.fpt.capstone.tourism.dto.response.ServiceInfoDTO;
import com.fpt.capstone.tourism.dto.response.UserBasicDTO;
import com.fpt.capstone.tourism.dto.response.tourManager.TourOptionsDTO;
import com.fpt.capstone.tourism.dto.response.tour.TourThemeOptionDTO;
import com.fpt.capstone.tourism.dto.response.tourManager.TourPaxManagerDTO;
import com.fpt.capstone.tourism.dto.response.tourManager.*;
import com.fpt.capstone.tourism.exception.common.BusinessException;
import com.fpt.capstone.tourism.helper.IHelper.TourHelper;
import com.fpt.capstone.tourism.mapper.UserMapper;
import com.fpt.capstone.tourism.mapper.booking.RequestBookingMapper;
import com.fpt.capstone.tourism.mapper.partner.ServiceInfoMapper;
import com.fpt.capstone.tourism.mapper.tourManager.TourDayManagerMapper;
import com.fpt.capstone.tourism.mapper.tourManager.TourManagementMapper;
import com.fpt.capstone.tourism.model.Location;
import com.fpt.capstone.tourism.model.RequestBooking;
import com.fpt.capstone.tourism.model.User;
import com.fpt.capstone.tourism.model.enums.PartnerServiceStatus;
import com.fpt.capstone.tourism.model.enums.RequestBookingStatus;
import com.fpt.capstone.tourism.model.enums.TourStatus;
import com.fpt.capstone.tourism.model.enums.TourType;
import com.fpt.capstone.tourism.model.partner.Partner;
import com.fpt.capstone.tourism.model.partner.PartnerService;
import com.fpt.capstone.tourism.model.partner.ServiceType;
import com.fpt.capstone.tourism.model.tour.*;
import com.fpt.capstone.tourism.repository.LocationRepository;
import com.fpt.capstone.tourism.repository.RequestBookingRepository;
import com.fpt.capstone.tourism.repository.TourManagementRepository;
import com.fpt.capstone.tourism.repository.partner.PartnerRepository;
import com.fpt.capstone.tourism.repository.partner.PartnerServiceRepository;
import com.fpt.capstone.tourism.repository.partner.ServiceTypeRepository;
import com.fpt.capstone.tourism.repository.tour.TourDayRepository;
import com.fpt.capstone.tourism.repository.tour.TourPaxRepository;
import com.fpt.capstone.tourism.repository.tour.TourScheduleRepository;
import com.fpt.capstone.tourism.repository.tour.TourThemeRepository;
import com.fpt.capstone.tourism.repository.user.UserRepository;
import com.fpt.capstone.tourism.service.LocationService;
import com.fpt.capstone.tourism.service.S3Service;
import com.fpt.capstone.tourism.specifications.TourSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class TourManagementServiceImpl implements com.fpt.capstone.tourism.service.TourManagementService {

    private final TourManagementRepository tourRepository;
    private final TourManagementMapper tourMapper;
    private final LocationService locationService;
    private final RequestBookingMapper requestBookingMapper;
    private final TourThemeRepository tourThemeRepository;
    private final LocationRepository locationRepository;
    private final TourDayRepository tourDayRepository;
    private final TourDayManagerMapper tourDayManagerMapper;
    private final PartnerServiceRepository partnerServiceRepository;
    private final ServiceTypeRepository serviceTypeRepository;
    private final TourHelper tourHelper;
    private final PartnerRepository partnerRepository;
    private final RequestBookingRepository requestBookingRepository;
    private final S3Service s3Service;
    private final ServiceInfoMapper serviceInfoMapper;


    @Value("${aws.s3.bucket-url}")
    private String bucketUrl;

    @Override
    public GeneralResponse<PagingDTO<TourResponseManagerDTO>> getListTours(int page, int size, String keyword,
                                                                           String tourCode, TourType tourType, TourStatus tourStatus) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
        Specification<Tour> spec = (root, query, cb) -> cb.conjunction();
        spec = spec.and((root, query, cb) -> cb.isFalse(root.get("deleted")));
        if (keyword != null && !keyword.trim().isEmpty()) {
            spec = spec.and(TourSpecification.hasNameLike(keyword));
        }
        if (tourType != null) {
            spec = spec.and(TourSpecification.hasTourType(tourType));
        }
        if (tourStatus != null) {
            spec = spec.and(TourSpecification.hasTourStatus(tourStatus));
        }
        if (tourCode != null && !tourCode.trim().isEmpty()) {
            spec = spec.and(TourSpecification.hasCodeLike(tourCode));
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
    public GeneralResponse<TourDetailManagerDTO> createTour(TourCreateManagerRequestDTO requestDTO, MultipartFile file) {
        Tour tour = new Tour();
        tour.setCode(tourHelper.generateTourCode());
        String name = requestDTO.getName();
        if (name == null || name.trim().isEmpty()) {
            throw BusinessException.of(HttpStatus.BAD_REQUEST, "Tour name is required");
        }
        tour.setName(name.trim());
        if (file != null && !file.isEmpty()) {
            String key = s3Service.uploadFile(file, "tours");
            tour.setThumbnailUrl(bucketUrl + "/" + key);
        }
        tour.setDescription(requestDTO.getDescription());
        tour.setTourType(requestDTO.getTourType() != null ? requestDTO.getTourType() : TourType.FIXED);
        tour.setTourStatus(TourStatus.DRAFT);
        if (requestDTO.getTourTransport() != null) {
            tour.setTourTransport(requestDTO.getTourTransport());
        }
        RequestBooking request = null;
        if (tour.getTourType() == TourType.CUSTOM) {
            if (requestDTO.getRequestBookingId() == null) {
                throw BusinessException.of(HttpStatus.BAD_REQUEST, "Custom tour requires requestBookingId");
            }
            request = requestBookingRepository.findById(requestDTO.getRequestBookingId())
                    .orElseThrow(() -> BusinessException.of(HttpStatus.NOT_FOUND, "Request booking not found"));
            request.setStatus(RequestBookingStatus.COMPLETED);
            tour.setCreatedBy(request.getUser());
        } else {
            if (requestDTO.getRequestBookingId() != null) {
                throw BusinessException.of(HttpStatus.BAD_REQUEST, "Fixed tour cannot have requestBookingId");
            }
        }
        if (requestDTO.getDepartLocationId() != null) {
            Location depart = locationRepository.findById(requestDTO.getDepartLocationId())
                    .orElseThrow(() -> BusinessException.of(HttpStatus.NOT_FOUND, "Depart location not found"));
            tour.setDepartLocation(depart);
        }
        if (requestDTO.getTourThemeIds() != null) {

            List<TourTheme> themes = tourThemeRepository.findAllById(requestDTO.getTourThemeIds());
            tour.setThemes(themes);
        }
        Tour savedTour = tourRepository.save(tour);
        if (request != null) {
            request.setTour(savedTour);
            requestBookingRepository.save(request);
            savedTour.setRequestBooking(request);
        }

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
    @Transactional
    public GeneralResponse<TourDetailManagerDTO> updateTour(Long id, TourUpdateManagerRequestDTO requestDTO, MultipartFile file) {
        Tour tour = tourRepository.findById(id)
                .orElseThrow(() -> BusinessException.of(HttpStatus.NOT_FOUND, "Tour not found"));

        // 1. Cập nhật thông tin cơ bản của tour
        if (requestDTO.getName() != null && !requestDTO.getName().trim().isEmpty()) {
            tour.setName(requestDTO.getName().trim());
        }
        if (requestDTO.getDescription() != null) {
            tour.setDescription(requestDTO.getDescription().trim());
        }

        // ✅ Cho phép tourStatus null hoặc rỗng => không cập nhật
        String statusStr = requestDTO.getTourStatus();
        if (statusStr != null && !statusStr.trim().isEmpty()) {
            try {
                TourStatus status = TourStatus.valueOf(statusStr.trim().toUpperCase());
                tour.setTourStatus(status);
            } catch (IllegalArgumentException ex) {
                throw BusinessException.of(HttpStatus.BAD_REQUEST, "Invalid tour status: " + statusStr);
            }
        }
        if (requestDTO.getTourTransport() != null) {
            tour.setTourTransport(requestDTO.getTourTransport());
        }

        // ✅ Cập nhật thumbnail nếu có file mới
        if (file != null && !file.isEmpty()) {
            String key = s3Service.uploadFile(file, "tours");
            tour.setThumbnailUrl(bucketUrl + "/" + key);
        }

        // ✅ Cập nhật điểm khởi hành nếu có
        if (requestDTO.getDepartLocationId() != null) {
            Location depart = locationRepository.findById(requestDTO.getDepartLocationId())
                    .orElseThrow(() -> BusinessException.of(HttpStatus.NOT_FOUND, "Location not found"));
            tour.setDepartLocation(depart);
        }

        // ✅ Cập nhật chủ đề tour nếu có
        if (requestDTO.getTourThemeIds() != null) {
            List<TourTheme> newThemes = tourThemeRepository.findAllById(requestDTO.getTourThemeIds());
            tour.getThemes().clear();
            tour.getThemes().addAll(newThemes);
        }

        // 2. LOGIC cập nhật danh sách ngày đi (TourDays)
        if (requestDTO.getDestinationLocationIds() != null) {
            List<Long> newDestinationIds = requestDTO.getDestinationLocationIds();
            List<TourDay> existingDays = tour.getTourDays();

            // Tìm những ngày cần xóa
            List<TourDay> daysToRemove = existingDays.stream()
                    .filter(day -> day.getLocation() != null && !newDestinationIds.contains(day.getLocation().getId()))
                    .collect(Collectors.toList());

            // ID điểm đến đã tồn tại
            List<Long> existingDestinationIds = existingDays.stream()
                    .map(day -> day.getLocation() != null ? day.getLocation().getId() : null)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

            // ID điểm đến mới cần thêm
            List<Long> idsToAdd = newDestinationIds.stream()
                    .filter(destId -> !existingDestinationIds.contains(destId))
                    .collect(Collectors.toList());

            // Xóa ngày không còn dùng
            existingDays.removeAll(daysToRemove);
            tourDayRepository.deleteAll(daysToRemove);

            // Thêm mới ngày
            for (Long destId : idsToAdd) {
                Location dest = locationRepository.findById(destId)
                        .orElseThrow(() -> BusinessException.of(HttpStatus.NOT_FOUND, "Location not found for new day"));
                TourDay day = new TourDay();
                day.setTour(tour);
                day.setLocation(dest);
                day.setTitle("Ngày mới: Tham quan " + dest.getName());
                existingDays.add(day);
            }

            // Sắp xếp lại thứ tự ngày đi
            List<TourDay> finalDayList = new ArrayList<>();
            Map<Long, TourDay> existingDaysByLocationId = existingDays.stream()
                    .filter(d -> d.getLocation() != null)
                    .collect(Collectors.toMap(d -> d.getLocation().getId(), d -> d, (d1, d2) -> d1));

            for (int i = 0; i < newDestinationIds.size(); i++) {
                Long destId = newDestinationIds.get(i);
                TourDay dayToOrder = existingDaysByLocationId.get(destId);
                if (dayToOrder != null) {
                    dayToOrder.setDayNumber(i + 1);
                    finalDayList.add(dayToOrder);
                }
            }

            tour.getTourDays().clear();
            tour.getTourDays().addAll(finalDayList);
            tour.setDurationDays(finalDayList.size());
        }

        Tour savedTour = tourRepository.save(tour);
        return GeneralResponse.of(buildDetailDTO(savedTour.getId()), "Tour updated successfully");
    }



    @Override
    public GeneralResponse<List<TourDayManagerDTO>> getTourDays(Long tourId) {
        tourRepository.findById(tourId)
                .orElseThrow(() -> BusinessException.of(HttpStatus.NOT_FOUND, "Tour not found"));

        List<TourDay> days = tourDayRepository.findByTourIdOrderByDayNumberAsc(tourId);

        // Map thủ công để đảm bảo dữ liệu services được load
        List<TourDayManagerDTO> dtos = days.stream()
                .map(day -> {
                    // Dùng mapper có sẵn để map các trường cơ bản
                    TourDayManagerDTO dto = tourDayManagerMapper.toDTO(day);

                    // Lấy và map danh sách services một cách tường minh
                    if (day.getServices() != null) {
                        List<ServiceInfoDTO> serviceInfos = day.getServices().stream()
                                .map(serviceInfoMapper::toDto) // Giả sử bạn có một mapper cho ServiceInfo
                                .collect(Collectors.toList());
                        dto.setServices(serviceInfos);
                    } else {
                        dto.setServices(new ArrayList<>()); // Trả về mảng rỗng thay vì null
                    }
                    return dto;
                })
                .collect(Collectors.toList());

        return GeneralResponse.of(dtos);
    }

    /**
     * HÀM HELPER MỚI: Dùng để xây dựng DTO chi tiết đúng chuẩn, trả về object {id, name}
     * thay vì chỉ trả về String. Đây là chìa khóa để sửa lỗi.
     */
    private TourDetailManagerDTO buildDetailDTO(Long tourId) {
        Tour tour = tourRepository.findById(tourId)
                .orElseThrow(() -> BusinessException.of(HttpStatus.NOT_FOUND, "Tour not found with id: " + tourId));

        List<TourThemeOptionDTO> themes = Optional.ofNullable(tour.getThemes())
                .orElse(Collections.emptyList())
                .stream()
                .map(theme -> new TourThemeOptionDTO(theme.getId(), theme.getName()))
                .collect(Collectors.toList());

        // SỬA LẠI: Dùng repository để lấy tour days, thay vì tour.getTourDays()
        List<LocationShortDTO> destinations = tourDayRepository.findByTourIdOrderByDayNumberAsc(tour.getId())
                .stream()
                .map(day -> {
                    Location loc = day.getLocation();
                    return (loc != null) ? new LocationShortDTO(loc.getId(), loc.getName()) : null;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        Location depart = tour.getDepartLocation();
        LocationShortDTO departDto = (depart != null)
                ? new LocationShortDTO(depart.getId(), depart.getName())
                : null;

        return TourDetailManagerDTO.builder()
                .id(tour.getId())
                .code(tour.getCode())
                .name(tour.getName())
                .thumbnailUrl(tour.getThumbnailUrl())
                .description(tour.getDescription())
                .tourType(tour.getTourType() != null ? tour.getTourType().name() : null)
                .tourStatus(tour.getTourStatus() != null ? tour.getTourStatus().name() : null)
                .tourTransport(tour.getTourTransport() != null ? tour.getTourTransport().name() : null)
                .durationDays(tour.getDurationDays())
                .departLocation(departDto)
                .destinations(destinations)
                .themes(themes)
                .requestBooking(tour.getRequestBooking() != null
                        ? requestBookingMapper.toDTO(tour.getRequestBooking())
                        : null)
                .requestBookingId(tour.getRequestBooking() != null ? tour.getRequestBooking().getId() : null)
                .createdByName(tour.getCreatedBy() != null ? tour.getCreatedBy().getFullName() : null)
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

        // 4. Thực hiện xóa TRỰC TIẾP (hard delete)
        tourDayRepository.delete(dayToDelete);

        // 5. Tải lại danh sách các ngày trong tour, đã được sắp xếp theo dayNumber
        List<TourDay> remainingDays = tourDayRepository.findByTourIdOrderByDayNumberAsc(tourId);

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
    public GeneralResponse<ServiceInfoDTO> createService(Long tourId, Long dayId, PartnerServiceCreateDTO dto) {
        TourDay day = tourDayRepository.findById(dayId)
                .orElseThrow(() -> BusinessException.of(HttpStatus.NOT_FOUND, Constants.Message.TOUR_DAY_NOT_FOUND));

        if (!day.getTour().getId().equals(tourId)) {
            throw BusinessException.of(HttpStatus.BAD_REQUEST, Constants.Message.TOUR_DAY_NOT_BELONG);
        }

        ServiceType type = serviceTypeRepository.findById(dto.getServiceTypeId())
                .orElseThrow(() -> BusinessException.of(HttpStatus.NOT_FOUND, Constants.Message.SERVICE_TYPE_NOT_FOUND));

        if (dto.getPartnerId() == null) {
            throw BusinessException.of(HttpStatus.BAD_REQUEST, Constants.Message.SERVICE_PROVIDER_NOT_FOUND);
        }
        Partner partner = partnerRepository.findById(Math.toIntExact(dto.getPartnerId()))
                .orElseThrow(() -> BusinessException.of(HttpStatus.NOT_FOUND, Constants.Message.SERVICE_PROVIDER_NOT_FOUND));
        PartnerService service = PartnerService.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .serviceType(type)
                .partner(partner)
                .status(PartnerServiceStatus.PENDING)
                .build();

        PartnerService savedService = partnerServiceRepository.save(service);
        day.getServices().add(savedService);
        tourDayRepository.save(day);

        return GeneralResponse.of(serviceInfoMapper.toDto(savedService), Constants.Message.SERVICE_CREATED);
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
    public GeneralResponse<TourOptionsDTO> getTourOptions() {
        List<TourThemeOptionDTO> themes = tourThemeRepository.findAll().stream()
                .map(t -> new TourThemeOptionDTO(t.getId(), t.getName()))
                .collect(Collectors.toList());

        List<LocationShortDTO> departures = locationService.getAllDepartures().stream()
                .map(d -> new LocationShortDTO(d.getId(), d.getName()))
                .collect(Collectors.toList());
        List<LocationShortDTO> destinations = locationService.getAllDestinations().stream()
                .map(d -> new LocationShortDTO(d.getId(), d.getName()))
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
    public GeneralResponse<List<PartnerServiceShortDTO>> getPartnerServices(Long serviceTypeId) {
        List<PartnerService> services;
        if (serviceTypeId != null) {
            services = partnerServiceRepository.findByServiceTypeId(serviceTypeId);
        } else {
            services = partnerServiceRepository.findAll();
        }
        services.forEach(service -> {
            if (service.getStatus() == null) {
                service.setStatus(PartnerServiceStatus.ACTIVE);
                partnerServiceRepository.save(service);
            }
        });

        List<PartnerServiceShortDTO> dtos = services.stream()
                .filter(s -> s.getStatus() == PartnerServiceStatus.ACTIVE)
                .map(s -> new PartnerServiceShortDTO(
                        s.getId(),
                        s.getName(),
                        s.getPartner() != null ? s.getPartner().getName() : null,
                        s.getServiceType().getName()
                ))
                .collect(Collectors.toList());

        return GeneralResponse.of(dtos);
    }
}
