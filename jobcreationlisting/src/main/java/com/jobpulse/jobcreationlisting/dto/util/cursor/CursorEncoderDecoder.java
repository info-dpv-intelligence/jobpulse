package com.jobpulse.jobcreationlisting.dto.util.cursor;

public interface CursorEncoderDecoder<T> {
    String encode(T input);
    CursorTypeWrapper<T> decode(String input);
}
