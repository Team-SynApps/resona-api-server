package synapps.resona.api.mysql.socialMedia.dto.feed;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;

public class ContentDeserializer extends JsonDeserializer<String> {
    @Override
    public String deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String content = p.getValueAsString();
        if (content != null) {
            return content.replaceAll("[\\x00-\\x1F\\x7F]", "");
        }
        return null;
    }
}
