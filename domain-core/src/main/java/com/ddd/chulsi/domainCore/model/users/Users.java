package com.ddd.chulsi.domainCore.model.users;

import com.ddd.chulsi.domainCore.model.shared.DateColumn;
import com.ddd.chulsi.domainCore.model.shared.DefinedCode;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Entity
@Table(name = "users")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@DynamicInsert
@DynamicUpdate
public class Users extends DateColumn {

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(name = "usersId", columnDefinition = "BINARY(16)")
    private UUID usersId;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "auth", length = 10, nullable = false)
    private DefinedCode auth = DefinedCode.C000100002;

    @Enumerated(EnumType.STRING)
    @Column(name = "oauthType", length = 10)
    private DefinedCode oauthType;

    @Column(name = "oauthId")
    private Long oauthId;

    @Column(name = "email")
    private String email;

    @Column(name = "password")
    private String password;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "lastLoginAt")
    private LocalDateTime lastLoginAt;

    @Column(name = "refreshToken")
    private String refreshToken;

    @Column(name = "level", nullable = false)
    private int level;

    @Column(name = "points", nullable = false)
    private long points;

    @Column(name = "photosLimit", nullable = false, length = 20)
    private int photosLimit;

    @Enumerated(EnumType.STRING)
    @Column(name = "appInfoType", length = 10)
    private DefinedCode appInfoType;

    @Column(name = "appInfoModel", length = 100)
    private int appInfoModel;

    @Column(name = "appInfoDeviceToken")
    private int appInfoDeviceToken;

    @Column(name = "revokedAt")
    private LocalDateTime revokedAt;

    // 마지막 로그인 시간 수정
    public void updateLastLoginAt() {
        this.lastLoginAt = LocalDateTime.now();
    }

    public void updatePassword(String password) {
        if (StringUtils.isNotBlank(password)) this.password = password;
    }
    public void updateEmail(String email) {
        if (StringUtils.isNotBlank(email)) this.email = email;
    }
    public void updateRefreshToken(String refreshToken) {
        if (StringUtils.isNotBlank(refreshToken)) this.refreshToken = refreshToken;
    }

    // 로그아웃
    public void logout() {
        this.refreshToken = null;
    }

    // 탈퇴
    public void revoke() {
        this.revokedAt = LocalDateTime.now();
    }

    // 비밀번호 변경
    public void passwordReset(String newPassword) {
        this.password = newPassword;
    }

    public void photosLimitPlus() {
        this.photosLimit++;
        if (this.photosLimit > 20)
            this.photosLimit = 20;
    }

    public void photosLimitMinus() {
        this.photosLimit--;
        if (this.photosLimit < 0)
            this.photosLimit = 0;
    }
}
