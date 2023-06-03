# Keycloak Face Authentication

## Overview
- The main objective of our hack is to provide a more secure and efficient way of authenticating users with multi factor authentication (MFA).
- Implemented solution will allow users to use face recognition functionality as a two-factor authentication method.

## Objectives
- Implement factors for Keycloak
- Customize themes
- Create custom auth flow

![How this works](https://i.imgur.com/IxFr75o.png "How this works")

## Keycloak IDP Functionalities
- Single-Sign On
- Standard Protocols
- Centralized management
- Identity Brokering

![Architecture](https://i.imgur.com/q3A1jhf.png "Architecture")

## Implementation Scope
- Developed a new face recognition factor for MFA as a plugin for Keycloak.
- Implemented a registration workflow with having ability to capture the user’s face.
- Authenticate users with face recognition as a two-factor authentication.

## Workflow

![Workflow](https://i.imgur.com/1gP3NFE.png "Workflow")

## Benefits & Use Cases
- Provide a more secure and seamless user authentication experience.
- Provide hassle free adaption since no special devices are required.
- Can be used as an authentication step in online examination registration.
- Use this plugin as a two-factor authentication option for client applications.
- Use facial recognition plugin for any Keycloak based identity management solutions.

## Technologies
- Keycloak
- AWS Rekognition
- Face-api.js
- Java, Maven

## Demo

![Demo GIF](https://i.imgur.com/8998QWg.gif "Demo GIF")
