package talis.bry.Utils;

import org.springframework.stereotype.Component;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Component
public class CPFHandler {
    public static boolean validate(String cpf) {
        cpf = clean(cpf);
        if (cpf.length() < 11) {
            return false;
        }
        try {
            Long.parseLong(cpf);
        } catch (NumberFormatException e) {
            return false;
        }

        // Checks for CPFs with all digits equal, which pass the validation algorithm but are invalid
        int firstDigit = Character.getNumericValue(cpf.charAt(0));
        for (int i = 1; i < 11; i++) {
            if (Character.getNumericValue(cpf.charAt(i)) != firstDigit) {
                break;
            }
            if (i == 10) {
                return false;
            }
        }

        // First step: check the first verifier digit
        // Get the sum of the products between the digits and the numbers in a sequence decreasing from 10 to 2
        // Example: 123.456.789-09
        // 1*10 + 2*9 + 3*8 + 4*7 + 5*6 + 6*5 + 7*4 + 8*3 + 9*2 = 280
        int sum = 0;
        for (int i = 0; i < 9; i++) {
            char digit = cpf.charAt(i);
            sum += Character.getNumericValue(digit) * (10 - i);
        }

        // The sum obtained is multiplied by 10 and divided by 11.
        // If the remainder is equal to the first verifier digit (10th digit), the first step is complete.
        int firstVerifierDigit = Character.getNumericValue(cpf.charAt(9));
        boolean validFirstDigit = ((sum*10) % 11) % 10 == firstVerifierDigit;
        if (!validFirstDigit) {
            throw new IllegalArgumentException("Invalid CPF");
        }

        // Sum of the products between the digits and the numbers in a sequence decreasing from 11 to 2
        // Example: 123.456.789-09
        // 1*11 + 2*10 + 3*9 + 4*8 + 5*7 + 6*6 + 7*5 + 8*4 + 9*3 + 0*2 = 319
        sum = 0;
        for (int i = 0; i < 10; i++) {
            char digit = cpf.charAt(i);
            sum += Character.getNumericValue(digit) * (11 - i);
        }

        // The sum obtained is multiplied by 10 and divided by 11.
        // If the remainder is equal to the second verifier digit (11th digit), the second step is complete.
        int secondVerifierDigit = Character.getNumericValue(cpf.charAt(10));
        return ((sum*10) % 11) % 10 == secondVerifierDigit;
    }

    public static String hash(String cpf) throws NoSuchAlgorithmException {
        cpf = clean(cpf);
        MessageDigest md = MessageDigest.getInstance("SHA-512");

        byte[] hashedCpf = md.digest(cpf.getBytes());

        StringBuilder sb = new StringBuilder();
        for (byte b : hashedCpf) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    public static String anonymize(String cpf) {
        String cleanCpf = clean(cpf);
        return "XXX." + cleanCpf.substring(3, 6) + "." + cleanCpf.substring(6, 9) + "-XX";
    }

    public static String clean(String cpf) {
        return cpf.replaceAll("[.-]", "");
    }
}
