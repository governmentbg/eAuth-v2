package bg.bulsi.egov.eauth;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

public class IntegrationTestUtil {

    public static final MediaType APPLICATION_JSON_UTF8 = new MediaType(MediaType.APPLICATION_JSON.getType(), MediaType.APPLICATION_JSON.getSubtype(), Charset.forName("utf8"));


    public static <T> T loadJsonData(String file, Class<T> valueType) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.findAndRegisterModules();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        InputStream in = valueType.getClassLoader()
                .getResourceAsStream(file);

        return mapper.readValue(in, valueType);
    }

    public static <T> T extractValueFromJsonPath(String path, String json) {
        return JsonPath.compile(path).read(json);
    }
}
