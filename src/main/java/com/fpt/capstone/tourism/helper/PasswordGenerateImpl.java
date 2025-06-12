package com.fpt.capstone.tourism.helper;

import org.springframework.stereotype.Service;

import java.security.SecureRandom;

@Service
public class PasswordGenerateImpl {
    private static final String LOWERCASE = "abcdefghijklmnopqrstuvwxyz";
    private static final String UPPERCASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String DIGITS = "0123456789";
    private static final String SPECIAL_CHARACTERS = "@$!%*?&";
    private static final String ALL_CHARACTERS = LOWERCASE + UPPERCASE + DIGITS + SPECIAL_CHARACTERS;
    private static final int PASSWORD_LENGTH = 8;

    private static final SecureRandom random = new SecureRandom();

    public String generatePassword() {
        StringBuilder password = new StringBuilder();

        // Ensure at least one character from each required set
        password.append(getRandomChar(LOWERCASE));
        password.append(getRandomChar(UPPERCASE));
        password.append(getRandomChar(SPECIAL_CHARACTERS));
        password.append(getRandomChar(DIGITS));

        // Fill the rest with random characters
        for (int i = 4; i < PASSWORD_LENGTH; i++) {
            password.append(getRandomChar(ALL_CHARACTERS));
        }

        // Shuffle to avoid predictable patterns
        return shuffleString(password.toString());
    }

    private static char getRandomChar(String charSet) {
        return charSet.charAt(random.nextInt(charSet.length()));
    }

    private static String shuffleString(String input) {
        char[] characters = input.toCharArray();
        for (int i = characters.length - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            char temp = characters[i];
            characters[i] = characters[j];
            characters[j] = temp;
        }
        return new String(characters);
    }

}
