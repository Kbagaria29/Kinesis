/*
 * Copyright 2012-2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.kinesis.main;

import java.nio.ByteBuffer;
import java.util.Random;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.RegionUtils;
import com.amazonaws.services.kinesis.AmazonKinesis;
import com.amazonaws.services.kinesis.AmazonKinesisClient;
import com.amazonaws.services.kinesis.model.DescribeStreamResult;
import com.amazonaws.services.kinesis.model.PutRecordRequest;
import com.amazonaws.services.kinesis.model.ResourceNotFoundException;
import com.kinesis.model.OauthJSON;
import com.kinesis.utils.ConfigurationUtils;
import com.kinesis.utils.CredentialUtils;

@Component
public class KinesisLogger {

	private static final Logger logger = LogManager.getLogger("KinesisLogger");

	@Autowired
	private ServiceProperties configuration;

	public String getHelloMessage() {
		return "Hello " + this.configuration.getName();
	}

	public void start() {
		logger.info("Start Kinesis Logging!");

        String streamName = "SahilStockTradeStream";
        String regionName = "us-west-2";
        Region region = RegionUtils.getRegion(regionName);
        if (region == null) {
            System.err.println(regionName + " is not a valid AWS region.");
            System.exit(1);
        }

        try {
			AWSCredentials credentials = CredentialUtils.getCredentialsProvider().getCredentials();
			AmazonKinesis kinesisClient = new AmazonKinesisClient(credentials,
	                ConfigurationUtils.getClientConfigWithUserAgent());
	        kinesisClient.setRegion(region);

	        // Validate that the stream exists and is active
	        validateStream(kinesisClient, streamName);

	        // Repeatedly send stock trades with a 100 milliseconds wait in between
	        while(true) {
	            sendOauthJson(null, kinesisClient, streamName);
	            Thread.sleep(getRandomNumberInRange(1,5));
	        }
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	  private static void validateStream(AmazonKinesis kinesisClient, String streamName) {
	        try {
	            DescribeStreamResult result = kinesisClient.describeStream(streamName);
	            if(!"ACTIVE".equals(result.getStreamDescription().getStreamStatus())) {
	                System.err.println("Stream " + streamName + " is not active. Please wait a few moments and try again.");
	                System.exit(1);
	            }
	        } catch (ResourceNotFoundException e) {
	            System.err.println("Stream " + streamName + " does not exist. Please create it in the console.");
	            System.err.println(e);
	            System.exit(1);
	        } catch (Exception e) {
	            System.err.println("Error found while describing the stream " + streamName);
	            System.err.println(e);
	            System.exit(1);
	        }
	    }
	  
	private static int getRandomNumberInRange(int min, int max) {

		if (min >= max) {
			throw new IllegalArgumentException("max must be greater than min");
		}

		Random r = new Random();
		return r.nextInt((max - min) + 1) + min;
	}
	
	private static void sendOauthJson(OauthJSON json, AmazonKinesis kinesisClient,
            String streamName) {

		json = new OauthJSON();
		API currentAPI = API.getRandom();
		json.setApi(currentAPI.name());
		json.setClientName(ClientName.getRandom().name());
		json.setServiceName(ServiceName.getRandom().name());
		json.setUserId(getRandomNumberInRange(10000,100000));
		json.setTokenId(UUID.randomUUID().toString());
		json.setTickerSymbol("AMZN");
		switch (currentAPI) {
		case CreateTokenFromUserIdFailure:
			json.setError(CreateTokenFailureErrors.getRandom().name());
			break;
		case ValidateTokenFailure:
			json.setError(CreateTokenFailureErrors.getRandom().name());
			break;
		case RevokeTokenFailure:
			json.setError(CreateTokenFailureErrors.getRandom().name());
			break;
		default:
			break;
		}
		logger.info(json.toString());

        byte[] bytes = json.toJsonAsBytes();
        // The bytes could be null if there is an issue with the JSON serialization by the Jackson JSON library.
        if (bytes == null) {
        	logger.info("Could not get JSON bytes for stock trade");
            return;
        }

        PutRecordRequest putRecord = new PutRecordRequest();
        putRecord.setStreamName(streamName);
        // We use the ticker symbol as the partition key, as explained in the tutorial.
        putRecord.setPartitionKey(json.getTickerSymbol());
        putRecord.setData(ByteBuffer.wrap(bytes));

        try {
            kinesisClient.putRecord(putRecord);
        } catch (AmazonClientException ex) {
        	logger.warn("Error sending record to Amazon Kinesis.", ex);
        }
    }
}
