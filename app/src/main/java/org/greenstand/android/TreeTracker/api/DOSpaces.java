package org.greenstand.android.TreeTracker.api;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.AccessControlList;
import com.amazonaws.services.s3.model.GroupGrantee;
import com.amazonaws.services.s3.model.Permission;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;

import org.greenstand.android.TreeTracker.BuildConfig;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * Created by lei on 12/11/17.
 */

public class DOSpaces {

    public static final String ENDPOINT = "https://nyc3.digitaloceanspaces.com";
    public static final String BUCKET = "treetracker-dev";


    private static DOSpaces sInstance;
    private AmazonS3 s3Client;

    public static DOSpaces instance() {
        if (sInstance == null) {
            sInstance = new DOSpaces();
        }
        return sInstance;
    }

    private DOSpaces() {
        BasicAWSCredentials basicAWSCredentials =
                new BasicAWSCredentials(BuildConfig.DOS_ACCESS_KEY, BuildConfig.DOS_SECRET_KEY);
        s3Client = new AmazonS3Client(basicAWSCredentials);
        s3Client.setEndpoint(ENDPOINT);
    }

    public String put(String path) throws AmazonClientException {
        AccessControlList acl = new AccessControlList();
        acl.grantPermission(GroupGrantee.AllUsers, Permission.Read);

        File image = new File(path);
        String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());

        String dosKey = timeStamp + '_' + UUID.randomUUID() + '_' + image.getName();
        PutObjectRequest poRequest =
                new PutObjectRequest(BUCKET, dosKey, image);
        poRequest.withAccessControlList(acl);
        PutObjectResult poResult = s3Client.putObject(poRequest);
        String imageUrl = String.format("https://%s.nyc3.digitaloceanspaces.com/%s", BUCKET, dosKey);
        return imageUrl;
    }

}
