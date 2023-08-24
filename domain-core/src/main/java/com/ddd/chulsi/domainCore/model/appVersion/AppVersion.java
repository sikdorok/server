package com.ddd.chulsi.domainCore.model.appVersion;

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
@Table(name = "appVersion")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@DynamicInsert
@DynamicUpdate
public class AppVersion {

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(name = "appVersionId", columnDefinition = "BINARY(16)")
    private UUID appVersionId;

    @Column(name = "appInfoAppVersion", nullable = false, length = 10)
    private String appInfoAppVersion;

    @Column(name = "major", columnDefinition = "TINYINT DEFAULT 1")
    private int major;

    @Column(name = "minor", columnDefinition = "TINYINT DEFAULT 1")
    private int minor;

    @Column(name = "patch", columnDefinition = "TINYINT DEFAULT 1")
    private int patch;

    @Column(name = "forceUpdateStatus", columnDefinition = "BIT(1) DEFAULT 0", nullable = false)
    private boolean forceUpdateStatus;

}
