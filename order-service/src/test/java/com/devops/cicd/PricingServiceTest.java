package com.devops.cicd;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

class PricingServiceTest {

    private final PricingConfig fakeConfig = new PricingConfig(20.0, 50.0);
    private final PricingService service = new PricingService(fakeConfig);

    @Test
    @DisplayName("La TVA de 20% doit être correctement appliquée")
    void shouldApplyVat() {
        double result = service.applyVat(100.0);
        assertEquals(120.0, result, "Le montant TTC devrait être de 120.0 pour 100.0 HT");
    }

    @Test
    @DisplayName("La remise VIP de 10% doit s'appliquer uniquement aux clients VIP")
    void shouldApplyVipDiscountOnlyForVip() {
        assertAll("Remise VIP",
                () -> assertEquals(90.0, service.applyVipDiscount(100.0, true), "Le VIP doit avoir 10% de remise"),
                () -> assertEquals(100.0, service.applyVipDiscount(100.0, false), "Le non-VIP ne doit avoir aucune remise")
        );
    }

    @Test
    @DisplayName("Les frais de port doivent être gratuits au-dessus du seuil")
    void shouldCalculateShippingCost() {
        assertAll("Frais de livraison",
                () -> assertEquals(0.0, service.shippingCost(50.0), "Gratuit à partir de 50€"),
                () -> assertEquals(4.99, service.shippingCost(49.99), "4.99€ en dessous de 50€")
        );
    }

    @Test
    @DisplayName("Le calcul final doit suivre l'ordre : TVA -> VIP -> Livraison")
    void shouldCalculateFinalTotalCorrectly() {
        // Détail du calcul pour 100€ HT, VIP :
        // 1. HT(100) + TVA(20%) = 120€
        // 2. VIP(120 - 10%) = 108€
        // 3. Livraison(120 >= 50) = 0€
        // Total = 108.00€
        double totalVip = service.finalTotal(100.0, true);
        assertEquals(108.0, totalVip, "Le total final pour un VIP avec 100€ HT devrait être 108.0");

        // Détail pour 10€ HT, non-VIP :
        // 1. HT(10) + TVA(20%) = 12€
        // 2. Non-VIP = 12€
        // 3. Livraison(12 < 50) = 4.99€
        // Total = 16.99€
        double totalStandard = service.finalTotal(10.0, false);
        assertEquals(16.99, totalStandard, "Le total pour un non-VIP avec 10€ HT devrait être 16.99");
    }
}