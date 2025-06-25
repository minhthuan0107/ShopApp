package com.project.shopapp.dtos.customer.user;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;
import java.util.Date;

@Setter
@Getter
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateProfileDto {
    @JsonProperty("full_name")
    @NotBlank(message = "Họ và tên không được để trống")
    private String fullName;

    private String address;
    @JsonProperty("date_of_birth")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @Past(message = "Ngày tháng năm sinh không hợp lệ")
    private LocalDate dateOfBirth;
}
