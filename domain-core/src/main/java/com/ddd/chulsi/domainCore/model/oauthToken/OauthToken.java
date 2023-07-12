package com.ddd.chulsi.domainCore.model.oauthToken;

import com.ddd.chulsi.domainCore.model.shared.DefinedCode;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;

import java.util.UUID;

@Getter
@Entity
@Table(name = "oauthToken")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@DynamicInsert
@DynamicUpdate
public class OauthToken {

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(name = "oauthTokenId", columnDefinition = "BINARY(16)")
    private UUID oauthTokenId;

    @Enumerated(EnumType.STRING)
    @Column(name = "oauthType", length = 10)
    private DefinedCode oauthType;

    @Column(name = "oauthId")
    private Long oauthId;

    @Column(name = "accessToken")
    private String accessToken;

    public void updateAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
}
