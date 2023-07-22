package com.ddd.chulsi.infrastructure.aws;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
@AllArgsConstructor
public class FileInfoDTO {
    private final String uploadPath;
    private final String uploadFileName;
    private final String originFileName;
    private final String contentType;
    private final String ext;
    private final long size;
}
