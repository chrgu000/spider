package com.dr.spider.utils;

import io.tus.java.client.*;
import java.util.HashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

public class TusUtils {

    public final static Logger logger = LoggerFactory.getLogger(TusUtils.class);

    public static String update(String url, File file, Map metadata) {
        final String[] result = new String[1];
        TusClient client = new TusClient();
        try {
            client.setUploadCreationURL(new URL(url));
            client.enableResuming(new TusURLMemoryStore());
            TusUpload upload = new TusUpload();
            upload.setSize(file.length());
            upload.setInputStream(new FileInputStream(file));
            upload.setFingerprint(String.format("%s-%d", file.getAbsolutePath(), upload.getSize()));
            upload.setMetadata(metadata);

            System.out.println("Starting upload...");

            TusExecutor executor = new TusExecutor() {
                @Override
                protected void makeAttempt() throws ProtocolException, IOException {
                    TusUploader uploader = client.resumeOrCreateUpload(upload);
                    uploader.setChunkSize(1024);
                    do {
                        long totalBytes = upload.getSize();
                        long bytesUploaded = uploader.getOffset();
                        double progress = (double) bytesUploaded / totalBytes * 100;
                        System.out.printf("Upload at %06.2f%%.\n", progress);
                    } while (uploader.uploadChunk() > -1);
                    uploader.finish();
                    result[0] = uploader.getUploadURL().toString();
                    System.out.println("Upload finished.");
                    System.out.format("Upload available at: %s", result[0]);

                }
            };
            executor.makeAttempts();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        }
        return result[0];
    }

    public static void main(String[] args) throws Exception {
        String url = "http://upload291.fvs.io/upload/";
        File file = new File("/Users/longlongl/work/tt_bak/Downloads/ts/test2.mp4");
        Map<String, String> metadata = new HashMap<>();
        metadata.put("token", "dytWbllXYUhJS1ZyMVdpZTFwQVhnUzRxdDVOU1l3bkZZS0F3cVhZOE1zdVJrM1E3NXFEK04xM1p1SU11d3habWh4a1J5LzV2UFdGY2w2Qk9zdz09Om9PMUs0TWpqeHJlb3FFa2VwZ2VKYnc9PQ");
        metadata.put("name", file.getName());
        TusUtils.update(url, file, metadata);
    }
}
