package com.bcb.util;

import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.Base64;

/**
 * Created by kx on 2018/1/26.
 */
public class FileUtil {
    public static File changeFile(MultipartFile file) {
        File f = null;
        try {
            f= File.createTempFile("file", file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".")));
            file.transferTo(f);
            f.deleteOnExit();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return f;
    }
    /*public static void main(String[] args) throws Exception {
        OkHttpClient client = OkHttpClientUtil.instance();
        String serviceUrl="http://148.66.11.205:8000";
        String token = SeafileUtil.obtainAuthToken(serviceUrl,client,"1822435212@qq.com","123456");
        String base64 = fileToBase64(new File("C:\\Users\\kx\\Desktop\\IMG_2393.JPG"));
        String uploadLink = SeafileUtil.getUploadLink(serviceUrl,client,token,"2c29418b-ccc6-4050-a8e2-fd3d0c81af61","/2018");
        File tmp = File.createTempFile("file", ".JPG");
        System.out.println("创建临时文件。。。");
        File file = base64ToFile(base64,tmp);
        System.out.println("文件初始化完成");
        List<UploadFileRes> list = SeafileUtil.uploadFile(client,token,uploadLink,"/2018","/2018",file);

        System.out.println(JSONArray.toJSONString(list));
    }*/

    public static String fileToBase64(File file) {
        String base64 = null;
        InputStream in = null;
        try {
            in = new FileInputStream(file);
            byte[] bytes = new byte[in.available()];
            int length = in.read(bytes);
            base64=Base64.getEncoder().encodeToString(bytes);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return base64;
    }
    public static File base64ToFile(String base64,File file) {
        FileOutputStream out = null;
        try {
            if (!file.exists())
                file.createNewFile();
            byte[] bytes = Base64.getDecoder().decode(base64);// 将字符串转换为byte数组
            ByteArrayInputStream in = new ByteArrayInputStream(bytes);
            byte[] buffer = new byte[1024];
            out = new FileOutputStream(file);
            int bytesum = 0;
            int byteread = 0;
            while ((byteread = in.read(buffer)) != -1) {
                bytesum += byteread;
                out.write(buffer, 0, byteread); // 文件写操作
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } finally {
            try {
                if (out!= null) {
                    out.close();
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return file;
    }
}
