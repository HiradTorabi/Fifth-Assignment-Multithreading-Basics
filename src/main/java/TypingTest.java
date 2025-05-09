import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class TypingTest
{
    private static String lastInput = "";
    private static Scanner scanner = new Scanner(System.in);
    public static class InputRunnable implements Runnable
    {
        @Override
        public void run()
        {
            try
            {
                if (scanner.hasNextLine())
                {
                    lastInput = scanner.nextLine();
                }
            } catch (Exception e)
            {
                lastInput = "";
            }
        }
    }

    public static void testWord(String wordToTest)
    {
        try
        {
            System.out.println(wordToTest);
            lastInput = "";
            Thread inputThread = new Thread(new InputRunnable());
            inputThread.start();
            long startTime = System.currentTimeMillis();
            int timeout = wordToTest.length() * 500;
            while (inputThread.isAlive() && System.currentTimeMillis() - startTime < timeout)
            {
                if (!lastInput.isEmpty()) break;
                Thread.sleep(100);
            }
            System.out.println();
            System.out.println("You typed: " + lastInput);
            if (lastInput.trim().equalsIgnoreCase(wordToTest.trim()))
            {
                System.out.println("Correct");
            }
            else
            {
                System.out.println("Incorrect");
            }

        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static void typingTest(List<String> inputList) throws InterruptedException
    {
        int correctCount = 0;
        int incorrectCount = 0;
        long totalTime = 0;

        for (String wordToTest : inputList)
        {
            long startTime = System.currentTimeMillis();
            testWord(wordToTest);
            long duration = wordToTest.length() * 500;
            totalTime += duration;

            if (lastInput.trim().equalsIgnoreCase(wordToTest.trim()))
            {
                correctCount++;
            }
            else
            {
                incorrectCount++;
            }

            Thread.sleep(2000);
        }
        System.out.println("Correct words: " + correctCount);
        System.out.println("Incorrect words: " + incorrectCount);
        System.out.println("Total time: " + (totalTime / 1000.0) + " seconds");
        System.out.println("Average time per word: " + (totalTime / inputList.size()) + " ms");
    }

    public static void main(String[] args) throws InterruptedException
    {
        List<String> words = new ArrayList<>();
        words.add("remember");
        words.add("my friend");
        words.add("boredom");
        words.add("is a");
        words.add("crime");
        typingTest(words);
        System.out.println("Press enter to exit.");
    }
}
