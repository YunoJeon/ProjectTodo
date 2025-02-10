package com.todo.image.controller;

import com.todo.image.service.ImageService;
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
public class ImageController {

  private final ImageService imageService;

  @PostMapping("/profile/upload")
  public ResponseEntity<Map<String, String>> uploadImage(@RequestParam("image") MultipartFile image) {

    return ResponseEntity.ok(Map.of("imageUrl", imageService.uploadImage(image)));
  }
}