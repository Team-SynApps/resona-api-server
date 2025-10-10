package com.synapps.resona.fanout.dto;

import java.util.List;

public record PaginatedResult<T>(List<T> content, boolean hasNext) {}