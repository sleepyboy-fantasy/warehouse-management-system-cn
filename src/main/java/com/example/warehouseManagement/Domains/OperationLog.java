package com.example.warehouseManagement.Domains;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "operation_log")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OperationLog {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false, length = 64)
    private String username;

    @Column(nullable = false, length = 32)
    private String action;

    @Column(nullable = false, length = 64)
    private String target;

    @Column(length = 500)
    private String details;

    @Column(nullable = false)
    @Builder.Default
    private Instant createdAt = Instant.now();
}