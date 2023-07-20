package com.ddd.chulsi.infrastructure.jwt;

import com.ddd.chulsi.domainCore.model.shared.DefinedCode;
import lombok.*;

import java.util.UUID;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class JWTClaim {

    private DefinedCode auth;
    private UUID usersId;

}
