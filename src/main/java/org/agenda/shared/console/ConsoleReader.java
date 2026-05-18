package org.agenda.shared.console;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.Scanner;

public class ConsoleReader {

    private static Scanner scanner = new Scanner(System.in);

    public static void setScanner(Scanner newScanner){
        scanner = newScanner;
    }

    public static Long readLong(String message){
        while (true){
            System.out.println(message);
            try {
                long value = Long.parseLong(scanner.nextLine().trim());
                if (value < 0) throw new InvalidInputTypeException("Number cannot be negative");
                return value;
            } catch (NumberFormatException | InvalidInputTypeException e){
                System.out.printf("Invalid Input: %s. Please enter a valid number.", e.getMessage());
            }
        }
    }

    public static int readInt(String message){
        while (true){
            System.out.println(message);
            try {
                int number = Integer.parseInt(scanner.nextLine().trim());
                if (number < 0) throw new RuntimeException("Number should not be negative");
                return number;
            } catch (RuntimeException e){
                System.out.printf("Invalid Input: %s. Please enter a valid number.", e.getMessage());
            }
        }
    }


    public static String readString(String message, int minLength){
        while(true) {
            System.out.println(message);
            String input = scanner.nextLine();
            try {
                return ConsoleReader.validateString(input, minLength);
            } catch (InvalidInputTypeException e) {
                System.out.println("Invalid Input: " + e.getMessage());
            }
        }
    }

    private static String validateString(String input, int minLength) throws InvalidInputTypeException {
        if (input.isBlank()) {
            throw new InvalidInputTypeException("The input can't be a blank space");
        } else if (input.trim().length() < minLength) {
            throw new InvalidInputTypeException(String.format("The input can't consist of less %s letter/s", minLength));
        }
        return input;
    }

    public static LocalDateTime readDate(String message, DateTimeFormatter DATE_FORMAT, LocalDateTime alternativeDate) {
        while (true) {
            System.out.print(message);
            String rawInput = scanner.nextLine().trim();
            if (rawInput.isBlank()) return alternativeDate;
            try {
                return LocalDateTime.parse(rawInput, DATE_FORMAT);
            } catch (DateTimeParseException e) {
                System.out.printf("Invalid Date Format: %s Please use the indicated date format.", e.getMessage());
            }
        }
    }

    public static <T extends Enum<T>> String readEnumName(Class<T> enumClass, String message) {
        while (true){
            System.out.print(message);
            String rawInput = scanner.nextLine().trim().toUpperCase();
            if (rawInput.isBlank()) return null;
            boolean isValid = validateEnumName(rawInput, enumClass);
            if (isValid) return rawInput;
        System.out.printf("Invalid %s Name: \"%s\" does not match with %s's name of constants (%s)", enumClass.getName(), rawInput, enumClass.toString(), Arrays.toString(enumClass.getEnumConstants()));
        }
    }

    private static <T extends Enum<T>> boolean validateEnumName(String input, Class<T> enumClass){
        return (Arrays.stream(enumClass.getEnumConstants()).anyMatch(v -> v.toString().equalsIgnoreCase(input)));
    }
}

