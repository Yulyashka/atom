package ru.atom.chat.server;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

@Controller
@RequestMapping("chat")
public class ChatController {
    private Queue<String> messages = new ConcurrentLinkedQueue<>();
    private Map<String, String> usersOnline = new ConcurrentHashMap<>();

    /**
     * curl -X POST -i localhost:8080/chat/login -d "name=MyName"
     */
    @RequestMapping(
            path = "login",
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> login(@RequestParam("name") String name) {
        if (name.length() < 1) {
            return ResponseEntity.badRequest().body("Too short name");
        }
        if (name.length() > 20) {
            return ResponseEntity.badRequest().body("Too long name");
        }
        if (usersOnline.containsKey(name)) {
            return ResponseEntity.badRequest().body("Already logged in");
        }
        usersOnline.put(name, name);
        messages.add("[" + name + "] logged in");
        return ResponseEntity.ok().build();
    }

    /**
     * curl -i localhost:8080/chat/online
     */
    @RequestMapping(
            path = "online",
            method = RequestMethod.GET,
            produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity online() {
        if (usersOnline.size() == 0)
            return ResponseEntity.badRequest().body("No users in chat");
        String responseBody = String.join("\n", usersOnline.keySet().stream().sorted().collect(Collectors.toList()));
        return ResponseEntity.ok(responseBody);
    }

    /**
     * curl -X POST -i localhost:8080/chat/logout -d "name=MyName"
     */
    @RequestMapping(
        path = "logout",
        method = RequestMethod.POST,
        consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> logout(@RequestParam("name") String name) {
        if (usersOnline.containsKey(name)) {
            usersOnline.remove(name,name);
            messages.add("[" + name + "] logged out");
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.badRequest().body("You didn't log in");
    }

    /**
     * curl -X POST -i localhost:8080/chat/say -d "name=MyName&msg=Hello everyone in this chat"
     */
    @RequestMapping(
        path = "say",
        method = RequestMethod.POST,
        produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity say(@RequestParam("name") String name, @RequestParam("msg") String msg) {
        if (usersOnline.containsKey(name)) {
            messages.add("[" + name + "]: " + msg);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.badRequest().body("You didn't log in");
    }


    /**
     * curl -i localhost:8080/chat/chat
     */
    @RequestMapping(
        path = "chat",
        method = RequestMethod.GET,
        produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity chat() {
        if (messages.size() == 0)
            return ResponseEntity.badRequest().body("No messages in chat");
        String responseBody = String.join("\n", messages);
        return ResponseEntity.ok(responseBody);
    }

    /**
     * curl -X DELETE localhost:8080/chat/deleteChat
     */
    @RequestMapping(
        path = "deleteChat",
        method = RequestMethod.DELETE,
        produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity deleteChat() {
        String responseBody;
        if (messages.size() > 0) {
            messages.clear();
            responseBody = String.join("\n", "No messages in chat");
        } else {
            responseBody = String.join("\n", "Chat was already empty");
        }
        return ResponseEntity.ok(responseBody);
    }

    /**
     * curl -i localhost:8080/chat/answer -d "name1=MyName&name2=YourName&msg=My answer to you"
     */
    @RequestMapping(
        path = "answer",
        method = RequestMethod.POST,
        produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity answer(@RequestParam("name1") String name1,@RequestParam("name2") String name2,
                                 @RequestParam("msg") String msg) {
        if (!usersOnline.containsKey(name1)) {
            return ResponseEntity.badRequest().body("You didn't log in");
        }
        if (!usersOnline.containsKey(name2)) {
            return ResponseEntity.badRequest().body("You want to answer user who didn't log in");
        }
        if (messages.size() == 0) {
            return ResponseEntity.badRequest().body("No messages to answer");
        }
        messages.add("[" + name1 + "] " + " answer to [" + name2 + "]: " + msg);
        return ResponseEntity.ok().build();
    }

}
