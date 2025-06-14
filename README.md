# MMB 채팅 서비스

## 🧠 Understanding the application with a diagram

![w15.png](..%2F..%2F..%2FDownloads%2Fw15.png)

**Flow**:
1. User connects to the Chat Service via WebSocket (`/ws/chat`).
2. The Chat Service publishes messages to Redis (Pub/Sub).
3. Redis broadcasts the messages to other instances.
4. Messages are saved into MongoDB with a TTL index.
5. WebSocketSessionManager broadcasts them to active clients.

---

## 🚀 Run `mmb-chat-service` locally

`mmb-chat-service` is a [Spring Boot](https://spring.io/projects/spring-boot) application built with [Gradle](https://gradle.org).  
You can build and run it with Java 21+:

```bash
git clone https://github.com/A-OverFlow/mmb-chat-service.git
cd mmb-chat-service
./gradlew build
java -jar build/libs/mmb-chat-service.jar
```

---

## 🐳 Build Docker Image

```bash
docker build -t mmb-chat-service .
```

---

## 📦 Create Volumes and Network

```bash
docker volume create mysql_data
docker volume create mongo_data
docker network create --driver bridge mmb-network
```

---

## 🧱 Redis & MongoDB Configuration

```bash
# Redis
docker run -d \
  --name redis \
  -p 6379:6379 \
  -v redis_data:/data \
  --network mmb-network \
  redis:7

# MongoDB
docker run -d \
  --name mongodb \
  -p 27017:27017 \
  -v mongo_data:/data/db \
  --network mmb-network \
  mongo:6
```

---

## ▶️ Run `mmb-chat-service` with Docker Compose

You can also start everything with `docker-compose.yml`:

```bash
docker-compose up --build -d
```

> ⚠️ Make sure Redis and MongoDB are reachable on the same network (`mmb-network`).

---

## 📌 API Summary

| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/api/v1/chat/messages` | Get recent chat messages (last 12h) |
| `POST` | `/api/v1/chat/message` | Store a chat message |
| `WSS` | `/ws/chat` | WebSocket for real-time messaging |

---

## 📁 Tech Stack

- **Spring Boot 3**
- **WebSocket**
- **Redis (Pub/Sub)**
- **MongoDB (TTL Index)**
- **Gradle / Docker**
