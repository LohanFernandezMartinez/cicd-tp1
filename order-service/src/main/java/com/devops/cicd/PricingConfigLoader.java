package com.devops.cicd;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PricingConfigLoader {

    /**
     * Charge la configuration depuis le fichier app.properties situé dans le classpath.
     */
    public PricingConfig load() {
        Properties props = new Properties();

        // Lecture du fichier dans src/main/resources ou src/test/resources
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("app.properties")) {
            if (input == null) {
                throw new RuntimeException("Désolé, impossible de trouver le fichier app.properties");
            }

            // Chargement du fichier properties
            props.load(input);

            // Extraction et conversion des valeurs
            double vatRate = Double.parseDouble(required(props, "vatRate"));
            double freeShippingThreshold = Double.parseDouble(required(props, "freeShippingThreshold"));

            return new PricingConfig(vatRate, freeShippingThreshold);

        } catch (IOException ex) {
            throw new RuntimeException("Erreur lors du chargement de la configuration", ex);
        }
    }

    /**
     * Vérifie qu'une clé existe dans le fichier properties avant de la retourner.
     */
    private String required(Properties props, String key) {
        String value = props.getProperty(key);
        if (value == null || value.trim().isEmpty()) {
            throw new RuntimeException("La configuration '" + key + "' est manquante dans app.properties");
        }
        return value;
    }
}