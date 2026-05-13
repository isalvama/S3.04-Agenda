package org.agenda.shared.console;

import org.agenda.shared.domain.exception.DomainException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.InputMismatchException;
import java.util.Scanner;

public class ConsoleReader {

    private static final Scanner SC = new Scanner(System.in);

    public static long readLong(String message){
        while (true){
            System.out.println(message);
            try {
                long input = SC.nextLong();
                SC.nextLine();
                return input;
            } catch (InputMismatchException e){
                System.out.println("Invalid type: Please enter a valid number");
                SC.nextLine();
            }
        }
    }

    public static int readInt(String message){
        while (true){
            System.out.println(message);
            try {
                int input = SC.nextInt();
                SC.nextLine();
                return input;
            } catch (InputMismatchException e){
                System.out.println("Invalid Input: the Input is of an invalid type");
                SC.nextLine();
            }
        }
    }


    public static String validateString(String message){
        while(true) {
            try {
                return ConsoleReader.readString(message);
            } catch (InvalidInputTypeException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    public static String readString(String message) throws InvalidInputTypeException {
        System.out.println(message);
        String input = SC.nextLine();
        if (input.isBlank()) {
            throw new InvalidInputTypeException("Invalid Input: The input can't be a blank space");
        } else if (input.trim().length() < 2) {
            throw new InvalidInputTypeException("Invalid Input: The input can't consist of 0 or 1 only letter");
        }
        return input;
    }



    public static LocalDateTime readDate(String message, DateTimeFormatter DATE_FORMAT) {
        while (true) {
            System.out.print(message);
            String rawInput = SC.nextLine().trim();
            try {
                return rawInput.isBlank() ? LocalDateTime.now().plusDays(1) : LocalDateTime.parse(rawInput, DATE_FORMAT);
            } catch (DateTimeParseException e) {
                System.out.println("Invalid date format: use the date format dd/MM/yyyy HH:mm");
            }
        }
    }

    public static <T extends Enum<T>> String readEnumName(Class<T> enumClass, String message) {
        while (true){
            System.out.print(message);
            String rawInput = SC.nextLine().trim();
            try {
                return rawInput.isBlank() ? null : validateEnumName(rawInput, enumClass);
            } catch (DomainException ex) {
                System.err.println(ex.getMessage());
            }
        }
    }

    private static <T extends Enum<T>>String validateEnumName(String name, Class<T> enumClass){
        if (Arrays.stream(enumClass.getEnumConstants()).noneMatch(v -> v.toString().equalsIgnoreCase(name))) throw new DomainException(String.format("Invalid %s name: \"%s\" does not match with %s's name of constants (%s)", enumClass.getName(), name, enumClass.getName(), Arrays.toString(enumClass.getEnumConstants())));
        return name;
    }
}

