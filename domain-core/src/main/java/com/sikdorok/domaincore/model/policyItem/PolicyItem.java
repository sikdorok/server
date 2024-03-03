package com.sikdorok.domaincore.model.policyItem;

import com.sikdorok.domaincore.model.shared.DefinedCode;
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
@Table(name = "policyItem")
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

    @Column(name = "data", length = 300)
    private String data;

    @Builder.Default
    @Column(name = "sort", nullable = false)
    private int sort = 1;

    @Column(name = "onOff", columnDefinition = "BIT(1) DEFAULT 0", nullable = false)
    private boolean onOff;

}
