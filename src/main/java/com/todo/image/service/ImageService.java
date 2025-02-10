package com.todo.image.service;

import static com.todo.exception.ErrorCode.INTERNAL_SERVER_ERROR;
import static com.todo.exception.ErrorCode.REQUEST_VALIDATION_FAIL;
import static java.util.Locale.ROOT;

import com.todo.exception.CustomException;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Slf4j
public class ImageService {

  private static final List<String> ALLOWED_FILE_EXTENSIONS = List.of("jpg", "jpeg", "png", "gif");

  private static final long MAX_FILE_SIZE = 5 * 1024 * 1024;

  private static final int MAX = 800;

  public String uploadImage(MultipartFile image) {

    String extension = getExtension(image);

    String folder = "uploads/profile/";
    File dir = new File(folder);
    if (!dir.exists()) {
      boolean created = dir.mkdirs();
      if (!created) {
        throw new CustomException(INTERNAL_SERVER_ERROR, "프로필 이미지 저장 폴더 생성에 실패하였습니다.");
      }
    }

    String filename = UUID.randomUUID() + "." + extension;
    File dest = new File(dir, filename);

    try {
      Thumbnails.of(image.getInputStream())
          .size(MAX, MAX)
          .outputFormat(extension)
          .toFile(dest);
    } catch (IOException e) {
      throw new CustomException(INTERNAL_SERVER_ERROR, "파일 업로드에 실패하였습니다.");
    }

    return "/uploads/profile/" + filename;
  }

  private static String getExtension(MultipartFile image) {

    if (image.isEmpty()) {
      throw new CustomException(REQUEST_VALIDATION_FAIL, "업로드할 이미지 파일이 없습니다.");
    }

    if (image.getSize() > MAX_FILE_SIZE) {
      throw new CustomException(REQUEST_VALIDATION_FAIL, "파일 용량은 최대 5MB 를 초과할 수 없습니다.");
    }

    String originalFilename = image.getOriginalFilename();
    if (originalFilename == null || !originalFilename.contains(".")) {
      throw new CustomException(REQUEST_VALIDATION_FAIL, "파일 확장자를 확인할 수 없습니다.");
    }

    String extension = originalFilename.substring(originalFilename.lastIndexOf(".") + 1)
        .toLowerCase(ROOT);
    if (!ALLOWED_FILE_EXTENSIONS.contains(extension)) {
      throw new CustomException(REQUEST_VALIDATION_FAIL, "허용되지 않은 파일 형식 입니다.");
    }
    return extension;
  }

  public String updateProfileImage(String currentImageUrl, MultipartFile image) {

    if (currentImageUrl != null && !currentImageUrl.isEmpty()) {

      String filePath = currentImageUrl.replaceFirst("^/uploads/", "uploads/");
      File existingFile = new File(filePath);
      if (existingFile.exists() && !existingFile.delete()) {
        log.error("Failed to delete existing file: {}", filePath);
      }
    }
    return uploadImage(image);
  }
}