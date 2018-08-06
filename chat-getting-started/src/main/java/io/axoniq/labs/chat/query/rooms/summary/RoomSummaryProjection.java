package io.axoniq.labs.chat.query.rooms.summary;

import io.axoniq.labs.chat.coreapi.ParticipantJoinedRoomEvent;
import io.axoniq.labs.chat.coreapi.ParticipantLeftRoomEvent;
import io.axoniq.labs.chat.coreapi.RoomCreatedEvent;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/rooms")
public class RoomSummaryProjection {

    private final RoomSummaryRepository roomSummaryRepository;

    public RoomSummaryProjection(RoomSummaryRepository roomSummaryRepository) {
        this.roomSummaryRepository = roomSummaryRepository;
    }

    @GetMapping
    public List<RoomSummary> listRooms() {
        return roomSummaryRepository.findAll();
    }

    @EventHandler
    public void handleRoomCreatedEvent(RoomCreatedEvent event) {
        RoomSummary roomSummary = new RoomSummary(event.getRoomId(), event.getName());
        this.roomSummaryRepository.save(roomSummary);
    }

    @EventHandler
    public void handleParticipantJoinedRoomEvent(ParticipantJoinedRoomEvent event) {
        final RoomSummary roomSummary = this.roomSummaryRepository.findOne(event.getRoomId());
        roomSummary.addParticipant();
        this.roomSummaryRepository.save(roomSummary);
    }

    @EventHandler
    public void handleParticipantLeftRoomEvent(ParticipantLeftRoomEvent event) {
        final RoomSummary roomSummary = this.roomSummaryRepository.findOne(event.getRoomId());
        roomSummary.removeParticipant();
        this.roomSummaryRepository.save(roomSummary);
    }


}
