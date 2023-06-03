package com.skaveesh.face.auth;

import com.skaveesh.face.auth.aws.AWSService;
import org.jboss.logging.Logger;
import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.authentication.AuthenticationFlowError;
import org.keycloak.authentication.Authenticator;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

public class FaceAuthenticator implements Authenticator {

    private static final String COLLECTION_ID = "MyCollection";
    private static Logger logger = Logger.getLogger(FaceAuthenticator.class);
    private final AWSService awsService;

    public FaceAuthenticator(AWSService awsService) {
        this.awsService = awsService;
    }

    @Override
    public void authenticate(AuthenticationFlowContext context) {
        logger.debug("authenticate called ... context = " + context);

        Response challenge = context.form().createForm("face-validation.ftl");
        context.challenge(challenge);
    }

    @Override
    public void action(AuthenticationFlowContext context) {
        logger.info("action called ... context = " + context);
        Response challenge = null;

        logger.info(context.getUser());

        String username = context.getUser().getUsername();
        MultivaluedMap<String, String> formData = context.getHttpRequest().getDecodedFormParameters();
        String imageData = formData.getFirst("imageCanvas").split(",")[1];

        if (awsService.validateFace(username,imageData)) {
            context.success();
        } else {
            challenge = context.form()
                    .setInfo("Cannot recognize user")
                    .createForm("face-validation.ftl");
            context.failureChallenge(AuthenticationFlowError.INVALID_USER, challenge);
        }
    }


    @Override
    public boolean requiresUser() {
        return false;
    }

    @Override
    public boolean configuredFor(KeycloakSession session, RealmModel realm, UserModel user) {
        logger.debug("configuredFor called ... session=" + session + ", realm=" + realm + ", user=" + user);
//  return session.userCredentialManager().isConfiguredFor(realm, user, "secret_question");
        return true;
    }

    @Override
    public void setRequiredActions(KeycloakSession keycloakSession, RealmModel realmModel, UserModel userModel) {

    }

    @Override
    public void close() {

    }
}
