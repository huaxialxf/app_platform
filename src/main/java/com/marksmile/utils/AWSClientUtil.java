package com.marksmile.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.sqs.AmazonSQSClient;

public class AWSClientUtil {
    public static AmazonS3Client getAmazonS3Client() {

        Properties properties = new Properties();
        InputStream in = AWSClientUtil.class.getClassLoader().getResourceAsStream("aws_s3.properties");
        // 使用properties对象加载输入流
        try {
            properties.load(in);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        final String secretKey = properties.getProperty("secretKey");
        final String accessKeyId = properties.getProperty("accessKeyId");
        String regionName = properties.getProperty("regionName");

        AWSCredentials awsCredentials = new AWSCredentials() {
            public String getAWSSecretKey() {
                return secretKey;
            }

            public String getAWSAccessKeyId() {
                return accessKeyId;
            }
        };

        ClientConfiguration clientConfiguration = new ClientConfiguration();
        clientConfiguration.setConnectionTTL(100);
        clientConfiguration.setConnectionTimeout(20 * 1000);
        clientConfiguration.setSocketTimeout(20 * 1000);
        AmazonS3Client s3client = new AmazonS3Client(awsCredentials, clientConfiguration);
//        s3client.setRegion(Region.getRegion(Regions.fromName(regionName)));
        return s3client;
    }

    public static AmazonSQSClient getSQSClient() {

        Properties properties = new Properties();
        InputStream in = AWSClientUtil.class.getClassLoader().getResourceAsStream("aws_s3.properties");
        // 使用properties对象加载输入流
        try {
            properties.load(in);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        final String secretKey = properties.getProperty("secretKey");
        final String accessKeyId = properties.getProperty("accessKeyId");
        String regionName = properties.getProperty("regionName");

        AWSCredentials awsCredentials = new AWSCredentials() {
            public String getAWSSecretKey() {
                return secretKey;
            }

            public String getAWSAccessKeyId() {
                return accessKeyId;
            }
        };

        ClientConfiguration clientConfiguration = new ClientConfiguration();
        clientConfiguration.setConnectionTTL(100);
        clientConfiguration.setConnectionTimeout(20 * 1000);
        clientConfiguration.setSocketTimeout(20 * 1000);
        AmazonSQSClient sqsClient = new AmazonSQSClient(awsCredentials, clientConfiguration);
        sqsClient.setRegion(Region.getRegion(Regions.fromName(regionName)));
        return sqsClient;
    }

    

    public static AmazonEC2Client getEc2Client() {

        Properties properties = new Properties();
        InputStream in = AWSClientUtil.class.getClassLoader().getResourceAsStream("aws_s3.properties");
        // 使用properties对象加载输入流
        try {
            properties.load(in);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        final String secretKey = properties.getProperty("secretKey");
        final String accessKeyId = properties.getProperty("accessKeyId");
        String regionName = properties.getProperty("regionName");

        AWSCredentials awsCredentials = new AWSCredentials() {
            public String getAWSSecretKey() {
                return secretKey;
            }

            public String getAWSAccessKeyId() {
                return accessKeyId;
            }
        };

        ClientConfiguration clientConfiguration = new ClientConfiguration();
        clientConfiguration.setConnectionTTL(100);
        clientConfiguration.setConnectionTimeout(20 * 1000);
        clientConfiguration.setSocketTimeout(20 * 1000);
        AmazonEC2Client ec2Client = new AmazonEC2Client(awsCredentials, clientConfiguration);
        ec2Client.setRegion(Region.getRegion(Regions.fromName(regionName)));
        return ec2Client;
    }

}
