# <img src="lg.png" width="40" align="center" /> ChoraleFBBT 

**ChoraleFBBT** est une application Android native conçue pour regrouper, organiser et synchroniser en temps réel l'ensemble du répertoire musical de la chorale au sein d'un groupe commun.

---

## 📌 Problématique
Actuellement, la gestion des chants de la chorale souffre de plusieurs contraintes :
- **Dispersion des médias :** Les enregistrements audio, accompagnements (playbacks) et paroles sont éparpillés entre WhatsApp, la galerie photo et des fichiers personnels.
- **Perte de données :** Les messages contenant des fichiers ou photos finissent par s'effacer ou devenir introuvables.
- **Problèmes de connexion :** Pas d'accès fiable aux partitions ou aux audios pendant les répétitions lorsqu'il n'y a pas de réseau internet.

---

## 💡 Solution & Fonctionnalités Prioritaires

L'application centralise toutes les ressources dans un groupe unique et partagé :

1. **Fiche d'un chant (Priorité 1) :**
   * Contient le **Titre**, les **Paroles** (saisie au clavier ou prise de photo), le fichier **Audio** (chant vocal) et le fichier **Playback** (accompagnement).
2. **Liste des chants (Priorité 2) :**
   * Moteur de recherche rapide et bouton d'action rapide `+ Ajouter un chant`.
3. **Lecteur intégré (Priorité 3) :**
   * Contrôles complets (Lecture, Pause, Avancer, Reculer) directement dans l'application.
4. **Synchronisation & Mode Hors-Ligne (Priorité 4) :**
   * **Automatique :** Tout ajout/modification fait par l'administrateur A est automatiquement reçu par l'utilisateur B (sans gestion complexe de base de données manuelle côté client).
   * **Fonctionnement hors-ligne :** Modification et consultation possibles sans internet ; les changements s'envoient automatiquement dès le retour de la connexion.
   * **Cache multimédia automatique :** Les fichiers audio et playbacks s'écoutent directement dans l'application sans téléchargement manuel préalable.
   * **Gestion des conflits :** La version la plus récente est toujours conservée, tout en gardant l'ancienne version dans l'historique.

---

## 👥 Rôles et Droits d'Accès

- **Administrateur :** Ajoute, modifie et supprime les fiches de chant.
- **Utilisateur (Choriste) :** Consulte les fiches (paroles, photos) et écoute les pistes audio/playback.

---

## 🚀 Démarrage rapide pour le binôme

### 1. Cloner le dépôt
```bash
git clone <URL_DU_DEPOT_GIT>
cd ChoraleFBBT