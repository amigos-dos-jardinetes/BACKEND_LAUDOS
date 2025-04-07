package com.example.tcc.controllers;

import com.example.tcc.jwt.CustomJwt;
import com.example.tcc.requests.AssetConfirmationRequestDto;
import com.example.tcc.responses.AssetConfirmationResponseDto;
import com.example.tcc.responses.CreateAssetResponseDto;
import com.example.tcc.services.*;
import com.example.tcc.util.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.multipart.MultipartFile;
import lombok.AllArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/asset")
@AllArgsConstructor
public class AssetController {
    private final AssetCreationService assetCreationService;
    private final AssetConfirmationService assetConfirmationService;
    private final AssetDeletionService assetDeletionService;
    private final AssetImageAdditionService assetImageAdditionService;
    private final AssetImageDeletionService assetImageDeletionService;
    private final PermissionCheckService permissionCheckService;
    private final AssetRecognitionService assetRecognitionService;

    @PostMapping
    public ResponseEntity<?> create(@RequestParam Long fileId, @RequestParam MultipartFile image) {
        var jwt = (CustomJwt) SecurityContextHolder.getContext().getAuthentication();
        if(!permissionCheckService.checkPermissionForFile(jwt.getEmail(), fileId))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

        try {
            CreateAssetResponseDto response = assetCreationService.createAndRecognize(fileId, image);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse(e.getMessage()));
        }
    }

    @PostMapping("/confirm/{assetId}")
    public ResponseEntity<?> confirm(@PathVariable Long assetId, @RequestBody AssetConfirmationRequestDto requestDto) {
        var jwt = (CustomJwt) SecurityContextHolder.getContext().getAuthentication();
        if(!permissionCheckService.checkPermissionForAsset(jwt.getEmail(), assetId))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

        try {
            AssetConfirmationResponseDto response = assetConfirmationService.confirm(requestDto.getAssetNumber(), assetId);
            return ResponseEntity.ok(response);
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.FORBIDDEN) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            } else if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse("Não foi possível encontrar bem com esse número de patrimônio"));
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse(e.getMessage()));
        }
    }

    @GetMapping("/recognize/{assetId}")
    public ResponseEntity<?> recognizeAssetNumber(@PathVariable Long assetId) {
        var jwt = (CustomJwt) SecurityContextHolder.getContext().getAuthentication();
        if(!permissionCheckService.checkPermissionForAsset(jwt.getEmail(), assetId))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        try {
            CreateAssetResponseDto response = assetRecognitionService.recognize(assetId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse(e.getMessage()));
        }
    }

    @PostMapping("/add-image")
    public ResponseEntity<Map<String, String>> add(@RequestParam Long assetId, @RequestParam MultipartFile image) {
        var jwt = (CustomJwt) SecurityContextHolder.getContext().getAuthentication();
        if(!permissionCheckService.checkPermissionForAsset(jwt.getEmail(), assetId))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

        String filepath = assetImageAdditionService.add(assetId, image);
        Map<String, String> response = new HashMap<>();
        response.put("path", filepath);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete-image/{filename}")
    public ResponseEntity<Void> deleteImage(@PathVariable String filename) {
        var jwt = (CustomJwt) SecurityContextHolder.getContext().getAuthentication();
        if(!permissionCheckService.checkPermissionForImageFilename(jwt.getEmail(), filename))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

        assetImageDeletionService.delete(filename);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{assetId}")
    public ResponseEntity<Void> deleteAsset(@PathVariable Long assetId) {
        var jwt = (CustomJwt) SecurityContextHolder.getContext().getAuthentication();
        if(!permissionCheckService.checkPermissionForAsset(jwt.getEmail(), assetId))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

        assetDeletionService.delete(assetId);
        return ResponseEntity.noContent().build();
    }
}