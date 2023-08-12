package com.ddd.chulsi.domainCore.model.feed;

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
@Table(name = "feed")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@DynamicInsert
@DynamicUpdate
public class Feed extends DateColumn {

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(name = "feedId", columnDefinition = "BINARY(16)")
    private UUID feedId;

    @Column(name = "usersId", columnDefinition = "BINARY(16)")
    private UUID usersId;

    @Enumerated(EnumType.STRING)
    @Column(name = "tag", nullable = false, length = 10)
    private DefinedCode tag;

    @Column(name = "time", nullable = false, columnDefinition = "DATETIME")
    private LocalDateTime time;

    @Column(name = "memo")
    private String memo;

    @Enumerated(EnumType.STRING)
    @Column(name = "icon", nullable = false, length = 10)
    private DefinedCode icon;

    @Builder.Default
    @Column(name = "likes", nullable = false)
    private long likes = 0;

    @Builder.Default
    @Column(name = "views", nullable = false)
    private long views = 0;

    @Column(name = "isMain", columnDefinition = "BIT(1) DEFAULT 0", nullable = false)
    private boolean isMain;

    public void infoUpdate(FeedCommand.InfoUpdateCommand infoUpdateCommand) {
        this.tag = infoUpdateCommand.tag();
        this.time = infoUpdateCommand.time();
        if (StringUtils.isNotBlank(infoUpdateCommand.memo())) this.memo = infoUpdateCommand.memo();
        this.icon = infoUpdateCommand.icon();
    }

    public void updateIsMainTrue() {
        this.isMain = true;
    }

    public void updateIsMainFalse() {
        this.isMain = false;
    }
}
