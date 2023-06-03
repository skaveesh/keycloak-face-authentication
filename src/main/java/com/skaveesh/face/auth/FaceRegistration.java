package com.skaveesh.face.auth;

import com.skaveesh.face.auth.aws.AWSService;
import org.jboss.logging.Logger;
import org.keycloak.authentication.RequiredActionContext;
import org.keycloak.authentication.RequiredActionProvider;
import org.keycloak.common.util.Time;
import org.keycloak.models.UserModel;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import java.util.Collections;

public class FaceRegistration implements RequiredActionProvider {

    public static final String PROVIDER_ID = "face-register";
    private static final String USER_ATTRIBUTE = PROVIDER_ID;

    private static final Logger logger = Logger.getLogger(FaceRegistration.class);

    private final AWSService awsService;

    public FaceRegistration(AWSService awsService) {
        this.awsService = awsService;
    }

    @Override
    public void evaluateTriggers(RequiredActionContext requiredActionContext) {
        logger.info("evaluate trigger face registration");
    }

    @Override
    public void requiredActionChallenge(RequiredActionContext context) {
        logger.info("required action challenge face registration");

        Response challenge = context.form().createForm("face-register.ftl");
        context.challenge(challenge);
    }

    @Override
    public void processAction(RequiredActionContext context) {
        logger.info("process action face registration");

        UserModel user = context.getUser();
        if (context.getHttpRequest().getDecodedFormParameters().containsKey("cancel")) {
            user.removeAttribute(USER_ATTRIBUTE);
            context.failure();
            return;
        }

        MultivaluedMap<String, String> formData = context.getHttpRequest().getDecodedFormParameters();
        String imageData = formData.getFirst("imageCanvas").split(",")[1];
        awsService.addFaceToCollection(imageData,user.getUsername());
        user.setAttribute(USER_ATTRIBUTE, Collections.singletonList(Integer.toString(Time.currentTime())));

        context.success();
    }

    @Override
    public void close() {
        logger.info("face registration close");
    }
}
