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

    @Column(name = "targetId", nullable = false, columnDefinition = "BINARY(16)")
    private UUID targetId;

    @Column(name = "uploadPath", nullable = false)
    private String uploadPath;

    @Column(name = "uploadFileName", nullable = false)
    private String uploadFileName;

    @Column(name = "originFileName", nullable = false)
    private String originFileName;

    @Column(name = "contentType", length = 200, nullable = false)
    private String contentType;

    @Column(name = "ext", length = 10, nullable = false)
    private String ext;

    @Column(name = "size", nullable = false)
    private long size;

}
