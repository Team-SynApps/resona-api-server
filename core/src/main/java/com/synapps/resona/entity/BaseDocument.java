package com.synapps.resona.entity;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.redis.core.index.Indexed;

@Getter
@SuperBuilder
public abstract class BaseDocument {

  @Indexed
  @CreatedDate
  private LocalDateTime createdAt;

  @LastModifiedDate
  private LocalDateTime modifiedAt;

  @Field("is_deleted")
  private boolean deleted = false;

  private LocalDateTime deletedAt;

  protected BaseDocument() {}

  public void softDelete() {
    this.deleted = true;
    this.deletedAt = LocalDateTime.now();
  }
}
