package com.synapps.resona.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CursorResult<T> {

    private final List<T> values;
    private final boolean hasNext;
    private final String nextCursor;
}