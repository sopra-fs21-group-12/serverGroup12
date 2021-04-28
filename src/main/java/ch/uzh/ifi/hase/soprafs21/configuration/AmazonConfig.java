package ch.uzh.ifi.hase.soprafs21.configuration;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AmazonConfig {

    // Passing our AWS Credentials to access it -> Gives us an instance of the amazon Client


    @Bean
    public AmazonS3 s3() {

        AWSCredentials awsCredentials = null;

        if (System.getenv("AWS_ACCESS_KEY_ID") == null ){
             awsCredentials = new BasicAWSCredentials("AKIASTGENLAI6WFXIH6S",
                   "PcCylY1invj/JS4mo1zZcVAkpL6GSb3brTs5tAJf");
        }else {
             awsCredentials = new BasicAWSCredentials(System.getenv("AWS_ACCESS_KEY_ID"),
                    System.getenv("AWS_SECRET_ACCESS_KEY"));
        }

        return AmazonS3Client.builder().withRegion("eu-central-1").withCredentials(new AWSStaticCredentialsProvider(awsCredentials)).build();
    }
}