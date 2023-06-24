package com.ddd.chulsi.domainCore.model.photos;

import com.ddd.chulsi.domainCore.model.shared.DateColumn;
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
@Table(name = "users")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@DynamicInsert
@DynamicUpdate
public class Photos extends DateColumn {

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(name = "photosId", columnDefinition = "BINARY(16)")
    private UUID photosId;

    @Column(name = "token", nullable = false, columnDefinition = "BINARY(16)")
    private UUID token;

    @Column(name = "feedId", nullable = false, columnDefinition = "BINARY(16)")
    private UUID feedId;

    @Column(name = "originFileName", nullable = false)
    private String originFileName;

    @Column(name = "path", nullable = false)
    private String path;

    @Column(name = "fullPath", nullable = false)
    private String fullPath;

    @Column(name = "ext", nullable = false)
    private String ext;

    @Builder.Default
    @Column(name = "size", nullable = false)
    private long size = 0L;

}
