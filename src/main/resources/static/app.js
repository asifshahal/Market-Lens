const $ = s => document.querySelector(s);
const state = { file: null, previewUrl: null, busy: false };
const assetSelect = $('#asset'), fileInput = $('#chart-file'), dropZone = $('#drop-zone');
const previewMode = location.protocol === 'file:';
if (previewMode) {
  const assets = [
    { name: 'Reliance Industries', symbol: 'RELIANCE.NS', type: 'STOCK', exchange: 'NSE' },
    { name: 'Tata Consultancy Services', symbol: 'TCS.NS', type: 'STOCK', exchange: 'NSE' },
    { name: 'Infosys', symbol: 'INFY.NS', type: 'STOCK', exchange: 'NSE' },
    { name: 'HDFC Bank', symbol: 'HDFCBANK.NS', type: 'STOCK', exchange: 'NSE' },
    { name: 'ICICI Bank', symbol: 'ICICIBANK.NS', type: 'STOCK', exchange: 'NSE' },
    ...[['Bitcoin','BTC'],['Ethereum','ETH'],['Solana','SOL'],['BNB','BNB'],['XRP','XRP']].map(([name,symbol]) => ({ name, symbol, type: 'CRYPTO', currency: 'USD' }))
  ];
  assetSelect.innerHTML = '<option value="">Select a stock or cryptocurrency</option>' + assets.map(a => `<option value="${a.symbol}">${a.name} · ${a.symbol} (${a.type === 'STOCK' ? a.exchange : 'Crypto'} · ${a.currency})</option>`).join('');
} else fetch('/api/assets').then(r => { if (!r.ok) throw new Error(); return r.json(); }).then(assets => {
  assetSelect.innerHTML = '<option value="">Select a stock or cryptocurrency</option>' + assets.map(a => `<option value="${a.symbol}">${a.name} · ${a.symbol} (${a.type === 'STOCK' ? a.exchange : 'Crypto'} · ${a.currency})</option>`).join('');
}).catch(() => { assetSelect.innerHTML = '<option value="">Could not load asset list</option>'; });

function setFile(file) {
  if (!file) return;
  if (!['image/png', 'image/jpeg'].includes(file.type)) { showError('Choose a PNG or JPG chart screenshot.'); return; }
  if (file.size > 5 * 1024 * 1024) { showError('The screenshot must be 5 MB or smaller.'); return; }
  if (state.previewUrl) URL.revokeObjectURL(state.previewUrl);
  state.file = file; state.previewUrl = URL.createObjectURL(file);
  $('#preview').src = state.previewUrl; $('#file-name').textContent = file.name;
  $('#upload-empty').hidden = true; $('#preview-wrap').hidden = false; updateButton();
}
function clearFile() {
  if (state.previewUrl) URL.revokeObjectURL(state.previewUrl);
  state.file = null; state.previewUrl = null; fileInput.value = '';
  $('#preview').removeAttribute('src'); $('#preview-wrap').hidden = true; $('#upload-empty').hidden = false; updateButton();
}
function updateButton() { $('#analyze-btn').disabled = !state.file || !assetSelect.value || state.busy; }
function showError(message) { $('#result').innerHTML = `<div class="error-state"><div class="error-mark">!</div><h2>Could not analyze this chart</h2><p>${escapeHtml(message)}</p><span>Check the image and try again.</span></div>`; }
function money(value, currency) {
  const isUsd = currency === 'USD';
  return new Intl.NumberFormat(isUsd ? 'en-US' : 'en-IN', {
    style: 'currency', currency,
    maximumFractionDigits: isUsd ? (value < 1 ? 4 : 2) : (value < 100 ? 3 : 2)
  }).format(value);
}
function resultMarkup(data) {
  const action = data.recommendation.action.toLowerCase(), sentiment = data.marketSentiment.toLowerCase();
  const level = (item, type) => `<div class="level-item"><span class="level-dot ${type}"></span><span><small>${type === 'support' ? 'SUPPORT ZONE' : 'RESISTANCE ZONE'}</small><b>${escapeHtml(item.range)}</b><em>${escapeHtml(item.significance)}</em></span></div>`;
  const targets = data.targets?.length ? data.targets.map((t, i) => `<div class="target"><small>TARGET ${i + 1}</small><b>${escapeHtml(t)}</b></div>`).join('') : '<div class="target"><small>TARGETS</small><b>Not clearly visible</b></div>';
  return `<div class="analysis-result">
    <div class="result-title-row"><div><div class="eyebrow">CHART ANALYSIS</div><h2>${escapeHtml(data.asset.name)} <span>${escapeHtml(data.timeframe)} chart</span></h2></div><span class="model-pill">${escapeHtml(data.analyzedBy)}</span></div>
    <div class="market-price"><span><small>LATEST MARKET REFERENCE</small><b>${money(data.currentPrice, data.currency)}</b><em>As of ${new Intl.DateTimeFormat('en-IN', { day: 'numeric', month: 'short', year: 'numeric' }).format(new Date(data.marketDataAsOf))} · ${escapeHtml(data.dataSource)}</em></span><span class="sentiment-tag ${sentiment}">${escapeHtml(data.marketSentiment)} · ${data.sentimentScore}/100</span></div>
    <div class="signal-overview"><div><small>CHART TREND</small><b>${escapeHtml(data.trend)}</b></div><div><small>READING CLARITY</small><b>${data.confidencePercent}%</b><span class="confidence"><i style="width:${data.confidencePercent}%"></i></span></div><div><small>CHART PATTERN</small><b>${escapeHtml(data.pattern)}</b></div></div>
    <div class="summary"><h3>Analysis summary</h3><p>${escapeHtml(data.summary)}</p></div>
    <div class="levels"><h3>Key levels</h3>${level(data.support, 'support')}${level(data.resistance, 'resistance')}</div>
    <div class="observations"><h3>Technical observations</h3><ul>${(data.observations || []).map(x => `<li>${escapeHtml(x)}</li>`).join('')}</ul></div>
    <div class="market-indicators"><h3>Live market indicators</h3><div class="indicator-grid"><div><small>RSI 14</small><b>${data.marketIndicators.rsi14.toFixed(1)}</b></div><div><small>SMA 20</small><b>${money(data.marketIndicators.sma20, data.currency)}</b></div><div><small>SMA 50</small><b>${money(data.marketIndicators.sma50, data.currency)}</b></div></div></div>
    <div class="trade-plan"><div class="plan-head"><h3>Scenario notes</h3><span class="recommendation ${action}">${escapeHtml(data.recommendation.action)}</span></div><p>${escapeHtml(data.recommendation.rationale)}</p><div class="plan-levels"><div><small>ENTRY AREA</small><b>${escapeHtml(data.entryZone)}</b></div><div><small>STOP LEVEL</small><b>${escapeHtml(data.stopLoss)}</b></div></div><div class="targets">${targets}</div><small class="plan-rationale">${escapeHtml(data.planRationale || '')}</small></div>
    <div class="result-disclaimer">${escapeHtml(data.disclaimer)}</div>
  </div>`;
}
function escapeHtml(input) { return String(input ?? '').replace(/[&<>"']/g, c => ({'&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;',"'":'&#39;'}[c])); }

$('#browse').addEventListener('click', e => { e.stopPropagation(); fileInput.click(); });
dropZone.addEventListener('click', e => { if (e.target.closest('button')) return; fileInput.click(); });
fileInput.addEventListener('change', e => setFile(e.target.files[0]));
$('#remove-file').addEventListener('click', e => { e.stopPropagation(); clearFile(); });
assetSelect.addEventListener('change', updateButton);
dropZone.addEventListener('dragover', e => { e.preventDefault(); dropZone.classList.add('dragging'); });
dropZone.addEventListener('dragleave', () => dropZone.classList.remove('dragging'));
dropZone.addEventListener('drop', e => { e.preventDefault(); dropZone.classList.remove('dragging'); setFile(e.dataTransfer.files[0]); });
$('#analyze-btn').addEventListener('click', async () => {
  if (!state.file || !assetSelect.value || state.busy) return;
  if (previewMode) { showError('Open Market Lens through the Spring Boot app at http://localhost:8080 to run an analysis.'); return; }
  state.busy = true; updateButton(); $('#analyze-btn').innerHTML = '<span class="spinner"></span> Reading chart…';
  $('#result').innerHTML = '<div class="loading-result"><span class="loading-orbit"></span><h2>Analyzing your chart</h2><p>Reading chart structure and retrieving live market context. This can take a few seconds.</p><div class="loading-bar"><i></i></div></div>';
  const form = new FormData(); form.append('chart', state.file); form.append('symbol', assetSelect.value); form.append('timeframe', $('#timeframe').value);
  try {
    const response = await fetch('/api/analysis', { method: 'POST', body: form });
    const data = await response.json(); if (!response.ok) throw new Error(data.message || 'The analysis service could not complete the request.');
    $('#result').innerHTML = resultMarkup(data);
  } catch (error) { showError(error.message || 'Please try again.'); }
  finally { state.busy = false; $('#analyze-btn').innerHTML = '<span class="sparkle">↗</span> Analyze chart'; updateButton(); }
});
