package com.mjy.easysignapp.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@SecurityScheme(
        name = "csrfToken",
        type = SecuritySchemeType.APIKEY,
        in = SecuritySchemeIn.HEADER,
        paramName = "X-XSRF-TOKEN"  // CSRF 토큰을 받을 헤더 이름
)
@OpenAPIDefinition(
        info = @Info(
                title = "EasySignApp API",
                description = "<p>간단한 회원가입 앱입니다.</p>" +
                        "<p>/api/csrf 호출로 CSRF 토큰을 헤더에 입력 후 API 요청이 가능합니다.</p>" +
                        "<p>회원가입 시 이메일 인증을 먼저 진행해야 합니다.</p>",
                license = @License(name = "GitHub", url = "https://github.com/JunYoungMoon/EasySignApp")
        ),
        servers = @Server(url = "http://localhost:8080")
)
public class SwaggerConfig {
}
