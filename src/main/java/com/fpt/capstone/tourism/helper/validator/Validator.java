package com.fpt.capstone.tourism.helper.validator;
import com.fpt.capstone.tourism.dto.request.*;
import com.fpt.capstone.tourism.exception.common.BusinessException;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;

import java.util.List;
import static com.fpt.capstone.tourism.constants.Constants.Message.*;
import static com.fpt.capstone.tourism.constants.Constants.Regex.*;
import static com.fpt.capstone.tourism.constants.Constants.UserExceptionInformation.*;

public class Validator {

    // General Validation
    public static void validateRegex(String value, String regex, String errorMessage) {
        if (!value.matches(regex)) {
            throw BusinessException.of(HttpStatus.BAD_REQUEST, errorMessage);
        }
    }

    public static void isNullOrEmpty(String value, String errorMessage) {
        if (!StringUtils.hasText(value)) {
            throw BusinessException.of(HttpStatus.BAD_REQUEST, errorMessage);
        }
    }

    // Authentication Validation
    public static void validateLogin(String username, String password) {
        isNullOrEmpty(username, EMPTY_USERNAME);
        validateRegex(username, REGEX_USERNAME, USERNAME_INVALID);
        isNullOrEmpty(password, EMPTY_PASSWORD);
        validateRegex(password, REGEX_PASSWORD, PASSWORD_INVALID);
    }

    public static void validateRegister(String username, String password, String rePassword, String fullName,
                                        String phone, String address, String email) {
        isNullOrEmpty(username, EMPTY_USERNAME);
        validateRegex(username, REGEX_USERNAME, USERNAME_INVALID);

        isNullOrEmpty(password, EMPTY_PASSWORD);
        validateRegex(password, REGEX_PASSWORD, PASSWORD_INVALID);

        isNullOrEmpty(rePassword, EMPTY_REPASSWORD);
        if (!password.equals(rePassword)) {
            throw BusinessException.of(PASSWORDS_DO_NOT_MATCH_MESSAGE);
        }

        isNullOrEmpty(fullName, EMPTY_FULL_NAME);
        validateRegex(fullName, REGEX_FULLNAME, FULL_NAME_INVALID);

        isNullOrEmpty(phone, EMPTY_PHONE_NUMBER);
        validateRegex(phone, REGEX_PHONE, PHONE_INVALID);

        isNullOrEmpty(address, EMPTY_ADDRESS);

        isNullOrEmpty(email, EMPTY_EMAIL);
        validateRegex(email, REGEX_EMAIL, EMAIL_INVALID);
    }

    public static void validateLocation(LocationRequestDTO locationRequestDTO){
        isNullOrEmpty(locationRequestDTO.getName(), EMPTY_LOCATION_NAME);
        isNullOrEmpty(locationRequestDTO.getDescription(), EMPTY_LOCATION_DESCRIPTION);
        isNullOrEmpty(locationRequestDTO.getImage(), EMPTY_LOCATION_IMAGE);
    }


}
