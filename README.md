# Bibliothèque - Projet Java Orienté Objet

Application de gestion de bibliothèque scolaire développée en Java 17 avec JavaFX et Maven.

Le projet permet la gestion complète des livres, des usagers, des emprunts et des retours, ainsi que la persistance des données au format JSON.

---

# Technologies utilisées

* Java 17
* JavaFX 21
* Maven
* Gson (JSON)
* JUnit 5
* ExecutorService
* AtomicInteger
* API Stream Java

---

# Fonctionnalités

## Gestion des livres

* Ajouter un livre
* Modifier un livre
* Supprimer un livre
* Gestion des exemplaires physiques
* Gestion des états :

  * Disponible
  * Emprunté
  * À réparer

## Gestion des usagers

* Étudiant
* Professeur
* Visiteur

| Type       | Limite d'emprunts | Durée d'emprunt |
| ---------- | ----------------- | --------------- |
| Étudiant   | 3 livres          | 14 jours        |
| Professeur | 6 livres          | 30 jours        |
| Visiteur   | 1 livre           | 7 jours         |

---

# Emprunts et retours

* Emprunt sécurisé
* Validation des règles métier
* Gestion des retards
* Gestion des réparations
* Réparation automatique après 3 jours ouvrables

---

# Programmation fonctionnelle

Le projet utilise l'API Stream Java :

* filter
* map
* sorted
* groupingBy
* Collectors
* Optional

---

# Persistance JSON

Les données sont sauvegardées automatiquement dans :

```text
data/bibliotheque.json
```

Les informations sauvegardées comprennent :

* livres
* usagers
* emprunts
* date courante
* compteur global d'emprunts

---

# Importation MARC ISO2709

Le projet permet l'importation de fichiers :

```text
.iso2709
```

Informations extraites :

* titre
* auteur
* ISBN

Les ouvrages importés sont automatiquement ajoutés à la bibliothèque avec leurs exemplaires associés.

---

# Structure du projet

```text
bibliotheque/
│
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/example/bibliotheque/
│   │   │       ├── controller/
│   │   │       ├── model/
│   │   │       ├── service/
│   │   │       └── view/
│   │   │
│   │   └── resources/
│   │
│   └── test/
│       └── java/
│           └── com/example/bibliotheque/service/
│
├── data/
│   └── bibliotheque.json
│
├── pom.xml
├── module-info.java
└── README.md
```

---

# Prérequis

* JDK 17 ou supérieur
* Maven 3.9 ou supérieur
* IntelliJ IDEA (recommandé)

---

# Compilation

```bash
mvn clean package
```

Cette commande :

* compile le projet ;
* exécute les tests unitaires ;
* génère le fichier JAR dans le dossier `target`.

---

# Exécution avec Maven

```bash
mvn javafx:run
```

---

# Exécution avec IntelliJ IDEA

1. Cloner le dépôt Git :

```bash
git clone https://github.com/Kitten-01/bibliotheque.git
```

2. Ouvrir le projet dans IntelliJ IDEA.

3. Attendre l'importation automatique des dépendances Maven.

4. Vérifier que le JDK 17 est sélectionné.

5. Exécuter la classe :

```text
com.example.bibliotheque.Main
```

L'application doit démarrer sans modification du code source ni configuration supplémentaire.

---

# Tests unitaires

Exécution :

```bash
mvn test
```

Résultat actuel :

```text
Tests run: 7
Failures: 0
Errors: 0
Skipped: 0
```

Les tests couvrent notamment :

* l'ajout de livres ;
* l'ajout d'usagers ;
* les emprunts ;
* les retours ;
* les limites d'emprunt ;
* la disponibilité des ouvrages ;
* les utilitaires de gestion des dates.

---

# Synchronisation et multithreading

Le projet respecte les contraintes de programmation concurrente :

* utilisation de `synchronized`
* utilisation de `ExecutorService`
* utilisation de `Callable`
* utilisation de `Runnable`
* compteur global avec `AtomicInteger`

Aucune collection concurrente n'est utilisée.

---

# Modularisation Java

Le projet utilise le système de modules Java :

```text
module-info.java
```

Les packages nécessaires sont exportés et configurés pour :

* JavaFX
* Gson

---

# Gestion du projet avec Git

Le développement a été réalisé avec Git et GitHub :

* branche principale `main`
* branche `feature-readme`
* branche `feature-tests`
* commits progressifs
* fusion des fonctionnalités dans `main`
* conservation des branches sur le dépôt distant pour l'évaluation

Le dépôt contient :

* le code source complet ;
* les tests unitaires ;
* le fichier de sauvegarde JSON ;
* le fichier `.gitignore` ;
* la documentation du projet.

---

# Auteur

Projet réalisé dans le cadre d'un cours de programmation orientée objet.
