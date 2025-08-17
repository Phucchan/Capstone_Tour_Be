package com.fpt.capstone.tourism.service.impl;

import com.fpt.capstone.tourism.config.VNPayConfig;
import com.fpt.capstone.tourism.constants.Constants;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class VNPayServiceImplTest {

    @InjectMocks
    private VNPayServiceImpl vnPayService;

    // =================================================================
    // Test Cases for generatePaymentUrl
    // =================================================================

    // --- Valid Input Cases ---

    @Test
    @DisplayName("[generatePaymentUrl] Valid Input: Tạo URL thành công với đầy đủ tham số hợp lệ")
    void generatePaymentUrl_whenInputsAreValid_shouldReturnCompleteUrl() throws UnsupportedEncodingException {
        System.out.println("Test Case: Valid Input - Tạo URL thành công với đầy đủ tham số hợp lệ.");
        // Arrange
        double total = 550000.0;
        String orderInformation = "BK-TEST-001";
        String urlReturn = "http://localhost:3000/payment-result";
        int minuteExpire = 15;

        // Act
        String resultUrl = vnPayService.generatePaymentUrl(total, orderInformation, urlReturn, minuteExpire);

        // Assert
        assertNotNull(resultUrl, "URL trả về không được là null.");
        assertTrue(resultUrl.startsWith(VNPayConfig.vnp_PayUrl), "URL phải bắt đầu với URL thanh toán của VNPay.");
        assertTrue(resultUrl.contains("vnp_Amount=55000000"), "Số tiền phải được nhân với 100.");
        assertTrue(resultUrl.contains("vnp_OrderInfo=" + URLEncoder.encode(orderInformation, StandardCharsets.US_ASCII)), "Thông tin đơn hàng phải có trong URL.");
        assertTrue(resultUrl.contains("vnp_ReturnUrl="), "URL trả về phải có trong URL.");
        assertTrue(resultUrl.contains("vnp_SecureHash="), "Phải có mã hash bảo mật.");

        System.out.println("Log: " + Constants.Message.SUCCESS);
    }

    @Test
    @DisplayName("[generatePaymentUrl] Valid Input: Tạo URL thành công khi tổng tiền là 0")
    void generatePaymentUrl_whenTotalIsZero_shouldGenerateUrlWithZeroAmount() {
        System.out.println("Test Case: Valid Input - Tạo URL thành công khi tổng tiền là 0.");
        // Arrange
        double total = 0.0;

        // Act
        String resultUrl = vnPayService.generatePaymentUrl(total, "ORDER-ZERO", "http://return.url", 10);

        // Assert
        assertNotNull(resultUrl);
        assertTrue(resultUrl.contains("vnp_Amount=0"), "Số tiền trong URL phải là 0.");

        System.out.println("Log: " + Constants.Message.SUCCESS);
    }

    // --- Invalid / Edge Input Cases ---

    @Test
    @DisplayName("[generatePaymentUrl] Invalid Input: Xử lý khi orderInformation là chuỗi rỗng")
    void generatePaymentUrl_whenOrderInfoIsEmpty_shouldGenerateUrlWithoutOrderInfo() {
        System.out.println("Test Case: Invalid Input - Xử lý khi orderInformation là chuỗi rỗng.");
        // Arrange
        String emptyOrderInfo = "";

        // Act
        String resultUrl = vnPayService.generatePaymentUrl(1000, emptyOrderInfo, "http://return.url", 10);

        // Assert
        assertNotNull(resultUrl);
        // Do logic `if ((fieldValue != null) && (!fieldValue.isEmpty()))`, tham số này sẽ bị bỏ qua
        assertFalse(resultUrl.contains("vnp_OrderInfo="), "URL không được chứa tham số vnp_OrderInfo khi giá trị là chuỗi rỗng.");

        System.out.println("Log: " + Constants.Message.SUCCESS + " (Hàm xử lý chuỗi rỗng như mong đợi).");
    }

    @Test
    @DisplayName("[generatePaymentUrl] Invalid Input: Xử lý khi urlReturn là null")
    void generatePaymentUrl_whenUrlReturnIsNull_shouldConcatenateWithNullString() throws UnsupportedEncodingException {
        System.out.println("Test Case: Invalid Input - Xử lý khi urlReturn là null.");
        // Arrange
        String nullUrlReturn = null;
        String expectedReturnUrlContent = "null" + VNPayConfig.vnp_Returnurl;

        // Act
        String resultUrl = vnPayService.generatePaymentUrl(1000, "ORDER-NULL-RETURN", nullUrlReturn, 10);

        // Assert
        assertNotNull(resultUrl);
        // Kiểm tra xem chuỗi "null" có được nối với URL mặc định hay không
        assertTrue(resultUrl.contains("vnp_ReturnUrl=" + URLEncoder.encode(expectedReturnUrlContent, StandardCharsets.US_ASCII)), "vnp_ReturnUrl phải chứa chuỗi 'null' và URL mặc định.");

        System.out.println("Log: " + Constants.Message.SUCCESS + " (Hàm xử lý null input như mong đợi).");
    }

    @Test
    @DisplayName("[generatePaymentUrl] Invalid Input: Xử lý khi orderInformation là null")
    void generatePaymentUrl_whenOrderInfoIsNull_shouldGenerateUrlWithoutOrderInfo() {
        System.out.println("Test Case: Invalid Input - Xử lý khi orderInformation là null.");
        // Arrange
        String nullOrderInfo = null;

        // Act
        String resultUrl = vnPayService.generatePaymentUrl(1000, nullOrderInfo, "http://return.url", 10);

        // Assert
        assertNotNull(resultUrl);
        // Do logic `if ((fieldValue != null) && (!fieldValue.isEmpty()))`, tham số này sẽ bị bỏ qua
        assertFalse(resultUrl.contains("vnp_OrderInfo="), "URL không được chứa tham số vnp_OrderInfo khi giá trị là null.");

        System.out.println("Log: " + Constants.Message.SUCCESS + " (Hàm xử lý null input như mong đợi).");
    }

    @Test
    @DisplayName("[generatePaymentUrl] Invalid Input: Xử lý khi tổng tiền là số âm")
    void generatePaymentUrl_whenTotalIsNegative_shouldGenerateUrlWithNegativeAmount() {
        System.out.println("Test Case: Invalid Input - Xử lý khi tổng tiền là số âm.");
        // Arrange
        double negativeTotal = -50000.0;

        // Act
        String resultUrl = vnPayService.generatePaymentUrl(negativeTotal, "ORDER-NEGATIVE", "http://return.url", 10);

        // Assert
        assertNotNull(resultUrl);
        // Hàm không validate giá trị âm, nó chỉ đơn giản là chuyển đổi
        assertTrue(resultUrl.contains("vnp_Amount=-5000000"), "Số tiền trong URL phải là một số âm.");

        System.out.println("Log: " + Constants.Message.SUCCESS + " (Hàm không validate giá trị âm, hoạt động như được viết).");
    }

    @Test
    @DisplayName("[generatePaymentUrl] Invalid Input: Xử lý khi minuteExpire là số âm")
    void generatePaymentUrl_whenMinuteExpireIsNegative_shouldGenerateUrlWithPastExpiry() {
        System.out.println("Test Case: Invalid Input - Xử lý khi minuteExpire là số âm.");
        // Arrange
        int negativeExpire = -10;

        // Act
        String resultUrl = vnPayService.generatePaymentUrl(1000, "ORDER-PAST-EXPIRY", "http://return.url", negativeExpire);

        // Assert
        assertNotNull(resultUrl);
        // Chúng ta không thể kiểm tra chính xác giá trị ngày hết hạn,
        // nhưng có thể xác nhận rằng URL vẫn được tạo thành công.
        assertTrue(resultUrl.contains("vnp_ExpireDate="), "URL vẫn phải chứa ngày hết hạn, dù nó đã qua.");

        System.out.println("Log: " + Constants.Message.SUCCESS + " (Hàm không validate giá trị âm, hoạt động như được viết).");
    }
}