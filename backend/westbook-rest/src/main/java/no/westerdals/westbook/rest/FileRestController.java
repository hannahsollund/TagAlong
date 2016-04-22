package no.westerdals.westbook.rest;

import no.westerdals.westbook.ImageType;
import no.westerdals.westbook.model.UserCredentials;
import no.westerdals.westbook.uploads.DownloadRequest;
import no.westerdals.westbook.MessageConstant;
import no.westerdals.westbook.model.FileMeta;
import no.westerdals.westbook.responses.ResultResponse;
import static no.westerdals.westbook.responses.ResultResponse.*;

import no.westerdals.westbook.uploads.UploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.security.Principal;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/rest/v1/uploads")
public class FileRestController {
    @Autowired
    UploadService uploadService;

    @RequestMapping(value="/meta/{uploadId}")
    public FileMeta getFileMeta(@PathVariable String uploadId)
    {
        return uploadService.getFileMeta(uploadId);
    }

    @RequestMapping(method=RequestMethod.POST)
    public ResultResponse handleUpload(@RequestParam("name") String name, // Ugly as fuck...
                                       @RequestParam("file") MultipartFile file,
                                       @RequestParam(required=false, value="imageType") ImageType imageType,
                                       Principal principal) throws IOException {
        UserCredentials userCredentials = (UserCredentials) ((Authentication)principal).getPrincipal();
        FileMeta fileMeta = uploadService.uploadFile(new FileMeta(userCredentials.getUserId(), name, imageType, new Date()), file.getInputStream());
        return newOkResult(MessageConstant.FILE_UPLOADED, fileMeta);
    }

    @RequestMapping(method=RequestMethod.GET)
    public List<FileMeta> getAllFileMeta()
    {
        return uploadService.getAllFileInfo();
    }

    @RequestMapping(value="/{fileId}", method=RequestMethod.GET)
    public void getUpload(@PathVariable String fileId, HttpServletResponse response) throws IOException
    {
        DownloadRequest downloadRequest = uploadService.downloadFile(fileId, response.getOutputStream());
        if (downloadRequest == null || downloadRequest.getFileMeta() == null) {
            response.sendError(404);
            return;
        }
        FileMeta fileMeta = downloadRequest.getFileMeta();
        if (fileMeta.getImageType() == null) {
            response.setHeader("Content-Disposition", "attachment;filename=\"" + fileMeta.getName() + "\"");
        } else {
            response.setHeader("Content-Type", "image/jpeg");
            response.setHeader("Content-Disposition", "filename=\"" + fileMeta.getName() + "\"");
        }
        downloadRequest.redirect();
    }
}
