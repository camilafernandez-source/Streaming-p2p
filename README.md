# Streaming P2P

## Descripción
Este proyecto implementa un sistema de distribución de video mediante una arquitectura Peer-to-Peer (P2P) simulada utilizando Spring Boot. El sistema divide un video en 10 fragmentos, los distribuye entre nodos virtuales y permite el intercambio de datos mediante eventos (Pub/Sub).

## Tecnologías
- Java 21
- Spring Boot
- Spring Events
- Swagger OpenAPI
- Docker & Docker Compose

## Funcionalidades
1. División de video en 10 fragmentos.
2. Registro y distribución de fragmentos entre nodos (NodoA, NodoB, NodoC).
3. Intercambio P2P automático de fragmentos faltantes.
4. Reconstrucción del video original.
5. API REST para pruebas y monitoreo.

## Endpoints Principales
- **Inicializar red:** `GET /api/p2p/inicializar`
- **Estado de la red:** `GET /api/p2p/red`
- **Solicitar fragmento:** `POST /api/p2p/solicitar`
- **Reconstruir video:** `GET /api/p2p/reconstruir?nodo=NodoA`

## Ejecución
### Local
`mvn spring-boot:run`

### Docker
`docker-compose up --build`

## Autor
Camila Alejandra Fernández Torres