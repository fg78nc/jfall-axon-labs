package io.axoniq.labs.chat.query.rooms.participants;

import io.axoniq.labs.chat.coreapi.ParticipantJoinedRoomEvent;
import io.axoniq.labs.chat.coreapi.ParticipantLeftRoomEvent;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static java.util.stream.Collectors.toList;

@RestController
@RequestMapping("/rooms/{roomId}/participants")
public class RoomParticipantsProjection {

    private final RoomParticipantsRepository repository;

    public RoomParticipantsProjection(RoomParticipantsRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<String> participantsInRoom(@PathVariable String roomId) {
        return repository.findRoomParticipantsByRoomId(roomId)
                         .stream()
                         .map(RoomParticipant::getParticipant).sorted().collect(toList());
    }

    @EventHandler
    public void handleParticipantJoinedRoomEvent(ParticipantJoinedRoomEvent event){
        RoomParticipant roomParticipant = new RoomParticipant(event.getRoomId(), event.getParticipant());
        this.repository.save(roomParticipant);
    }

    @EventHandler
    public void handleParticipantLeftRoomEvent(ParticipantLeftRoomEvent event){
        this.repository.deleteByParticipantAndRoomId(event.getParticipant(), event.getRoomId());
    }

}
