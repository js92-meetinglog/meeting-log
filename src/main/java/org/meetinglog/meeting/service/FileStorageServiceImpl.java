package org.meetinglog.meeting.service;

import lombok.RequiredArgsConstructor;
import org.meetinglog.jpa.entity.FileMst;
import org.meetinglog.jpa.repository.FileMstRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@Service
@RequiredArgsConstructor
public class FileStorageServiceImpl implements FileStorageService {

    private final FileMstRepository fileMstRepository;

    private final String UPLOAD_DIR = System.getProperty("user.dir") + "/uploads";


    @Override
    public FileMst saveFile(MultipartFile file) {

        try {
            // 1) uploads 폴더 생성
            File dir = new File(UPLOAD_DIR);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            // 2) 저장 파일명
            String savedFileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            File saveFile = new File(dir, savedFileName);

            // 3) 실제 파일 저장
            file.transferTo(saveFile);

            // 4) FileMst 저장
            FileMst fileMst = FileMst.builder()
                    .filePath(saveFile.getAbsolutePath()) // 전체 경로
                    .fileExtension(getExtension(file.getOriginalFilename())) // 확장자
                    .build();

            return fileMstRepository.save(fileMst);

        } catch (IOException e) {
            throw new RuntimeException("파일 저장 실패", e);
        }
    }

    private String getExtension(String originalFilename) {
        return originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
    }
}
