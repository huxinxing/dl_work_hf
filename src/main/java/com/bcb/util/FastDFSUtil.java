package com.bcb.util;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Iterator;
import java.util.Map;

import com.bcb.bean.SystemProperties;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.csource.common.NameValuePair;
import org.csource.fastdfs.ClientGlobal;
import org.csource.fastdfs.StorageClient1;
import org.csource.fastdfs.StorageServer;
import org.csource.fastdfs.TrackerClient;
import org.csource.fastdfs.TrackerGroup;
import org.csource.fastdfs.TrackerServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class FastDFSUtil {

	@Autowired
	SystemProperties systemProperties;

	private static StorageClient1 storageClient1 = null;

	// 初始化FastDFS Client
	public void init (){
		try {
			 // 连接超时的时限,单位为秒
	        ClientGlobal.setG_connect_timeout(systemProperties.getFastdfsG_connect_timeout());
	        // 网络超时的时限，单位为秒
	        ClientGlobal.setG_network_timeout(systemProperties.getFastdfsG_connect_timeout());
	        ClientGlobal.setG_anti_steal_token(false);
	        // 字符集
	        ClientGlobal.setG_charset("UTF-8");
	        ClientGlobal.setG_secret_key(null);
	        // HTTP访问服务的端口号
	        ClientGlobal.setG_tracker_http_port(systemProperties.getFastdfsG_tracker_http_port());
	        // Tracker服务器列表
	        InetSocketAddress[] tracker_servers = new InetSocketAddress[1];
	        tracker_servers[0] = new InetSocketAddress(systemProperties.getFastdfsIP(), systemProperties.getFastdfsPort());
	        ClientGlobal.setG_tracker_group(new TrackerGroup(tracker_servers));
			
			TrackerClient trackerClient = new TrackerClient(ClientGlobal.g_tracker_group);
			TrackerServer trackerServer = trackerClient.getConnection();
			if (trackerServer == null) {
				throw new IllegalStateException("getConnection return null");
			}
			StorageServer storageServer = trackerClient.getStoreStorage(trackerServer);
			if (storageServer == null) {
				throw new IllegalStateException("getStoreStorage return null");
			}
			storageClient1 = new StorageClient1(trackerServer, storageServer);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 上传文件
	 * 
	 * @param file
	 *            文件对象
	 * @param fileName
	 *            文件名
	 * @return
	 */
	public  String uploadFile(MultipartFile file, String fileName) {
		return uploadFile(file, fileName, null);
	}

	/**
	 * 上传文件
	 * 
	 * @param file
	 *            文件对象
	 * @param fileName
	 *            文件名
	 * @param metaList
	 *            文件元数据
	 * @return
	 */
	public  String uploadFile(MultipartFile file, String fileName, Map<String, String> metaList) {

		init();

		try {
			byte[] buff = file.getBytes();
			NameValuePair[] nameValuePairs = null;
			if (metaList != null) {
				nameValuePairs = new NameValuePair[metaList.size()];
				int index = 0;
				for (Iterator<Map.Entry<String, String>> iterator = metaList.entrySet().iterator(); iterator
						.hasNext();) {
					Map.Entry<String, String> entry = iterator.next();
					String name = entry.getKey();
					String value = entry.getValue();
					nameValuePairs[index++] = new NameValuePair(name, value);
				}
			}
			return storageClient1.upload_file1(buff, FilenameUtils.getExtension(fileName), nameValuePairs);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private byte[] toByteArray(File f) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream((int) f.length());  
        BufferedInputStream in = null;  
        try {  
            in = new BufferedInputStream(new FileInputStream(f));  
            int buf_size = systemProperties.getFastdfsSize();
            byte[] buffer = new byte[buf_size];  
            int len = 0;  
            while (-1 != (len = in.read(buffer, 0, buf_size))) {  
                bos.write(buffer, 0, len);  
            }  
            return bos.toByteArray();  
        } catch (IOException e) {  
            e.printStackTrace();  
            throw e;  
        } finally {  
            try {  
                in.close();  
            } catch (IOException e) {  
                e.printStackTrace();  
            }  
            bos.close();  
        }  
    } 

	/**
	 * 下载文件
	 * 
	 * @param fileId
	 *            文件ID（上传文件成功后返回的ID）
	 * @param outFile
	 *            文件下载保存位置
	 * @return
	 */
	public int downloadFile(String fileId, File outFile) {
		FileOutputStream fos = null;
		try {
			byte[] content = storageClient1.download_file1(fileId);
			fos = new FileOutputStream(outFile);
			IOUtils.copy(new ByteArrayInputStream(content), fos);
			return 0;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return -1;
	}
	
//	public static void main(String[]args){
//		File file = new File("E:\\SZG\\fastdfs-client-java\\target\\fastdfs-client-java-1.27-SNAPSHOT.jar");
//
//        Map<String,String> metaList = new HashMap<String, String>();
//        metaList.put("author","dana");
//        metaList.put("date","20180703");
//        String fid = uploadFile(file,file.getName(),metaList);
//        System.out.println("upload local file " + file.getPath() + " ok, fileid=" + fid);
//
//        int r = downloadFile(fid, new File("E:\\SZG\\fastdfs-client-java\\target\\aaa.jar"));
//        System.out.println(r == 0 ? "下载成功" : "下载失败");
//	}
}
