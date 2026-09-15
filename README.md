# TP Selenium - BDD et CI/CD

## 1. Contexte

Ce projet automatise le parcours de connexion de l'application OrangeHRM Demo :

- URL : https://opensource-demo.orangehrmlive.com/web/index.php/auth/login
- Utilisateur de démonstration : `Admin`
- Mot de passe de démonstration : `admin123`
- Outils : Java, Maven, Selenium WebDriver, Cucumber, JUnit 5, Selenium Grid et Jenkins

> Les identifiants ci-dessus sont ceux du site de démonstration public. Ils ne doivent pas être utilisés comme modèle pour une application réelle.

## 2. User Story challengée

**En tant qu'utilisateur connecté,**
**je veux accéder à mon dashboard,**
**afin de suivre l'état du personnel de l'entreprise.**

### Critères d'acceptation

1. Après une connexion réussie, l'utilisateur est redirigé vers le dashboard.
2. Le dashboard affiche les widgets `My Actions` et `Quick Launch`.

### Points à préciser ou à challenger

- Le terme « utilisateur connecté » doit être remplacé par un rôle précis, par exemple administrateur ou collaborateur.
- La redirection doit être vérifiée par l'URL ou un élément stable du dashboard, et pas uniquement par l'affichage des widgets.
- Il faut préciser le comportement attendu avec des identifiants invalides, des champs vides et une indisponibilité du site.
- Il faut préciser si les widgets doivent être visibles, actifs et accessibles, ou seulement présents dans le DOM.
- Les critères devraient définir un délai maximal de redirection et le comportement attendu sur ordinateur et mobile.
- Pour un vrai projet, les identifiants ne doivent pas être écrits en clair dans le code ou les logs. Ils doivent être fournis par des variables sécurisées de Jenkins.

## 3. Cas de test

| Champ | Valeur |
|---|---|
| Identifiant | CT-LOGIN-001 |
| Titre | Connexion réussie et accès au dashboard |
| Préconditions | Le site est disponible et Selenium Grid est démarré |
| Données | `Admin` / `admin123` |
| Priorité | Haute |
| Type | Test fonctionnel automatisé, positif |

### Étapes et résultats attendus

| # | Étape | Résultat attendu |
|---:|---|---|
| 1 | Ouvrir l'URL de connexion OrangeHRM | La page de connexion est affichée |
| 2 | Saisir `Admin` dans le champ Username | Le champ contient `Admin` |
| 3 | Saisir `admin123` dans le champ Password | Le mot de passe est saisi et masqué |
| 4 | Cliquer sur `Login` | La connexion est acceptée |
| 5 | Contrôler l'URL et le dashboard | L'utilisateur arrive sur `/dashboard/index` |
| 6 | Contrôler `My Actions` et `Quick Launch` | Les deux widgets sont visibles |

## 4. Automatisation BDD avec Gherkin

Le scénario automatisé se trouve dans [`demo/src/test/resources/com/example/Login.feature`](demo/src/test/resources/com/example/Login.feature).

```gherkin
Feature: Connexion OrangeHRM

  Background:
    Given go to "https://opensource-demo.orangehrmlive.com/web/index.php/auth/login"

  Scenario: User Story
    When Enter username "Admin" and password "admin123"
    And Click Login button
    Then Verify Existance of "My Actions" and "Quick Launch"
```

Les définitions des étapes sont dans [`demo/src/test/java/com/example/steps/StepLogin.java`](demo/src/test/java/com/example/steps/StepLogin.java). Le navigateur distant est initialisé et fermé dans [`demo/src/test/java/com/example/hooks/BaseTest.java`](demo/src/test/java/com/example/hooks/BaseTest.java).

### Limite actuelle de l'automatisation

Le scénario vérifie actuellement les deux widgets, mais ne vérifie pas explicitement l'URL du dashboard. La vérification des widgets utilise également leur position dans la liste retournée par Selenium. Ces points sont à durcir avant de considérer la couverture comme complète.

## 5. Rapport de test

Le runner Cucumber est [`demo/src/test/java/com/example/RunCucumberTest.java`](demo/src/test/java/com/example/RunCucumberTest.java). Il génère :

- rapport HTML : [`demo/target/html_report.html`](demo/target/html_report.html)
- rapport JSON : [`demo/target/junit_report.xml`](demo/target/junit_report.xml)
- rapport Surefire : [`demo/target/surefire-reports/`](demo/target/surefire-reports/)

Commande de génération :

```bash
cd demo
mvn clean test
```

### Résultat observé dans le rapport fourni

Le fichier Surefire existant indique : `Tests run: 1, Failures: 0, Errors: 1`.

L'erreur est une `SessionNotCreatedException` provoquée par l'impossibilité de résoudre `selenium-hub` depuis l'environnement local Windows. Il s'agit d'un problème de configuration d'exécution, pas d'une preuve que la connexion OrangeHRM est fonctionnellement incorrecte. Le test doit être exécuté dans le réseau Docker prévu par le projet, ou la configuration WebDriver doit être adaptée à une exécution locale.

## 6. Ticket de bug

### BUG-001 - Le test ne peut pas créer une session Selenium depuis l'environnement local

- **Type :** anomalie technique d'automatisation
- **Priorité :** Haute pour l'exécution locale
- **Environnement :** Windows, Maven, Selenium Grid dans Docker
- **Précondition :** lancer `mvn clean test` depuis la machine hôte
- **Étapes de reproduction :**
  1. Démarrer ou ne pas démarrer Docker selon l'environnement.
  2. Depuis `demo`, exécuter `mvn clean test`.
  3. Observer la création du `RemoteWebDriver`.
- **Résultat actuel :** `selenium-hub` est introuvable et la session Chrome ne démarre pas.
- **Résultat attendu :** le test doit utiliser une adresse résolvable depuis son environnement d'exécution, par exemple `selenium-hub` dans le réseau Docker ou `localhost` depuis la machine hôte.
- **Cause probable :** l'URL `http://selenium-hub:4444/wd/hub` est codée en dur dans `BaseTest.java`.
- **Correction proposée :** rendre l'URL configurable avec une propriété Maven ou une variable d'environnement, puis utiliser `selenium-hub` dans Jenkins/Docker et `localhost` en local.

Aucun bug fonctionnel OrangeHRM ne peut être déclaré sur la base du rapport fourni, car le navigateur n'a pas atteint l'écran de connexion.

## 7. CI/CD avec Jenkins et Docker

Le pipeline est défini dans [`Jenkinsfile`](Jenkinsfile). Il :

1. supprime l'ancien conteneur Selenium Hub si nécessaire ;
2. arrête les services Docker existants ;
3. démarre Selenium Grid avec `docker compose up -d` ;
4. affiche l'état des conteneurs ;
5. utilise l'image Maven `maven:3.8.3-openjdk-17` ;
6. exécute `cd demo && mvn clean test` dans le réseau `orangehrm_default`.

Le fichier [`docker-compose.yml`](docker-compose.yml) fournit un hub Selenium ainsi que des nœuds Chrome, Edge et Firefox. Le scénario actuel utilise Chrome via `RemoteWebDriver`.

### Exemple de déroulement Jenkins

```text
Checkout du projet
  -> Démarrage Selenium Grid
  -> Exécution des tests Cucumber/Maven
  -> Génération des rapports HTML, JSON et Surefire
  -> Nettoyage Docker recommandé dans une étape post-build
```

Pour une CI/CD plus robuste, il est recommandé d'ajouter une étape `post { always { ... } }` afin d'archiver les rapports et d'arrêter Docker même lorsque les tests échouent. Il est également recommandé d'attendre que le hub Selenium soit réellement prêt avant de lancer Maven.

## 8. Structure principale

```text
BDD_CICD/
├── docker-compose.yml
├── Jenkinsfile
├── README.md
└── demo/
    ├── pom.xml
    └── src/test/
        ├── java/com/example/
        │   ├── RunCucumberTest.java
        │   ├── hooks/BaseTest.java
        │   └── steps/StepLogin.java
        └── resources/com/example/Login.feature
```
