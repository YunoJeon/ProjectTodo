package com.todo.image.controller;

import com.todo.image.service.ImageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/images")
@RequiredArgsConstructor
@Tag(name = "Image API", description = "프로필 이미지 등록 API")
public class ImageController {

  private final ImageService imageService;

  @PostMapping("/profile/upload")
  @Operation(summary = "프로필 이미지 등록 API", description = "회원가입 시 이미지를 등록할 수 있습니다. 현재 로컬 스토리지에 저장이 됩니다.")
  public ResponseEntity<Map<String, String>> uploadImage(@RequestParam("image") MultipartFile image) {

    return ResponseEntity.ok(Map.of("imageUrl", imageService.uploadImage(image)));
  }
}