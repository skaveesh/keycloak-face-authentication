package com.skaveesh.face.auth;

import com.skaveesh.face.auth.aws.AWSService;
import org.jboss.logging.Logger;
import org.keycloak.Config;
import org.keycloak.authentication.Authenticator;
import org.keycloak.authentication.AuthenticatorFactory;
import org.keycloak.authentication.ConfigurableAuthenticatorFactory;
import org.keycloak.models.AuthenticationExecutionModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.provider.ProviderConfigProperty;

import java.util.ArrayList;
import java.util.List;

public class FaceAuthenticatorFactory implements AuthenticatorFactory, ConfigurableAuthenticatorFactory {

    public static final String PROVIDER_ID = "face-authentication";
    public static final AuthenticationExecutionModel.Requirement[] REQUIREMENT_CHOICES = {
            AuthenticationExecutionModel.Requirement.REQUIRED,
            AuthenticationExecutionModel.Requirement.CONDITIONAL,
            AuthenticationExecutionModel.Requirement.DISABLED};
    private static final FaceAuthenticator SINGLETON = new FaceAuthenticator(new AWSService());
    private static Logger logger = Logger.getLogger(FaceAuthenticatorFactory.class);

    public String getId() {
        logger.debug("getId called ... returning " + PROVIDER_ID);
        return PROVIDER_ID;
    }

    public Authenticator create(KeycloakSession session) {
        logger.debug("create called ... returning " + SINGLETON);
        return SINGLETON;
    }


    public AuthenticationExecutionModel.Requirement[] getRequirementChoices() {
        logger.debug("getRequirementChoices called ... returning " + REQUIREMENT_CHOICES);
        return REQUIREMENT_CHOICES;
    }

    public boolean isUserSetupAllowed() {
        logger.debug("isUserSetupAllowed called ... returning true");
        return true;
    }

    public boolean isConfigurable() {
        logger.debug("isConfigurable called ... returning true");
        return true;
    }

    public String getHelpText() {
        logger.debug("getHelpText called ...");
        return "Recognises Faces";
    }

    public String getDisplayType() {
        String result = "Face Authentication";
        logger.debug("getDisplayType called ... returning " + result);
        return result;
    }


    public List<ProviderConfigProperty> getConfigProperties() {

        return new ArrayList<ProviderConfigProperty>();
    }

    public void init(Config.Scope config) {
        logger.debug("init called ... config.scope = " + config);
    }

    public void postInit(KeycloakSessionFactory factory) {
        logger.debug("postInit called ... factory = " + factory);
    }

    public void close() {
        logger.debug("close called ...");
    }

    @Override
    public String getReferenceCategory() {
        // TODO Auto-generated method stub
        return "face-id";
    }
}
