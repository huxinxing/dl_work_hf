package com.bcb.controller;

import com.bcb.bean.SystemProperties;
import com.bcb.util.HttpConvertUtil;
import com.bcb.util.seafile.OkHttpClientUtil;
import com.bcb.util.seafile.SeafileUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;

/**
 * Created by kx on 2018/1/26.
 */
@Slf4j
@RestController
@RequestMapping(value = "/file")
public class FileController {
    @Autowired
    private SystemProperties systemProperties;
    @RequestMapping(value = "/steam",method = RequestMethod.GET)
    public void fileStream(HttpServletRequest request, HttpServletResponse response) throws Exception{
        try{
            String path = request.getParameter("path");
            String url = SeafileUtil.returnDownLoadLink(OkHttpClientUtil.instance(),systemProperties.getSeafileUrl(),
                    systemProperties.getSeafileUserName(),
                    systemProperties.getSeafilePassword(),systemProperties.getSeafileRepoId(),path);
            url = url.replaceAll(systemProperties.getSeaHttpOnlineUrl(),systemProperties.getSeaHttpInnerUrl());
            OutputStream out = response.getOutputStream();
            out.write(HttpConvertUtil.bytesFromURL(url));
            out.close();
        }catch (Exception e){
            log.error("文件写出失败",e);
        }

    }

    @RequestMapping(value = "/download",method = RequestMethod.GET)
    public void fileDownload(HttpServletRequest request,HttpServletResponse response) throws Exception {
        OutputStream os = response.getOutputStream();
        String filePath = request.getParameter("filePath");
        String url = SeafileUtil.returnDownLoadLink(OkHttpClientUtil.instance(),systemProperties.getSeafileUrl(),
                systemProperties.getSeafileUserName(),
                systemProperties.getSeafilePassword(),systemProperties.getSeafileRepoId(),filePath);
        url = url.replaceAll(systemProperties.getSeaHttpOnlineUrl(),systemProperties.getSeaHttpInnerUrl());
        String fileName = request.getParameter("fileName");
        try {
            response.reset();
            response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
            response.setContentType("application/octet-stream; charset=utf-8");
            os.write(HttpConvertUtil.bytesFromURL(url));
            os.flush();
        } finally {
            if (os != null) {
                os.close();
            }
        }
    }
}
