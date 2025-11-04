/**
 * GOOGLE CLOUD FUNCTION - RELATÓRIO SEMANAL AUTOMÁTICO
 *
 * Esta função é agendada para executar toda segunda-feira às 08:00.
 * Ela consulta a API de feedbacks, gera estatísticas e envia relatório por email.
 *
 * Deploy:
 * gcloud functions deploy generateReport \
 *   --runtime nodejs20 \
 *   --trigger-topic weekly-report \
 *   --entry-point generateWeeklyReport \
 *   --region us-central1
 *
 * Criar agendamento (Cloud Scheduler):
 * gcloud scheduler jobs create pubsub weekly-report-job \
 *   --schedule="0 8 * * 1" \
 *   --topic=weekly-report \
 *   --message-body='{"action":"generate_report"}' \
 *   --time-zone="America/Sao_Paulo"
 */

const functions = require('@google-cloud/functions-framework');
const https = require('https');

// URL da API (será configurada via variável de ambiente)
const API_BASE_URL = process.env.API_URL || 'http://localhost:8080';
const ADMIN_EMAIL = process.env.ADMIN_EMAIL || 'admin@feedback.com';
const ADMIN_PASSWORD = process.env.ADMIN_PASSWORD || 'admin123';

/**
 * Função auxiliar para fazer requisições HTTP.
 */
function httpRequest(url, options = {}) {
  return new Promise((resolve, reject) => {
    const req = https.request(url, options, (res) => {
      let data = '';
      res.on('data', (chunk) => { data += chunk; });
      res.on('end', () => {
        try {
          resolve({ statusCode: res.statusCode, data: JSON.parse(data) });
        } catch (e) {
          resolve({ statusCode: res.statusCode, data: data });
        }
      });
    });
    req.on('error', reject);
    if (options.body) {
      req.write(JSON.stringify(options.body));
    }
    req.end();
  });
}

/**
 * Autentica na API e retorna o token JWT.
 */
async function authenticate() {
  console.log('🔐 Autenticando na API...');

  try {
    const response = await fetch(`${API_BASE_URL}/api/auth/login`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        email: ADMIN_EMAIL,
        password: ADMIN_PASSWORD
      })
    });

    if (!response.ok) {
      throw new Error(`Erro na autenticação: ${response.status}`);
    }

    const data = await response.json();
    console.log('✅ Autenticação bem-sucedida');
    return data.token;
  } catch (error) {
    console.error('❌ Erro na autenticação:', error.message);
    throw error;
  }
}

/**
 * Busca o relatório semanal da API.
 */
async function fetchWeeklyReport(token) {
  console.log('📊 Buscando relatório semanal...');

  try {
    const response = await fetch(`${API_BASE_URL}/api/admin/report/weekly`, {
      method: 'POST',
      headers: {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json'
      }
    });

    if (!response.ok) {
      throw new Error(`Erro ao buscar relatório: ${response.status}`);
    }

    const report = await response.json();
    console.log('✅ Relatório obtido com sucesso');
    return report;
  } catch (error) {
    console.error('❌ Erro ao buscar relatório:', error.message);
    throw error;
  }
}

/**
 * Formata o relatório em texto legível.
 */
function formatReportText(report) {
  let text = `
╔════════════════════════════════════════════════════════════╗
║         RELATÓRIO SEMANAL DE FEEDBACKS - AUTOMÁTICO       ║
╚════════════════════════════════════════════════════════════╝

📅 Período: ${report.periodStart} até ${report.periodEnd}
🕐 Gerado em: ${report.reportGeneratedAt}

═══════════════════════════════════════════════════════════
📊 ESTATÍSTICAS GERAIS
═══════════════════════════════════════════════════════════

  Total de Feedbacks: ${report.totalFeedbacks}
  Feedbacks Urgentes: ${report.urgentFeedbacks}
  Feedbacks Críticos: ${report.criticalFeedbacks}
  Média Geral: ${report.averageRating} ⭐

═══════════════════════════════════════════════════════════
📈 DISTRIBUIÇÃO DE NOTAS
═══════════════════════════════════════════════════════════
`;

  const distribution = report.ratingDistribution || {};
  for (let i = 5; i >= 1; i--) {
    const count = distribution[i] || 0;
    const bar = '█'.repeat(Math.min(count, 20));
    text += `  ⭐ ${i} estrelas: ${bar} (${count})\n`;
  }

  text += `
═══════════════════════════════════════════════════════════
📚 CURSOS MAIS AVALIADOS
═══════════════════════════════════════════════════════════
`;

  const courses = report.topCourses || {};
  Object.entries(courses)
    .sort((a, b) => b[1] - a[1])
    .slice(0, 5)
    .forEach(([course, count]) => {
      text += `  📖 ${course}: ${count} feedbacks\n`;
    });

  if (report.criticalFeedbacks > 0) {
    text += `
═══════════════════════════════════════════════════════════
⚠️  ALERTAS
═══════════════════════════════════════════════════════════

  ${report.criticalFeedbacks} feedback(s) com nota crítica (≤ 2)
  Requer atenção imediata da equipe pedagógica!
`;
  }

  text += `
═══════════════════════════════════════════════════════════
✨ Relatório gerado automaticamente pelo Sistema de Feedbacks
═══════════════════════════════════════════════════════════
`;

  return text;
}

/**
 * Função principal acionada pelo Cloud Scheduler (Pub/Sub).
 */
functions.cloudEvent('generateWeeklyReport', async (cloudEvent) => {
  console.log('═══════════════════════════════════════════════════');
  console.log('📊 GERAÇÃO DE RELATÓRIO SEMANAL INICIADA');
  console.log('═══════════════════════════════════════════════════');

  try {
    // 1. Autentica na API
    const token = await authenticate();

    // 2. Busca o relatório semanal
    const report = await fetchWeeklyReport(token);

    // 3. Formata o relatório
    const reportText = formatReportText(report);

    console.log(reportText);

    // 4. Aqui você pode salvar no Cloud Storage ou enviar por email
    // Exemplo com Cloud Storage:
    /*
    const {Storage} = require('@google-cloud/storage');
    const storage = new Storage();
    const bucket = storage.bucket('feedback-reports');
    const filename = `report_${new Date().toISOString()}.txt`;
    const file = bucket.file(filename);

    await file.save(reportText);
    console.log(`✅ Relatório salvo: gs://feedback-reports/${filename}`);
    */

    // Exemplo com SendGrid para envio por email:
    /*
    const sgMail = require('@sendgrid/mail');
    sgMail.setApiKey(process.env.SENDGRID_API_KEY);

    const msg = {
      to: ['admin@feedback.com', 'coordenacao@feedback.com'],
      from: 'reports@feedback.com',
      subject: `📊 Relatório Semanal - ${new Date().toLocaleDateString('pt-BR')}`,
      text: reportText,
    };

    await sgMail.send(msg);
    console.log('✅ Relatório enviado por email');
    */

    console.log('═══════════════════════════════════════════════════');
    console.log('✅ RELATÓRIO SEMANAL GERADO COM SUCESSO');
    console.log('═══════════════════════════════════════════════════');

  } catch (error) {
    console.error('❌ Erro ao gerar relatório:', error);
    throw error;
  }
});

/**
 * Função HTTP alternativa para testes manuais.
 */
functions.http('generateWeeklyReportHttp', async (req, res) => {
  console.log('📊 Geração manual de relatório solicitada');

  res.set('Access-Control-Allow-Origin', '*');

  if (req.method === 'OPTIONS') {
    res.set('Access-Control-Allow-Methods', 'POST, GET');
    res.status(204).send('');
    return;
  }

  try {
    const token = await authenticate();
    const report = await fetchWeeklyReport(token);
    const reportText = formatReportText(report);

    console.log(reportText);

    res.status(200).json({
      success: true,
      message: 'Relatório gerado com sucesso',
      report: report,
      reportText: reportText
    });

  } catch (error) {
    console.error('❌ Erro:', error);
    res.status(500).json({
      success: false,
      message: 'Erro ao gerar relatório',
      error: error.message
    });
  }
});
