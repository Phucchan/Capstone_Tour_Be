package com.fpt.capstone.tourism.constants;


public class Constants {
    public static final class UserExceptionInformation {
        public static final String USER_NOT_FOUND_MESSAGE = "Không tìm thấy người dùng";
        public static final String USERNAME_ALREADY_EXISTS_MESSAGE = "Tên đăng nhập đã tồn tại";
        public static final String EMAIL_ALREADY_EXISTS_MESSAGE = "Email đã được sử dụng";
        public static final String PHONE_ALREADY_EXISTS_MESSAGE = "Số điện thoại đã được sử dụng";
        public static final String FAIL_TO_SAVE_USER_MESSAGE = "Lưu thông tin người dùng thất bại";
        public static final String GENDER_INVALID = "Giới tính không hợp lệ";
        public static final String USER_INFORMATION_NULL_OR_EMPTY = "Thông tin này là bắt buộc";
        public static final String USERNAME_INVALID = "Tên đăng nhập chỉ bao gồm chữ cái, số, dấu gạch ngang (-), gạch dưới (_) và có độ dài từ 8 đến 30 ký tự";
        public static final String PASSWORD_INVALID = "Mật khẩu phải từ 8 ký tự trở lên, bao gồm ít nhất 1 chữ hoa, 1 chữ thường và 1 ký tự đặc biệt";
        public static final String FULL_NAME_INVALID = "Họ tên phải bắt đầu bằng chữ cái, chỉ chứa chữ cái và khoảng trắng";
        public static final String PHONE_INVALID = "Số điện thoại phải gồm đúng 10-15 chữ số";
        public static final String EMAIL_INVALID = "Email không hợp lệ";
        public static final String ROLES_NAME_INVALID = "Tên vai trò không hợp lệ";
        public static final String USER_NOT_FOUND = "Không tìm thấy người dùng, vui lòng đăng nhập bằng tài khoản hợp lệ để xem thông tin cá nhân";
    }

    public static final class Default {
        public static final String DEFAULT_AVATAR_URL = "https://i.pinimg.com/originals/0d/64/98/0d64989794b1a4c9d89bff571d3d5842.jpg";
    }

    public static final class Message {
        //===================================================
        // Authentication & User Management Messages
        //===================================================
        // Login related
        public static final String LOGIN_SUCCESS_MESSAGE = "Đăng nhập thành công";
        public static final String LOGIN_FAIL_MESSAGE = "Đăng nhập thất bại! Tên đăng nhập hoặc mật khẩu không đúng";
        public static final String USER_NOT_AUTHENTICATED = "Người dùng chưa được xác thực";

        // Password related
        public static final String PASSWORDS_DO_NOT_MATCH_MESSAGE = "Mật khẩu và xác nhận mật khẩu không trùng khớp";
        public static final String INVALID_OLD_PASSWORD_MESSAGE = "Mật khẩu hiện tại không chính xác";
        public static final String RESET_PASSWORD_EMAIL_SENT = "Mật khẩu mới đã được gửi về email của bạn";

        // Registration & Email confirmation
        public static final String EMAIL_CONFIRMATION_REQUEST_MESSAGE = "Cảm ơn bạn đã đăng ký. Vui lòng kiểm tra email để hoàn tất xác minh";
        public static final String REGISTER_FAIL_MESSAGE = "Đăng ký tài khoản thất bại do lỗi hệ thống!";
        public static final String INVALID_CONFIRMATION_TOKEN_MESSAGE = "Liên kết xác nhận không hợp lệ hoặc đã hết hạn";
        public static final String TOKEN_USED_MESSAGE = "Email này đã được xác nhận trước đó. Không cần xác nhận lại";
        public static final String EMAIL_CONFIRMED_SUCCESS_MESSAGE = "Đăng ký thành công! Vui lòng đăng nhập để tiếp tục";
        public static final String TOKEN_ENCRYPTION_FAILED_MESSAGE = "Mã hóa token thất bại";
        public static final String CONFIRM_EMAIL_FAILED = "Xác nhận email thất bại";

        public static final String SEND_EMAIL_ACCOUNT_FAIL = "Gửi email thất bại";


        // Roles
        public static final String ROLES_RETRIEVED_SUCCESS_MESSAGE = "Lấy danh sách vai trò thành công";
        public static final String ROLES_RETRIEVED_FAIL_MESSAGE = "Lấy danh sách vai trò thất bại";


        //===================================================
        // Location Related Messages
        //===================================================
        // Location validation
        public static final String EMPTY_LOCATION_NAME = "Tên địa điểm không được để trống";
        public static final String EMPTY_LOCATION_DESCRIPTION = "Mô tả địa điểm không được để trống";
        public static final String EMPTY_LOCATION_IMAGE = "Hình ảnh địa điểm không được để trống";
        public static final String EMPTY_LOCATION_GEO_POSITION = "Vị trí địa lý của địa điểm không được để trống";
        public static final String EMPTY_LOCATION = "Địa điểm không được để trống";
        public static final String TOUR_NOT_PUBLISHED = "Tour chưa được mở bán";
        // Location management
        public static final String EXISTED_LOCATION = "Địa điểm đã tồn tại";
        public static final String CREATE_LOCATION_SUCCESS = "Tạo địa điểm thành công";
        public static final String CREATE_LOCATION_FAIL = "Tạo địa điểm thất bại";
        public static final String LOCATION_NOT_FOUND = "Không tìm thấy địa điểm";
        public static final String DEPART_LOCATION_NOT_FOUND = "Không tìm thấy địa điểm khởi hành";
        public static final String LOCATION_DETAIL_LOAD_SUCCESS = "Tải chi tiết địa điểm thành công";
        public static final String LOCATION_DETAIL_LOAD_FAIL = "Tải chi tiết địa điểm thất bại";
        public static final String GET_LOCATIONS_SUCCESS = "Lấy danh sách địa điểm thành công";
        public static final String GET_LOCATIONS_FAIL = "Lấy danh sách địa điểm thất bại";
        public static final String LOCATION_NOT_FOUND_BY_ID = "Không tìm thấy địa điểm với ID: %s";
        public static final String GET_TOUR_LOCATIONS_FAIL = "Không thể lấy địa điểm của tour";

        //===================================================
        // PartnerService Provider Related Messages
        //===================================================
        // Provider management
        public static final String SERVICE_PROVIDER_NOT_FOUND = "Không tìm thấy nhà cung cấp dịch vụ. Vui lòng thử lại";
        public static final String CREATE_SERVICE_PROVIDER_SUCCESS = "Tạo nhà cung cấp dịch vụ thành công";
        public static final String CREATE_SERVICE_PROVIDER_FAIL = "Tạo nhà cung cấp dịch vụ thất bại";
        public static final String UPDATE_SERVICE_PROVIDER_SUCCESS = "Cập nhật nhà cung cấp dịch vụ thành công";
        public static final String UPDATE_SERVICE_PROVIDER_FAIL = "Cập nhật nhà cung cấp dịch vụ thất bại";
        public static final String SERVICE_PROVIDER_RETRIEVED_SUCCESS = "Lấy danh sách nhà cung cấp dịch vụ thành công.";
        public static final String SERVICE_PROVIDER_RETRIEVED_FAILED = "Lỗi khi lấy danh sách nhà cung cấp dịch vụ.";
        public static final String GET_PROVIDER_BY_LOCATION_FAIL = "Không thể lấy nhà cung cấp theo địa điểm";

        // Provider services
        public static final String PROVIDER_SERVICES_LOAD_SUCCESS = "Tải dịch vụ của nhà cung cấp thành công";
        public static final String PROVIDER_SERVICES_LOAD_FAIL = "Tải dịch vụ của nhà cung cấp thất bại";
        public static final String PROVIDER_CATEGORY_SERVICES_LOAD_SUCCESS = "Tải dịch vụ theo loại của nhà cung cấp thành công";
        public static final String PROVIDER_CATEGORY_SERVICES_LOAD_FAIL = "Tải dịch vụ theo loại của nhà cung cấp thất bại";
        public static final String GET_PROVIDER_SERVICES_FAIL = "Không thể lấy dịch vụ của nhà cung cấp";
        public static final String NO_PROVIDER_FOR_CATEGORY_IN_LOCATION = "Không có nhà cung cấp dịch vụ cho danh mục tại địa điểm đã chọn.";

        //===================================================
        // PartnerService Related Messages
        //===================================================
        // PartnerService management
        public static final String SERVICE_NOT_FOUND = "Không tìm thấy dịch vụ";
        public static final String SERVICE_RETRIEVE_SUCCESS = "Lấy thông tin dịch vụ thành công";
        public static final String SERVICE_CREATED = "Tạo dịch vụ thành công";
        public static final String SERVICE_UPDATED = "Cập nhật dịch vụ thành công";
        public static final String SERVICE_DELETED = "Xóa dịch vụ thành công";
        public static final String SERVICE_RESTORED = "Khôi phục dịch vụ thành công";
        public static final String SERVICE_NAME_EXISTS = "Tên dịch vụ đã tồn tại";
        public static final String NO_SERVICES_AVAILABLE = "Không có dịch vụ tour nào";
        public static final String SERVICE_ID_REQUIRED = "Cần có ID dịch vụ để thêm dịch vụ vào ngày tour mới";
        public static final String SERVICE_NOT_ASSOCIATED = "Dịch vụ không được liên kết với tour này";

        // PartnerService operations
        public static final String SERVICE_CREATE_FAIL = "Tạo dịch vụ thất bại";
        public static final String SERVICE_UPDATE_FAIL = "Cập nhật dịch vụ thất bại";
        public static final String SERVICE_DELETE_FAIL = "Xóa dịch vụ thất bại";
        public static final String CHANGE_SERVICE_STATUS_FAIL = "Thay đổi trạng thái dịch vụ thất bại";
        public static final String SERVICE_REMOVE_FAIL = "Xóa dịch vụ khỏi tour thất bại";
        public static final String UPDATE_SERVICES_FAIL = "Không thể cập nhật dịch vụ";
        public static final String CREATE_SERVICE_FAIL = "Tạo chi tiết dịch vụ thất bại";
        public static final String UPDATE_SERVICE_FAIL = "Cập nhật chi tiết dịch vụ thất bại";

        // PartnerService categories
        public static final String SERVICE_CATEGORY_NOT_FOUND = "Không tìm thấy danh mục dịch vụ";
        public static final String INVALID_SERVICE_CATEGORY = "Loại dịch vụ không hợp lệ. Phải là: Khách sạn, Nhà hàng, Vận chuyển";
        public static final String SERVICE_CATEGORY_REQUIRED = "Cần chọn ít nhất một danh mục dịch vụ.";
        public static final String HOTEL = "Hotel";
        public static final String RESTAURANT = "Restaurant";
        public static final String TRANSPORT = "Transport";
        public static final String ACTIVITY = "Activity";
        public static final String TICKET = "Flight Ticket";
        public static final String EMPTY_ACTIVITY_CATEGORY = "Danh mục hoạt động không được để trống";

        // PartnerService details
        public static final String SERVICE_DETAILS_RETRIEVED = "Lấy chi tiết dịch vụ thành công";
        public static final String GET_SERVICE_DETAIL_FAIL = "Lấy chi tiết dịch vụ thất bại";
        public static final String SERVICE_DETAIL_LOAD_SUCCESS = "Tải chi tiết dịch vụ thành công";
        public static final String SERVICE_DETAIL_LOAD_FAIL = "Tải chi tiết dịch vụ thất bại";

        // Services in tour days
        public static final String TOUR_DAY_SERVICES_RETRIEVED = "Lấy danh sách dịch vụ theo ngày tour thành công";
        public static final String GET_TOUR_DAY_SERVICE_FAIL = "Lấy dịch vụ theo ngày tour thất bại";
        public static final String SERVICES_LOAD_SUCCESS = "Tải dịch vụ tour thành công";
        public static final String SERVICES_LOAD_FAIL = "Tải dịch vụ tour thất bại";
        public static final String DAY_NUMBER_REQUIRED_WHEN_CREATING_SERVICE = "Số ngày là bắt buộc khi tạo mới dịch vụ";
        public static final String GET_TOUR_BOOKING_SERVICES_FOR_SALE_FAIL = "Lấy dịch vụ đặt tour để bán thất bại";
        public static final String CANCEL_TOUR_BOOKING_SERVICES_FAIL = "Hủy dịch vụ đặt tour thất bại";
        public static final String SEND_EMAIL_ORDER_SERVICE_FAIL = "Gửi email đặt dịch vụ thất bại";

        //===================================================
        // Hotel PartnerService Specific Messages
        //===================================================
        public static final String ROOM_NOT_FOUND = "Không tìm thấy phòng";
        public static final String ROOM_DETAILS_REQUIRED = "Cần có thông tin chi tiết phòng cho dịch vụ khách sạn";
        public static final String INVALID_ROOM_CAPACITY = "Sức chứa phòng phải lớn hơn 0";
        public static final String NEGATIVE_AVAILABLE_QUANTITY = "Số lượng khả dụng không được là số âm";
        public static final String UNEXPECTED_ROOM_DETAILS = "Không nên có thông tin phòng cho dịch vụ không phải khách sạn";
        public static final String ROOM_NOT_FOUND_BY_SERVICE_ID = "Không tìm thấy phòng cho ID dịch vụ: %s";
        public static final String HOTEL_DETAIL_LOAD_SUCCESS = "Tải chi tiết khách sạn thành công";
        public static final String HOTEL_DETAIL_LOAD_FAIL = "Tải chi tiết khách sạn thất bại";

        //===================================================
        // Restaurant PartnerService Specific Messages
        //===================================================
        public static final String MEAL_NOT_FOUND = "Không tìm thấy bữa ăn";
        public static final String MEAL_DETAILS_REQUIRED = "Cần có thông tin chi tiết bữa ăn cho dịch vụ nhà hàng";
        public static final String MEAL_TYPE_REQUIRED = "Phải chỉ định loại bữa ăn";
        public static final String UNEXPECTED_MEAL_DETAILS = "Không nên có thông tin bữa ăn cho dịch vụ không phải nhà hàng";
        public static final String MEAL_NOT_FOUND_BY_SERVICE_ID = "Không tìm thấy bữa ăn cho ID dịch vụ: %s";

        //===================================================
        // Transport PartnerService Specific Messages
        //===================================================
        public static final String TRANSPORT_NOT_FOUND = "Không tìm thấy phương tiện";
        public static final String TRANSPORT_DETAILS_REQUIRED = "Cần có thông tin chi tiết phương tiện cho dịch vụ vận chuyển";
        public static final String INVALID_SEAT_CAPACITY = "Số ghế phải lớn hơn 0";
        public static final String UNEXPECTED_TRANSPORT_DETAILS = "Không nên có thông tin phương tiện cho dịch vụ không phải vận chuyển";
        public static final String TRANSPORT_NOT_FOUND_BY_SERVICE_ID = "Không tìm thấy phương tiện di chuyển cho ID dịch vụ: %s";

        //===================================================
        // Tour Related Messages
        //===================================================
        // Tour management
        public static final String TOUR_NOT_FOUND = "Không tìm thấy tour";
        public static final String TOUR_CREATE_SUCCESS = "Tạo tour thành công";
        public static final String TOUR_UPDATE_SUCCESS = "Cập nhật tour thành công";
        public static final String TOUR_CREATE_FAIL = "Tạo tour thất bại";
        public static final String TOUR_UPDATE_FAIL = "Cập nhật tour thất bại";
        public static final String TOUR_DETAIL_LOAD_SUCCESS = "Tải chi tiết tour thành công";
        public static final String TOUR_DETAIL_LOAD_FAIL = "Tải chi tiết tour thất bại";
        public static final String GET_TOUR_DETAILS_FOR_SALE_FAIL = "Lấy chi tiết tour để bán thất bại";
        public static final String GET_TOUR_PRIVATE_LIST_FAIL = "Không thể lấy danh sách tour riêng tư";
        public static final String UPDATE_TOUR_PRIVATE_FAIL = "Không thể cập nhật tour riêng tư";
        public static final String UPDATE_TOUR_STATUS_FAIL = "Không thể cập nhật trạng thái tour";
        public static final String ERROR_RETRIEVING_TRENDING_TOURS = "Lỗi khi lấy các tour thịnh hành";
        public static final String GET_ALL_PUBLIC_TOUR_FAIL = "Lấy tất cả các tour công khai thất bại";
        public static final String ERROR_RETRIEVING_SAME_LOCATION_PUBLIC_TOURS = "Lỗi khi lấy các tour công khai cùng địa điểm";

        // Tour approval process
        public static final String ONLY_DRAFT_OR_REJECTED_CAN_BE_UPDATED = "Chỉ những tour có trạng thái DRAFT hoặc REJECTED mới có thể được cập nhật";
        public static final String TOUR_STATUS_NOT_VALID = "Trạng thái tour không phải là pending, approved hoặc rejected";
        public static final String GET_DETAIL_TOUR_SUCCESS = "Lấy thông tin chi tiết tour cần xử lý thành công";
        public static final String TOUR_STATUS_NOT_PENDING = "Trạng thái tour không phải là pending";
        public static final String APPROVE_SUCCESS = "Phê duyệt thành công";
        public static final String REJECT_SUCCESS = "Từ chối thành công";
        public static final String ONLY_DRAFT_CAN_BE_SENT = "Chỉ những tour có trạng thái DRAFT mới có thể được gửi để phê duyệt. Trạng thái hiện tại: ";
        public static final String ONLY_CREATOR_CAN_SEND = "Chỉ người tạo tour mới có thể gửi tour để phê duyệt";
        public static final String TOUR_SENT_FOR_APPROVAL_SUCCESS = "Tour đã được gửi phê duyệt thành công";
        public static final String FAILED_TO_SEND_TOUR = "Không thể gửi tour để phê duyệt: ";
        public static final String TOUR_MISSING_REQUIRED_INFO = "Tour thiếu thông tin bắt buộc: ";
        public static final String APPROVE_FAIL = "Phê duyệt thất bại";
        public static final String REJECT_FAIL = "Từ chối thất bại";
        public static final String TOUR_STATUS_NOT_APPROVED_OR_OPENED = "Tour phải ở trạng thái APPROVED hoặc OPEN để thiết lập lịch trình";

        // Tour request validation
        public static final String TOUR_REQUEST_NULL = "Yêu cầu tour không được để trống";
        public static final String TOUR_NAME_EMPTY = "Tên tour không được để trống";
        public static final String NUMBER_DAYS_INVALID = "Số ngày phải lớn hơn 0";
        public static final String NUMBER_NIGHTS_INVALID = "Số đêm không được là số âm";
        public static final String TOUR_MUST_HAVE_LOCATION = "Tour phải có ít nhất một địa điểm";
        public static final String TOUR_TYPE_REQUIRED = "Phải chỉ định loại tour";
        public static final String TOUR_TYPE_INVALID = "Loại tour không hợp lệ";
        public static final String TOUR_STATUS_REQUIRED = "Phải chỉ định trạng thái tour";
        public static final String TOUR_STATUS_INVALID = "Trạng thái tour không hợp lệ";
        public static final String MARKUP_PERCENT_INVALID = "Phần trăm lợi nhuận không được âm";

        // Tour description options
        public static final String DESC_STANDARD_OPTION = "Tùy chọn cơ bản: %d ngày, %d đêm";
        public static final String DESC_EXTENDED_WEEKEND_OPTION = "Tùy chọn cuối tuần mở rộng: %d ngày, %d đêm";
        public static final String DESC_EXTENDED_OPTION = "Tùy chọn mở rộng: %d ngày, %d đêm";

        // Blog Related Messages
        public static final String BLOG_LIST_SUCCESS = "Lấy danh sách bài viết thành công";
        public static final String BLOG_LIST_FAIL = "Lấy danh sách bài viết thất bại";
        public static final String BLOG_CREATE_SUCCESS = "Tạo blog thành công";
        public static final String BLOG_UPDATE_SUCCESS = "Cập nhật blog thành công";
        public static final String BLOG_DELETE_SUCCESS = "Xóa blog thành công";
        public static final String BLOG_CREATE_FAIL = "Tạo blog thất bại";
        public static final String BLOG_UPDATE_FAIL = "Cập nhật blog thất bại";
        public static final String BLOG_DELETE_FAIL = "Xóa blog thất bại";
        public static final String BLOG_NOT_FOUND = "Không tìm thấy blog";
        public static final String BLOG_DETAIL_SUCCESS = "Lấy chi tiết blog thành công";
        public static final String BLOG_DETAIL_FAIL = "Lấy chi tiết blog thất bại";
        //===================================================
        // Voucher Related Messages
        public static final String VOUCHER_CREATE_SUCCESS = "Tạo voucher thành công";
        public static final String VOUCHER_CREATE_FAIL = "Tạo voucher thất bại";
        public static final String VOUCHER_CODE_EXISTS = "Mã voucher đã tồn tại";
        public static final String VOUCHER_LIST_SUCCESS = "Lấy danh sách voucher thành công";
        public static final String VOUCHER_LIST_FAIL = "Lấy danh sách voucher thất bại";
        //===================================================
        // Tour day management
        public static final String TOUR_DAY_SERVICE_ADDED_SUCCESS = "Thêm dịch vụ vào ngày tour thành công";
        public static final String TOUR_DAY_SERVICE_UPDATED_SUCCESS = "Cập nhật dịch vụ của ngày tour thành công";
        public static final String TOUR_DAY_SERVICE_REMOVED_SUCCESS = "Xóa dịch vụ khỏi ngày tour thành công";
        public static final String TOUR_DAY_NOT_FOUND = "Không tìm thấy ngày tour";
        public static final String NO_TOUR_DAY_FOUND = "Không tìm thấy ngày tour nào cho tour này";
        public static final String NO_TOUR_DAYS_FOUND = "Không tìm thấy ngày tour nào";
        public static final String TOUR_DAY_DETAIL_LOAD_SUCCESS = "Tải chi tiết ngày tour thành công";
        public static final String TOUR_DAY_DETAIL_LOAD_FAIL = "Tải chi tiết ngày tour thất bại";
        public static final String TOUR_DAY_CREATED_SUCCESS = "Tạo ngày tour thành công";
        public static final String TOUR_DAY_UPDATED_SUCCESS = "Cập nhật ngày tour thành công";
        public static final String TOUR_DAY_DELETED_SUCCESS = "Xóa ngày tour thành công";
        public static final String DAY_NUMBER_REQUIRED = "Số ngày là bắt buộc";
        public static final String TOUR_DAY_NOT_BELONG = "Ngày tour không thuộc tour này";
        public static final String GET_DETAIL_TOUR_DAY_SUCCESS = "Lấy thông tin chi tiết ngày tour thành công";

        // Tour day validation and errors
        public static final String TOUR_DAY_EXCEEDS_MAX_LIMIT = "Không thể tạo thêm ngày vì đã vượt quá số ngày/đêm tối đa được định nghĩa trong tour: ";
        public static final String TOUR_DAY_CREATE_FAILED = "Tạo ngày tour thất bại.";
        public static final String TOUR_DAY_NUMBER_ALREADY_EXISTS = "Số thứ tự ngày đã tồn tại trong tour: ";
        public static final String TOUR_DAY_UPDATE_FAILED = "Cập nhật ngày tour thất bại.";
        public static final String TOUR_DAY_RESTORED_SUCCESS = "Khôi phục ngày tour thành công.";

        //===================================================
        // Tour PAX Configuration Messages
        //===================================================
        // PAX configuration management
        public static final String PAX_CONFIG_NOT_FOUND = "Không tìm thấy cấu hình Pax";
        public static final String PAX_CONFIG_NOT_ASSOCIATED = "Cấu hình Pax không liên kết với tour này";
        public static final String PAX_CONFIG_OVERLAP = "Cấu hình Pax bị trùng với cấu hình đã có";
        public static final String PAX_CONFIG_INVALID_RANGE = "Số lượng Pax tối thiểu phải nhỏ hơn hoặc bằng tối đa";
        public static final String PAX_CONFIG_INVALID_DATES = "Ngày bắt đầu phải trước ngày kết thúc";
        public static final String PAX_CONFIG_LOAD_SUCCESS = "Tải cấu hình Pax thành công";
        public static final String PAX_CONFIG_CREATE_SUCCESS = "Tạo cấu hình Pax thành công";
        public static final String PAX_CONFIG_UPDATE_SUCCESS = "Cập nhật cấu hình Pax thành công";
        public static final String PAX_CONFIG_DELETE_SUCCESS = "Xóa cấu hình Pax thành công";
        public static final String FAILED_TO_RETRIEVE_PAX_CONFIGURATION = "Không thể lấy cấu hình pax.";
        public static final String FAILED_TO_CREATE_PAX_CONFIGURATION = "Không thể tạo cấu hình pax.";
        public static final String FAILED_TO_UPDATE_PAX_CONFIGURATION = "Không thể cập nhật cấu hình pax.";
        public static final String FAILED_TO_DELETE_PAX_CONFIGURATION = "Không thể xóa cấu hình pax.";

        // Tour PAX validation
        public static final String TOUR_PAX_NOT_FOUND = "Không tìm thấy cấu hình số lượng khách của tour";
        public static final String TOUR_PAX_MISMATCH = "Cấu hình số lượng khách không thuộc về tour này";
        public static final String TOUR_PAX_DELETED = "Không thể sử dụng cấu hình số lượng khách đã bị xóa";
        public static final String TOUR_PAX_INVALID_DATES = "Lịch trình tour nằm ngoài khoảng thời gian hiệu lực của cấu hình số lượng khách đã chọn";
        public static final String TOUR_PAX_NOT_AVAILABLE = "Không có cấu hình số lượng khách nào cho tour này. Vui lòng tạo trước.";
        public static final String TOUR_PAX_NO_VALID = "Không tìm thấy cấu hình số lượng khách hợp lệ trong khoảng ngày được chọn. Vui lòng tạo cấu hình hoặc điều chỉnh ngày lịch trình.";
        public static final String INVALID_PAX_ID_FORMAT = "Định dạng ID hành khách không hợp lệ: %s";
        public static final String TOUR_PAX_NOT_FOUND_BY_ID = "Không tìm thấy hành khách trong tour với ID: %s";
        public static final String TOUR_PAX_NOT_BELONG_TO_TOUR = "Hành khách với ID %s không thuộc tour có ID %s";

        //===================================================
        // Tour Schedule & Operator Messages
        //===================================================
        // Schedule management
        public static final String GET_SCHEDULE_OPTIONS_SUCCESS = "Lấy danh sách tùy chọn lịch trình thành công";
        public static final String SCHEDULE_CREATED_SUCCESS = "Tạo lịch trình tour thành công";
        public static final String SCHEDULE_ID_REQUIRED = "Mã lịch trình là bắt buộc đối với các cập nhật";
        public static final String TOUR_SCHEDULE_NOT_FOUND = "Không tìm thấy lịch trình tour";
        public static final String SCHEDULE_CANNOT_BE_UPDATED = "Không thể cập nhật lịch trình có trạng thái ĐANG DIỄN RA";

        //===================================================
        // Pricing & Markup Messages
        //===================================================
        // Markup management
        public static final String MARKUP_UPDATE_SUCCESS = "Cập nhật lợi nhuận thành công";
        public static final String MARKUP_UPDATE_FAIL = "Cập nhật lợi nhuận thất bại";
        public static final String MARKUP_RETRIEVE_SUCCESS = "Lấy thông tin lợi nhuận thành công";
        public static final String MARKUP_RETRIEVE_FAIL = "Lấy thông tin lợi nhuận thất bại";

        // Markup validation
        public static final String MARK_UP_REQUIRED = "Phải nhập tỷ lệ lợi nhuận";
        public static final String MARK_UP_MUST_BE_NUMBER = "Tỷ lệ lợi nhuận phải là một số";
        public static final String MARK_UP_POSITIVE = "Tỷ lệ lợi nhuận phải là số dương";
        public static final String MARK_UP_LIMIT = "Tỷ lệ lợi nhuận không được vượt quá 100%";

        // Price configuration
        public static final String EMPTY_PRICE = "Giá không được để trống";
        public static final String INVALID_PRICE = "Giá phải là một số hợp lệ";
        public static final String INVALID_PRICE_RANGE = "Giá bán phải lớn hơn hoặc bằng giá gốc (nett)";
        public static final String CONFIG_UPDATED = "Cập nhật cấu hình giá thành công";
        public static final String CONFIG_DELETED = "Xóa cấu hình giá thành công";
        public static final String CONFIGS_RETRIEVED = "Lấy danh sách cấu hình giá thành công";
        public static final String SEND_PRICING_FAIL = "Gửi báo giá thất bại";

        //===================================================
        // Tour Guide Messages
        //===================================================
        public static final String TOUR_GUIDE_CREATED_SUCCESSFULLY = "Hướng dẫn viên đã được tạo thành công.";
        public static final String FAILED_TO_CREATE_TOUR_GUIDE = "Tạo hướng dẫn viên không thành công.";
        public static final String TOUR_GUIDE_UPDATED_SUCCESSFULLY = "Hướng dẫn viên đã được cập nhật thành công.";
        public static final String FAILED_TO_UPDATE_TOUR_GUIDE = "Không thể cập nhật hướng dẫn viên.";
        public static final String TOUR_GUIDE_DELETED_SUCCESSFULLY = "Hướng dẫn viên đã được xóa thành công.";
        public static final String FAILED_TO_DELETE_TOUR_GUIDE = "Không thể xóa hướng dẫn viên.";

        //===================================================
        // Category Related Messages
        //===================================================
        public static final String CATEGORY_ALREADY_EXISTS = "Danh mục đã tồn tại";
        public static final String CATEGORY_NOT_FOUND = "Không tìm thấy danh mục";
        public static final String CATEGORY_LOADED = "Tải danh mục thành công";
        public static final String CATEGORY_CREATED = "Tạo danh mục thành công";
        public static final String CATEGORY_UPDATED = "Cập nhật danh mục thành công";
        public static final String CATEGORIES_LOAD_SUCCESS = "Tải danh mục thành công";
        public static final String CATEGORIES_LOAD_FAIL = "Tải danh mục thất bại";

        //===================================================
        // Booking Related Messages
        //===================================================
        // Booking management
        public static final String TOUR_BOOKING_DETAIL_LOAD_SUCCESS = "Chi tiết đặt tour được tải thành công";
        public static final String TOUR_BOOKING_DETAIL_LOAD_FAIL = "Tải chi tiết đặt tour thất bại";
        public static final String CREATE_PUBLIC_BOOKING_FAIL = "Tạo đặt tour công khai thất bại";
        public static final String UPDATE_CUSTOMER_STATUS_FAIL = "Cập nhật trạng thái khách hàng thất bại";
        public static final String GET_CUSTOMERS_FOR_SALE_FAIL = "Lấy danh sách khách hàng để bán thất bại";
        public static final String UPDATE_BOOKING_STATUS_FAIL = "Cập nhật trạng thái đặt tour thất bại";
        public static final String TAKE_BOOKING_FAIL = "Nhận đặt tour thất bại";
        public static final String GET_BOOKING_HISTORY_LIST_FAIL = "Lấy danh sách lịch sử đặt tour thất bại";
        public static final String GET_BOOKING_FAILED = "Lấy thông tin đặt tour thất bại";
        public static final String CREATE_BOOKING_FAILED = "Tạo đặt tour thất bại";

        //===================================================
        // Transaction Related Messages
        //===================================================
        public static final String GET_DATA_FAILED = "Lấy dữ liệu thất bại";
        public static final String GET_TRANSACTION_DETAILS_FAILED = "Lấy chi tiết giao dịch cho kế toán thất bại";
        public static final String UPDATE_TRANSACTION_FAILED = "Cập nhật giao dịch thất bại";

        //===================================================
        // Wishlist Related Messages
        //===================================================
        public static final String CREATE_WISHLIST_SUCCESS = "Thêm wishlist thành công";
        public static final String CREATE_WISHLIST_FAIL = "Thêm wishlist thất bại";
        public static final String WISHLIST_LOAD_SUCCESS = "Lấy danh sách yêu thích thành công";
        public static final String WISHLIST_LOAD_FAIL = "Lấy danh sách yêu thích thất bại";
        public static final String WISHLIST_NOT_FOUND = "Không tìm thấy danh sách yêu thích";
        public static final String NO_PERMISSION_TO_DELETE = "Bạn không có quyền xóa dữ liệu này";
        public static final String DELETE_WISHLIST_SUCCESS = "Xóa wishlist thành công";
        public static final String DELETE_WISHLIST_FAIL = "Xóa wishlist thất bại";

        //===================================================
        // Operator Related Messages
        //===================================================
        public static final String OPERATOR_GET_ALL_TOUR_FAIL = "Lấy tất cả các tour của điều hành viên thất bại";
        public static final String OPERATOR_RECEIVED_TOUR_SUCCESS = "Điều hành viên đã nhận tour để vận hành thành công";
        public static final String OPERATOR_RECEIVE_TOUR_FAIL = "Điều hành viên nhận tour thất bại";
        public static final String OPERATOR_GET_TOUR_DETAIL_SUCCESS = "Điều hành viên lấy chi tiết tour thành công";
        public static final String OPERATOR_GET_TOUR_DETAIL_FAIL = "Điều hành viên lấy chi tiết tour thất bại";
        public static final String OPERATOR_GET_CUSTOMER_LIST_SUCCESS = "Điều hành viên lấy danh sách khách hàng của tour thành công";
        public static final String OPERATOR_GET_CUSTOMER_LIST_FAIL = "Điều hành viên lấy danh sách khách hàng của tour thất bại";
        public static final String OPERATOR_GET_BOOKING_LIST_SUCCESS = "Điều hành viên lấy danh sách đặt tour thành công";
        public static final String OPERATOR_GET_BOOKING_LIST_FAIL = "Điều hành viên lấy danh sách đặt tour thất bại";
        public static final String GET_TOUR_LOG_LIST_SUCCESS = "Lấy danh sách nhật ký của tour thành công";
        public static final String GET_TOUR_LOG_LIST_FAIL = "Lấy danh sách nhật ký của tour thất bại";
        public static final String CREATE_LOG_SUCCESS = "Tạo nhật ký hoạt động thành công";
        public static final String CREATE_LOG_FAIL = "Tạo nhật ký hoạt động thất bại";
        public static final String TOUR_LOG_NOT_FOUND = "Không tìm thấy nhật ký tour";
        public static final String DELETE_LOG_SUCCESS = "Xóa nhật ký hoạt động thành công";
        public static final String DELETE_LOG_FAIL = "Xóa nhật ký hoạt động thất bại";
        public static final String TOUR_GUIDE_NOT_FOUND = "Không tìm thấy hướng dẫn viên";
        public static final String ASSIGN_TOUR_GUIDE_SUCCESS = "Phân công hướng dẫn viên thành công";
        public static final String ASSIGN_TOUR_GUIDE_FAIL = "Phân công hướng dẫn viên thất bại";
        public static final String GET_AVAILABLE_TOUR_GUIDE_SUCCESS = "Lấy danh sách hướng dẫn viên khả dụng thành công";
        public static final String GET_AVAILABLE_TOUR_GUIDE_FAIL = "Lấy danh sách hướng dẫn viên khả dụng thất bại";
        public static final String GET_TRANSACTION_LIST_SUCCESS = "Lấy danh sách giao dịch thành công";
        public static final String GET_TRANSACTION_LIST_FAIL = "Lấy danh sách giao dịch thất bại";
        public static final String GET_SERVICE_LIST_SUCCESS = "Lấy danh sách dịch vụ thành công";
        public static final String GET_SERVICE_LIST_FAIL = "Lấy danh sách dịch vụ thất bại";
        public static final String CHOOSE_SERVICE_SUCCESS = "Chọn dịch vụ thành công";
        public static final String CHOOSE_SERVICE_FAIL = "Chọn dịch vụ thất bại";
        public static final String BOOKING_NOT_FOUND = "Không tìm thấy đặt tour";
        public static final String SERVICE_REQUEST_NOT_APPROVED = "Đơn này chưa thể gửi thanh toán";
        public static final String PAY_SERVICE_SUCCESS = "Thanh toán dịch vụ thành công";
        public static final String PAY_SERVICE_FAIL = "Thanh toán dịch vụ thất bại";
        public static final String GET_LOCATIONS_CATEGORIES_SUCCESS = "Lấy danh sách địa điểm và danh mục dịch vụ thành công";
        public static final String GET_LOCATIONS_CATEGORIES_FAIL = "Lấy danh sách địa điểm và danh mục dịch vụ thất bại";
        public static final String GET_PROVIDERS_BY_LOCATION_SUCCESS = "Lấy danh sách nhà cung cấp theo địa điểm thành công";
        public static final String GET_PROVIDERS_BY_LOCATION_FAIL = "Lấy danh sách nhà cung cấp theo địa điểm thất bại";
        public static final String GET_SERVICES_BY_PROVIDER_SUCCESS = "Lấy danh sách dịch vụ theo nhà cung cấp thành công";
        public static final String GET_SERVICES_BY_PROVIDER_FAIL = "Lấy danh sách dịch vụ theo nhà cung cấp thất bại";
        public static final String GET_SERVICE_DETAIL_SUCCESS = "Lấy chi tiết dịch vụ thành công";
        public static final String ADD_SERVICE_SUCCESS = "Thêm dịch vụ vào booking thành công";
        public static final String ADD_SERVICE_FAIL = "Thêm dịch vụ thất bại";
        public static final String TOUR_BOOKING_NOT_FOUND = "Không tìm thấy đặt tour";
        public static final String SERVICE_ALREADY_EXISTS = "Dịch vụ đã tồn tại";
        public static final String PREVIEW_MAIL_SUCCESS = "Xem trước email thành công";
        public static final String PREVIEW_MAIL_FAIL = "Xem trước email thất bại";
        public static final String BOOKING_SERVICE_NOT_FOUND = "Không tìm thấy dịch vụ đặt tour";
        public static final String SERVICE_STATUS_CANNOT_SEND_EMAIL = "Trạng thái dịch vụ không thể gửi email";
        public static final String GET_BOOKING_LIST_SUCCESS = "Lấy danh sách đặt tour thành công";
        public static final String GET_BOOKING_LIST_FAIL = "Lấy danh sách đặt tour thất bại";
        public static final String CANCEL_SERVICE_SUCCESS = "Hủy dịch vụ thành công";
        public static final String CANCEL_SERVICE_FAIL = "Hủy dịch vụ thất bại";
        public static final String INVALID_SERVICE_QUANTITY = "Số lượng dịch vụ không hợp lệ";
        public static final String UPDATE_SERVICE_QUANTITY_SUCCESS = "Cập nhật số lượng dịch vụ thành công";
        public static final String UPDATE_SERVICE_QUANTITY_FAIL = "Cập nhật số lượng dịch vụ thất bại";
        public static final String NO_TOUR_SCHEDULE_FOUND = "Không tìm thấy lịch trình tour";
        public static final String TOUR_SCHEDULE_NOT_ONGOING = "Lịch trình tour này không đang diễn ra";
        public static final String SEND_ACCOUNTANT_SUCCESS = "Gửi kế toán thành công";
        public static final String SEND_ACCOUNTANT_FAIL = "Gửi kế toán thất bại";
        public static final String OPERATOR_GET_PRIVATE_TOUR_FAIL = "Điều hành viên lấy danh sách tour riêng tư thất bại";
        public static final String GET_TOUR_DAY_LIST_SUCCESS = "Lấy danh sách ngày tour thành công";
        public static final String GET_TOUR_DAY_LIST_FAIL = "Lấy danh sách ngày tour thất bại";
        public static final String SEND_MAIL_TO_PROVIDER_SUCCESS = "Gửi email cho nhà cung cấp thành công";
        public static final String SEND_MAIL_TO_PROVIDER_FAIL = "Gửi email cho nhà cung cấp thất bại";
        public static final String GET_SERVICE_REQUEST_LIST_SUCCESS = "Lấy danh sách yêu cầu dịch vụ thành công";
        public static final String GET_SERVICE_REQUEST_LIST_FAIL = "Lấy danh sách yêu cầu dịch vụ thất bại";
        public static final String GET_SERVICE_REQUEST_DETAIL_SUCCESS = "Lấy chi tiết yêu cầu dịch vụ thành công";
        public static final String GET_SERVICE_REQUEST_DETAIL_FAIL = "Lấy chi tiết yêu cầu dịch vụ thất bại";
        public static final String REJECT_SERVICE_REQUEST_SUCCESS = "Từ chối yêu cầu dịch vụ thành công";
        public static final String REJECT_SERVICE_REQUEST_FAIL = "Từ chối yêu cầu dịch vụ thất bại";
        public static final String APPROVE_SERVICE_REQUEST_SUCCESS = "Phê duyệt yêu cầu dịch vụ thành công";
        public static final String APPROVE_SERVICE_REQUEST_FAIL = "Phê duyệt yêu cầu dịch vụ thất bại";
        public static final String GET_TOUR_SUMMARY_SUCCESS = "Lấy tổng quan tour thành công";
        public static final String GET_TOUR_SUMMARY_FAIL = "Lấy tổng quan tour thất bại";
        public static final String INVALID_STATUS_VALUE = "Giá trị trạng thái không hợp lệ: ";
        public static final String USER_INFO_NOT_FOUND = "Không tìm thấy thông tin người dùng";
        public static final String UNAUTHORIZED = "Không có quyền truy cập";
        public static final String PAGE_SUCCESS = "Thành công";

        //===================================================
        // Date & Time Related Messages
        //===================================================
        public static final String INVALID_DATE_RANGE = "Khoảng thời gian không hợp lệ";
        public static final String DATE_RANGE_INVALID = "Ngày kết thúc phải sau ngày bắt đầu";

        //===================================================
        // General Validation Messages
        //===================================================
        // User information
        public static final String EMPTY_FULL_NAME = "Họ và tên không được để trống";
        public static final String EMPTY_USERNAME = "Tên đăng nhập không được để trống";
        public static final String EMPTY_PASSWORD = "Mật khẩu không được để trống";
        public static final String EMPTY_REPASSWORD = "Xác nhận mật khẩu không được để trống";

        // Contact information
        public static final String EMPTY_ADDRESS = "Địa chỉ không được để trống";
        public static final String EMPTY_PHONE_NUMBER = "Số điện thoại không được để trống";
        public static final String INVALID_PHONE_NUMBER = "Định dạng số điện thoại không hợp lệ. Phải từ 10 đến 15 chữ số";
        public static final String EMPTY_EMAIL = "Email không được để trống";
        public static final String INVALID_EMAIL = "Định dạng email không hợp lệ";

        // Other validations
        public static final String EMPTY_POSITION = "Vị trí không được để trống";
        public static final String EMPTY_IMAGE_URL = "Đường dẫn hình ảnh không được để trống";
        public static final String EMPTY_ABBREVIATION = "Từ viết tắt không được để trống";
        public static final String EMPTY_WEBSITE = "Website không được để trống";

        //===================================================
        // Misc Operations Messages
        //===================================================
        public static final String CHECKING_ALL_SERVICE_FAIL = "Kiểm tra tất cả dịch vụ thất bại";
        public static final String GET_DATA_FAIL = "Lấy dữ liệu thất bại";
        public static final String HOMEPAGE_LOAD_SUCCESS = "Tải trang chủ thành công";
        public static final String HOMEPAGE_LOAD_FAIL = "Tải trang chủ thất bại";
        public static final String SEARCH_SUCCESS = "Tìm kiếm thành công";
        public static final String SEARCH_FAIL = "Tìm kiếm thất bại";

        //===================================================
        // General Status Messages
        //===================================================
        public static final String GENERAL_SUCCESS_MESSAGE = "Thành công";
        public static final String GENERAL_FAIL_MESSAGE = "Thất bại";
        public static final String GENERAL_FAIL = "Thất bại";
        public static final String SUCCESS = "Thành công";
        public static final String FAILED = "Thất bại";
        public static final String SUCCESSFULLY = " thành công";
    }


    public static final class Regex {
        //public static final String REGEX_PASSWORD = "$d{8}^";
        public static final String REGEX_USERNAME = "^[a-zA-Z0-9-_]{8,30}$";
        public static final String REGEX_PASSWORD = "^(?=.*[a-z])(?=.*[A-Z])(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";
        public static final String REGEX_FULLNAME = "^[a-zA-Z][a-zA-Z\s]*$";
        public static final String REGEX_EMAIL = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$";
        public static final String REGEX_PHONE = "^[0-9]{10,15}$";

    }

    public static final class FilePath {
        public static final String PRICE_EMAIL_PATH = "templates/pricing.html";
        public static final String TOUR_IMAGE_FALL_BACK_URL = "https://media.travel.com.vn/TourFiles/4967/Hoi%20An%20Ve%20Dem%20(4).jpg";
    }


    public static final class AI {

        public static final String PLAN_DAY_RESPONSE_JSON = """
                                                                {
                                                                        "dayNumber": 1,
                                                                        "date": "2025-07-20",
                                                                        "totalSpend": 2400000,
                                                                        "locationId": 101,
                                                                        "locationName": "Sa Pa",
                                                                        "longDescription": "Khởi hành từ Hà Nội, bạn sẽ đến Sa Pa vào buổi sáng. Buổi sáng bắt đầu bằng việc chinh phục đỉnh Fansipan bằng cáp treo – nóc nhà Đông Dương. Sau đó, bạn sẽ tham quan khu du lịch Sun World Fansipan Legend và chiêm bái tại Bích Vân Thiền Tự. Chiều đến là thời gian khám phá bản Cát Cát – ngôi làng truyền thống của người H'Mông với nghề dệt vải, nhuộm chàm. Tối thưởng thức các món nướng đặc sản Sa Pa tại chợ đêm.",
                                                                        "activities": [
                                                                          {
                                                                            "id": 1,
                                                                            "title": "Chinh phục Fansipan",
                                                                            "content": "Trải nghiệm cáp treo Fansipan ngắm nhìn dãy Hoàng Liên Sơn hùng vĩ. Tham quan quần thể tâm linh trên đỉnh núi.",
                                                                            "category": "Tham quan",
                                                                            "duration": "3 giờ",
                                                                            "imageUrl": "https://example.com/fansipan.jpg",
                                                                            "startTime": "2025-07-20T08:30:00",
                                                                            "endTime": "2025-07-20T11:30:00",
                                                                            "estimatedCost": 750000
                                                                          }
                                                                        ],
                                                                        "hotels": [
                                                                          {
                                                                            "id": 1,
                                                                            "name": "Sapa Charm Hotel",
                                                                            "websiteUrl": "https://sapacharmhotel.com",
                                                                            "logoUrl": "https://example.com/sapa-hotel.jpg",
                                                                            "checkInDate": "2025-07-20T14:00:00",
                                                                            "checkOutDate": "2025-07-21T08:00:00",
                                                                            "roomDetails": "Phòng view núi, 2 người",
                                                                            "total": 900000,
                                                                            "estimatedCost": 900000
                                                                          }
                                                                        ],
                                                                        "restaurants": [
                                                                          {
                                                                            "id": 1,
                                                                            "name": "Quán nướng A Quỳnh",
                                                                            "websiteUrl": "https://sapacharmhotel.com",
                                                                            "logoUrl": "https://example.com/anuong.jpg",
                                                                            "menuItems": ["Thịt xiên nướng", "Cá hồi gỏi", "Cơm lam"],
                                                                            "useDate": "2025-07-20T19:00:00",
                                                                            "estimatedCost": 300000
                                                                          }
                                                                        ]
                                                                      }
                                                            """;

        public static final String PLAN_RESPONSE_JSON = """
                    {
                      "plan": {
                        "numberDays": 3,
                        "title": "Khám phá miền Bắc Việt Nam: Sa Pa - Ninh Bình - Hà Nội",
                        "planCategory": "Du lịch Cá Nhân",
                        "thumbnailImageUrl": "",
                        "preferences": ["Danh lam thắng cảnh", "Ẩm thực", "Văn hóa"],
                        "description": "Hành trình 3 ngày khám phá miền Bắc Việt Nam, trải nghiệm cảnh sắc thiên nhiên hùng vĩ tại Sa Pa, vẻ đẹp thanh bình của Ninh Bình và nét văn hóa đặc sắc của thủ đô Hà Nội. Mỗi ngày mang đến một trải nghiệm độc đáo từ núi rừng, đồng bằng cho tới phố cổ, kết hợp hoàn hảo giữa hoạt động tham quan, ẩm thực địa phương và nghỉ ngơi thư giãn.",
                        "days": [
                          {
                            "dayNumber": 1,
                            "date": "2025-07-20",
                            "totalSpend": 2400000,
                            "locationId": 101,
                            "locationName": "Sa Pa",
                            "longDescription": "Khởi hành từ Hà Nội, bạn sẽ đến Sa Pa vào buổi sáng. Buổi sáng bắt đầu bằng việc chinh phục đỉnh Fansipan bằng cáp treo – nóc nhà Đông Dương. Sau đó, bạn sẽ tham quan khu du lịch Sun World Fansipan Legend và chiêm bái tại Bích Vân Thiền Tự. Chiều đến là thời gian khám phá bản Cát Cát – ngôi làng truyền thống của người H'Mông với nghề dệt vải, nhuộm chàm. Tối thưởng thức các món nướng đặc sản Sa Pa tại chợ đêm.",
                            "activities": [
                              {
                                "id": 1,
                                "title": "Chinh phục Fansipan",
                                "content": "Trải nghiệm cáp treo Fansipan ngắm nhìn dãy Hoàng Liên Sơn hùng vĩ. Tham quan quần thể tâm linh trên đỉnh núi.",
                                "category": "Tham quan",
                                "duration": "3 giờ",
                                "imageUrl": "https://example.com/fansipan.jpg",
                                "startTime": "2025-07-20T08:30:00",
                                "endTime": "2025-07-20T11:30:00",
                                "estimatedCost": 750000
                              }
                            ],
                            "hotels": [
                              {
                                "id": 1,
                                "name": "Sapa Charm Hotel",
                                "address": "32 Mường Hoa, thị xã Sa Pa",
                                "imageUrl": "https://example.com/sapa-hotel.jpg",
                                "checkInDate": "2025-07-20T14:00:00",
                                "checkOutDate": "2025-07-21T08:00:00",
                                "roomDetails": "Phòng view núi, 2 người",
                                "total": 900000,
                                "estimatedCost": 900000
                              }
                            ],
                            "restaurants": [
                              {
                                "id": 1,
                                "name": "Quán nướng A Quỳnh",
                                "address": "15 Thạch Sơn, Sa Pa",
                                "imageUrl": "https://example.com/anuong.jpg",
                                "menuItems": ["Thịt xiên nướng", "Cá hồi gỏi", "Cơm lam"],
                                "useDate": "2025-07-20T19:00:00",
                                "estimatedCost": 300000
                              }
                            ]
                          },
                          {
                            "dayNumber": 2,
                            "date": "2025-07-21",
                            "totalSpend": 2100000,
                            "locationId": 102,
                            "locationName": "Ninh Bình",
                            "longDescription": "Sau khi rời Sa Pa vào sáng sớm, bạn đến Ninh Bình vào buổi trưa. Chiều ghé thăm Tràng An – di sản văn hóa và thiên nhiên thế giới, nơi bạn sẽ ngồi thuyền khám phá hang động và cảnh sắc non nước hữu tình. Tối thưởng thức dê núi Ninh Bình tại nhà hàng địa phương.",
                            "activities": [
                              {
                                "id": 2,
                                "title": "Thăm quần thể Tràng An",
                                "content": "Đi thuyền qua các hang động tự nhiên, chiêm ngưỡng vẻ đẹp hùng vĩ của núi đá vôi và di tích cổ kính.",
                                "category": "Tham quan",
                                "duration": "2 giờ",
                                "imageUrl": "https://example.com/trangan.jpg",
                                "startTime": "2025-07-21T14:00:00",
                                "endTime": "2025-07-21T16:00:00",
                                "estimatedCost": 250000
                              }
                            ],
                            "hotels": [
                              {
                                "id": 2,
                                "name": "Tam Coc Rice Fields Resort",
                                "address": "Tam Cốc - Bích Động, Hoa Lư",
                                "imageUrl": "https://example.com/ninhbinh-hotel.jpg",
                                "checkInDate": "2025-07-21T13:00:00",
                                "checkOutDate": "2025-07-22T08:00:00",
                                "roomDetails": "Bungalow giữa đồng lúa",
                                "total": 1000000,
                                "estimatedCost": 1000000
                              }
                            ],
                            "restaurants": [
                              {
                                "id": 2,
                                "name": "Nhà hàng Dê 35",
                                "address": "Đường 27, TP. Ninh Bình",
                                "imageUrl": "https://example.com/de35.jpg",
                                "menuItems": ["Dê tái chanh", "Lẩu dê", "Rượu Kim Sơn"],
                                "useDate": "2025-07-21T18:30:00",
                                "estimatedCost": 300000
                              }
                            ]
                          },
                          {
                            "dayNumber": 3,
                            "date": "2025-07-22",
                            "totalSpend": 1900000,
                            "locationId": 103,
                            "locationName": "Hà Nội",
                            "longDescription": "Ngày cuối cùng bạn trở về Hà Nội. Buổi sáng đi dạo hồ Hoàn Kiếm, ghé thăm đền Ngọc Sơn và cầu Thê Húc. Trưa ăn bún chả truyền thống tại phố cổ. Chiều tham quan Văn Miếu – Quốc Tử Giám và đi bộ khám phá phố sách Đinh Lễ. Kết thúc hành trình tại chợ đêm phố cổ.",
                            "activities": [
                              {
                                "id": 3,
                                "title": "Tham quan Hồ Gươm & Văn Miếu",
                                "content": "Khám phá những biểu tượng văn hóa thủ đô, từ hồ Gươm đến Văn Miếu – trường đại học đầu tiên của Việt Nam.",
                                "category": "Văn hóa",
                                "duration": "3 giờ",
                                "imageUrl": "https://example.com/hoguom.jpg",
                                "startTime": "2025-07-22T08:00:00",
                                "endTime": "2025-07-22T11:00:00",
                                "estimatedCost": 100000
                              }
                            ],
                            "hotels": [
                              {
                                "id": 3,
                                "name": "La Siesta Hotel & Spa",
                                "address": "94 Mã Mây, Hoàn Kiếm",
                                "imageUrl": "https://example.com/hanoi-hotel.jpg",
                                "checkInDate": "2025-07-22T13:00:00",
                                "checkOutDate": "2025-07-23T11:00:00",
                                "roomDetails": "Phòng classic 2 người",
                                "total": 1200000,
                                "estimatedCost": 1200000
                              }
                            ],
                            "restaurants": [
                              {
                                "id": 3,
                                "name": "Bún chả Hương Liên",
                                "address": "24 Lê Văn Hưu, Hà Nội",
                                "imageUrl": "https://example.com/buncha.jpg",
                                "menuItems": ["Bún chả truyền thống", "Nem cua bể", "Trà đá"],
                                "useDate": "2025-07-22T12:30:00",
                                "estimatedCost": 250000
                              }
                            ]
                          }
                        ]
                      }
                    }
                """;


        public static final String PROMPT_START = """ 
                Bạn là một chuyên gia trong lĩnh vực du lịch Việt Nam và đang hoạt động trong việc giúp khách hàng thiết kế chương trình du lịch cá nhân hoá.
                """;

        public static final String PROMPT_DAY_START = """ 
                    Bạn là một chuyên gia trong lĩnh vực du lịch Việt Nam và đang hoạt động trong việc giúp khách hàng thiết kế chương trình du lịch cá nhân hoá.
                    Hãy xây dựng chương trình trong 1 ngày cho chuyến du lịch cá nhân hoá cho khách hàng dựa trên các thông tin sau:
                """;

        public static final String PROMPT_DAY_END = """ 
                
                Các lưu ý quan trọng:
                - KHÔNG được tự nghĩ ra nhà hàng, khách sạn mới mà không có trong danh sách đã cho trước.
                - Cung cấp một hành trình cân đối bao gồm nhiều hoạt động, địa điểm lưu trú và lựa chọn ăn uống khác nhau.
                - Kế hoạch của ngày cần phản ánh một trình tự hợp lý, có xét đến thời gian di chuyển và khả năng hoạt động.
                - Phần longDescription trong ngày (PlanDay) phải >= 250 từ, giải thích cụ thể, dễ hiểu chuyến di cho người dùng và content trong mỗi Activity phải >= 50 từ → Không viết gộp, không quá ngắn hoặc sơ sài
                - Không bao gồm thêm thông tin gì thêm ngoài định dạng phản hồi để tránh lỗi
                - Với mỗi sở thích, hãy chọn các hoạt động phù hợp nhất, ví dụ:
                    *) Nếu là "làm nông dân/ngư dân" → gợi ý các hoạt động như: gặt lúa, bắt cá, chèo thuyền, đi chợ quê...
                    *) Nếu là "ẩm thực" → gợi ý lớp học nấu ăn, tour ẩm thực đường phố, chợ đêm địa phương...
                    *) Nếu là "mạo hiểm" → gợi ý leo núi, trekking, zipline...
                - Không đưa ra các địa điểm/hoạt động không liên quan đến sở thích.
                - Thời gian định dạng theo ISO 8601 (VD: 2025-07-20T08:00:00)
                
                Hãy trình bày rõ ràng từng ngày với các hoạt động, nơi lưu trú và lựa chọn ăn uống tương ứng.
                Định dạng phản hồi của bạn BẮT BUỘC tuân theo cấu trúc JSON sau:
                
                ### ĐỊNH_DẠNG_PHẢN_HỒI_JSON:
                
                """ + PLAN_DAY_RESPONSE_JSON;
    }
}

