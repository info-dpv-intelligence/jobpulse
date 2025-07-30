package com.jobpulse.jobcreationlisting.dto.util.cursor;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CursorTypeWrapper<T> {
    private final T cursor;
}
