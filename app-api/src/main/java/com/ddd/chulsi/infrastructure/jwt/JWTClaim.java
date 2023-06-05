package com.ddd.chulsi.infrastructure.jwt;

import com.ddd.chulsi.domainCore.model.shared.DefinedCode;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class JWTClaim {

    private DefinedCode auth;
    private Long userId;

}
