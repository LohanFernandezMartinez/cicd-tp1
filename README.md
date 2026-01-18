# TP – Fondamentaux CI/CD & Automatisation des Tests

## Contexte
Dans ce TP, vous allez mettre en place les fondations de l’automatisation des tests et comprendre le rôle d’une pipeline
CI/CD dans un projet Java.

Vous travaillerez sur un mini moteur de calcul de prix (pricing engine) utilisé dans un contexte e-commerce.
Chaque règle métier devra être automatiquement validée par des tests, et ces tests seront exécutés à chaque commit via une pipeline CI.

### Objectif final :
Aucun code ne doit être livré sans passer par les tests.


## Objectifs pédagogiques
À l’issue de ce TP, vous serez capables de :
* Comprendre le fonctionnement d’une pipeline CI/CD
* Écrire et exécuter des tests unitaires automatisés
* Appliquer une démarche TDD (Test Driven Development)
* Comprendre la différence entre :
    * tests unitaires
    * tests d’intégration
* Comprendre pourquoi et comment un build CI échoue

## Environnement technique
* Java 11+
* Maven
* JUnit 5
* Git
* GitHub ou GitLab
* IDE de votre choix

## Structure attendue du projet
```bash
order-service
├── pom.xml
└── src
    ├── main
    │   └── java/com/devops/cicd
    │       ├── PricingService.java
    │       ├── PricingConfig.java
    │       ├── PricingConfigLoader.java
    │       └── PasswordPolicy.java
    └── test
        ├── java/com/devops/cicd
        │   ├── PricingServiceTest.java
        │   ├── PricingIntegrationTest.java
        │   └── PasswordPolicyTest.java
        └── resources
            └── app.properties
```

## Partie A – Logique métier puis tests unitaires
### Contexte métier

Vous devez implémenter un service de calcul de prix avec les règles suivantes :

Règles métier
1. Remise VIP
    * Si le client est VIP → remise de 10 %
    * Sinon → aucune remise
2. Frais de livraison
    * Offerts si le montant est ≥ 50 €
    * Sinon → 4.99 €
3. TVA
    * Appliquer un taux de TVA (ex: 20 %) sur le montant hors taxe
4. Total final
    * Calculer le montant TTC
    * Appliquer la remise VIP
    * Ajouter les frais de livraison


### Étape 1 – Créer la configuration métier
Créez la classe PricingConfig qui contient :
* le taux de TVA *vatRate*
* le seuil de livraison gratuite *freeShippingThreshold*
* et les getters pour récupérer ces valeurs

```java
private final double vatRate;
private final double freeShippingThreshold;

public double getVatRate();
public double getFreeShippingThreshold() ;
```

*Cette classe représente une dépendance du service métier.*

### Étape 2 – Implémenter PricingService
PricingService doit :
* recevoir un PricingConfig via son constructeur
* utiliser cette configuration pour effectuer les calculs

```java

    public PricingService(PricingConfig config) ;

    public double applyVat(double amountExclVat) ;

    public double applyVipDiscount(double amount, boolean vip);

    public double shippingCost(double amount) ;

    /**
     * - TVA appliquée d'abord : HT -> TTC
     * - remise VIP appliquée sur TTC
     * - frais de livraison ajoutés ensuite (calculés sur TTC)
     */
    public double finalTotal(double amountExclVat, boolean vip) ;
```

### Étape 3 – Écrire les tests unitaires

Dans PricingServiceTest :
* Écrivez un test par règle métier
* Fournissez une configuration contrôlée (valeurs codées en dur)
* Les tests doivent :
    * décrire le comportement attendu
    * échouer tant que l’implémentation n’est pas correcte

*💡 Un test = une règle métier*

### Étape 4 – Lancer les tests
```bash
mvn clean test
```
Résultat attendu
* Les tests unitaires passent
* Le build est SUCCESS


## Partie B – Test d’intégration : composants réels ensemble
### Objectif
Comprendre ce qui distingue réellement un test d’intégration d’un test unitaire.

### Principe du test d’intégration
Dans cette partie, vous allez tester :
* le chargement réel d’une configuration depuis un fichier
* la logique métier complète
* la collaboration entre plusieurs classes

### Étape 1 – Ajouter une configuration réelle
Créez le fichier src/test/resources/app.properties :
```ini
vatRate=20
freeShippingThreshold=50
```

### Étape 2 – Charger la configuration
Créez la classe *PricingConfigLoader* :
* elle lit le fichier app.properties
* elle crée un objet PricingConfig à partir du fichier

```java
public class PricingConfigLoader {

    public PricingConfig load() {
        //TODo
    }

    private String required(Properties props, String key) {
        //TODO
    }
}
```

### Étape 3 – Écrire le test d’intégration
Dans *PricingIntegrationTest* dans src/test/:
* Chargez la configuration réelle depuis le fichier
* Instanciez le PricingService avec cette configuration
* Testez le scénario métier complet
  Exemple :
* montant HT = 100
* TVA = 20 %
* client VIP
* livraison gratuite

*Un test d’intégration vérifie que plusieurs composants réels fonctionnent correctement ensemble.*

```java
class PricingIntegrationTest {

    @Test
    void fullPricingFlow_withRealConfigFile() {
        //TODO
    }
}
```

## Partie C – Tests d’abord (TDD)
Dans cette partie, vous appliquerez une approche Test Driven Development.

### Contexte métier
Vous devez implémenter une politique de mot de passe sécurisé.

Règles :
* Un mot de passe est considéré comme fort s’il contient :
* au moins 8 caractères
* au moins une majuscule
* au moins une minuscule
* au moins un chiffre
* au moins un caractère spécial

### Étape 1 – Écrire uniquement les tests
Créez PasswordPolicyTest dans src/test/ :
* La classe PasswordPolicy n’existe pas encore
* Les tests doivent décrire les règles de validation

*Le projet ne compile pas : c’est normal (phase RED).*

```java
package com.devops.cicd;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PasswordPolicyTest {
    //TODO
}

```
### Étape 2 – Créer la classe minimale
Créez PasswordPolicy dans src/main/ avec une méthode statique :
```java
public class PasswordPolicy {

    public static boolean isStrong(String password) {
        return password != null;
    }
}
```
Retournez une valeur simple pour permettre la compilation.

Lancer les tests uniquement pour la class *PasswordPolicyTest*
```bash
mvn test -Dtest=com.devops.cicd.PasswordPolicyTest
```
Résultat attendu
* Les tests unitaires échouent
* Le build est FAILURE

### Étape 3 – Implémenter la logique
Implémentez la méthode *isStrong* jusqu’à ce que tous les tests passent.

Résultat attendu
* Les tests unitaires passent
* Le build est SUCCESS

## Partie D – Pipeline CI
### Objectif
Automatiser l’exécution des tests à chaque commit.

### Étape 1 – Créer un dépôt Git
* Initialisez un dépôt Git
* Poussez le projet sur GitHub

### Étape 2 – Ajouter une pipeline CI
#### GitHub Actions
Créer .github/workflows/ci.yml

### GitLab CI
Créer .gitlab-ci.yml

La pipeline doit contenir au moins deux jobs :
1. unit-tests
    * exécute uniquement les tests unitaires
2. integration-tests
    * dépend du job unit-tests
    * exécute les tests d’intégration

*Si unit-tests échoue, integration-tests ne démarre pas.*

## BONUS – Qualité de code dans la pipeline CI (niveau avancé)
### Objectifs du bonus
* Comprendre ce qu’on appelle la qualité de code
* Découvrir des outils utilisés en entreprise pour :
    * imposer un style de code cohérent
    * détecter des bugs potentiels
* Ajouter un troisième job dans la pipeline CI
* Observer l’impact d’une règle de qualité sur la livraison

### Contexte
Jusqu’ici, la pipeline vérifie :
* que le code fonctionne (tests unitaires)
* que l’ensemble fonctionne (tests d’intégration)

En entreprise, ce n’est pas suffisant.

Un code peut :
* passer les tests
* mais être difficile à maintenir
* ou contenir des bugs subtils

C’est là qu’interviennent les outils de qualité statique.

### Étape 1 – Ajouter les outils de qualité au projet
Ajoutez dans le pom.xml les plugins :
* maven-checkstyle-plugin
* spotbugs-maven-plugin
```xml
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-checkstyle-plugin</artifactId>
                <version>3.4.0</version>
                <configuration>
                    <configLocation>checkstyle.xml</configLocation>
                    <encoding>UTF-8</encoding>
                    <consoleOutput>true</consoleOutput>
                    <failsOnError>true</failsOnError>
                    <includeTestSourceDirectory>false</includeTestSourceDirectory>
                </configuration>
                <executions>
                    <execution>
                        <id>checkstyle</id>
                        <phase>verify</phase>
                        <goals>
                            <goal>check</goal>
                        </goals>
                    </execution>
                </executions>
            </plugin>

            <plugin>
                <groupId>com.github.spotbugs</groupId>
                <artifactId>spotbugs-maven-plugin</artifactId>
                <version>4.8.6.4</version>
                <configuration>
                    <effort>Max</effort>
                    <threshold>Low</threshold>
                    <failOnError>true</failOnError>
                </configuration>
                <executions>
                    <execution>
                        <id>spotbugs</id>
                        <phase>verify</phase>
                        <goals>
                            <goal>check</goal>
                        </goals>
                    </execution>
                </executions>
            </plugin>
```

Ces plugins doivent :
* s’exécuter lors de la phase verify
* faire échouer le build en cas de problème

### Étape 2 – Ajouter les règles Checkstyle
Créez un fichier checkstyle.xml à la racine du projet.
```xml
<?xml version="1.0"?>
<!DOCTYPE module PUBLIC
        "-//Checkstyle//DTD Checkstyle Configuration 1.3//EN"
        "https://checkstyle.org/dtds/configuration_1_3.dtd">

<module name="Checker">
    <module name="TreeWalker">

        <!-- Indentation / lisibilité -->
        <module name="NeedBraces"/>
        <module name="AvoidNestedBlocks"/>
        <module name="EmptyBlock">
            <property name="option" value="text"/>
        </module>

        <!-- Nommage basique -->
        <module name="TypeName"/>
        <module name="MethodName">
            <property name="format" value="^[a-z][a-zA-Z0-9_]*$"/>
        </module>
        <module name="ParameterName"/>
        <module name="LocalVariableName"/>

        <!-- Bonnes pratiques -->
        <!-- <module name="FinalParameters"/> -->
        <module name="UnusedImports"/>
        <module name="AvoidStarImport"/>
    </module>

    <!-- Longueur ligne (souple) -->
    <module name="LineLength">
        <property name="max" value="140"/>
        <property name="ignorePattern" value="^package|^import|http://|https://"/>
    </module>
</module>
```

Ce fichier définit :
* des règles de nommage
* des règles de lisibilité
* des règles de bonnes pratiques

*L’objectif n’est pas de “punir”, mais de standardiser le code.*

### Étape 3 – Tester la qualité en local
Avant la CI, vérifiez localement :
```bash
mvn verify -DskipTests
```
Résultat attendu
* le build échoue si une règle de qualité n’est pas respectée
* le build passe si le code est propre

### Étape 4 – Ajouter un job quality dans la pipeline CI
Modifiez votre pipeline CI pour ajouter un troisième job :
1. unit-tests
2. quality
3. integration-tests
   Règles de dépendance
* quality ne s’exécute que si les tests unitaires passent
* integration-tests ne s’exécute que si quality réussit

*La pipeline devient une chaîne de validation progressive.*