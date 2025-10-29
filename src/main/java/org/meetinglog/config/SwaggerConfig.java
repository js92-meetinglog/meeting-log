package org.meetinglog.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Value("${server.servlet.context-path:/api}")
    private String contextPath;

    @Bean
    public OpenAPI openAPI() {
        Server server = new Server();
        server.setUrl(contextPath);
        server.setDescription("Meeting Log API Server");

        Contact contact = new Contact();
        contact.setEmail("admin@meetinglog.com");
        contact.setName("Meeting Log Team");

        License license = new License()
                .name("MeetingLog License")
                .url("https://github.com/js92-meetinglog/meeting-log");

        Info info = new Info()
                .title("Meeting Log API")
                .version("1.0")
                .contact(contact)
                .description("Meeting Log 서비스를 위한 REST API 문서입니다.")
                .license(license);

        return new OpenAPI()
                .info(info)
                .servers(List.of(server));
    }
}
