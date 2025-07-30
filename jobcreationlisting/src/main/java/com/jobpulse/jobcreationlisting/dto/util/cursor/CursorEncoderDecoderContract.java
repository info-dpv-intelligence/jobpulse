package com.jobpulse.jobcreationlisting.dto.util.cursor;

public interface CursorEncoderDecoderContract<T> {
    String encode(T input);
    CursorTypeWrapper<T> decode(String input);
}
