package com.sikdorok.appapi.infrastructure.jwt;

import com.sikdorok.domaincore.model.shared.DefinedCode;
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
