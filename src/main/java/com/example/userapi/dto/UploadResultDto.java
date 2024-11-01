package com.example.userapi.dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class UploadResultDto {
    private int success;
    private int duplicate;
}
