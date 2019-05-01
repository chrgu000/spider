package com.dr.spider.utils;

import io.tus.java.client.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

public class TusUtils {

    public final static Logger logger = LoggerFactory.getLogger(TusUtils.class);

    public static void update(String url, File file, Map metadata) {
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

                    System.out.println("Upload finished.");
                    System.out.format("Upload available at: %s", uploader.getUploadURL().toString());
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
    }
}
