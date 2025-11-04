# 📘 Deploy Alternativo - Microsoft Azure

Este guia mostra como fazer deploy do sistema no Microsoft Azure, como alternativa ao Google Cloud.

---

## 🏗️ Arquitetura Azure

```
┌─────────────────────────────────────────────────────┐
│              MICROSOFT AZURE                        │
├─────────────────────────────────────────────────────┤
│                                                     │
│  ┌──────────────────────────────────────────────┐  │
│  │      Azure App Service (Spring Boot)         │  │
│  │      - Runtime: Java 17                      │  │
│  │      - Plan: B1 Basic                        │  │
│  └────────┬─────────────────────────────────────┘  │
│           │                                         │
│           ├─► Azure Database for PostgreSQL        │
│           │    - Tier: Basic                        │
│           │    - 1 vCore, 50GB                      │
│           │                                         │
│           ├─► Azure Functions                       │
│           │    - Runtime: Node.js 20                │
│           │    - Plan: Consumption                  │
│           │                                         │
│           └─► Azure Monitor + App Insights          │
│                - Logs centralizados                 │
│                - Application Performance Monitoring │
│                                                     │
└─────────────────────────────────────────────────────┘
```

---

## 📋 Pré-requisitos

1. Conta Azure (free tier disponível)
2. Azure CLI instalado
3. Maven 3.8+
4. Node.js 18+

---

## 🚀 Passo a Passo

### 1. Instalar Azure CLI

```bash
# Windows (via Chocolatey)
choco install azure-cli

# macOS (via Homebrew)
brew install azure-cli

# Linux
curl -sL https://aka.ms/InstallAzureCLIDeb | sudo bash
```

### 2. Login no Azure

```bash
az login

# Listar subscriptions
az account list --output table

# Definir subscription padrão
az account set --subscription "sua-subscription-id"
```

### 3. Criar Resource Group

```bash
az group create \
  --name feedback-system-rg \
  --location brazilsouth
```

### 4. Criar Azure Database for PostgreSQL

```bash
# Criar servidor PostgreSQL
az postgres flexible-server create \
  --name feedback-postgres-server \
  --resource-group feedback-system-rg \
  --location brazilsouth \
  --admin-user feedbackadmin \
  --admin-password "SuaSenhaSegura123!" \
  --sku-name Standard_B1ms \
  --tier Burstable \
  --storage-size 32 \
  --version 15

# Criar banco de dados
az postgres flexible-server db create \
  --resource-group feedback-system-rg \
  --server-name feedback-postgres-server \
  --database-name feedbackdb

# Configurar firewall (permitir Azure services)
az postgres flexible-server firewall-rule create \
  --resource-group feedback-system-rg \
  --name feedback-postgres-server \
  --rule-name AllowAzureServices \
  --start-ip-address 0.0.0.0 \
  --end-ip-address 0.0.0.0
```

### 5. Deploy da Aplicação Spring Boot

```bash
# Build da aplicação
./mvnw clean package -DskipTests

# Criar App Service Plan
az appservice plan create \
  --name feedback-app-plan \
  --resource-group feedback-system-rg \
  --location brazilsouth \
  --sku B1 \
  --is-linux

# Criar Web App
az webapp create \
  --name feedback-api-app \
  --resource-group feedback-system-rg \
  --plan feedback-app-plan \
  --runtime "JAVA:17-java17"

# Configurar variáveis de ambiente
az webapp config appsettings set \
  --resource-group feedback-system-rg \
  --name feedback-api-app \
  --settings \
    SPRING_DATASOURCE_URL="jdbc:postgresql://feedback-postgres-server.postgres.database.azure.com:5432/feedbackdb?sslmode=require" \
    SPRING_DATASOURCE_USERNAME="feedbackadmin" \
    SPRING_DATASOURCE_PASSWORD="SuaSenhaSegura123!" \
    SPRING_JPA_HIBERNATE_DDL_AUTO="update" \
    JWT_SECRET="sua-chave-secreta-jwt-256-bits" \
    CLOUD_FUNCTION_NOTIFICATION_URL="https://feedback-functions.azurewebsites.net/api/notify" \
    CLOUD_FUNCTION_REPORT_URL="https://feedback-functions.azurewebsites.net/api/report"

# Deploy do JAR
az webapp deploy \
  --resource-group feedback-system-rg \
  --name feedback-api-app \
  --src-path target/feedback-system-cloud-1.0.0.jar \
  --type jar
```

### 6. Deploy das Azure Functions

#### Função de Notificação

```bash
cd cloud-functions/notification-function

# Criar Function App
az functionapp create \
  --resource-group feedback-system-rg \
  --name feedback-notification-func \
  --storage-account feedbackstorage \
  --consumption-plan-location brazilsouth \
  --runtime node \
  --runtime-version 20 \
  --functions-version 4

# Instalar Azure Functions Core Tools
npm install -g azure-functions-core-tools@4

# Criar function.json
cat > function.json << 'EOF'
{
  "bindings": [
    {
      "authLevel": "anonymous",
      "type": "httpTrigger",
      "direction": "in",
      "name": "req",
      "methods": ["post"]
    },
    {
      "type": "http",
      "direction": "out",
      "name": "res"
    }
  ]
}
EOF

# Deploy
func azure functionapp publish feedback-notification-func
```

#### Função de Relatório

```bash
cd ../report-function

# Criar Function App
az functionapp create \
  --resource-group feedback-system-rg \
  --name feedback-report-func \
  --storage-account feedbackstorage \
  --consumption-plan-location brazilsouth \
  --runtime node \
  --runtime-version 20 \
  --functions-version 4

# Configurar timer trigger (toda segunda-feira às 08:00)
cat > function.json << 'EOF'
{
  "bindings": [
    {
      "name": "myTimer",
      "type": "timerTrigger",
      "direction": "in",
      "schedule": "0 0 8 * * 1"
    }
  ]
}
EOF

# Configurar variáveis
az functionapp config appsettings set \
  --resource-group feedback-system-rg \
  --name feedback-report-func \
  --settings \
    API_URL="https://feedback-api-app.azurewebsites.net" \
    ADMIN_EMAIL="admin@feedback.com" \
    ADMIN_PASSWORD="admin123"

# Deploy
func azure functionapp publish feedback-report-func
```

### 7. Configurar Application Insights

```bash
# Criar Application Insights
az monitor app-insights component create \
  --app feedback-insights \
  --location brazilsouth \
  --resource-group feedback-system-rg

# Obter instrumentation key
INSTRUMENTATION_KEY=$(az monitor app-insights component show \
  --app feedback-insights \
  --resource-group feedback-system-rg \
  --query instrumentationKey \
  --output tsv)

# Configurar no App Service
az webapp config appsettings set \
  --resource-group feedback-system-rg \
  --name feedback-api-app \
  --settings \
    APPINSIGHTS_INSTRUMENTATIONKEY="$INSTRUMENTATION_KEY"
```

---

## 📊 Monitoramento

### Application Insights

```bash
# Ver logs em tempo real
az webapp log tail \
  --resource-group feedback-system-rg \
  --name feedback-api-app

# Ver métricas
az monitor metrics list \
  --resource feedback-api-app \
  --resource-group feedback-system-rg \
  --resource-type "Microsoft.Web/sites"
```

### Portal Azure

1. Acesse: https://portal.azure.com
2. Navegue até Resource Group: `feedback-system-rg`
3. Visualize métricas, logs e alertas

---

## 💰 Custos Azure

### Free Tier

- **App Service**: 10 apps gratuitos (F1 tier)
- **Azure Functions**: 1M execuções/mês grátis
- **Azure Database**: $0 por 12 meses (novo usuário)
- **Application Insights**: 5GB/mês grátis

### Custos Mensais (após free tier)

| Serviço | Configuração | Custo |
|---------|--------------|-------|
| App Service | B1 Basic | ~$13/mês |
| PostgreSQL | Basic, 1 vCore | ~$25/mês |
| Functions | Consumption | ~$0.50/mês |
| Storage | 5GB | ~$0.10/mês |
| **TOTAL** | | **~$38/mês** |

---

## 🛑 Encerramento

```bash
# Deletar todo o resource group (CUIDADO!)
az group delete \
  --name feedback-system-rg \
  --yes \
  --no-wait

# Ou parar os serviços sem deletar
az webapp stop \
  --resource-group feedback-system-rg \
  --name feedback-api-app

az postgres flexible-server stop \
  --resource-group feedback-system-rg \
  --name feedback-postgres-server
```

---

## 🔄 CI/CD com Azure DevOps

### Azure DevOps Pipeline (azure-pipelines.yml)

```yaml
trigger:
  - main

pool:
  vmImage: 'ubuntu-latest'

variables:
  mavenPomFile: 'pom.xml'

stages:
- stage: Build
  jobs:
  - job: BuildJob
    steps:
    - task: Maven@3
      inputs:
        mavenPomFile: '$(mavenPomFile)'
        goals: 'clean package'
        options: '-DskipTests'
        publishJUnitResults: false

    - task: CopyFiles@2
      inputs:
        SourceFolder: '$(System.DefaultWorkingDirectory)/target'
        Contents: '*.jar'
        TargetFolder: '$(Build.ArtifactStagingDirectory)'

    - task: PublishBuildArtifacts@1
      inputs:
        PathtoPublish: '$(Build.ArtifactStagingDirectory)'
        ArtifactName: 'drop'

- stage: Deploy
  dependsOn: Build
  condition: succeeded()
  jobs:
  - job: DeployJob
    steps:
    - task: DownloadBuildArtifacts@0
      inputs:
        artifactName: 'drop'

    - task: AzureWebApp@1
      inputs:
        azureSubscription: 'Azure Subscription'
        appName: 'feedback-api-app'
        package: '$(System.ArtifactsDirectory)/drop/*.jar'
```

---

## 📚 Comparação: Azure vs Google Cloud

| Aspecto | Azure | Google Cloud |
|---------|-------|--------------|
| App Hosting | App Service | App Engine |
| Serverless | Azure Functions | Cloud Functions |
| Database | Azure Database | Cloud SQL |
| Monitoring | Application Insights | Cloud Monitoring |
| Custo | ~$38/mês | ~$18/mês |
| Região Brasil | Brazil South | São Paulo |
| Free Tier | 12 meses | Permanente |
| Integração Microsoft | Excelente | Boa |
| Documentação | Muito boa | Excelente |

---

## ✅ Checklist de Deploy Azure

- [ ] Conta Azure criada
- [ ] Azure CLI instalado
- [ ] Resource Group criado
- [ ] PostgreSQL provisionado
- [ ] App Service criado e configurado
- [ ] Aplicação deployada
- [ ] Azure Functions deployadas
- [ ] Application Insights configurado
- [ ] Firewall rules configurados
- [ ] Testes de endpoints realizados
- [ ] Monitoramento ativo

---

## 🆘 Troubleshooting

### Problema: App não inicia

```bash
# Ver logs detalhados
az webapp log tail \
  --resource-group feedback-system-rg \
  --name feedback-api-app

# Verificar configurações
az webapp config appsettings list \
  --resource-group feedback-system-rg \
  --name feedback-api-app
```

### Problema: Conexão com banco falha

```bash
# Testar conectividade
az postgres flexible-server connect \
  --name feedback-postgres-server \
  --admin-user feedbackadmin

# Verificar firewall
az postgres flexible-server firewall-rule list \
  --resource-group feedback-system-rg \
  --name feedback-postgres-server
```

---

## 📖 Referências

- [Azure App Service Documentation](https://docs.microsoft.com/azure/app-service/)
- [Azure Functions Documentation](https://docs.microsoft.com/azure/azure-functions/)
- [Azure Database for PostgreSQL](https://docs.microsoft.com/azure/postgresql/)
- [Application Insights](https://docs.microsoft.com/azure/azure-monitor/app/app-insights-overview)

---

**🎓 Deploy Azure concluído com sucesso!**
