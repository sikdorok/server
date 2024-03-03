package com.sikdorok.appapi.presentation.shared;

import com.sikdorok.appapi.infrastructure.WebSecurityConfig;
import com.sikdorok.appapi.infrastructure.config.common.WebConfig;
import com.sikdorok.appapi.infrastructure.jwt.JWTProperties;
import com.sikdorok.appapi.infrastructure.jwt.JwtTokenUtil;
import com.sikdorok.appapi.infrastructure.util.BCryptUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith({SpringExtension.class, RestDocumentationExtension.class})
@AutoConfigureRestDocs
@MockBean(JpaMetamodelMappingContext.class) // @EnableJpaAuditing 사용으로 인해 추가
@Import({BCryptUtils.class, WebConfig.class, WebSecurityConfig.class})
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class) // method _ 를 공백으로 치환
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ControllerTest { // 자식 클래스에서 상속 받아서 사용할 부모 클래스

    @MockBean
    private JwtTokenUtil jwtTokenUtil;
    @MockBean
    private JWTProperties properties;

    @BeforeAll
    void setSalt() {
        ReflectionTestUtils.setField(new BCryptUtils(), "salt", "salt-validations");
    }

}
