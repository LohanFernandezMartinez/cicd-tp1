package com.devops.cicd;

import java.math.BigDecimal;
import java.math.RoundingMode;

public final class PricingService {

    private final PricingConfig config;

    public PricingService(PricingConfig config) {
        this.config = config;
    }

    public double applyVat(double amountExclVat) {
        return amountExclVat * (1 + config.getVatRate() / 100);
    }

    public double applyVipDiscount(double amount, boolean vip) {
        if (vip) {
            return amount * 0.90;
        }
        return amount;
    }

    public double shippingCost(double amount) {
        if (amount >= config.getFreeShippingThreshold()) {
            return 0.0;
        }
        return 4.99;
    }

    /**
     * Calcul final selon l'ordre du TP :
     * 1. TVA appliquée d'abord : HT -> TTC
     * 2. Remise VIP appliquée sur le TTC
     * 3. Frais de livraison ajoutés à la fin (calculés sur le TTC)
     */
    public double finalTotal(double amountExclVat, boolean vip) {

        double amountTtc = applyVat(amountExclVat);

        double amountAfterDiscount = applyVipDiscount(amountTtc, vip);

        double shipping = shippingCost(amountTtc);

        BigDecimal bd = BigDecimal.valueOf(amountAfterDiscount + shipping);
        return bd.setScale(2, RoundingMode.HALF_UP).doubleValue();
    }
}