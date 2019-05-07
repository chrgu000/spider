package com.dr.spider.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import okhttp3.Response;
import org.apache.http.HttpEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileIOUtils {

  public final static Logger logger = LoggerFactory.getLogger(FileIOUtils.class);


  /**
   * 下载视频
   *
   * @param urlString 视频下载地址
   * @param fileUrl sn
   * @param endFormat 视频格式 例如 .mp4
   * @param globalPath 根路径
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


  private static String downloadM3u8(String globalPath, String sn, List<String> tsList) {
    int tsRepetCount = 5;
    // 文件存在，是否重新下载
    boolean fileExistsRepet = true;
    try {
      int hash = HashUtils.getHashFilePath(sn);
      if (tsList != null && tsList.size() > 0) {
        String folderPath = globalPath + File.separator + hash + File.separator + sn;
        File folder = new File(folderPath);
        if (!folder.exists()) {
          folder.mkdirs();
        }

        tsList.parallelStream().forEach(tsUrl -> {
          FileOutputStream fos = null;
          // CloseableHttpResponse res = null;
          Response res = null;
          InputStream inputStream = null;
          HttpEntity entity;
          try {
            if (fileExistsRepet) {
              for (int i = 0; i < tsRepetCount; i++) {
                boolean isSendPotato = false;
                String fileName;
                if (tsUrl.contains("?")) {
                  fileName = tsUrl.substring(tsUrl.lastIndexOf("/") + 1, tsUrl.indexOf("?"));
                } else {
                  fileName = tsUrl.substring(tsUrl.lastIndexOf("/") + 1, tsUrl.length());
                }

                File f = new File(folderPath + File.separator + fileName);
                Integer code = null;
                try {
                  res = new OkHttpUtils(tsUrl).response();
                  code = res.code();
                  long size = res.body().contentLength();
                  inputStream = res.body().byteStream();

                  // 会自动创建文件
                  fos = new FileOutputStream(f);
                  int len;
                  byte[] buf = new byte[1024];
                  while ((len = inputStream.read(buf)) != -1) {
                    // 写入流中
                    fos.write(buf, 0, len);
                  }
                  fos.flush();
                  if (size == -1) {
                    size = f.length();
                  }
                  if (f.exists() && size == f.length()) {
                    break;
                  }
                  if (i == tsRepetCount - 1) {
                    isSendPotato = true;
                  }
                } catch (Exception e) {
                  if (i == tsRepetCount - 1) {
                    isSendPotato = true;
                  }
                  e.printStackTrace();
                  logger.error("下载失败：e {}", e.getMessage());
                } finally {// 关流
                  try {
                    if (inputStream != null) {
                      inputStream.close();
                    }
                    if (res != null) {
                      res.close();
                    }
                    if (fos != null) {
                      fos.close();
                    }
                    if (isSendPotato) {
                      StringBuilder sb = new StringBuilder();
                      sb.append("下载TS文件:" + tsRepetCount + "次全部失败：\n");
                      sb.append("method:dowload4Resolution \n");
                      sb.append("url:" + tsUrl + "\n");
                      sb.append("\ncode:" + code);
                      sb.append("\nfileUrl:" + f);
                    }
                  } catch (Exception e) {
                    e.printStackTrace();
                  }
                }
              }
            }
          } catch (Exception e) {
            logger.error("m3u8请求下载时发生错误：" + sn + "-------" + e.toString());
            e.printStackTrace();
          } finally {// 关流
            try {
              if (inputStream != null) {
                inputStream.close();
              }
              if (fos != null) {
                fos.close();
              }
            } catch (Exception e) {
              e.printStackTrace();
            }
          }
        });

        System.out.println("ts文件下载完毕!");
        String fileName = sn + ".ts";
        String filePath = hash + File.separator + sn + File.separator + fileName;
        boolean b = mergeFiles(orderByName(folderPath), filePath, globalPath);
        if (b) {
          if (tsToMp4(globalPath + File.separator + filePath)) {
            File tsFile = new File(globalPath + File.separator + filePath);
            tsFile.delete();
            String fileMp4Path = filePath.replace(".ts", ".mp4");
            File mp4File = new File(globalPath + File.separator + fileMp4Path);
            if (mp4File.exists()) {
              return File.separator + fileMp4Path;
            }
          }
        }
      }
    } catch (Exception e) {
      logger.error("下载失败：e {} SN {}", e.getMessage(), sn);
      e.printStackTrace();
    }
    return "";
  }

  /**
   * 将视频片段合成为MP4格式
   */
  public static boolean mergeFiles(File[] fpaths, String filePath, String globalPath) {
    String resultPath = globalPath + File.separator + filePath;
    if (fpaths == null || fpaths.length < 1) {
      return false;
    }
    try {
      if (fpaths.length == 1) {
        return fpaths[0].renameTo(new File(resultPath));
      }
      for (int i = 0; i < fpaths.length; i++) {
        if (!fpaths[i].exists() || !fpaths[i].isFile()) {
          return false;
        }
      }
      File resultFile = new File(resultPath);
      FileOutputStream fs = new FileOutputStream(resultFile, true);
      FileChannel resultFileChannel = fs.getChannel();
      FileInputStream tfs;
      for (int i = 0; i < fpaths.length; i++) {
        tfs = new FileInputStream(fpaths[i]);
        FileChannel blk = tfs.getChannel();
        resultFileChannel.transferFrom(blk, resultFileChannel.size(), blk.size());
        tfs.close();
        blk.close();
      }
      resultFileChannel.close();
      fs.close();
      Thread.sleep(2000);
      for (int i = 0; i < fpaths.length; i++) {
        if (!fpaths[i].getName().endsWith(".mp4")) {
          fpaths[i].delete();
        }
      }
      return true;
    } catch (Exception e) {
      e.printStackTrace();
      logger.error("视频片段合成为MP4格式时路径：" + filePath + "-------" + e.toString());
      return false;
    }
  }

  /**
   * 文件夹文件排序
   *
   * @param flieT 文件夹路径
   */
  public static File[] orderByName(String flieT) {
    File[] fileArr = null;
    List<File> files = new ArrayList<>();
    try {
      fileArr = new File(flieT).listFiles();
      //过滤掉非ts文件
      for (File file : fileArr) {
        if (file.getName().endsWith(".ts")) {
          files.add(file);
        }
      }
      Collections.sort(files, new Comparator<File>() {
        @Override
        public int compare(File o1, File o2) {
          if (Long.parseLong(getNum(o1.getName())) < Long.parseLong(getNum(o2.getName()))) {
            return -1;
          }
          if (Long.parseLong(getNum(o1.getName())) > Long.parseLong(getNum(o2.getName()))) {
            return 1;
          }
          return 0;
        }
      });
      fileArr = files.toArray(new File[files.size()]);
      return fileArr;
    } catch (Exception e) {
      e.printStackTrace();
      logger.error("m3u8排序时错误文件路径：" + flieT + "-------" + e.toString());
    }
    return null;

  }

  public static boolean tsToMp4(String filePath) {
    try {
      String mp4Path = filePath.replace(".ts", ".mp4");
      Runtime runtime = Runtime.getRuntime();
      String cutCmd =
          "ffmpeg -i \"concat:" + filePath + "\" -c copy -bsf:a aac_adtstoasc -movflags +faststart "
              + mp4Path + "";
      logger.warn("执行命令--------ffmpeg -i \"concat:" + filePath
          + "\" -c copy -bsf:a aac_adtstoasc -movflags +faststart " + mp4Path + "");
      Process proce = runtime.exec(new String[]{"/bin/sh", "-c", cutCmd});
      proce.waitFor();
      Thread.sleep(3000);
      return true;
    } catch (Exception e) {
      e.printStackTrace();
    }
    return false;
  }

  /**
   * 获取数字
   *
   * @param str 字符串
   * @author liuk
   */
  public static String getNum(String str) {
    String result = "";
    Pattern p = Pattern.compile("\\d+");
    Matcher m = p.matcher(str);
    while (m.find()) {
      result += m.group();
    }
    return result;
  }

}
