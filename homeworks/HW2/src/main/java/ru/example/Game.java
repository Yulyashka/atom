package ru.example;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Random;
import java.util.Scanner;

import java.util.logging.*;

public class Game {

    // создание логгера
    private static String timeStamp = new SimpleDateFormat("yyyy.MM.dd HH.mm.ss").format(Calendar.getInstance().getTime());
    private static final Logger LOGGER = Logger.getLogger(Game.class.getName());
    private static FileHandler fh;

    // получение случайного слова
    public static String getRandomWord(String filename) {

        try {
            Scanner scanner = new Scanner(new File(filename));
            int count = 0;
            //подсчет количества строк в файле
            while (scanner.hasNext()) {
                scanner.nextLine();
                count++;
            }

            Random rnd = new Random(System.currentTimeMillis());
            //выбор случайного слова
            int number = rnd.nextInt(count);

            scanner = new Scanner(new File(filename));
            for (int i = 0; i < number - 1; i++)
                scanner.nextLine();
            //возврат случайного слова
            return (scanner.nextLine());

        } //если файл не найден, то выводит ошибку
        catch (FileNotFoundException ex) {

            System.err.println("File not found: " + ex.getMessage());
            LOGGER.log(Level.WARNING, "File not found: ", ex);
            return null;
        }

    }

    //подсчет быков
    public static int countBulls(String inputWord, String hiddenWord) {

        int count = Math.min(hiddenWord.length(), inputWord.length());
        int bulls = 0;
        for (int j = 0; j < count; j++)
            if (hiddenWord.charAt(j) == inputWord.charAt(j))
                bulls++;
        return bulls;

    }

    //подсчет коров
    public static int countCows(String inputWord, String hiddenWord) {

        int count = Math.min(hiddenWord.length(), inputWord.length());
        int cows = 0;
        for (int j = 0; j < count; j++)
            if (hiddenWord.charAt(j) != inputWord.charAt(j) && hiddenWord.contains(Character.toString(inputWord.charAt(j))))
                cows++;
        return cows;
    }

    public static boolean play(String filename) {

        //получаем случайное слово
        String hiddenWord = getRandomWord(filename);
        //System.out.println(hiddenWord);
        //длина слова;
        int count = hiddenWord.length();
        //количество попыток
        int countOfAttempt = 10;

        System.out.println("I offered a " + count + "-letter word, your guess?");
        LOGGER.log(Level.INFO, "I offered a " + count + "-letter word, your guess?");

        Scanner scan = new Scanner(System.in);
        for (int i = 0; i < countOfAttempt; i++)
        {
            System.out.println("It's your " + (i+1) + " attempt out of 10");
            LOGGER.log(Level.INFO,"It's your " + (i+1) + " attempt out of 10");
            String inputWord = scan.nextLine();
            //проверка на совпадение слов
            if (hiddenWord.equals(inputWord)) {
                System.out.println("You Won!!!");
                LOGGER.log(Level.INFO,"You won!!!");
                return true;
            }

            //подсчет быков и коров
            int bulls = countBulls(inputWord, hiddenWord);
            int cows = countCows(inputWord, hiddenWord);
            System.out.println("Bulls: " + bulls);
            System.out.println("Cows: " + cows);
            LOGGER.log(Level.INFO,"Bulls: " + bulls + ";  Cows: " + cows);
        }

        //10 попыток закончились
        System.out.println("You Lose: " + hiddenWord);
        LOGGER.log(Level.INFO,"You Lose: " + hiddenWord);
        return false;

    }

    public static void main(String[] args) {

        try {
            boolean again = true;

            fh = new FileHandler("log_" + timeStamp + ".log");
            LOGGER.addHandler(fh);
            SimpleFormatter formatter = new SimpleFormatter();
            fh.setFormatter(formatter);

            System.out.println("Welcome to Bulls and Cows game!");
            LOGGER.log(Level.INFO,"Logger started!");

            play("dictionary.txt");
            while(again)
            {
                System.out.println("Wanna play again? Y/N");
                Scanner scan = new Scanner(System.in);
                String answer = scan.nextLine();
                if (answer.equals("Y")|| answer.equals("y"))
                    play("dictionary.txt");
                else if(answer.equals("N")|| answer.equals("n")) again = false;
            }

        } catch (SecurityException ex) {
            LOGGER.log(Level.SEVERE, "Exception: ", ex);
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, "Exception: ", ex);
        }

    }

}
