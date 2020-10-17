package com.ksr.api.client.spotify.authorization;

import com.ksr.kafka.producer.spotify.AppConfig;
import com.typesafe.config.ConfigFactory;
import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.requests.authorization.client_credentials.ClientCredentialsRequest;
import org.apache.hc.core5.http.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class ClientCredential {
    private Logger log = LoggerFactory.getLogger(ClientCredential.class.getSimpleName());

    private static final ClientCredential clientCredentials = new ClientCredential( );
    private static  ClientCredentialsRequest clientCredentialsRequest;

    private ClientCredential() {
        AppConfig appConfig = new AppConfig(ConfigFactory.load());
        SpotifyApi spotifyApi = new SpotifyApi.Builder()
                .setClientId(appConfig.getClientId())
                .setClientSecret(appConfig.getClientSecret())
                .build();
         clientCredentialsRequest =
                spotifyApi.clientCredentials()
                        .build();
    }

    public String getCredentials() {
        try {
            final com.wrapper.spotify.model_objects.credentials.ClientCredentials clientCredentials = clientCredentialsRequest.execute();
            return clientCredentials.getAccessToken();
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            log.info("Error: " + e.getMessage());
            return "";
        }
    }

    public static ClientCredential getInstance() {
        return clientCredentials;
    }

}
