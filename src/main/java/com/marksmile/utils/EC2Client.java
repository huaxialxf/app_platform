package com.marksmile.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.CreateTagsRequest;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.InstanceStateChange;
import com.amazonaws.services.ec2.model.InstanceType;
import com.amazonaws.services.ec2.model.Reservation;
import com.amazonaws.services.ec2.model.RunInstancesRequest;
import com.amazonaws.services.ec2.model.RunInstancesResult;
import com.amazonaws.services.ec2.model.Tag;
import com.amazonaws.services.ec2.model.TerminateInstancesRequest;
import com.amazonaws.services.ec2.model.TerminateInstancesResult;

public class EC2Client {
    private static Logger logger = LoggerFactory.getLogger(EC2Client.class);
    private AmazonEC2Client ec2Client = null;
    // private String key = "testUser";

    private synchronized AmazonEC2Client getEc2Client() {
        if (ec2Client == null) {
            ec2Client = AWSClientUtil.getEc2Client();
        }
        return ec2Client;
    }

    private List<Instance> _runInstances(String imageId, InstanceType instanceType, int count, String subnetRange, String userData, String key) {
        RunInstancesRequest runInstancesRequest = null;
        runInstancesRequest = new RunInstancesRequest();
        // 设置 id
        runInstancesRequest.setImageId(imageId);
        runInstancesRequest.setInstanceType(instanceType);
        runInstancesRequest.setMinCount(count);
        runInstancesRequest.setMaxCount(count);
        runInstancesRequest.setUserData(Base64.encodeBase64String((userData).getBytes()));
        runInstancesRequest.setKeyName(key);
        // 子网
        String[] str = subnetRange.split(";");
        int index = new Random().nextInt(str.length);
        String subnetId = str[index];
        runInstancesRequest.setSubnetId(subnetId);
        RunInstancesResult runInstancesResult = getEc2Client().runInstances(runInstancesRequest);
        Reservation reservation = runInstancesResult.getReservation();
        List<Instance> instanceList = reservation.getInstances();
        return instanceList;

    }

    public List<Instance> runInstances(String imageId, InstanceType instanceType, int count, String subnetRange, String userData, String key) {
        try {
            return _runInstances(imageId, instanceType, count, subnetRange, userData, key);
        } catch (Exception e) {
            try {
                Thread.sleep(1000 * 3);
            } catch (InterruptedException e1) {
            }
            logger.error(e.getMessage());
            return _runInstances(imageId, instanceType, count, subnetRange, userData, key);
        }

    }

    private void _batchSetTagName(List<Instance> instanceList, String tagName) {
        if (instanceList != null && instanceList.size() != 0) {
            List<String> instanceIds = new ArrayList<String>();
            List<Tag> listTag = new ArrayList<Tag>();
            for (Instance instance : instanceList) {
                Tag tag = new Tag();
                tag.setKey("Name");
                tag.setValue(tagName);
                instanceIds.add(instance.getInstanceId());
                listTag.add(tag);
            }

            CreateTagsRequest createTagsRequest = new CreateTagsRequest(instanceIds, listTag);
            getEc2Client().createTags(createTagsRequest);
        }
    }

    public void batchSetTagName(List<Instance> instanceList, String tagName) {
        try {
            _batchSetTagName(instanceList, tagName);
        } catch (Exception e) {
            try {
                Thread.sleep(1000 * 3);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
            logger.error(e.getMessage());
            _batchSetTagName(instanceList, tagName);
        }

    }

    private void _stopInstace(List<String> instanceIds) {
        TerminateInstancesRequest request = new TerminateInstancesRequest(instanceIds);
        TerminateInstancesResult instancesResult = getEc2Client().terminateInstances(request);
        List<InstanceStateChange> listInstanceStateChange = instancesResult.getTerminatingInstances();
        for (InstanceStateChange instanceStateChange : listInstanceStateChange) {
            logger.info("InstanceStateChange InstanceId={},{}>>>{}", instanceStateChange.getInstanceId(), instanceStateChange.getPreviousState(),
                    instanceStateChange.getCurrentState());

        }
    }
    

    public void stopInstace(List<String> instanceIds) {
        try {
            _stopInstace(instanceIds);
        } catch (Exception e) {
            try {
                Thread.sleep(1000 * 3);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
            logger.error(e.getMessage());
            _stopInstace(instanceIds);
        }

    }

}
