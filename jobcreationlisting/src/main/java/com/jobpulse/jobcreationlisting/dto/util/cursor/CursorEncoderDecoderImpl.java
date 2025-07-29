public class CursorEncoderDecoderImpl implements CursorEncoderDecoder<CursorV1> {

    private final ObjectMapper objectMapper;
    
    @Autowired
    public CursorEncoderDecoderImpl(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }
    
    @Override
    public String encode(CursorV1 input) {
        try {
            // Convert cursor object to JSON string
            String jsonString = objectMapper.writeValueAsString(input);
            // Encode using Base64 to make it URL-safe
            return Base64.getUrlEncoder().encodeToString(jsonString.getBytes(StandardCharsets.UTF_8));
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to encode cursor", e);
        }
    }

    @Override
    public CursorTypeWrapper<CursorV1> decode(String input) {
        try {
            // Decode Base64 string
            byte[] decodedBytes = Base64.getUrlDecoder().decode(input);
            String jsonString = new String(decodedBytes, StandardCharsets.UTF_8);
            // Convert JSON string back to cursor object
            CursorV1 cursor = objectMapper.readValue(jsonString, CursorV1.class);
            return new CursorTypeWrapper<>(cursor);
        } catch (IOException e) {
            throw new RuntimeException("Failed to decode cursor", e);
        }
    }
}