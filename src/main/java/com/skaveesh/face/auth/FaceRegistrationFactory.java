package com.skaveesh.face.auth;

import com.skaveesh.face.auth.aws.AWSService;
import org.keycloak.Config;
import org.keycloak.authentication.RequiredActionFactory;
import org.keycloak.authentication.RequiredActionProvider;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;

public class FaceRegistrationFactory implements RequiredActionFactory {

    private static final FaceRegistration SINGLETON = new FaceRegistration(new AWSService());

    @Override
    public String getDisplayText() {
        return "Face Registration";
    }

    @Override
    public RequiredActionProvider create(KeycloakSession keycloakSession) {
        return SINGLETON;
    }

    @Override
    public void init(Config.Scope scope) {

    }

    @Override
    public void postInit(KeycloakSessionFactory keycloakSessionFactory) {

    }

    @Override
    public void close() {

    }

    @Override
    public String getId() {
        return FaceRegistration.PROVIDER_ID;
    }
}
