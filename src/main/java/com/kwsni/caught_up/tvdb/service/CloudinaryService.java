package com.kwsni.caught_up.tvdb.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.cloudinary.Cloudinary;
import com.cloudinary.Transformation;

@Service
public class CloudinaryService {
    private final Cloudinary cloudinary;

    public CloudinaryService(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    public String fetchPoster(String url, String sizeCode) {
        int width;
        switch(sizeCode) {
            case "SM":
                width = 70;
                break;
            case "MD":
                width = 150;
                break;
            case "LG":
            default:
                width = 230;
                break;

        }
        
        return cloudinary.url().transformation(new Transformation<>()
            .aspectRatio(17, 25)
            .width(width)
            .fetchFormat("auto")
        ).type("fetch").generate(url);
    }

    public String transformAvatar(String publicId, String version, String sizeCode) {
        return cloudinary.url()
            .transformation(
                new Transformation<>()
                    .named("avatar-" + sizeCode)
            ).version(version).generate("avatars/" + publicId);
    }

    public String uploadImage(
        MultipartFile uploadFile,
        String username,
        Map<?, ?> options) {
        try {
            File file = new File(System.getProperty("java.io.tmpdir") + "/" + username);
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(uploadFile.getBytes());
            fos.close();

            var img = cloudinary.uploader().upload(file, options);
            return img.get("version").toString();
        } catch(IOException e) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Failed to upload the file.");
        }
    }
}
