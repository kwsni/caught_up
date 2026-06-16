package com.kwsni.caught_up.social.controller.dto;

import org.springframework.web.multipart.MultipartFile;

public record AvatarDto(
    String imageUrl,
    MultipartFile file
) {
    
}
