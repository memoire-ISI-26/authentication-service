# authentication-service

Ce microservice est responsable de l'**authentification** des utilisateurs de la plateforme (clients et administrateurs) et de la génération des jetons de sécurité JWT.

## ⚙️ Rôle et Fonctionnalités

- **Authentification double** :
  - Recherche d'un administrateur par son `username` via le proxy Feign `UserProxy`.
  - Recherche d'un client par son numéro de téléphone `number` (msisdn) si l'administrateur n'est pas trouvé.
- **Validation cryptographique** : Comparaison du mot de passe saisi avec le hash stocké en base de données à l'aide de `BCryptPasswordEncoder`.
- **Génération de JWT** : Création d'un token sécurisé contenant l'ID de l'utilisateur, son identifiant (username ou numéro) et son rôle (`CLIENT` ou `ADMINISTRATOR`).
- **Audit / Tracking** : Lors d'un succès de connexion, un événement de type `LOGIN` est envoyé de façon asynchrone et résiliente au `tracking-service`.

---

## 🔌 Configuration et Endpoints

- **Port par défaut** : `8081`
- **Technologie** : Spring Boot, Spring Security (Crypto), Spring Cloud Netflix Eureka Client, Feign Client, JWT

### Endpoints exposés :

#### 1. Connexion de l'utilisateur
* **URL** : `POST /auth/login`
* **Corps de la requête (JSON)** :
  ```json
  {
    "identifier": "771234567",
    "password": "mon_mot_de_passe"
  }
  ```
* **Réponse (JSON - 200 OK)** :
  ```json
  {
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "type": "Bearer",
    "role": "CLIENT"
  }
  ```

#### 2. Validation de jeton (Usage interne ou Gateway)
* **URL** : `GET /auth/validate?token=<jwt>`
* **Réponse (200 OK ou 401 Unauthorized)** : Retourne `"Token est valide"` ou `"Token est invalide ou expiré"`.

---

## 🔗 Interactions avec les autres services

Le service d'authentification communique avec d'autres services via Feign Client :
- **`UserProxy`** (cible `user-service`) : Pour vérifier l'existence de l'utilisateur et récupérer ses informations.
- **`TrackingProxy`** (cible `tracking-service`) : Pour enregistrer l'historique des connexions des utilisateurs dans MongoDB.
