arc42 Projektbeschreibung – Themen-Explorer Service
==================================================

1. Einführung und Ziele
-----------------------
Die Fachabteilung erhält regelmäßig Daten von einem zentralen Remote-SFTP-Server.
Diese Daten werden bereits durch einen bestehenden Python-Service mit rclone
von Remote → lokal auf ein Netzlaufwerk gespiegelt.

Ziel des neuen Projekts ist es, die gespiegelt vorliegende Ordnerstruktur
(jedes Verzeichnis entspricht einem „Thema“) über einen modernen, sicheren
und erweiterbaren Spring Boot Service bereitzustellen:
- per REST API mit HATEOAS
- optional auch als Feed (Atom/RSS) für abonnierbare Änderungen

Zielgruppe: interne Fachabteilungen, ggf. externe Systeme mit Lesezugriff.

2. Randbedingungen
------------------
Technisch
- Java 25 / Spring Boot 3.5
- Zugriff auf lokales Netzlaufwerk (Linux mount oder Windows Share)
- REST API mit Spring HATEOAS
- Feed-Ausgabe via ROME (RSS/Atom)
- Security via Spring Security (Keycloak/OpenID Connect)

Organisatorisch
- Kein Schreibzugriff auf SFTP oder Netzlaufwerk (read-only)
- Betrieb in Container-Umgebung (Docker/Podman/Kubernetes)

3. Kontextabgrenzung
--------------------
Systemkontext (vereinfacht):

 Remote SFTP  -->  Python rclone Sync-Service  -->  Themen-Explorer (Spring Boot)
                                                       | REST HATEOAS API
                                                       | RSS/Atom Feed
                                                       v
                                              Fachabteilungen / Feedreader / UI

5. Bausteinsicht (grob)
-----------------------
- TopicController
  - stellt HATEOAS-Ressourcen bereit
  - listet Themen (Ordner) und Dateien
  - berücksichtigt konfigurierbares Schlüsselwort („ausgabe“) für flache Darstellung
- FeedGenerator
  - erstellt RSS/Atom-Feed aus aktuellen Dateien
- FileService
  - kapselt Zugriff auf Netzlaufwerk (java.nio.file.Files)
  - entscheidet: flach (bei „ausgabe“) oder baumförmig (bei allen anderen Strukturen)
- SecurityConfig
  - Authentifizierung/Autorisierung

6. Laufzeitsicht (Use Case: Themen-Liste abrufen)
-------------------------------------------------
1. Fachanwender ruft GET /topics auf
2. TopicController fragt per FileService die Ordnerstruktur ab
3. Falls ein Unterordner mit konfiguriertem Namen (z. B. „ausgabe“) existiert → Dateien werden flach dargestellt.
4. Ansonsten → baumförmige Darstellung (Ordner + Unterordner).
5. Ergebnis wird als HATEOAS-JSON zurückgegeben
6. Optional: Feed-Aufruf GET /feed liefert RSS/Atom

8. Querschnittliche Konzepte
----------------------------
- HATEOAS für navigierbare REST-API
- Feed (RSS/Atom) für abonnierbare Änderungen
- Konfigurierbarer Darstellungsmodus:
  - Schlüsselwort (Default: „ausgabe“) → flache Auflistung
  - ohne Schlüsselwort → baumförmige Darstellung
- Security: OpenID Connect Integration (z. B. Keycloak)
- Deployment: Container-Image, Konfiguration via Environment Variables (Pfad Netzlaufwerk, Security, Schlüsselwort, Logging)

11. Risiken
-----------
- Große Ordnerstrukturen könnten Performance belasten → evtl. Caching notwendig
- Feed-Größe bei vielen Dateien → evtl. Paginierung / Limitierung
- Abhängigkeit vom rclone-Sync (Zeitverzögerung)
- Unterschiedliche Tiefen der Verzeichnisstruktur könnten die API uneinheitlich machen (flach vs. baumförmig)

12. Glossar
-----------
- Thema: entspricht einem Ordner auf dem Netzlaufwerk
- Feed: abonnierbare Darstellung (RSS/Atom) der neuesten Inhalte
- HATEOAS: Hypermedia as the Engine of Application State – REST-Prinzip mit Links
- Flache Darstellung: Dateien liegen direkt unterhalb des Themas (z. B. „ausgabe“)
- Baumförmige Darstellung: Verschachtelte Ordner-Struktur wird beibehalten


Nutze das Basis für die Codeerstellung Java 25 mit Spring Boot in aktueller Version.
Wir wollen auch JUNIT 5 tests haben und die Endpoints mit Openapi dokumentieren.
