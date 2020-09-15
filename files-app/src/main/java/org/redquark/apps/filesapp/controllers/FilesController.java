package org.redquark.apps.filesapp.controllers;

import org.redquark.apps.filesapp.services.AmazonClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/files")
public class FilesController {

    private final AmazonClientService amazonClientService;

    @Autowired
    public FilesController(AmazonClientService amazonClientService) {
        this.amazonClientService = amazonClientService;
    }

    @PostMapping("/upload")
    public String uploadFile(@RequestPart(value = "file") MultipartFile multipartFile) {
        return this.amazonClientService.upload(multipartFile);
    }

    @DeleteMapping("/delete")
    public String deleteFile(@RequestPart(value = "url") String url) {
        return this.amazonClientService.delete(url);
    }
}
