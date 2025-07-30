package com.jobpulse.jobcreationlisting.dto.util.cursor;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class CursorEncoderDecoderImpl implements CursorEncoderDecoderContract<CursorV1> {

    private final ObjectMapper objectMapper;
    
    @Autowired
    public CursorEncoderDecoderImpl(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }
    
    @Override
    public String encode(CursorV1 input) {
        try {
            String jsonString = objectMapper.writeValueAsString(input);
            return Base64.getUrlEncoder().encodeToString(jsonString.getBytes(StandardCharsets.UTF_8));
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to encode cursor", e);
        }
    }

    @Override
    public CursorTypeWrapper<CursorV1> decode(String input) {
        try {
            byte[] decodedBytes = Base64.getUrlDecoder().decode(input);
            String jsonString = new String(decodedBytes, StandardCharsets.UTF_8);
            CursorV1 cursor = objectMapper.readValue(jsonString, CursorV1.class);
            return new CursorTypeWrapper<>(cursor);
        } catch (IOException e) {
            throw new RuntimeException("Failed to decode cursor", e);
        }
    }
}