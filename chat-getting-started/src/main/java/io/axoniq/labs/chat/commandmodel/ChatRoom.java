package io.axoniq.labs.chat.commandmodel;

import io.axoniq.labs.chat.coreapi.*;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.commandhandling.model.AggregateIdentifier;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.spring.stereotype.Aggregate;

import java.util.ArrayList;
import java.util.List;

import static org.axonframework.commandhandling.model.AggregateLifecycle.apply;

// Single aggregate to Process Commands and Produce Events
@Aggregate
public class ChatRoom {

    @AggregateIdentifier
    private String roomId;
    private List<String> joinedParticipants = new ArrayList<>();

    public ChatRoom() {
    }

    //  testCreateChatRoom()
    @CommandHandler
    public ChatRoom(CreateRoomCommand command) {
        apply(new RoomCreatedEvent(command.getRoomId(), command.getName()));
    }

    @EventSourcingHandler
    public void handleRoomCreatedEvent(RoomCreatedEvent event) {
        this.roomId = event.getRoomId();
    }


    //  testJoinChatRoom()
    @CommandHandler
    public void handleJoinRoomCommand(JoinRoomCommand command) {
        if (command.getRoomId().equalsIgnoreCase(this.roomId) && !joinedParticipants.contains(command.getParticipant())) {
            apply(new ParticipantJoinedRoomEvent(command.getParticipant(), command.getRoomId()));
        }
    }

    @EventSourcingHandler
    public void handleParticipantJoinedRoomEvent(ParticipantJoinedRoomEvent event) {
        this.joinedParticipants.add(event.getParticipant());
    }


    // Candidate can leave room once only if he has joined it already
    @CommandHandler
    public void handleLeaveRoomCommand(LeaveRoomCommand leaveRoomCommand) {
        if (leaveRoomCommand.getRoomId().equalsIgnoreCase(this.roomId) &&
                joinedParticipants.contains(leaveRoomCommand.getParticipant())) {
            apply(new ParticipantLeftRoomEvent(leaveRoomCommand.getParticipant(), leaveRoomCommand.getRoomId()));
        }
    }

    @EventSourcingHandler
    public void handleParticipantLeftRoomEvent(ParticipantLeftRoomEvent participantLeftRoomEvent) {
        this.joinedParticipants.remove(participantLeftRoomEvent.getParticipant());
    }


    // testPostMessage((
    @CommandHandler
    public void handlePostMessageCommand(PostMessageCommand command) {
        if (command.getRoomId().equalsIgnoreCase(this.roomId) && this.joinedParticipants.contains(command.getParticipant())) {
            apply(new MessagePostedEvent(command.getParticipant(), command.getRoomId(), command.getMessage()));
        } else {
            throw new IllegalStateException();
        }
    }

    @EventSourcingHandler
    public void handleMessagePostedEvent(MessagePostedEvent event) {
        // not sure if this event must be handled in any way...
    }


}
