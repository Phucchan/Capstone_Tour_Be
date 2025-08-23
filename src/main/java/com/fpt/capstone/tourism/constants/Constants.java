package com.fpt.capstone.tourism.constants;


public class Constants {
    public static final class UserExceptionInformation {
        public static final String USER_NOT_FOUND_MESSAGE = "Không tìm thấy người dùng";
        public static final String USERNAME_ALREADY_EXISTS_MESSAGE = "Tên đăng nhập đã tồn tại";
        public static final String EMAIL_ALREADY_EXISTS_MESSAGE = "Email already exists";
        public static final String PHONE_ALREADY_EXISTS_MESSAGE = "Số điện thoại đã được sử dụng";
        public static final String FAIL_TO_SAVE_USER_MESSAGE = "Lưu thông tin người dùng thất bại";
        public static final String USER_INFORMATION_NULL_OR_EMPTY = "Thông tin này là bắt buộc";
        public static final String USERNAME_INVALID = "Tên đăng nhập chỉ bao gồm chữ cái, số, dấu gạch ngang (-), gạch dưới (_) và có độ dài từ 8 đến 30 ký tự";
        public static final String PASSWORD_INVALID = "Mật khẩu phải từ 8 ký tự trở lên, bao gồm ít nhất 1 chữ hoa, 1 chữ thường và 1 ký tự đặc biệt";
        public static final String FULL_NAME_INVALID = "Họ tên phải bắt đầu bằng chữ cái, chỉ chứa chữ cái và khoảng trắng";
        public static final String PHONE_INVALID = "Số điện thoại phải gồm đúng 10-15 chữ số";
        public static final String EMAIL_INVALID = "Email không hợp lệ";
        public static final String USER_NOT_FOUND = "Không tìm thấy người dùng, vui lòng đăng nhập bằng tài khoản hợp lệ để xem thông tin cá nhân";
    }

    public static final class Default {
        public static final String DEFAULT_AVATAR_URL = "https://i.pinimg.com/originals/0d/64/98/0d64989794b1a4c9d89bff571d3d5842.jpg";
    }

    public static final class Message {
        // Authentication & User Management Messages
        //===================================================
        // Login related
        public static final String LOGIN_SUCCESS_MESSAGE = "Đăng nhập thành công";
        public static final String LOGIN_FAIL_MESSAGE = "Đăng nhập thất bại! Tên đăng nhập hoặc mật khẩu không đúng";
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
        public static final String TOUR_NOT_PUBLISHED = "Tour chưa được mở bán";
        // Location management
        public static final String EXISTED_LOCATION = "Địa điểm đã tồn tại";
        public static final String CREATE_LOCATION_SUCCESS = "Tạo địa điểm thành công";
        public static final String CREATE_LOCATION_FAIL = "Tạo địa điểm thất bại";
        public static final String LOCATION_NOT_FOUND = "Không tìm thấy địa điểm";
        public static final String GET_LOCATIONS_SUCCESS = "Lấy danh sách địa điểm thành công";
        public static final String GET_LOCATIONS_FAIL = "Lấy danh sách địa điểm thất bại";

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
        public static final String SERVICE_CREATED = "Tạo dịch vụ thành công";
        public static final String NO_SERVICES_AVAILABLE = "Không có dịch vụ tour nào";
        public static final String SERVICE_NOT_ASSOCIATED = "Dịch vụ không được liên kết với tour này";
        public static final String PARTNER_STATUS_UPDATED = "Thay đổi trạng thái nhà cung cấp thành công";

        // PartnerService operations
        public static final String PARTNER_LIST_SUCCESS = "Lấy danh sách nhà cung cấp thành công";
        public static final String PARTNER_LIST_FAIL = "Lấy danh sách nhà cung cấp thất bại";
        public static final String PARTNER_DETAIL_SUCCESS = "Lấy chi tiết nhà cung cấp thành công";
        public static final String PARTNER_DETAIL_FAIL = "Lấy chi tiết nhà cung cấp thất bại";
        public static final String PARTNER_UPDATE_SUCCESS = "Cập nhật nhà cung cấp thành công";
        public static final String PARTNER_UPDATE_FAIL = "Cập nhật nhà cung cấp thất bại";
        public static final String PARTNER_ADD_SUCCESS = "Tạo nhà cung cấp thành công";
        public static final String PARTNER_ADD_FAIL = "Tạo nhà cung cấp thất bại";
        // Service Type management
        public static final String SERVICE_TYPE_NOT_FOUND = "Không tìm thấy loại dịch vụ";
        public static final String SERVICE_TYPE_CREATE_SUCCESS = "Tạo loại dịch vụ thành công";
        public static final String SERVICE_TYPE_CREATE_FAIL = "Tạo loại dịch vụ thất bại";
        public static final String SERVICE_TYPE_UPDATE_SUCCESS = "Cập nhật loại dịch vụ thành công";
        public static final String SERVICE_TYPE_UPDATE_FAIL = "Cập nhật loại dịch vụ thất bại";
        public static final String SERVICE_TYPE_DELETE_SUCCESS = "Xóa loại dịch vụ thành công";
        public static final String SERVICE_TYPE_DELETE_FAIL = "Xóa loại dịch vụ thất bại";
        public static final String SERVICE_TYPE_STATUS_UPDATED = "Thay đổi trạng thái loại dịch vụ thành công";

        public static final String SEND_EMAIL_ORDER_SERVICE_FAIL = "Gửi email đặt dịch vụ thất bại";

        public static final String TOUR_NOT_FOUND = "Không tìm thấy tour";
        public static final String TOUR_STATUS_INVALID = "Trạng thái tour không hợp lệ";

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
        public static final String VOUCHER_REDEEM_SUCCESS = "Đổi voucher thành công";
        public static final String VOUCHER_REDEEM_FAIL = "Đổi voucher thất bại";
        public static final String VOUCHER_NOT_FOUND = "Không tìm thấy voucher";
        public static final String VOUCHER_OUT_OF_STOCK = "Voucher đã hết lượt sử dụng";
        public static final String VOUCHER_NOT_ENOUGH_POINTS = "Không đủ điểm để đổi voucher";
        // Tour discount related
        public static final String TOUR_DISCOUNT_CREATE_SUCCESS = "Tạo giảm giá tour thành công";
        public static final String TOUR_DISCOUNT_CREATE_FAIL = "Tạo giảm giá tour thất bại";
        public static final String TOUR_DISCOUNT_EXISTS = "Giảm giá cho lịch trình đã tồn tại";
        public static final String TOUR_DISCOUNT_LIST_SUCCESS = "Lấy danh sách giảm giá tour thành công";
        public static final String TOUR_DISCOUNT_LIST_FAIL = "Lấy danh sách giảm giá tour thất bại";
        public static final String TOUR_DISCOUNT_INVALID_PERCENT = "Phần trăm giảm giá phải lớn hơn 0 và nhỏ hơn hoặc bằng 100";
        public static final String TOUR_DISCOUNT_INVALID_DATE_RANGE = "Thời gian bắt đầu phải trước thời gian kết thúc";
        public static final String TOUR_DISCOUNT_START_DATE_IN_PAST = "Ngày bắt đầu phải ở tương lai";
        //===================================================
        // Tour day management
        public static final String TOUR_DAY_SERVICE_ADDED_SUCCESS = "Thêm dịch vụ vào ngày tour thành công";
        public static final String TOUR_DAY_SERVICE_UPDATED_SUCCESS = "Cập nhật dịch vụ của ngày tour thành công";
        public static final String TOUR_DAY_SERVICE_REMOVED_SUCCESS = "Xóa dịch vụ khỏi ngày tour thành công";
        public static final String TOUR_DAY_NOT_FOUND = "Không tìm thấy ngày tour";
        public static final String TOUR_DAY_UPDATED_SUCCESS = "Cập nhật ngày tour thành công";
        public static final String TOUR_DAY_DELETED_SUCCESS = "Xóa ngày tour thành công";
        public static final String TOUR_DAY_NOT_BELONG = "Ngày tour không thuộc tour này";

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
        public static final String DEPARTURE_DATE_IN_PAST = "Ngày khởi hành phải ở trong tương lai";
        public static final String TOUR_SCHEDULE_NOT_FOUND = "Không tìm thấy lịch trình tour";
        public static final String SCHEDULE_DELETED_SUCCESS = "Xóa lịch trình tour thành công";
        public static final String SCHEDULE_NOT_BELONG = "Lịch trình không thuộc tour này";

        // Price configuration
        public static final String INVALID_PRICE = "Giá phải là một số hợp lệ";
        public static final String CONFIG_UPDATED = "Cập nhật cấu hình giá thành công";
        public static final String CREATE_BOOKING_FAILED = "Tạo Tour Booking Thất Bại";

        //===================================================
        // Wishlist Related Messages
        //===================================================
        public static final String CREATE_WISHLIST_SUCCESS = "Thêm wishlist thành công";
        public static final String CREATE_WISHLIST_FAIL = "Thêm wishlist thất bại";
        public static final String WISHLIST_LOAD_SUCCESS = "Lấy danh sách yêu thích thành công";
        public static final String WISHLIST_LOAD_FAIL = "Lấy danh sách yêu thích thất bại";
        public static final String WISHLIST_NOT_FOUND = "Không tìm thấy danh sách yêu thích";
        public static final String NO_PERMISSION_TO_DELETE = "Bạn không có quyền xóa dữ liệu này";
        public static final String NO_PERMISSION_TO_ACCESS = "Bạn không có quyền truy cập dữ liệu này";
        public static final String DELETE_WISHLIST_SUCCESS = "Xóa wishlist thành công";
        public static final String DELETE_WISHLIST_FAIL = "Xóa wishlist thất bại";

        public static final String GET_SERVICE_LIST_SUCCESS = "Lấy danh sách dịch vụ thành công";
        public static final String GET_SERVICE_LIST_FAIL = "Lấy danh sách dịch vụ thất bại";
        public static final String BOOKING_NOT_FOUND = "Không tìm thấy đặt tour";
        public static final String SERVICE_ALREADY_EXISTS = "Dịch vụ đã tồn tại";

        public static final String GET_CHECKINS_SUCCESS = "Lấy danh sách check-in thành công";
        public static final String GET_CHECKINS_FAIL = "Lấy danh sách check-in thất bại";
        public static final String ADD_CHECKIN_SUCCESS = "Thêm check-in thành công";
        public static final String ADD_CHECKIN_FAIL = "Thêm check-in thất bại";
        public static final String DELETE_CHECKIN_SUCCESS = "Xóa check-in thành công";
        public static final String DELETE_CHECKIN_FAIL = "Xóa check-in thất bại";
        public static final String CHECKIN_NOT_FOUND = "Không tìm thấy check-in";

        public static final String GET_BOOKING_LIST_SUCCESS = "Lấy danh sách đặt tour thành công";
        public static final String GET_BOOKING_LIST_FAIL = "Lấy danh sách đặt tour thất bại";
        public static final String USER_INFO_NOT_FOUND = "Không tìm thấy thông tin người dùng";

        //===================================================
        // Date & Time Related Messages
        //===================================================
        public static final String INVALID_DATE_RANGE = "Khoảng thời gian không hợp lệ";

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
        public static final String EMPTY_EMAIL = "Email không được để trống";

        //===================================================
        // Misc Operations Messages
        //===================================================
        public static final String GET_DATA_FAIL = "Lấy dữ liệu thất bại";
        public static final String SEARCH_SUCCESS = "Tìm kiếm thành công";
        public static final String SEARCH_FAIL = "Tìm kiếm thất bại";

        //===================================================
        // General Status Messages
        //===================================================
        public static final String GENERAL_SUCCESS_MESSAGE = "Thành công";
        public static final String GENERAL_FAIL_MESSAGE = "Thất bại";
        public static final String SUCCESS = "Thành công";
        public static final String FAILED = "Thất bại";
        public static final String TOUR_DISCOUNT_UPDATE_SUCCESS = "Thành công";
        public static final String TOUR_DISCOUNT_NOT_FOUND = "Thất bại";
        public static final String TOUR_DISCOUNT_DELETE_SUCCESS = "Thành công";
        public static final String TOUR_DISCOUNT_END_DATE_AFTER_DEPARTURE = "Thất bại";
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
                                                                        "title": "Khám phá Sa Pa",
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
                - **Không thêm bất kỳ thông tin nào ngoài định dạng JSON kết quả.**
                - Với mỗi sở thích, hãy chọn các hoạt động phù hợp nhất, ví dụ:
                    *) Nếu là "làm nông dân/ngư dân" → gợi ý các hoạt động như: gặt lúa, bắt cá, chèo thuyền, đi chợ quê...
                    *) Nếu là "ẩm thực" → gợi ý lớp học nấu ăn, tour ẩm thực đường phố, chợ đêm địa phương...
                    *) Nếu là "mạo hiểm" → gợi ý leo núi, trekking, zipline...
                - Chỉ được chọn nội dung sát với các sở thích đã cung cấp. Tuyệt đối không đưa ra hoạt động không liên quan, dù có vẻ thú vị..
                - Thời gian định dạng theo ISO 8601 (VD: 2025-07-20T08:00:00)
                
                Định dạng phản hồi của bạn BẮT BUỘC tuân theo cấu trúc JSON sau:
                
                ### ĐỊNH_DẠNG_PHẢN_HỒI_JSON:
                
                """ + PLAN_DAY_RESPONSE_JSON;
    }
}

