package com.example.jfxchat.server;

import java.io.*;
import java.nio.file.Path;
import java.util.*;

import static java.lang.System.in;

public class Retrieval {
    private static Path file = Path.of("files","history.txt");
    private static BufferedOutputStream out;
    private static Scanner scanner;

    static {
        try {
            out = new BufferedOutputStream(new FileOutputStream(file.toFile(), true));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void write(UserMessage userMessage) throws IOException {
        out.write((userMessage.getMessage() + "\n").getBytes());
        out.flush();
    }

    public static List<UserMessage> read(int n) {
        try {
            scanner = new Scanner(file.toFile());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }

        List<UserMessage> list = new ArrayList<>();
        String str;

        try {
            while ((str = scanner.nextLine()) != null) {
                System.out.println("str: " + str);
                list.add(new UserMessage(str));
            }
        } catch (NoSuchElementException e) {

        }

        if(list.size() <= n)
            return list;

        return list.stream().skip(list.size() - (list.size() - n)).toList();
    }
}
