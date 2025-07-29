package com.project.shopapp.responses.Object;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.*;import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@MappedSuperclass
public class BaseResponseEntity {
        @Column(name = "create_at")
        private LocalDateTime createAt;
        @Column(name = "update_at")
        private LocalDateTime updateAt;
}
