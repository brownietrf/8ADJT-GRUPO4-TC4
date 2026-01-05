# ⚡ Quick Start - 5 Minutos

Guia rápido para rodar o sistema **localmente** em 5 minutos.

---

## 🚀 Execução Rápida Local

### 1. Clone o repositório

```bash
git clone https://github.com/ersmoraes/8ADJT-GRUPO4-TC4.git
cd 8ADJT-GRUPO4-TC4
```

---

### 2. Execute a aplicação

A aplicação usa **Java 17+, Maven Wrapper** e **H2 em memória**.

```bash
# Windows
mvnw.cmd spring-boot:run

# Linux/Mac
./mvnw spring-boot:run
```

Aguarde até aparecer no console:

```
✓ SISTEMA PRONTO! Acesse: http://localhost:8080
```

---

## 🔐 Usuários criados automaticamente

| Email | Senha | Perfil | Acesso |
|-------|-------|--------|--------|
| admin@feedback.com | admin123 | ADMIN | Todos os endpoints |
| maria.silva@aluno.com | maria123 | STUDENT | Próprios feedbacks |
| joao.santos@aluno.com | joao123 | STUDENT | Próprios feedbacks |

---

## 🎯 Teste a API — Terminal / CURL

Abra outro terminal.

### 🔑 Login aluno

```bash
curl -X POST http://localhost:8080/api/auth/login   -H "Content-Type: application/json"   -d '{"email": "maria.silva@aluno.com", "password": "maria123"}'
```
Copie `"token"`:

```bash
TOKEN="COLE_SEU_TOKEN_AQUI"
```

---

### ✍️ Criar feedback

```bash
curl -X POST http://localhost:8080/api/feedbacks   -H "Content-Type: application/json"   -H "Authorization: Bearer $TOKEN"   -d '{
    "studentName": "Maria Silva",
    "studentEmail": "maria.silva@aluno.com",
    "course": "Engenharia de Software",
    "rating": 5,
    "comment": "Curso excelente!",
    "urgent": false
  }'
```

---

### 👀 Listar meus feedbacks

```bash
curl -X GET http://localhost:8080/api/feedbacks/me   -H "Authorization: Bearer $TOKEN"
```

---

## 👨‍💼 Teste como Admin

Login:
```bash
curl -X POST http://localhost:8080/api/auth/login   -H "Content-Type: application/json"   -d '{"email": "admin@feedback.com", "password": "admin123"}'
```
```bash
ADMIN_TOKEN="cole_token_admin"
```

Ver todos feedbacks:
```bash
curl -X GET http://localhost:8080/api/admin/feedbacks   -H "Authorization: Bearer $ADMIN_TOKEN"
```

Relatório semanal:
```bash
curl -X POST http://localhost:8080/api/admin/report/weekly   -H "Authorization: Bearer $ADMIN_TOKEN"
```

---

## 🗄️ Banco H2

```
http://localhost:8080/h2-console
```

| Campo | Valor |
|-------|-------|
| JDBC URL | jdbc:h2:mem:feedbackdb |
| Username | sa |
| Password | *(vazio)* |

---

## 🧪 Feedback urgente

```bash
curl -X POST http://localhost:8080/api/feedbacks   -H "Content-Type: application/json"   -H "Authorization: Bearer $TOKEN"   -d '{
    "rating": 1,
    "urgent": true,
    "comment": "PROBLEMA GRAVE"
  }'
```

---

## 📦 Endpoints

| Método | Endpoint | Perfil |
|--------|----------|--------|
| POST | /api/auth/login | Público |
| POST | /api/feedbacks | Student/Admin |
| GET | /api/feedbacks/me | Student |
| GET | /api/admin/feedbacks | Admin |
| GET | /api/admin/feedbacks/urgent | Admin |
| POST | /api/admin/report/weekly | Admin |

---

## Troubleshooting

Porta 8080 ocupada — libere  
Java 17+ obrigatório  
Use mvnw se não tiver Maven

---

## Próximos passos
README.md  
API_EXAMPLES.md  
GCP_DEPLOYMENT.md  

---
✔️ Se rodou local, está pronto pra nuvem!
