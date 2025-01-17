# Base Spring Boot Application

Cette application Spring Boot offre une structure de base pour gérer des entités, y compris un repository générique, et est conçue pour être étendue et utilisée comme modèle pour des projets plus complexes.

## Table des matières

1. [Description](#description)
2. [Prérequis](#prérequis)
3. [Installation](#installation)
4. [Structure du projet](#structure-du-projet)
5. [Configuration](#configuration)
6. [Utilisation](#utilisation)
7. [Tests](#tests)
8. [Contribuer](#contribuer)
9. [Licences](#licences)

## Description

Cette application Spring Boot fournit une architecture de base avec les éléments suivants :

- **BaseEntity** : Une classe abstraite servant de base pour d'autres entités, avec un identifiant généré automatiquement.
- **BaseRepository** : Un repository générique permettant la gestion des entités étendues de `BaseEntity`.
- **TestEntity** : Une entité d'exemple pour démontrer l'utilisation des classes `BaseEntity` et `BaseRepository`.
- **TestEntityRepository** : Un repository étendant `BaseRepository` pour la gestion de l'entité `TestEntity`.

## Prérequis

Avant de commencer, assurez-vous d'avoir installé les outils suivants :

- **Java 21** ou supérieur
- **Maven** pour la gestion des dépendances
- **Spring Boot** (version 3.x ou supérieure)
- Une **base de données** configurée (par défaut, l'application utilise H2 en mémoire)

## Installation

1. Clonez ce repository :
   ```bash
   git clone https://github.com/NARIHY/Spring-starter-API.git
   ```

2. Accédez au répertoire du projet :
   ```bash
   cd Spring-starter-API
   ```

3. Utilisez Maven pour installer les dépendances :
   ```bash
   mvn install
   ```

4. Exécutez l'application Spring Boot :
   ```bash
   mvn spring-boot:run
   ```

L'application sera accessible à l'adresse suivante : `http://localhost:8080`.

## Structure du projet

Le projet est structuré de la manière suivante :

```
base-spring-boot-application/
│
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   ├── com/
│   │   │   │   ├── tm_service/
│   │   │   │   │   ├── com/
│   │   │   │   │   │   ├── tmoto/
│   │   │   │   │   │   │   ├── base/
│   │   │   │   │   │   │   │   ├── model/
│   │   │   │   │   │   │   │   │   ├── BaseEntity.java
│   │   │   │   │   │   │   │   ├── repository/
│   │   │   │   │   │   │   │   │   ├── BaseRepository.java
│   │   │   │   │   │   │   │   ├── microservice/
│   │   │   │   │   │   │   │   │   ├── model/
│   │   │   │   │   │   │   │   │   │   ├── TestEntity.java
│   │   │   │   │   │   │   │   │   ├── repository/
│   │   │   │   │   │   │   │   │   │   ├── TestEntityRepository.java
│   │   ├── resources/
│   │   │   ├── application.properties
│   │   │   ├── static/
│   │   │   ├── templates/
│   │   └── application.properties
└── pom.xml
```

## Configuration

L'application utilise Spring Boot avec les configurations suivantes par défaut :

- **Port** : 8080
- **Base de données** : H2 (en mémoire)

### Exemple de `application.properties` :

```properties
# Configuration de la base de données (H2 en mémoire)
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=password
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect

# Activer la console H2
spring.h2.console.enabled=true

# Configuration des logs
logging.level.org.springframework=INFO
logging.level.com.tm_service=DEBUG
```

## Utilisation

### Exposition des API REST :

Une fois l'application démarrée, elle expose des API pour interagir avec les entités. Par défaut, aucune API REST n'est définie, mais vous pouvez facilement les ajouter pour `TestEntity`.

Voici un exemple de contrôleur pour l'entité `TestEntity` :

```java
package com.tm_service.com.tmoto.microservice.controller;

import model.microservice.tmoto.com.base_spring_boot.com.TestEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/test-entity")
public class TestEntityController {

   @Autowired
   private TestEntityRepository testEntityRepository;

   @GetMapping
   public List<TestEntity> getAllEntities() {
      return testEntityRepository.findAll();
   }

   @PostMapping
   public TestEntity createEntity(@RequestBody TestEntity testEntity) {
      return testEntityRepository.save(testEntity);
   }

   @GetMapping("/{id}")
   public TestEntity getEntityById(@PathVariable Long id) {
      return testEntityRepository.findById(id).orElse(null);
   }

   @PutMapping("/{id}")
   public TestEntity updateEntity(@PathVariable Long id, @RequestBody TestEntity testEntity) {
      if (!testEntityRepository.existsById(id)) {
         return null;
      }
      testEntity.setId(id);
      return testEntityRepository.save(testEntity);
   }

   @DeleteMapping("/{id}")
   public void deleteEntity(@PathVariable Long id) {
      testEntityRepository.deleteById(id);
   }
}
```
## Auteur
**NARIHY**

## Contribuer

Les contributions sont les bienvenues ! Si vous avez des suggestions, des améliorations ou des corrections à apporter, ouvrez une **pull request** ou **issue**. Pour toute question ou suggestion, vous pouvez ouvrir une issue.

## Licences

Ce projet est sous **MIT License**. Consultez le fichier `LICENSE` pour plus de détails.

---

N'hésitez pas à personnaliser ce README en fonction de l'évolution de votre application et des fonctionnalités ajoutées.