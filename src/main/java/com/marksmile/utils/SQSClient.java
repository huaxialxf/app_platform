package com.marksmile.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.model.CreateQueueRequest;
import com.amazonaws.services.sqs.model.DeleteMessageBatchRequestEntry;
import com.amazonaws.services.sqs.model.DeleteMessageRequest;
import com.amazonaws.services.sqs.model.DeleteQueueRequest;
import com.amazonaws.services.sqs.model.GetQueueAttributesRequest;
import com.amazonaws.services.sqs.model.GetQueueAttributesResult;
import com.amazonaws.services.sqs.model.ListQueuesRequest;
import com.amazonaws.services.sqs.model.ListQueuesResult;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.PurgeQueueRequest;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.amazonaws.services.sqs.model.SendMessageBatchRequestEntry;
import com.amazonaws.services.sqs.model.SendMessageRequest;

public class SQSClient {
    private static Logger logger = LoggerFactory.getLogger(SQSClient.class);
    private AmazonSQSClient sqsClient = null;

    private synchronized AmazonSQSClient getSQSClient() {
        if (sqsClient == null) {
            sqsClient = AWSClientUtil.getSQSClient();
        }
        return sqsClient;
    }

    public String createQueue(String queueName) {
        CreateQueueRequest createQueueRequest = new CreateQueueRequest(queueName);
        String myQueueUrl = getSQSClient().createQueue(createQueueRequest).getQueueUrl();
        return myQueueUrl;
    }

    public boolean queueExist(String queueName) {
        ListQueuesRequest listQueuesRequest = new ListQueuesRequest(queueName);
        ListQueuesResult listQueuesResult = getSQSClient().listQueues(listQueuesRequest);
        boolean result = (listQueuesResult.getQueueUrls().size() > 0 ? true : false);
        return result;
    }

    public String createQueue(String queueName, int visibilityTimeout) {
        CreateQueueRequest createQueueRequest = new CreateQueueRequest(queueName);
        createQueueRequest.addAttributesEntry("VisibilityTimeout", String.valueOf(visibilityTimeout));
        String myQueueUrl = getSQSClient().createQueue(createQueueRequest).getQueueUrl();
        return myQueueUrl;
    }

    public String createQueue(String queueName, Map<String, String> mapAttrs) {
        CreateQueueRequest createQueueRequest = new CreateQueueRequest(queueName);
        for (String key : mapAttrs.keySet()) {
            String value = mapAttrs.get(key);
            createQueueRequest.addAttributesEntry(key, value);
        }
        String myQueueUrl = getSQSClient().createQueue(createQueueRequest).getQueueUrl();
        return myQueueUrl;
    }

    public void deleteQueue(String queueUrl) {
        getSQSClient().deleteQueue(new DeleteQueueRequest(queueUrl));
    }

    public void clearQueue(String queueUrl) {
        getSQSClient().purgeQueue(new PurgeQueueRequest(queueUrl));
    }

    public List<String> listQueues() {
        return getSQSClient().listQueues().getQueueUrls();
    }

    public String sendMessage(String queueUrl, String body) {
        return getSQSClient().sendMessage(new SendMessageRequest(queueUrl, body)).getMessageId();
    }

    public void sendMessageBatch(String queueUrl, List<SendMessageBatchRequestEntry> messaageList) {
        getSQSClient().sendMessageBatch(queueUrl, messaageList);
    }

    public Message receiveMessage(String queueUrl) {
        ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest(queueUrl);

        List<Message> messages = getSQSClient().receiveMessage(receiveMessageRequest.withMaxNumberOfMessages(1)).getMessages();
        if (messages.size() > 0) {
            return messages.get(0);
        }
        return null;
    }

    public List<Message> receiveMessage(String queueUrl, int num) {
        ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest(queueUrl);
        List<Message> messages = getSQSClient().receiveMessage(receiveMessageRequest.withMaxNumberOfMessages(num)).getMessages();
        if (messages.size() > 0) {
            return messages;
        }
        return null;
    }

    public void deleteMessage(String queueUrl, Message message) {
        try {
            _deleteMessage(queueUrl, message);
        } catch (Exception e) {
            try {
                Thread.sleep(1000 * 3);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
            logger.error(e.getMessage());
            _deleteMessage(queueUrl, message);
        }
    }

    public void _deleteMessage(String queueUrl, Message message) {
        String messageRecieptHandle = message.getReceiptHandle();
        getSQSClient().deleteMessage(new DeleteMessageRequest(queueUrl, messageRecieptHandle));
    }

    private void _batchDeleteMessage(String queueUrl, List<String> recieptHandles) {
        List<DeleteMessageBatchRequestEntry> entrys = new ArrayList<DeleteMessageBatchRequestEntry>();
        DeleteMessageBatchRequestEntry entry = null;
        for (String recieptHandle : recieptHandles) {
            entry = new DeleteMessageBatchRequestEntry();
            entry.setId(UUID.randomUUID().toString());
            entry.setReceiptHandle(recieptHandle);
            entrys.add(entry);
            if (entrys.size() == 10) {
                getSQSClient().deleteMessageBatch(queueUrl, entrys);
                entrys.clear();
            }
        }
        if (!entrys.isEmpty()) {
            getSQSClient().deleteMessageBatch(queueUrl, entrys);
        }
    }

    public void batchDeleteMessage(String queueUrl, List<String> recieptHandles) {
        try {
            _batchDeleteMessage(queueUrl, recieptHandles);
        } catch (Exception e) {
            try {
                Thread.sleep(1000 * 3);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
            logger.error(e.getMessage());
            _batchDeleteMessage(queueUrl, recieptHandles);
        }

    }

    public String getQueueLength(String queueUrl) {
        List<String> attrs = Arrays.asList("ApproximateNumberOfMessages");
        GetQueueAttributesResult result = getSQSClient().getQueueAttributes(new GetQueueAttributesRequest(queueUrl, attrs));
        return result.getAttributes().get("ApproximateNumberOfMessages");
    }

    public boolean isEmptyQueue(String queueUrl) {
        List<String> attrs = Arrays.asList("ApproximateNumberOfMessages", "ApproximateNumberOfMessagesNotVisible");

        GetQueueAttributesResult result = getSQSClient().getQueueAttributes(new GetQueueAttributesRequest(queueUrl, attrs));
        if ("0".equals(result.getAttributes().get("ApproximateNumberOfMessages"))
                && "0".equals(result.getAttributes().get("ApproximateNumberOfMessagesNotVisible"))) {
            return true;
        }
        return false;
    }

    public int getQueueSize(String queueUrl) {
        List<String> attrs = Arrays.asList("ApproximateNumberOfMessages");

        GetQueueAttributesResult result = getSQSClient().getQueueAttributes(new GetQueueAttributesRequest(queueUrl, attrs));
        return Integer.parseInt(result.getAttributes().get("ApproximateNumberOfMessages"));
    }

    public int getQueueSize2(String queueUrl) {
        List<String> attrs = Arrays.asList("ApproximateNumberOfMessages", "ApproximateNumberOfMessagesNotVisible");

        GetQueueAttributesResult result = getSQSClient().getQueueAttributes(new GetQueueAttributesRequest(queueUrl, attrs));
        int size1 = Integer.parseInt(result.getAttributes().get("ApproximateNumberOfMessages"));
        int size2 = Integer.parseInt(result.getAttributes().get("ApproximateNumberOfMessagesNotVisible"));

        return size1 + size2;
    }

    public String getAttrValue(String attr, String queueUrl) {
        List<String> attrs = Arrays.asList("All");

        GetQueueAttributesResult result = getSQSClient().getQueueAttributes(new GetQueueAttributesRequest(queueUrl, attrs));
        Map<String, String> map = result.getAttributes();
        return map.get(attr);
    }

    public void testAttr(String queueUrl) {
        List<String> attrs = Arrays.asList("All");

        GetQueueAttributesResult result = getSQSClient().getQueueAttributes(new GetQueueAttributesRequest(queueUrl, attrs));
        Map<String, String> map = result.getAttributes();
        for (String key : map.keySet()) {
            System.out.println(key + ":" + map.get(key));
        }
    }

    public String getQueueUrl(String queueName) {
        return getSQSClient().getQueueUrl(queueName).getQueueUrl();
    }


    public  void importData( String sqsUrl, Queue<String> tasks, int numThread) throws Exception {
        long start = System.currentTimeMillis();
        final AtomicInteger requestId = new AtomicInteger(0);
        CountDownLatch latch = new CountDownLatch(numThread);
        for (int i = 0; i < numThread; i++) {
            Thread t = new Thread() {
                public void run() {
                    while (true) {
                        try {
                            List<SendMessageBatchRequestEntry> messaageList = new ArrayList<SendMessageBatchRequestEntry>();
                            for (int j = 0; j < 10; j++) {
                                String taskInfo = tasks.poll();
                                if (taskInfo == null) {
                                    break;
                                }
                                SendMessageBatchRequestEntry requestEntry = new SendMessageBatchRequestEntry();
                                requestEntry.setMessageBody(taskInfo);
                                requestEntry.setId(String.valueOf(requestId.incrementAndGet()));
                                messaageList.add(requestEntry);
                            }
                            if (messaageList.size() > 0) {
                                sendMessageBatch(sqsUrl, messaageList);
                                // logger.info("sendMessageBatch:{}",
                                // messaageList.size());
                            }
                            if (messaageList.size() < 10) {
                                break;
                            }

                        } catch (Throwable e) {
                            logger.info(e.getMessage(), e);
                        }
                        try {
                            Thread.sleep(10);
                        } catch (InterruptedException e) {
                        }
                    }
                    latch.countDown();
                };
            };
            t.setName("import_sqs_" + i);
            t.start();
        }
        latch.await();
        logger.info("导入数据成功:耗时:" + (System.currentTimeMillis() - start));
    }
    public void exportSQSMessageByUrl(String sqsFileDir, final String queueUrl, int exportNumThread) throws Exception {

        final File fileBase = new File(sqsFileDir);
        if (!fileBase.exists()) {
            fileBase.mkdirs();
        }
        final CountDownLatch countDownLatch = new CountDownLatch(exportNumThread);
        for (int i = 0; i < exportNumThread; i++) {
            final int index = i;
            Thread thread = new Thread() {
                public void run() {
                    FileOutputStream fos = null;
                    try {
                        fos = new FileOutputStream(new File(fileBase, String.format("%2d.txt", index)));
                        while (true) {
                            try {
                                ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest(queueUrl);
                                List<Message> messages = getSQSClient().receiveMessage(receiveMessageRequest.withMaxNumberOfMessages(10)).getMessages();
                                if (messages != null && messages.size() > 0) {
//                                    logger.info("收到{}条信息", messages.size());
                                    List<String> recieptHandles = new ArrayList<String>();
                                    for (Message message : messages) {
                                        recieptHandles.add(message.getReceiptHandle());
                                        fos.write((message.getBody() + "\n").getBytes());
                                    }
                                    batchDeleteMessage(queueUrl, recieptHandles);
                                } else {
                                    if (getMessageCountWithNotVisible(queueUrl) == 0) {
                                        break;
                                    } else {
                                        try {
                                            Thread.sleep(1000);
                                        } catch (InterruptedException e) {
                                        }
                                    }
                                }

                            } catch (Throwable e) {
                                logger.error(e.getMessage(), e);
                                try {
                                    Thread.sleep(1000 * 3);
                                } catch (InterruptedException e1) {
                                }
                            }

                        }
                    } catch (Exception e2) {
                    } finally {
                        if (fos != null) {
                            try {
                                fos.close();
                            } catch (IOException e) {
                            }
                        }
                        countDownLatch.countDown();
                    }
                }
            };
            thread.start();
        }

        countDownLatch.await();
    }

    /**
     * sqs数据导出
     * 
     * @param sqsFilePath
     *            导出文件路径
     * @param queueName
     *            sqs队列名称
     * @throws Exception
     */
    public void exportSQSMessage(String sqsFilePath, String queueName, int exportNumThread) throws Exception {
        String queueUrl = getQueueUrl(queueName);
        exportSQSMessageByUrl(sqsFilePath, queueUrl, exportNumThread);

    }

    /**
     * 获取当前队列的消息总数 包括传输中的消息
     */
    private Integer getMessageCountWithNotVisible(String sqsUrl) {
        List<String> attrs = Arrays.asList("ApproximateNumberOfMessages", "ApproximateNumberOfMessagesNotVisible");
        GetQueueAttributesResult result = getSQSClient().getQueueAttributes(new GetQueueAttributesRequest(sqsUrl, attrs));
        String count = result.getAttributes().get("ApproximateNumberOfMessages");
        String notVisibleCount = result.getAttributes().get("ApproximateNumberOfMessagesNotVisible");
        Integer count_interger = null;
        Integer notVisibleCount_integer = null;
        if (count == null) {
            count_interger = 0;
        } else {
            count_interger = Integer.parseInt(count);
        }
        if (notVisibleCount == null) {
            notVisibleCount_integer = 0;
        } else {
            notVisibleCount_integer = Integer.parseInt(notVisibleCount);
        }
        return (count_interger + notVisibleCount_integer);
    }

    public String createQueue(String queueName, String queueDeadName, int visibilityTimeout, int maxReceiveTimes) {
        Map<String, String> attributes = new HashMap<String, String>();
        // sqs队列保存时间 为 14天
        attributes.put("MessageRetentionPeriod", "1209600");
        logger.debug("创建死信队列:" + queueDeadName);
        String sqsUrl = null;
        while (true) {
            try {
                sqsUrl = createQueue(queueDeadName, attributes);
                break;
            } catch (Exception e) {
            }
        }
        String deadLetterTargetArn = getAttrValue("QueueArn", sqsUrl);
        attributes = new HashMap<String, String>();
        attributes.put("VisibilityTimeout", String.valueOf(visibilityTimeout));
        Map<String, String> deadAttrs = new HashMap<String, String>();
        deadAttrs.put("deadLetterTargetArn", deadLetterTargetArn);
        deadAttrs.put("maxReceiveCount", String.valueOf(maxReceiveTimes));
        attributes.put("RedrivePolicy", JSONUtil.toJsonString(deadAttrs));
        // sqs队列保存时间 为 14天
        attributes.put("MessageRetentionPeriod", "1209600");
        logger.debug("创建队列:" + queueName);
        return createQueue(queueName, attributes);
    }

    /**
     * 
     * @param queueName
     * @param queueDeadName
     * @param visibilityTimeout
     * @param maxReceiveTimes
     * @return 死信队列URL 任务队列URL
     * @Description :
     */
    public String[] createQueueWithDead(String queueName, String queueDeadName, String visibilityTimeout, String maxReceiveTimes) {
        Map<String, String> attributes = new HashMap<String, String>();
        // sqs队列保存时间 为 14天
        attributes.put("MessageRetentionPeriod", "1209600");
        logger.debug("创建死信队列:" + queueDeadName);
        String sqsUrlDead = null;
        while (true) {
            try {
                sqsUrlDead = createQueue(queueDeadName, attributes);
                break;
            } catch (Exception e) {
                logger.error("创建队列失败:" + queueDeadName, e);
                try {
                    Thread.sleep(1000 * 1);
                } catch (InterruptedException e1) {
                }
            }
        }
        String[] ret = new String[2];
        ret[0] = sqsUrlDead;
        String deadLetterTargetArn = getAttrValue("QueueArn", sqsUrlDead);
        attributes = new HashMap<String, String>();
        attributes.put("VisibilityTimeout", visibilityTimeout);
        Map<String, String> deadAttrs = new HashMap<String, String>();
        deadAttrs.put("deadLetterTargetArn", deadLetterTargetArn);
        deadAttrs.put("maxReceiveCount", maxReceiveTimes);
        attributes.put("RedrivePolicy", JSONUtil.toJsonString(deadAttrs));
        // sqs队列保存时间 为 14天
        attributes.put("MessageRetentionPeriod", "1209600");
        logger.debug("创建队列:" + queueName);
        String sqsUrl = null;
        while (true) {
            try {
                System.out.println("queueName:" + queueName);
                sqsUrl = createQueue(queueName, attributes);
                break;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        ret[1] = sqsUrl;
        return ret;
    }

}
