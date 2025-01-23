package org.anonymous.global.entities;

import jakarta.persistence.Entity;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;

@Data
@Entity
@RedisHash(timeToLive = 300) // 5분간 값 유지
public class CodeValue implements Serializable {
    @Id
    private String code;
    private Object value;
}
