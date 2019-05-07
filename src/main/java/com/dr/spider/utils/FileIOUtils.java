package com.dr.spider.utils;

import com.dr.spider.service.PopjavService;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileIOUtils {

  public final static Logger logger = LoggerFactory.getLogger(FileIOUtils.class);


  /**
   * 下载视频
   * @param urlString 视频下载地址
   * @param fileUrl sn
   * @param endFormat 视频格式 例如 .mp4
   * @param globalPath 根路径
   * @return
   */
  public static String downloadVideo(String urlString, String fileUrl, String endFormat,
      String globalPath) {
    if (urlString == null || "".equals(urlString)) {
      return "";
    }
    int hash = HashUtils.getHashFilePath(fileUrl);
    File fileT = new File(globalPath + File.separator + hash + File.separator + fileUrl);
    OutputStream os = null;
    InputStream is = null;
    if (!fileT.exists()) {
      fileT.mkdirs();
    }
    try {
      // 构造URL
      URL url = new URL(urlString);
      // 打开连接
      HttpURLConnection con = (HttpURLConnection) url.openConnection();
      con.setRequestProperty("User-Agent",
          "Mozilla/5.0 (iPhone; CPU iPhone OS 11_1 like Mac OS X) AppleWebKit/604.3.5 (KHTML, like Gecko) Mobile/15B93");
      con.setConnectTimeout(60000);
      con.setReadTimeout(60000);
      con.connect();
      // 输入流
      is = con.getInputStream();
      // 1K的数据缓冲
      byte[] bs = new byte[1024];
      // 读取到的数据长度
      int len;
      // 输出的文件流
      String fileName = fileUrl + endFormat;
      String filePath = hash + File.separator + fileUrl + File.separator + fileName;
      String realPath = globalPath + File.separator + filePath;
      File file = new File(realPath);
      file.createNewFile();// 创建文件
      os = new FileOutputStream(file);
      // 开始读取
      while ((len = is.read(bs)) != -1) {
        os.write(bs, 0, len);
      }
      String videoPath = File.separator + filePath;
      int fileLength = con.getContentLength();
      logger.warn("下载文件大小------------》》》 流大小 {} 下载文件大小 {}", fileLength, file.length());
      if (file.exists() && fileLength == file.length()) {
        return videoPath;
      }
    } catch (Exception e) {
      e.printStackTrace();
      logger.warn("视频下载失败，url：" + urlString + "------" + e.toString());
    } finally {
      try {
        if (is != null) {
          is.close();
        }
        if (os != null) {
          os.close();
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    return "";
  }

}
