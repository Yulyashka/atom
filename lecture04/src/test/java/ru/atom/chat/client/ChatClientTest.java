package ru.atom.chat.client;

import okhttp3.Response;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import ru.atom.chat.client.ChatClient;
import ru.atom.chat.server.ChatApplication;

import java.io.IOException;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {ChatApplication.class}, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class ChatClientTest {
    private static String MY_NAME_IN_CHAT = "MyName";
    private static String YOUR_NAME_IN_CHAT = "YourName";
    private static String MY_MESSAGE_TO_CHAT = "Hello everyone";

    @Test
    public void login() throws IOException {
        Response response = ChatClient.login(MY_NAME_IN_CHAT);
        System.out.println("[" + response + "]");
        String body = response.body().string();
        ChatClient.logout(MY_NAME_IN_CHAT);
        ChatClient.deleteChat();
        Assert.assertTrue(response.code() == 200 || body.equals("Already logged in"));
    }

    @Test
    public void viewChat() throws IOException {
        Response response = ChatClient.viewChat();
        System.out.println("[" + response + "]");
        String body = response.body().string();
        System.out.println(body);
        ChatClient.deleteChat();
        Assert.assertEquals("No messages in chat", body);
    }


    @Test
    public void viewOnline() throws IOException {
        Response response = ChatClient.viewOnline();
        System.out.println("[" + response + "]");
        String body = response.body().string();
        System.out.println(body);
        ChatClient.deleteChat();
        Assert.assertEquals("No users in chat", body);
    }

    @Test
    public void say1() throws IOException {
        ChatClient.login(MY_NAME_IN_CHAT);
        Response response = ChatClient.say(MY_NAME_IN_CHAT, MY_MESSAGE_TO_CHAT);
        System.out.println("[" + response + "]");
        System.out.println(response.body().string());
        ChatClient.logout(MY_NAME_IN_CHAT);
        ChatClient.deleteChat();
        Assert.assertEquals(200, response.code());
    }

    @Test
    public void say2() throws IOException {
        Response response = ChatClient.say(MY_NAME_IN_CHAT, MY_MESSAGE_TO_CHAT);
        System.out.println("[" + response + "]");
        String body = response.body().string();
        ChatClient.deleteChat();
        Assert.assertEquals("You didn't log in", body);
    }

    @Test
    public void logout1() throws IOException {
        ChatClient.login(MY_NAME_IN_CHAT);
        Response response = ChatClient.logout(MY_NAME_IN_CHAT);
        System.out.println("[" + response + "]");
        ChatClient.deleteChat();
        Assert.assertEquals(200, response.code());
    }

    @Test
    public void logout2() throws IOException {
        Response response = ChatClient.logout(MY_NAME_IN_CHAT);
        System.out.println("[" + response + "]");
        String body = response.body().string();
        ChatClient.deleteChat();
        Assert.assertEquals("You didn't log in", body);
    }

    @Test
    public void deleteChat1() throws IOException {
        ChatClient.login(MY_NAME_IN_CHAT);
        ChatClient.say(MY_NAME_IN_CHAT, MY_MESSAGE_TO_CHAT);
        ChatClient.logout(MY_NAME_IN_CHAT);
        Response response = ChatClient.deleteChat();
        System.out.println("[" + response + "]");
        String body = response.body().string();
        Assert.assertEquals("No messages in chat", body);
    }

    @Test
    public void deleteChat2() throws IOException {
        Response response = ChatClient.deleteChat();
        System.out.println("[" + response + "]");
        String body = response.body().string();
        Assert.assertEquals("Chat was already empty", body);
    }

    @Test
    public void answer1() throws IOException {
        ChatClient.login(MY_NAME_IN_CHAT);
        ChatClient.login(YOUR_NAME_IN_CHAT);
        Response response = ChatClient.answer(MY_NAME_IN_CHAT, YOUR_NAME_IN_CHAT, MY_MESSAGE_TO_CHAT);
        System.out.println("[" + response + "]");
        ChatClient.logout(MY_NAME_IN_CHAT);
        ChatClient.logout(YOUR_NAME_IN_CHAT);
        ChatClient.deleteChat();
        Assert.assertEquals(200, response.code());
    }

    @Test
    public void answer2() throws IOException {
        ChatClient.login(YOUR_NAME_IN_CHAT);
        Response response = ChatClient.answer(MY_NAME_IN_CHAT, YOUR_NAME_IN_CHAT, MY_MESSAGE_TO_CHAT);
        System.out.println("[" + response + "]");
        String body = response.body().string();
        ChatClient.logout(YOUR_NAME_IN_CHAT);
        ChatClient.deleteChat();
        Assert.assertEquals("You didn't log in", body);
    }

    @Test
    public void answer3() throws IOException {
        ChatClient.login(MY_NAME_IN_CHAT);
        Response response = ChatClient.answer(MY_NAME_IN_CHAT, YOUR_NAME_IN_CHAT, MY_MESSAGE_TO_CHAT);
        System.out.println("[" + response + "]");
        String body = response.body().string();
        ChatClient.logout(MY_NAME_IN_CHAT);
        ChatClient.deleteChat();
        Assert.assertEquals("You want to answer user who didn't log in", body);
    }

    @Test
    public void answer4() throws IOException {
        ChatClient.login(MY_NAME_IN_CHAT);
        ChatClient.login(YOUR_NAME_IN_CHAT);
        ChatClient.deleteChat();
        Response response = ChatClient.answer(MY_NAME_IN_CHAT, YOUR_NAME_IN_CHAT, MY_MESSAGE_TO_CHAT);
        System.out.println("[" + response + "]");
        String body = response.body().string();
        ChatClient.logout(MY_NAME_IN_CHAT);
        ChatClient.logout(YOUR_NAME_IN_CHAT);
        ChatClient.deleteChat();
        Assert.assertEquals("No messages to answer", body);
    }
}
