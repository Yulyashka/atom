package ru.example;

import org.junit.Test;
import static junit.framework.Assert.assertEquals;
import static ru.example.Game.*;

public class testGame {

    @Test
    public void getWord() throws Exception {
        assertEquals(null, getRandomWord("testNoDictionary.txt"));
    }

    @Test
    public void getWord2() throws Exception {
        assertEquals("believer", getRandomWord("testWord.txt"));
    }

    @Test
    public void bulls() throws Exception {
        assertEquals(2, countBulls("brainiac", "believer"));
    }

    @Test
    public void cows() throws Exception {
        assertEquals(2, countCows("brainiac", "believer"));
    }


}
