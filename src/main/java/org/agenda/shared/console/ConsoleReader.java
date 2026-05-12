package org.agenda.shared.console;

import java.util.InputMismatchException;
import java.util.Scanner;

public class ConsoleReader {

    private static final Scanner SC = new Scanner(System.in);

    public static byte readByte(String message){
        while (true) {
            System.out.println(message);
            try {
                byte b = SC.nextByte();
                SC.nextLine();
                return b;
            } catch (InputMismatchException e) {
                System.out.println("It is an invalid type");
                SC.nextLine();
            }
        }
    }

    public static long readLong(String message){
        while (true){
            System.out.println(message);
            try {
                long input = SC.nextLong();
                SC.nextLine();
                return input;
            } catch (InputMismatchException e){
                System.out.println("It is an invalid type");
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
                System.out.println("It is an invalid type");
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
            throw new InvalidInputTypeException("The input can't be a blank space");
        } else if (input.trim().length() < 2) {
            throw new InvalidInputTypeException("The input can't consist of 0 or 1 only letter");
        }
        return input;
    }
}

