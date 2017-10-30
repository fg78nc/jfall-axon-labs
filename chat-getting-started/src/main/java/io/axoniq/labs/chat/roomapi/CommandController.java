package io.axoniq.labs.chat.roomapi;

import org.axonframework.commandhandling.gateway.CommandGateway;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.concurrent.Future;

@RestController
public class CommandController {

    private final CommandGateway commandGateway;

    public CommandController(CommandGateway commandGateway) {
        this.commandGateway = commandGateway;
    }

    @PostMapping("/rooms")
    public Future<String> createChatRoom(@RequestBody @Valid Room room) {
        // TODO: Send a command for this API call. Don't forget to default the 'roomId' to a random UUID.
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @PostMapping("/rooms/{roomId}/participants")
    public Future<Void> joinChatRoom(@PathVariable String roomId, @RequestBody @Valid Participant participant) {
        // TODO: Send a command for this API call
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @PostMapping("/rooms/{roomId}/messages")
    public Future<Void> postMessage(@PathVariable String roomId, @RequestBody @Valid Message message) {
        // TODO: Send a command for this API call
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @DeleteMapping("/rooms/{roomId}/participants")
    public Future<Void> leaveChatRoom(@PathVariable String roomId, @RequestBody @Valid Participant participant) {
        // TODO: Send a command for this API call
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public static class Message {

        @NotEmpty
        private String name;
        @NotEmpty
        private String message;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }

    public static class Participant {

        @NotEmpty
        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    public static class Room {

        private String roomId;
        @NotEmpty
        private String name;

        public String getRoomId() {
            return roomId;
        }

        public void setRoomId(String roomId) {
            this.roomId = roomId;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
