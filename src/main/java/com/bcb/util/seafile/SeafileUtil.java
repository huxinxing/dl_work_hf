package com.bcb.util.seafile;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;
import com.bcb.util.seafile.jsonObject.DirectoryEntry;
import com.bcb.util.seafile.jsonObject.Library;
import com.bcb.util.seafile.jsonObject.UploadFileRes;
import okhttp3.*;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Created by kx on 2018/1/26.
 */
public class SeafileUtil {
    public static String returnDownLoadLink(OkHttpClient client,String serviceUrl,
                                            String userName,String password,
                                            String repo_id,String path) {
        return getFileDownloadLink(serviceUrl,client,obtainAuthToken(serviceUrl,client,userName,password),repo_id,path,true);
    }

    /**
     * 获取token
     * @param client
     * @param username
     * @param password
     * @return
     */
    public static String obtainAuthToken(String serviceUrl,OkHttpClient client, String username, String password) {
        RequestBody body=new FormBody.Builder()
                .add("username",username)
                .add("password",password)
                .build();
        Request request=new Request.Builder()
                .url(serviceUrl+"/api2/auth-token/")
                .header("Content-Type","application/x-www-form-urlencoded")
                .post(body)
                .build();
        try (Response response=client.newCall(request).execute()){
            JSONObject jObj =parseJson(response.body().string());
            return jObj.getString("token");
        }catch (IOException e){
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 创建目录
     * @param client
     * @param token
     * @param repo_id
     * @param dirs
     * @return
     */
    public  static String createDirs(String serviceUrl,OkHttpClient client,String token,String repo_id,String... dirs) {
        String path = "";
        for(String _path:dirs) {
           path=path+"/"+_path;
           if(listDirEntriesByP(serviceUrl,client,token,repo_id,path)==null) {
               createNewDir(serviceUrl,client,token,repo_id,path);
           }
        }
        return path;
    }

    /**
     * 获取所有资料库
     * @param client
     * @param token
     * @return
     */
    public static List<Library> listLibraries(String serviceUrl,OkHttpClient client, String token) {
        Request request=new Request.Builder()
                .header("Content-Type","application/x-www-form-urlencoded")
                .header("Authorization","Token "+token)
                .header("Accept","application/json")
                .header("indent","4")
                .get()
                .url(serviceUrl+"/api2/repos")
                .build();
        try (Response response=client.newCall(request).execute()){
            return JSON.parseArray(response.body().string(), Library.class);
        }catch (IOException e){
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 创建一个新的资料库
     * @param client
     * @param token
     * @param libName
     * @param desc
     * @param password
     * @return
     */
    public static JSONObject createNewLibrary(String serviceUrl,OkHttpClient client, String token, String libName, String desc, String password) {
        FormBody.Builder builder=new FormBody.Builder()
                .add("name",libName);
        if(desc != null){
            builder.add("desc",desc);
        }
        if(password != null){
            builder.add("password",password);
        }
        RequestBody requestBody=builder.build();
        Request request=new Request.Builder()
                .header("Content-Type","application/x-www-form-urlencoded")
                .header("Authorization","Token "+token)
                .header("Accept","application/json")
                .header("indent","4")
                .post(requestBody)
                .url(serviceUrl+"/api2/repos/")
                .build();
        try (Response response=client.newCall(request).execute()){
            if(response.isSuccessful()){
                return parseJson(response.body().string());
            }else {
                System.out.println(response.code());
                System.out.println(response.body().string());
            }
        }catch (IOException e){
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取下载链接
     * @param client
     * @param token
     * @param repo_id
     * @param p
     * @param reuse
     * @return
     */
    public static String getFileDownloadLink(String serviceUrl,OkHttpClient client, String token, String repo_id, String p,boolean reuse) {
        String reuse_Temp=reuse?"&reuse=1":"";
        Request request=new Request.Builder()
                .header("Content-Type","application/x-www-form-urlencoded")
                .header("Authorization","Token "+token)
                .header("Accept","application/json")
                .header("indent","4")
                .get()
                .url(serviceUrl+"/api2/repos/"+repo_id+"/file/?p="+p+reuse_Temp)
                .build();
        try (Response response=client.newCall(request).execute()){
            if(response.isSuccessful()){
                String url =  response.body().string();
                return url.substring(1,url.length()-1);
            }else {
                System.out.println(response.code());
                System.out.println(response.body().string());
            }
        }catch (IOException e){
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 删除文件
     * @param client
     * @param token
     * @param repo_id
     * @param p
     * @return
     */
    public static boolean deleteFile(String serviceUrl,OkHttpClient client, String token, String repo_id, String p) {
        Request request=new Request.Builder()
                .header("Content-Type","application/x-www-form-urlencoded")
                .header("Authorization","Token "+token)
                .header("Accept","application/json")
                .header("indent","4")
                .url(serviceUrl+"/api2/repos/"+repo_id+"/file/?p="+p)
                .delete()
                .build();
        try (Response response=client.newCall(request).execute()){
            if (response.isSuccessful()){
                System.out.println(response.code());
                return true;
            }else {
                System.out.println(response.code());
                System.out.println(response.body().string());
                return false;
            }

        }catch (IOException e){
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 获取上传链接
     * @param client
     * @param token
     * @param repo_id
     * @param p
     * @return
     */
    public static String getUploadLink(String serviceUrl,OkHttpClient client, String token, String repo_id,String p) {
        Request request=new Request.Builder()
                .header("Content-Type","application/x-www-form-urlencoded")
                .header("Authorization","Token "+token)
                .url(serviceUrl+"/api2/repos/"+repo_id+"/upload-link/?p="+p)
                .get()
                .build();
        try (Response response=client.newCall(request).execute()){
            if (response.isSuccessful()){
                return response.body().string().replaceAll("\"","");
            }else {
                System.out.println(response.code());
                System.out.println(response.body().string());
            }

        }catch (IOException e){
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 上传文件
     * @param client
     * @param token
     * @param uploadLink
     * @param parent_dir
     * @param relative_path
     * @param files
     * @return
     */
    public static List<UploadFileRes> uploadFile(OkHttpClient client, String token,String uploadLink,String parent_dir, String relative_path, File... files) {
        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM);

        for (File file:files){
            builder.addFormDataPart("file",file.getName(),RequestBody.create(MediaType.parse("application/octet-stream"), file));
        }

        builder.addFormDataPart("parent_dir",parent_dir);
        builder.addFormDataPart("relative_path",relative_path);
        RequestBody body=builder.build();

        Request request=new Request.Builder()
                .header("Content-Type","application/x-www-form-urlencoded")
                .header("Authorization","Token "+token)
                .post(body)
                .url(uploadLink+"?ret-json=1")
                .build();
        try (Response response=client.newCall(request).execute()){
            if (response.isSuccessful()){
                return JSONObject.parseArray(response.body().string(),UploadFileRes.class);
            }else {
                System.out.println(response.code());
                System.out.println(response.body().string());
            }
        }catch (IOException e){
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 查询目录下的文件
     * @param client
     * @param token
     * @param repo_id
     * @param p
     * @return
     */
    public static List<DirectoryEntry> listDirEntriesByP(String serviceUrl,OkHttpClient client, String token, String repo_id, String p) {
        Request request=new Request.Builder()
                .header("Content-Type","application/x-www-form-urlencoded")
                .header("Authorization","Token "+token)
                .url(serviceUrl+"/api2/repos/"+repo_id+"/dir/?p="+p)
                .get()
                .build();
        try (Response response=client.newCall(request).execute()){
            if (response.isSuccessful()){
                return JSONObject.parseArray(response.body().string(),DirectoryEntry.class);
            }else {
                System.out.println(response.body().string());
            }
        }catch (IOException e){
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 创建文件夹
     * @param client
     * @param token
     * @param repo_id
     * @param p
     * @return
     */
    public static boolean createNewDir(String serviceUrl,OkHttpClient client, String token, String repo_id, String p) {
        RequestBody body=new FormBody.Builder()
                .add("operation","mkdir")
                .build();
        Request request=new Request.Builder()
                .header("Content-Type","application/x-www-form-urlencoded")
                .header("Authorization","Token "+token)
                .header("Accept","application/json")
                .header("indent","4")
                .url(serviceUrl+"/api2/repos/"+repo_id+"/dir/?p="+p)
                .post(body)
                .build();
        try (Response response=client.newCall(request).execute()){
            if (response.isSuccessful()){
                return true;
            }else {
                System.out.println(response.code());
                System.out.println(response.body().string());
            }
        }catch (IOException e){
            e.printStackTrace();
        }
        return false;
    }


    /**
     * 解析json
     * @param jsonStr
     * @return
     */
    private static JSONObject parseJson(String jsonStr){
        return JSON.parseObject(jsonStr, Feature.AutoCloseSource);
    }
}
