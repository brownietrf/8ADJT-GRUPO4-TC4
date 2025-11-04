#!/bin/bash

# ==============================================================================
# SCRIPT DE ENCERRAMENTO - GOOGLE CLOUD
# ==============================================================================
# Este script desativa todos os serviços para evitar custos após a demonstração.
#
# Uso: ./shutdown.sh

set -e

echo "═══════════════════════════════════════════════════"
echo "   ENCERRANDO SERVIÇOS NO GOOGLE CLOUD"
echo "═══════════════════════════════════════════════════"

# Cores para output
RED='\033[0;31m'
YELLOW='\033[1;33m'
GREEN='\033[0;32m'
NC='\033[0m' # No Color

PROJECT_ID="your-project-id"
REGION="us-central1"

echo -e "${YELLOW}⚠️  ATENÇÃO: Este script irá desativar TODOS os serviços!${NC}"
echo "Projeto: ${PROJECT_ID}"
echo ""
read -p "Tem certeza que deseja continuar? (digite 'SIM' para confirmar): " CONFIRM

if [ "$CONFIRM" != "SIM" ]; then
    echo "Operação cancelada."
    exit 0
fi

echo -e "\n${RED}Iniciando encerramento...${NC}"

# ==============================================================================
# 1. PARAR APP ENGINE
# ==============================================================================
echo -e "\n${YELLOW}[1/5] Parando App Engine...${NC}"
gcloud app versions list --format="value(version.id)" | while read version; do
    gcloud app versions stop ${version} --quiet
done
echo -e "${GREEN}✓ App Engine parado${NC}"

# ==============================================================================
# 2. DELETAR CLOUD FUNCTIONS
# ==============================================================================
echo -e "\n${YELLOW}[2/5] Deletando Cloud Functions...${NC}"

if gcloud functions describe notifyAdmin --region=${REGION} 2>/dev/null; then
    gcloud functions delete notifyAdmin --region=${REGION} --quiet
    echo "✓ Função notifyAdmin deletada"
fi

if gcloud functions describe generateReport --region=${REGION} 2>/dev/null; then
    gcloud functions delete generateReport --region=${REGION} --quiet
    echo "✓ Função generateReport deletada"
fi

echo -e "${GREEN}✓ Cloud Functions removidas${NC}"

# ==============================================================================
# 3. DELETAR CLOUD SCHEDULER JOBS
# ==============================================================================
echo -e "\n${YELLOW}[3/5] Deletando Cloud Scheduler Jobs...${NC}"
if gcloud scheduler jobs describe weekly-report-job 2>/dev/null; then
    gcloud scheduler jobs delete weekly-report-job --quiet
    echo -e "${GREEN}✓ Job agendado removido${NC}"
fi

# ==============================================================================
# 4. DELETAR PUB/SUB TOPICS
# ==============================================================================
echo -e "\n${YELLOW}[4/5] Deletando Pub/Sub Topics...${NC}"
if gcloud pubsub topics describe weekly-report 2>/dev/null; then
    gcloud pubsub topics delete weekly-report --quiet
    echo -e "${GREEN}✓ Tópico Pub/Sub removido${NC}"
fi

# ==============================================================================
# 5. PARAR CLOUD SQL (Opcional - comentado por segurança)
# ==============================================================================
echo -e "\n${YELLOW}[5/5] Cloud SQL Instance...${NC}"
echo "⚠️  A instância Cloud SQL NÃO foi deletada automaticamente."
echo "Para deletar manualmente (DADOS SERÃO PERDIDOS):"
echo ""
echo "  gcloud sql instances delete feedbackdb --quiet"
echo ""
echo -e "${YELLOW}Ou para apenas parar (sem deletar dados):${NC}"
echo "  gcloud sql instances patch feedbackdb --activation-policy=NEVER"

# ==============================================================================
# FINALIZAÇÃO
# ==============================================================================
echo -e "\n${GREEN}═══════════════════════════════════════════════════${NC}"
echo -e "${GREEN}   ✓ ENCERRAMENTO CONCLUÍDO!${NC}"
echo -e "${GREEN}═══════════════════════════════════════════════════${NC}"
echo ""
echo -e "${YELLOW}Serviços Desativados:${NC}"
echo "  ✓ App Engine (versões paradas)"
echo "  ✓ Cloud Functions (deletadas)"
echo "  ✓ Cloud Scheduler (deletado)"
echo "  ✓ Pub/Sub Topics (deletados)"
echo "  ⚠️  Cloud SQL (ainda ativo - requer ação manual)"
echo ""
echo -e "${YELLOW}Para verificar custos remanescentes:${NC}"
echo "  https://console.cloud.google.com/billing"
echo ""
