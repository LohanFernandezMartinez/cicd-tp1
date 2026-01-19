package com.devops.cicd;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

class PricingIntegrationTest {

    @Test
    @DisplayName("Test d'intégration : Flux complet avec fichier de config réel")
    void fullPricingFlow_withRealConfigFile() {
        // 1. Initialisation des composants réels
        PricingConfigLoader loader = new PricingConfigLoader();

        // 2. Chargement de la configuration depuis src/test/resources/app.properties
        PricingConfig realConfig = loader.load();

        // 3. Injection de la config réelle dans le service
        PricingService service = new PricingService(realConfig);

        // 4. Scénario métier complet (Partie B - Étape 3)
        // Montant HT = 100, TVA = 20%, Client VIP, Livraison gratuite
        double amountExclVat = 100.0;
        boolean isVip = true;

        double finalResult = service.finalTotal(amountExclVat, isVip);

        // Vérification du résultat attendu :
        // 100€ + 20% TVA = 120€
        // 120€ - 10% remise VIP = 108€
        // 120€ >= 50€ (seuil) -> Livraison 0€
        // Total final = 108.00€
        assertEquals(108.0, finalResult, "Le calcul avec la config réelle du fichier devrait donner 108.0");
    }
}