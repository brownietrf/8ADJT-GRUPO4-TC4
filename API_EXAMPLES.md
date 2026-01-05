# 📡 Exemplos de Requisições da API

Exemplos práticos de como usar a API do Sistema de Feedbacks.

> **⚙️ Variáveis usadas nos exemplos**

```bash
BACKEND_URL="https://feedback-backend-385950174704.us-central1.run.app"
NOTIFY_URL="https://notifyadmin-2onaas43pa-uc.a.run.app"
REPORT_URL="https://generatereport-2onaas43pa-uc.a.run.app"
WEEKLY_REPORT_URL="https://generateweeklyreporthttp-2onaas43pa-uc.a.run.app"
MANUAL_REPORT_URL="https://reporthttp-2onaas43pa-uc.a.run.app"
```

---

Exemplos práticos de como usar a API do Sistema de Feedbacks.

---

## 🔐 Autenticação

### 1. Login como Aluno

```bash
curl -X POST $BACKEND_URL/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "maria.silva@aluno.com",
    "password": "maria123"
  }'
```

**Resposta:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJtYXJpYS5zaWx2YUBhbHVuby5jb20iLCJpYXQiOjE3MDUzMjAwMDAsImV4cCI6MTcwNTQwNjQwMH0.signature",
  "type": "Bearer",
  "email": "maria.silva@aluno.com",
  "name": "Maria Silva",
  "role": "ROLE_STUDENT"
}
```

### 2. Login como Admin

```bash
curl -X POST $BACKEND_URL/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "admin@feedback.com",
    "password": "admin123"
  }'
```

---

## 📝 Feedbacks (Aluno)

### 3. Criar Feedback Normal

```bash
# Salve o token em uma variável
TOKEN="seu_token_aqui"

curl -X POST $BACKEND_URL/api/feedbacks \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "studentName": "Maria Silva",
    "studentEmail": "maria.silva@aluno.com",
    "course": "Engenharia de Software",
    "rating": 5,
    "comment": "Curso excelente! O professor explica muito bem e os exemplos práticos ajudaram bastante.",
    "urgent": false
  }'
```

### 4. Criar Feedback Urgente (Dispara Notificação)

```bash
curl -X POST $BACKEND_URL/api/feedbacks \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "studentName": "João Santos",
    "studentEmail": "joao.santos@aluno.com",
    "course": "Banco de Dados",
    "rating": 1,
    "comment": "Sistema de submissão de trabalhos está fora do ar há 2 dias! Prazo de entrega se aproximando.",
    "urgent": true
  }'
```

### 5. Listar Meus Feedbacks

```bash
curl -X GET $BACKEND_URL/api/feedbacks/me \
  -H "Authorization: Bearer $TOKEN"
```

### 6. Buscar Feedback por ID

```bash
curl -X GET $BACKEND_URL/api/feedbacks/123e4567-e89b-12d3-a456-426614174000 \
  -H "Authorization: Bearer $TOKEN"
```

---

## 👨‍💼 Administração

### 7. Listar Todos os Feedbacks

```bash
# Use o token do admin
ADMIN_TOKEN="token_admin_aqui"

curl -X GET $BACKEND_URL/api/admin/feedbacks \
  -H "Authorization: Bearer $ADMIN_TOKEN"
```

### 8. Listar Feedbacks da Última Semana

```bash
curl -X GET "$BACKEND_URL/api/admin/feedbacks?lastWeek=true" \
  -H "Authorization: Bearer $ADMIN_TOKEN"
```

### 9. Listar Feedbacks Urgentes

```bash
curl -X GET $BACKEND_URL/api/admin/feedbacks/urgent \
  -H "Authorization: Bearer $ADMIN_TOKEN"
```

### 10. Gerar Relatório Semanal (JSON)

```bash
curl -X POST $BACKEND_URL/api/admin/report/weekly \
  -H "Authorization: Bearer $ADMIN_TOKEN"
```

**Resposta:**
```json
{
  "reportGeneratedAt": "2024-01-15T10:00:00",
  "periodStart": "2024-01-08T10:00:00",
  "periodEnd": "2024-01-15T10:00:00",
  "totalFeedbacks": 25,
  "urgentFeedbacks": 3,
  "averageRating": "4.20",
  "ratingDistribution": {
    "5": 10,
    "4": 8,
    "3": 4,
    "2": 2,
    "1": 1
  },
  "topCourses": {
    "Engenharia de Software": 10,
    "Ciência de Dados": 8,
    "DevOps": 7
  },
  "recentComments": [
    {
      "course": "Engenharia de Software",
      "rating": "5",
      "comment": "Excelente curso!",
      "date": "2024-01-15T09:30:00"
    }
  ],
  "criticalFeedbacks": 3
}
```

### 11. Gerar Relatório Semanal (Texto)

```bash
curl -X GET $BACKEND_URL/api/admin/report/weekly/text \
  -H "Authorization: Bearer $ADMIN_TOKEN"
```

**Resposta:**
```
═══════════════════════════════════════════════════
      RELATÓRIO SEMANAL DE FEEDBACKS
═══════════════════════════════════════════════════

📅 Período: 2024-01-08T10:00:00 até 2024-01-15T10:00:00
🕐 Gerado em: 2024-01-15T10:00:00

📊 ESTATÍSTICAS GERAIS
─────────────────────────────────────────────────
Total de Feedbacks: 25
Feedbacks Urgentes: 3
Feedbacks Críticos (nota ≤ 2): 3
Média Geral: 4.20 ⭐

📈 DISTRIBUIÇÃO DE NOTAS
─────────────────────────────────────────────────
⭐ 5 estrelas: 10
⭐ 4 estrelas: 8
⭐ 3 estrelas: 4
⭐ 2 estrelas: 2
⭐ 1 estrelas: 1

═══════════════════════════════════════════════════
```

### 12. Ver Estatísticas Gerais

```bash
curl -X GET $BACKEND_URL/api/admin/stats \
  -H "Authorization: Bearer $ADMIN_TOKEN"
```

---

## 🏥 Health Check

### 13. Verificar Status da API

```bash
curl -X GET $BACKEND_URL/api/auth/health
```

**Resposta:**
```
Sistema de Feedbacks - Online
```

### 14. Actuator Health

```bash
curl -X GET $BACKEND_URL/actuator/health
```

**Resposta:**
```json
{
  "status": "UP",
  "components": {
    "db": {
      "status": "UP",
      "details": {
        "database": "H2",
        "validationQuery": "isValid()"
      }
    },
    "diskSpace": {
      "status": "UP",
      "details": {
        "total": 499963174912,
        "free": 245821485056,
        "threshold": 10485760,
        "exists": true
      }
    }
  }
}
```

---

## 🧪 Cenários de Teste Completos

### Cenário 1: Fluxo Completo de Aluno

```bash
# 1. Login
RESPONSE=$(curl -s -X POST $BACKEND_URL/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email": "maria.silva@aluno.com", "password": "maria123"}')

TOKEN=$(echo $RESPONSE | grep -o '"token":"[^"]*' | cut -d'"' -f4)
echo "Token obtido: $TOKEN"

# 2. Criar feedback
curl -X POST $BACKEND_URL/api/feedbacks \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "studentName": "Maria Silva",
    "studentEmail": "maria.silva@aluno.com",
    "course": "Arquitetura de Software",
    "rating": 5,
    "comment": "Melhor curso que já fiz!",
    "urgent": false
  }'

# 3. Listar meus feedbacks
curl -X GET $BACKEND_URL/api/feedbacks/me \
  -H "Authorization: Bearer $TOKEN"
```

### Cenário 2: Fluxo de Admin com Relatório

```bash
# 1. Login como admin
ADMIN_RESPONSE=$(curl -s -X POST $BACKEND_URL/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email": "admin@feedback.com", "password": "admin123"}')

ADMIN_TOKEN=$(echo $ADMIN_RESPONSE | grep -o '"token":"[^"]*' | cut -d'"' -f4)
echo "Token Admin: $ADMIN_TOKEN"

# 2. Ver estatísticas
curl -X GET $BACKEND_URL/api/admin/stats \
  -H "Authorization: Bearer $ADMIN_TOKEN"

# 3. Ver feedbacks urgentes
curl -X GET $BACKEND_URL/api/admin/feedbacks/urgent \
  -H "Authorization: Bearer $ADMIN_TOKEN"

# 4. Gerar relatório
curl -X POST $BACKEND_URL/api/admin/report/weekly \
  -H "Authorization: Bearer $ADMIN_TOKEN"
```

### Cenário 3: Testar Feedback Urgente

```bash
# 1. Login
TOKEN="seu_token_aqui"

# 2. Criar feedback urgente (verifique os logs para ver a notificação)
curl -X POST $BACKEND_URL/api/feedbacks \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "studentName": "Aluno Preocupado",
    "studentEmail": "aluno@example.com",
    "course": "Sistemas Operacionais",
    "rating": 2,
    "comment": "Laboratório indisponível há uma semana",
    "urgent": true
  }'

# Verifique os logs da aplicação:
# Você verá: "Feedback urgente detectado! Enviando notificação..."
```

---

## 🐛 Testes de Erro

### Erro 401 - Não Autenticado

```bash
curl -X GET $BACKEND_URL/api/feedbacks/me
# Resposta: 401 Unauthorized
```

### Erro 403 - Não Autorizado

```bash
# Aluno tentando acessar endpoint de admin
curl -X GET $BACKEND_URL/api/admin/feedbacks \
  -H "Authorization: Bearer $TOKEN_ALUNO"
# Resposta: 403 Forbidden
```

### Erro 400 - Validação

```bash
# Feedback com nota inválida
curl -X POST $BACKEND_URL/api/feedbacks \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "studentName": "Maria Silva",
    "studentEmail": "invalid-email",
    "course": "Teste",
    "rating": 10,
    "comment": "Teste",
    "urgent": false
  }'
# Resposta: 400 Bad Request com mensagens de validação
```

---

## 📦 Usando com Postman

### Importar Collection

Crie uma Collection no Postman com:

1. **Environment Variables**:
   - `BASE_URL`: `$BACKEND_URL`
   - `token_aluno`: (será preenchido automaticamente)
   - `token_admin`: (será preenchido automaticamente)

2. **Pre-request Script**:
```javascript
// Salva o token automaticamente após login
pm.environment.set("token_aluno", pm.response.json().token);
```

3. **Authorization**: Type = Bearer Token, Token = `{{token_aluno}}`

---

## 🌐 Testando em Produção (Google Cloud)

Substitua `localhost:8080` pela URL do App Engine:

```bash
BASE_URL="$BACKEND_URL"

curl -X POST $BASE_URL/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email": "admin@feedback.com", "password": "admin123"}'
```

---

## 📊 Testando Cloud Functions Diretamente

### Notificação

```bash
NOTIFICATION_URL="$NOTIFY_URL"

curl -X POST $NOTIFICATION_URL \
  -H "Content-Type: application/json" \
  -d '{
    "feedbackId": "test-123",
    "studentName": "Teste",
    "studentEmail": "teste@example.com",
    "course": "Teste",
    "rating": 1,
    "comment": "Teste urgente",
    "createdAt": "2024-01-15T10:00:00"
  }'
```

### Relatório (via HTTP)

```bash
REPORT_URL="$REPORT_URL"

curl -X POST $REPORT_URL
```

---

## 💡 Dicas

### Salvar Token em Variável

```bash
# Linux/Mac
export TOKEN=$(curl -s -X POST $BACKEND_URL/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"maria.silva@aluno.com","password":"maria123"}' \
  | jq -r '.token')

echo $TOKEN

# Windows PowerShell
$response = Invoke-RestMethod -Uri "$BACKEND_URL/api/auth/login" `
  -Method Post -ContentType "application/json" `
  -Body '{"email":"maria.silva@aluno.com","password":"maria123"}'

$TOKEN = $response.token
```

### Pretty Print JSON

```bash
# Com jq
curl -X GET $BACKEND_URL/api/admin/stats \
  -H "Authorization: Bearer $TOKEN" | jq '.'

# Com Python
curl -X GET $BACKEND_URL/api/admin/stats \
  -H "Authorization: Bearer $TOKEN" | python -m json.tool
```

---