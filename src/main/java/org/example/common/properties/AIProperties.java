package org.example.common.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "ai")
@Data
public class AIProperties {
	private String COZE_URL;
	private String BOT_ID;
	private String TOKEN;
}
