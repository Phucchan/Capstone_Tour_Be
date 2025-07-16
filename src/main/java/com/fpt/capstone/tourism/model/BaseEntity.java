package com.fpt.capstone.tourism.model;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.*;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode
@MappedSuperclass
public class BaseEntity {
    @Column(name = "created_at", nullable = false, updatable = false)
    protected LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    protected LocalDateTime updatedAt;

    @Column(name = "is_deleted")
    private Boolean deleted;



    @PrePersist
    protected void onCreate() {
        ZoneId vietnamZone = ZoneId.of("Asia/Ho_Chi_Minh");
        createdAt = LocalDateTime.now(vietnamZone);
        updatedAt = LocalDateTime.now(vietnamZone);
        deleted = false;
        beforePersist();
    }

    @PreUpdate
    protected void onUpdate() {
        ZoneId vietnamZone = ZoneId.of("Asia/Ho_Chi_Minh");
        updatedAt = LocalDateTime.now(vietnamZone);
    }

    public void softDelete() {
        this.deleted = true;
        this.updatedAt = LocalDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh"));
    }

    public void restore() {
        this.deleted = false;
        this.updatedAt = LocalDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh"));
    }


    protected void beforePersist() {}
}
