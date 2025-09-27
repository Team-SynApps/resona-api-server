package com.synapps.resona.dto;

import java.util.List;

public record CreateRoomRequest(String roomName, List<Long> memberIds) {
}