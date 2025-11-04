# ⚡ Quick Start - 5 Minutos

Guia rápido para rodar o sistema localmente em 5 minutos.

---

## 🚀 Execução Rápida Local

### 1. Clone o repositório

```bash
git clone https://github.com/seu-usuario/feedback-system-cloud.git
cd feedback-system-cloud
```

### 2. Execute a aplicação

```bash
# Windows
mvnw.cmd spring-boot:run

# Linux/Mac
./mvnw spring-boot:run
```

**Aguarde a mensagem:**
```
✓ SISTEMA PRONTO! Acesse: http://localhost:8080
```

### 3. Teste a API

Abra outro terminal:

```bash
# Login como aluno
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email": "maria.silva@aluno.com", "password": "maria123"}'

# Salve o token retornado
TOKEN="cole_o_token_aqui"

# Criar feedback
curl -X POST http://localhost:8080/api/feedbacks \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "studentName": "Maria Silva",
    "studentEmail": "maria.silva@aluno.com",
    "course": "Engenharia de Software",
    "rating": 5,
    "comment": "Curso excelente!",
    "urgent": false
  }'

# Listar seus feedbacks
curl -X GET http://localhost:8080/api/feedbacks/me \
  -H "Authorization: Bearer $TOKEN"
```

---

## 👨‍💼 Teste como Admin

```bash
# Login como admin
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email": "admin@feedback.com", "password": "admin123"}'

# Use o token retornado
ADMIN_TOKEN="cole_o_token_admin_aqui"

# Ver todos os feedbacks
curl -X GET http://localhost:8080/api/admin/feedbacks \
  -H "Authorization: Bearer $ADMIN_TOKEN"

# Gerar relatório
curl -X POST http://localhost:8080/api/admin/report/weekly \
  -H "Authorization: Bearer $ADMIN_TOKEN"
```

---

## 🌐 Usando Navegador

1. Instale uma extensão REST Client (ex: [Talend API Tester](https://chrome.google.com/webstore))

2. **Login (POST)**:
   - URL: `http://localhost:8080/api/auth/login`
   - Body (JSON):
   ```json
   {
     "email": "maria.silva@aluno.com",
     "password": "maria123"
   }
   ```

3. **Copie o token** da resposta

4. **Criar Feedback (POST)**:
   - URL: `http://localhost:8080/api/feedbacks`
   - Headers: `Authorization: Bearer SEU_TOKEN`
   - Body (JSON):
   ```json
   {
     "studentName": "Maria Silva",
     "studentEmail": "maria.silva@aluno.com",
     "course": "Teste",
     "rating": 5,
     "comment": "Funcionou!",
     "urgent": false
   }
   ```

---

## 🐳 Com Docker (Alternativa)

```bash
# Inicie PostgreSQL + API
docker-compose up -d

# Aguarde ~30 segundos
# API estará em: http://localhost:8080
```

---

## 🗄️ Acessar Banco de Dados (H2 Console)

1. Acesse: http://localhost:8080/h2-console
2. Configurações:
   - **JDBC URL**: `jdbc:h2:mem:feedbackdb`
   - **Username**: `sa`
   - **Password**: (deixe em branco)
3. Clique em "Connect"
4. Explore as tabelas: `USERS`, `FEEDBACKS`

---

## 📦 Usuários Pré-configurados

| Email | Senha | Perfil | Acesso |
|-------|-------|--------|--------|
| admin@feedback.com | admin123 | ADMIN | Todos os endpoints |
| maria.silva@aluno.com | maria123 | STUDENT | Próprios feedbacks |
| joao.santos@aluno.com | joao123 | STUDENT | Próprios feedbacks |

---

## 🧪 Testar Feedback Urgente

```bash
# Login
TOKEN="seu_token"

# Criar feedback urgente (observe os logs da aplicação!)
curl -X POST http://localhost:8080/api/feedbacks \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "studentName": "Maria Silva",
    "studentEmail": "maria.silva@aluno.com",
    "course": "Teste",
    "rating": 1,
    "comment": "URGENTE: Sistema fora do ar!",
    "urgent": true
  }'

# Nos logs você verá:
# "Feedback urgente detectado! Enviando notificação..."
```

---

## 📊 Endpoints Principais

| Método | Endpoint | Descrição | Auth |
|--------|----------|-----------|------|
| POST | `/api/auth/login` | Login | Público |
| POST | `/api/feedbacks` | Criar feedback | Student/Admin |
| GET | `/api/feedbacks/me` | Meus feedbacks | Student |
| GET | `/api/admin/feedbacks` | Todos feedbacks | Admin |
| GET | `/api/admin/feedbacks/urgent` | Feedbacks urgentes | Admin |
| POST | `/api/admin/report/weekly` | Gerar relatório | Admin |
| GET | `/api/admin/stats` | Estatísticas | Admin |

---

## 🔧 Troubleshooting

### Erro: "Port 8080 already in use"

```bash
# Windows
netstat -ano | findstr :8080
taskkill /PID <PID> /F

# Linux/Mac
lsof -ti:8080 | xargs kill -9
```

### Erro: "JAVA_HOME not found"

```bash
# Verificar Java
java -version

# Deve ser Java 17+
# Baixe em: https://adoptium.net/
```

### Erro: Maven não encontrado

```bash
# Use o Maven wrapper incluído
# Windows: mvnw.cmd
# Linux/Mac: ./mvnw
```

---

## 📚 Próximos Passos

Após testar localmente:

1. ✅ Leia o [README.md](README.md) completo
2. ✅ Veja exemplos em [API_EXAMPLES.md](API_EXAMPLES.md)
3. ✅ Explore o código em `src/main/java/`
4. ✅ Para deploy na nuvem: [deploy.sh](deploy.sh)
5. ✅ Relatório técnico: [RELATORIO_TECNICO.md](RELATORIO_TECNICO.md)

---

## 🎯 Checklist de Teste

- [ ] Sistema iniciou sem erros
- [ ] Login como aluno funcionou
- [ ] Feedback criado com sucesso
- [ ] Login como admin funcionou
- [ ] Listagem de feedbacks funciona
- [ ] Relatório gerado com sucesso
- [ ] H2 Console acessível
- [ ] Feedback urgente dispara log de notificação

---

## 🆘 Precisa de Ajuda?

- 📖 Documentação completa: [README.md](README.md)
- 💻 Exemplos de API: [API_EXAMPLES.md](API_EXAMPLES.md)
- ☁️ Deploy Google Cloud: [deploy.sh](deploy.sh)
- ☁️ Deploy Azure: [AZURE_DEPLOYMENT.md](AZURE_DEPLOYMENT.md)

---

**✅ Sistema rodando? Parabéns! Agora explore os outros arquivos de documentação.**
