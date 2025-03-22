package computer_store_app.config;

import com.cloudinary.Cloudinary;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class CloudinaryConfiguration {
    @Bean
    public Cloudinary cloudinary() {
        final Map<String, String> config = new HashMap<>();
        config.put("cloud_name", "dwgcb1ecd");
        config.put("api_key", "573971848884194");
        config.put("api_secret", "WyP8ZA9RR91yxx3qy71zNAsRwR0");
        return new Cloudinary(config);
    }
}
