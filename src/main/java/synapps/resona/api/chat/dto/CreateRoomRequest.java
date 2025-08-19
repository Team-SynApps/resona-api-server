package synapps.resona.api.chat.dto;

import java.util.List;

public record CreateRoomRequest(String roomName, List<Long> memberIds) {
}