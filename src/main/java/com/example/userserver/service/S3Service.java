package com.example.userserver.service;

import static com.example.userserver.common.response.BaseResponseStatus.FAIL_SAVE_FILE;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.example.userserver.common.exceptions.BaseException;
import java.io.IOException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@Service
public class S3Service {

    private final AmazonS3 amazonS3;
    @Value("${cloud.aws.s3.bucket}")
    private String BUCKET_NAME;

    public String uploadImage(MultipartFile file){ // todo -> 예외터졌을때 응답 코드로 보낼수 있게 test후 변경
        String fileName = createFileName(file.getOriginalFilename());// multipart 객체에서 파일 명 추출

        ObjectMetadata objectMetadata = new ObjectMetadata(); // aws s3에서 제공하는 객체(파일) 을 담는 클래스
        objectMetadata.setContentLength(file.getSize());
        objectMetadata.setContentType(file.getContentType());

        try{
            amazonS3.putObject(new PutObjectRequest(BUCKET_NAME,fileName,file.getInputStream(),objectMetadata)
                    .withCannedAcl(CannedAccessControlList.PublicRead));

        }catch (IOException e){
            throw new BaseException(FAIL_SAVE_FILE);
        }
        return amazonS3.getUrl(BUCKET_NAME,fileName).toString();
    }

    private String createFileName(String fileName){
        return UUID.randomUUID().toString().concat(fileName.substring(fileName.lastIndexOf(".")));

    }

    public void deleteImage(String fileName){
        amazonS3.deleteObject(BUCKET_NAME,fileName);
    }
}
