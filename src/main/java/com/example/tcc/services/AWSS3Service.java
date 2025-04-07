package com.example.tcc.services;

import com.example.tcc.Representation.BucketObjectRepresentation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.*;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class AWSS3Service {

    private final S3Client s3Client;
    private final String bucketName;

    public List<Bucket> listBuckets() {
        ListBucketsResponse response = s3Client.listBuckets();
        return response.buckets();
    }

    public void putObject(BucketObjectRepresentation representation) throws IOException {
        String objectName = representation.getObjectName();
        String objectValue = representation.getText();

        File file = new File("." + File.separator + objectName);
        try (FileWriter fileWriter = new FileWriter(file, false);
             PrintWriter printWriter = new PrintWriter(fileWriter)) {
            printWriter.println(objectValue);
        }

        try (InputStream inputStream = new FileInputStream(file)) {
            s3Client.putObject(
                PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(objectName)
                    .build(),
                RequestBody.fromInputStream(inputStream, file.length())
            );
        } catch (Exception e) {
            log.error("Some error has occurred.", e);
            throw e;
        }
    }

    public void putImage(String objectName, MultipartFile multipartFile) throws IOException {
        try (InputStream inputStream = multipartFile.getInputStream()) {
            s3Client.putObject(
                PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(objectName)
                    .contentType("image/jpeg")
                    .build(),
                RequestBody.fromInputStream(inputStream, multipartFile.getSize())
            );
        } catch (Exception e) {
            log.error("An error occurred while uploading the file.", e);
            throw e;
        }
    }

    public void putPDF(String objectName, ByteArrayOutputStream outputStream) {
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray())) {
            s3Client.putObject(
                PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(objectName)
                    .contentType("application/pdf")
                    .build(),
                RequestBody.fromInputStream(inputStream, outputStream.size())
            );
        } catch (Exception e) {
            log.error("Error while creating or uploading PDF to S3", e);
        }
    }

    public List<S3Object> listObjects() {
        ListObjectsV2Response response = s3Client.listObjectsV2(
            ListObjectsV2Request.builder()
                .bucket(bucketName)
                .build()
        );
        return response.contents();
    }

    public void downloadObject(String objectName) {
        GetObjectRequest request = GetObjectRequest.builder()
            .bucket(bucketName)
            .key(objectName)
            .build();

        try (ResponseInputStream<GetObjectResponse> s3Object = s3Client.getObject(request)) {
            FileUtils.copyInputStreamToFile(s3Object, new File("." + File.separator + objectName));
        } catch (IOException e) {
            log.error("Erro ao baixar objeto do S3", e);
        }
    }

    public byte[] downloadImage(String objectName) {
        GetObjectRequest request = GetObjectRequest.builder()
            .bucket(bucketName)
            .key(objectName)
            .build();

        try (ResponseInputStream<GetObjectResponse> s3Object = s3Client.getObject(request);
             ByteArrayOutputStream output = new ByteArrayOutputStream()) {

            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = s3Object.read(buffer)) != -1) {
                output.write(buffer, 0, bytesRead);
            }

            return output.toByteArray();
        } catch (IOException e) {
            log.error("Erro ao baixar imagem do S3", e);
            return null;
        }
    }

    public void deleteObject(String objectName) {
        s3Client.deleteObject(DeleteObjectRequest.builder()
            .bucket(bucketName)
            .key(objectName)
            .build()
        );
    }

    public void deleteMultipleObjects(List<String> objects) {
        List<ObjectIdentifier> toDelete = objects.stream()
            .map(key -> ObjectIdentifier.builder().key(key).build())
            .collect(Collectors.toList());

        s3Client.deleteObjects(DeleteObjectsRequest.builder()
            .bucket(bucketName)
            .delete(Delete.builder().objects(toDelete).build())
            .build()
        );
    }

    public void moveObject(String bucketSourceName, String objectName, String bucketTargetName) {
        s3Client.copyObject(CopyObjectRequest.builder()
            .sourceBucket(bucketSourceName)
            .sourceKey(objectName)
            .destinationBucket(bucketTargetName)
            .destinationKey(objectName)
            .build()
        );
    }
}
