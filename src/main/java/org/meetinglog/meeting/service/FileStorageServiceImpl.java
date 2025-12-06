package org.meetinglog.meeting.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.meetinglog.jpa.entity.FileMst;
import org.meetinglog.jpa.repository.FileMstRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileStorageServiceImpl implements FileStorageService {

  private final FileMstRepository fileMstRepository;

  private static final String UPLOAD_DIR = System.getProperty("user.dir") + "/uploads";

  @Override
  public FileMst saveFile(MultipartFile file) {
    try {
      File dir = new File(UPLOAD_DIR);
      if (!dir.exists()) {
        dir.mkdirs();
      }

      String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
      File savedFile = new File(dir, fileName);
      file.transferTo(savedFile);

      FileMst fileMst = FileMst.builder()
          .filePath(savedFile.getAbsolutePath())
          .fileExtension(getExt(file.getOriginalFilename()))
          .originFileNm(file.getOriginalFilename())
          .build();

      return fileMstRepository.save(fileMst);

    } catch (Exception e) {
      throw new RuntimeException("파일 저장 실패", e);
    }
  }

  @Override
  public byte[] loadFileBytes(String path) {
    try {
      return Files.readAllBytes(Path.of(path));
    } catch (Exception e) {
      throw new RuntimeException("파일 로딩 실패: " + path, e);
    }
  }

  private String getExt(String fileName) {
    return fileName.substring(fileName.lastIndexOf(".") + 1);
  }
}
