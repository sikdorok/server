package com.ddd.chulsi.domainCore.model.policyItem;

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
@Table(name = "users")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@DynamicInsert
@DynamicUpdate
public class PolicyItem {

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(name = "policyItemId", columnDefinition = "BINARY(16)")
    private UUID policyItemId;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 10)
    private DefinedCode type;

    @Column(name = "subject", length = 30)
    private String subject;

    @Column(name = "message", nullable = false)
    private String message;

    @Builder.Default
    @Column(name = "sort", nullable = false)
    private int sort = 1;

}
