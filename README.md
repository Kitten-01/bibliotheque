# Bibliothèque - Projet Java Orienté Objet

Application Java de gestion de bibliothèque scolaire développée avec Java 17, JavaFX et Maven.

Le projet permet :

* la gestion des livres et exemplaires,
* la gestion des usagers,
* les emprunts et retours,
* la persistance JSON,
* l’importation MARC ISO 2709,
* les statistiques,
* la simulation multithread,
* les recherches optimisées avec l’API Stream.

---

# Technologies utilisées

* Java 17
* JavaFX
* Maven
* Gson (JSON)
* JUnit 5
* ExecutorService
* AtomicInteger

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

Chaque type possède :

| Type       | Limite   | Durée    |
| ---------- | -------- | -------- |
| Étudiant   | 3 livres | 14 jours |
| Professeur | 6 livres | 30 jours |
| Visiteur   | 1 livre  | 7 jours  |

---

# Emprunts et retours

* Emprunt sécurisé multithread
* Validation des règles métier
* Impossible d’emprunter deux exemplaires du même ISBN
* Gestion des retards
* Gestion des réparations
* Réparation automatique après 3 jours ouvrables

---

# Programmation fonctionnelle

Le projet utilise l’API Stream Java :

* `filter`
* `map`
* `sorted`
* `groupingBy`
* `Collectors`
* `Optional`

---

# Persistance JSON

Les données sont sauvegardées automatiquement dans :

```text
bibliotheque.json
```

Contenu sauvegardé :

* livres
* usagers
* emprunts
* date courante
* compteur global d’emprunts

---

# Importation MARC ISO2709

Le projet supporte l’importation de fichiers :

```text
.iso2709
```

Le parsing extrait :

* titre
* auteur
* ISBN

Plusieurs exemplaires sont générés automatiquement selon la distribution demandée dans l’énoncé.

---

# Structure du projet

```text
src/
 └── main/
      ├── java/
      │    └── com/example/bibliotheque/
      │          ├── controller/
      │          ├── model/
      │          ├── persistence/
      │          ├── service/
      │          └── view/
      │
      └── resources/

 └── test/
      └── java/
```

---

# Exécution du projet

## Avec IntelliJ

1. Cloner le dépôt Git
2. Ouvrir le dossier dans IntelliJ
3. Attendre le chargement Maven
4. Lancer la classe :

```text
Main.java
```

---

# Exécution avec Maven

Compilation :

```bash
mvn clean install
```

Exécution :

```bash
mvn javafx:run
```

Tests :

```bash
mvn test
```

---

# Tests unitaires

Le projet contient :

* tests CRUD
* tests d’emprunts
* tests de saturation
* tests de recherche
* tests de statistiques
* tests JSON
* tests multithread
* tests de concurrence

---

# Synchronisation et multithreading

Le projet respecte les contraintes :

* aucune collection concurrente utilisée
* synchronisation avec `synchronized`
* utilisation de `ExecutorService`
* utilisation de `Callable`
* utilisation de `Runnable`
* compteur global avec `AtomicInteger`

---

# Git

Le développement a été réalisé avec :

* branches Git
* commits progressifs
* fusion régulière dans `main`

---

# Auteur

Projet réalisé dans le cadre du cours de programmation orientée objet.
