package com.skaveesh.face.auth.aws;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.rekognition.AmazonRekognition;
import com.amazonaws.services.rekognition.AmazonRekognitionClientBuilder;
import com.amazonaws.services.rekognition.model.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.jboss.logging.Logger;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;

public class AWSService {

    private static Logger logger = Logger.getLogger(AWSService.class);

    private static final String COLLECTION_ID = "MyCollection";

    private final AmazonRekognition rekognitionClient;

    public AWSService() {
        BasicAWSCredentials awsCreds = new BasicAWSCredentials( System.getenv("AWS_ACCESS_KEY"), System.getenv("AWS_SECRET_KEY"));
        rekognitionClient = AmazonRekognitionClientBuilder
                .standard().withRegion(Regions.US_EAST_2)
                .withCredentials(new AWSStaticCredentialsProvider(awsCreds)).build();
    }

    private Image getImage(String imageData) {
        logger.debug(imageData);
        byte[] decodedImage = Base64.getDecoder().decode(imageData.getBytes(StandardCharsets.UTF_8));
        return new Image().withBytes(ByteBuffer.wrap(decodedImage));
    }


    private boolean searchInCollection(Image target,String userName) throws JsonProcessingException {
        SearchFacesByImageRequest searchFacesByImageRequest = new SearchFacesByImageRequest()
                .withCollectionId(COLLECTION_ID)
                .withImage(target)
                .withFaceMatchThreshold(85F);
//                .withMaxFaces(2);

        SearchFacesByImageResult searchFacesByImageResult =
                rekognitionClient.searchFacesByImage(searchFacesByImageRequest);

        List<FaceMatch> faceImageMatches = searchFacesByImageResult.getFaceMatches();
        if(faceImageMatches.isEmpty()){
            logger.debug("No face found");
            return false;
        }
        logger.info("Found matching "+faceImageMatches.size()+" images in the collection ");

        for (FaceMatch face: faceImageMatches) {
            String faceId = face.getFace().getExternalImageId();
            logger.debug("Found face id "+faceId);
            if(userName.equalsIgnoreCase(faceId)){
                return true;
            }
        }

        return false;
    }


    public boolean validateFace(String username,String base64Image) {

        Image image = getImage(base64Image);
        try {
            return searchInCollection(image, username);
        } catch (JsonProcessingException e) {
            logger.error("action called ... context = " );
        }
        return false;
    }


    public void addFaceToCollection(String base64Image,String userName){
        IndexFacesRequest indexFacesRequest = new IndexFacesRequest()
                .withImage(getImage(base64Image))
                .withQualityFilter(QualityFilter.AUTO)
                .withMaxFaces(1)
                .withCollectionId(COLLECTION_ID)
                .withExternalImageId(userName)
                .withDetectionAttributes("DEFAULT");

//        createCollection(rekognitionClient);

        IndexFacesResult indexFacesResult = rekognitionClient.indexFaces(indexFacesRequest);

        System.out.println("Faces indexed:");
        List<FaceRecord> faceRecords = indexFacesResult.getFaceRecords();
        for (FaceRecord faceRecord : faceRecords) {
            logger.info("  Face ID: " + faceRecord.getFace().getFaceId());
            logger.info("  Location:" + faceRecord.getFaceDetail().getBoundingBox().toString());
        }
    }

}
