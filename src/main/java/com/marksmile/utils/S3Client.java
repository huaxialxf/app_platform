package com.marksmile.utils;

import java.io.File;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;

public class S3Client {

    private static Logger logger = LoggerFactory.getLogger(S3Client.class);

    private AmazonS3Client amazonS3Client = null;

    private synchronized AmazonS3Client getAmazonS3Client() {
        if (amazonS3Client == null) {
            amazonS3Client = AWSClientUtil.getAmazonS3Client();
        }
        return amazonS3Client;
    }


    /**
     * 下载数据
     * 
     * @param 存储桶名
     * @param 远程文件名（就是s3上的文件）--带路径
     * @param 本地文件
     *            --带路径
     * @return
     * @throws IOException
     */
    public boolean downLoadFile(String bucketName, String remoteFile, String localFile) throws Exception {
        int times = 3;
        return downLoad(bucketName, remoteFile, localFile, times);
    }

    public boolean downLoad(String bucketName, String remoteFile, String localFile, int times) throws Exception {
        boolean bool = false;
        try {
            if (remoteFile.startsWith("/")) {
                remoteFile = remoteFile.substring(1);
            }
            GetObjectRequest getObjectRequest = new GetObjectRequest(bucketName, remoteFile);

            getAmazonS3Client().getObject(getObjectRequest, new File(localFile));
            bool = true;
        } catch (Exception e) {
            logger.error(e.getMessage());
            times--;
            if (times > 0) {
                logger.info("s3下载文件" + bucketName + "/" + remoteFile + "失败，正重新下载。。。");
                downLoad(bucketName, remoteFile, localFile, times);
            } else {
                throw e;
            }
        }
        return bool;
    }

    /**
     * 上传
     * 
     * @param 本地文件
     *            --带路径
     * @param 远程文件名（就是s3上的文件）
     * @param 存储桶名
     * @return
     * @throws IOException
     */
    public boolean upLoadFile(String bucketName, String remoteFile, String localFile) throws Exception {
        int times = 3;
        return upLoad(bucketName, remoteFile, localFile, times);
    }

    public boolean upLoad(String bucketName, String remoteFile, String localFile, int times) throws Exception {
        boolean bool = false;
        try {
            File file = new File(localFile);
            getAmazonS3Client().putObject(new PutObjectRequest(bucketName, remoteFile, file));
            bool = true;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            times--;
            if (times > 0) {
                logger.info("s3上传文件" + bucketName + "/" + localFile + "失败，正重新上传。。。");
                return upLoad(bucketName, remoteFile, localFile, times);
            } else {
                throw e;
            }
        }
        return bool;
    }

    public static void main(String[] args) throws Exception {
        new S3Client().downLoad("t.yumi.com", "ec2code/send_email/sendmail.zip", "sendmail.zip", 3);
    }

}
