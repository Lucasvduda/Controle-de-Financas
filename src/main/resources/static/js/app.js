const API = '';

// State
let currentPage = 'dashboard';
let dashboardData = null;

// Navigation
document.querySelectorAll('.nav-link').forEach(link => {
    link.addEventListener('click', e => {
        e.preventDefault();
        const page = link.dataset.page;
        navigateTo(page);
    });
});

function navigateTo(page) {
    currentPage = page;
    document.querySelectorAll('.nav-link').forEach(l => l.classList.remove('active'));
    document.querySelector(`[data-page="${page}"]`)?.classList.add('active');
    const titles = {
        dashboard: 'Dashboard',
        cartoes: 'Cartões de Crédito',
        gastos: 'Gastos',
        contas: 'Contas a Pagar',
        investimentos: 'Investimentos',
        objetivos: 'Objetivos de Economia',
        projecao: 'Projeção Financeira',
        configuracoes: 'Configurações'
    };
    document.getElementById('pageTitle').textContent = titles[page] || '';
    loadPage(page);
    if (window.innerWidth < 768) toggleSidebar();
}

function toggleSidebar() {
    document.getElementById('sidebar').classList.toggle('open');
}

// API Helper
async function api(path, method = 'GET', body = null) {
    const opts = { method, headers: { 'Content-Type': 'application/json' } };
    if (body) opts.body = JSON.stringify(body);
    const res = await fetch(API + path, opts);
    if (res.status === 204) return null;
    if (!res.ok) {
        const erro = await res.json().catch(() => null);
        const msg = erro?.message || erro?.errors?.map(e => e.defaultMessage).join(', ') || `Erro ${res.status}`;
        throw new Error(msg);
    }
    return res.json();
}

function formatMoney(v) {
    return new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' }).format(v || 0);
}

// Toast
function toast(msg, type = 'success') {
    const container = document.getElementById('toastContainer');
    const t = document.createElement('div');
    t.className = `toast toast-${type}`;
    t.textContent = msg;
    container.appendChild(t);
    setTimeout(() => t.remove(), 4000);
}

// Modal
function abrirModal(title, html) {
    document.getElementById('modalTitle').textContent = title;
    document.getElementById('modalBody').innerHTML = html;
    document.getElementById('modalOverlay').classList.add('open');
}

function fecharModal() {
    document.getElementById('modalOverlay').classList.remove('open');
}

// Page Loader
function loadPage(page) {
    const c = document.getElementById('content');
    switch (page) {
        case 'dashboard': loadDashboard(c); break;
        case 'cartoes': loadCartoes(c); break;
        case 'gastos': loadGastos(c); break;
        case 'contas': loadContas(c); break;
        case 'investimentos': loadInvestimentos(c); break;
        case 'objetivos': loadObjetivos(c); break;
        case 'projecao': loadProjecao(c); break;
        case 'configuracoes': loadConfiguracoes(c); break;
    }
}

// ==================== DASHBOARD ====================
async function loadDashboard(c) {
    c.innerHTML = '<p>Carregando...</p>';
    try {
        dashboardData = await api('/api/dashboard');
        const d = dashboardData;
        const saldoClass = (d.saldoMensal || 0) >= 0 ? 'text-success' : 'text-danger';
        c.innerHTML = `
            <div class="grid grid-4">
                <div class="summary-card">
                    <div class="label">Salário</div>
                    <div class="value text-success">${formatMoney(d.salario)}</div>
                </div>
                <div class="summary-card">
                    <div class="label">Investimentos</div>
                    <div class="value text-info">${formatMoney(d.totalInvestimentos)}</div>
                    <div class="sub">Rend: ${formatMoney(d.rendimentoMensal)}/mês</div>
                </div>
                <div class="summary-card">
                    <div class="label">Gastos Mensais</div>
                    <div class="value text-danger">${formatMoney(d.totalGastos)}</div>
                    <div class="sub">Fixos: ${formatMoney(d.totalGastosFixos)} | Var: ${formatMoney(d.totalGastosVariaveis)}</div>
                </div>
                <div class="summary-card">
                    <div class="label">Saldo Mensal</div>
                    <div class="value ${saldoClass}">${formatMoney(d.saldoMensal)}</div>
                    <div class="sub">${d.tempoRestante || '-'}</div>
                </div>
            </div>

            <div class="grid grid-2" style="margin-top:20px">
                <div class="card">
                    <div class="card-header"><h3>Alertas de Contas</h3></div>
                    <div class="grid grid-3" style="margin-bottom:16px">
                        <div style="text-align:center">
                            <div style="font-size:2rem;font-weight:700" class="text-danger">${d.contasAtrasadas}</div>
                            <div style="font-size:0.8rem;color:var(--text-muted)">Atrasadas</div>
                        </div>
                        <div style="text-align:center">
                            <div style="font-size:2rem;font-weight:700" class="text-warning">${d.contasVencendoHoje}</div>
                            <div style="font-size:0.8rem;color:var(--text-muted)">Vencem Hoje</div>
                        </div>
                        <div style="text-align:center">
                            <div style="font-size:2rem;font-weight:700" class="text-info">${d.contasVencendoSemana}</div>
                            <div style="font-size:0.8rem;color:var(--text-muted)">Esta Semana</div>
                        </div>
                    </div>
                    ${d.proximasContas && d.proximasContas.length > 0 ? `
                    <div class="table-container">
                        <table>
                            <thead><tr><th>Conta</th><th>Valor</th><th>Vencimento</th><th>Status</th></tr></thead>
                            <tbody>
                                ${d.proximasContas.map(ct => `
                                    <tr>
                                        <td>${ct.descricao}</td>
                                        <td>${formatMoney(ct.valor)}</td>
                                        <td>${ct.dataVencimento}</td>
                                        <td><span class="status status-${ct.status.toLowerCase()}">${statusLabel(ct.status)}</span></td>
                                    </tr>
                                `).join('')}
                            </tbody>
                        </table>
                    </div>` : '<div class="empty-state"><p>Nenhuma conta pendente</p></div>'}
                </div>

                <div class="card">
                    <div class="card-header"><h3>Gastos por Categoria</h3></div>
                    ${d.gastosPorCategoria && Object.keys(d.gastosPorCategoria).length > 0 ? `
                    <div>
                        ${Object.entries(d.gastosPorCategoria).map(([cat, val]) => {
                            const pct = d.totalGastos > 0 ? (val / d.totalGastos * 100).toFixed(1) : 0;
                            return `
                            <div style="margin-bottom:12px">
                                <div style="display:flex;justify-content:space-between;margin-bottom:4px">
                                    <span style="font-size:0.9rem">${cat}</span>
                                    <span style="font-size:0.9rem;font-weight:600">${formatMoney(val)} (${pct}%)</span>
                                </div>
                                <div class="progress-bar">
                                    <div class="progress-fill" style="width:${pct}%"></div>
                                </div>
                            </div>`;
                        }).join('')}
                    </div>` : '<div class="empty-state"><p>Nenhum gasto cadastrado</p></div>'}
                </div>
            </div>
        `;
        document.getElementById('alertBadge').textContent = d.contasAtrasadas + d.contasVencendoHoje;
    } catch (e) {
        c.innerHTML = '<div class="empty-state"><p>Erro ao carregar dashboard</p></div>';
    }
}

function statusLabel(s) {
    const m = { ATRASADA: 'Atrasada', VENCE_HOJE: 'Vence Hoje', PROXIMA: 'Próxima', NORMAL: 'Normal' };
    return m[s] || s;
}

// ==================== CARTÕES ====================
async function loadCartoes(c) {
    c.innerHTML = '<p>Carregando...</p>';
    const cartoes = await api('/api/cartoes');
    c.innerHTML = `
        <div style="margin-bottom:20px">
            <button class="btn btn-primary" onclick="modalNovoCartao()">+ Novo Cartão</button>
        </div>
        <div class="grid grid-3" id="cartoesGrid">
            ${cartoes.length === 0 ? '<div class="empty-state"><p>Nenhum cartão cadastrado</p></div>' :
            cartoes.map(ct => {
                const cor = ct.cor || '#6366f1';
                const usado = ct.limiteUsado || 0;
                const pct = ct.limiteTotal > 0 ? (usado / ct.limiteTotal * 100).toFixed(0) : 0;
                return `
                <div class="card" style="padding:0;overflow:hidden">
                    <div class="cartao-visual" style="background:linear-gradient(135deg, ${cor}, ${cor}cc)">
                        <div class="cartao-bandeira">${ct.bandeira || 'Cartão'}</div>
                        <div class="cartao-nome">${ct.nome}</div>
                        <div class="cartao-info">
                            <span>Fecha dia ${ct.diaFechamento}</span>
                            <span>Vence dia ${ct.diaVencimento}</span>
                        </div>
                    </div>
                    <div style="padding:16px">
                        <div style="display:flex;justify-content:space-between;margin-bottom:8px">
                            <span style="color:var(--text-muted);font-size:0.85rem">Limite usado</span>
                            <span style="font-weight:600">${formatMoney(usado)} / ${formatMoney(ct.limiteTotal)}</span>
                        </div>
                        <div class="progress-bar" style="margin-bottom:12px">
                            <div class="progress-fill" style="width:${pct}%;background:${Number(pct) > 80 ? 'var(--danger)' : ''}"></div>
                        </div>
                        <div class="actions">
                            <button class="btn btn-sm btn-primary" onclick="verCartao(${ct.id})">Ver Gastos</button>
                            <button class="btn btn-sm btn-success" onclick="modalGastoCartao(${ct.id})">+ Gasto</button>
                            <button class="btn btn-sm btn-danger" onclick="deletarCartao(${ct.id})">Excluir</button>
                        </div>
                    </div>
                </div>`;
            }).join('')}
        </div>
    `;
}

function modalNovoCartao() {
    abrirModal('Novo Cartão', `
        <div class="form-group"><label>Nome</label><input id="fCartaoNome" placeholder="Ex: Nubank"></div>
        <div class="form-group"><label>Bandeira</label><input id="fCartaoBandeira" placeholder="Ex: Mastercard"></div>
        <div class="form-group"><label>Limite Total (R$)</label><input type="number" id="fCartaoLimite" step="0.01"></div>
        <div class="form-row">
            <div class="form-group"><label>Dia Fechamento</label><input type="number" id="fCartaoFecha" min="1" max="31"></div>
            <div class="form-group"><label>Dia Vencimento</label><input type="number" id="fCartaoVence" min="1" max="31"></div>
        </div>
        <div class="form-group"><label>Cor</label><input type="color" id="fCartaoCor" value="#6366f1"></div>
        <button class="btn btn-primary btn-block" onclick="salvarCartao()">Salvar</button>
    `);
}

async function salvarCartao() {
    const nome = document.getElementById('fCartaoNome').value.trim();
    if (!nome) return toast('Preencha o nome do cartão', 'error');

    const data = {
        nome,
        bandeira: document.getElementById('fCartaoBandeira').value,
        limiteTotal: parseFloat(document.getElementById('fCartaoLimite').value) || 0,
        diaFechamento: parseInt(document.getElementById('fCartaoFecha').value) || 1,
        diaVencimento: parseInt(document.getElementById('fCartaoVence').value) || 10,
        cor: document.getElementById('fCartaoCor').value
    };
    try {
        await api('/api/cartoes', 'POST', data);
        fecharModal();
        toast('Cartão criado!');
        loadCartoes(document.getElementById('content'));
    } catch (e) {
        toast('Erro ao salvar cartão: ' + e.message, 'error');
    }
}

async function deletarCartao(id) {
    if (!confirm('Excluir este cartão e todos os gastos vinculados?')) return;
    await api(`/api/cartoes/${id}`, 'DELETE');
    toast('Cartão excluído!');
    loadCartoes(document.getElementById('content'));
}

async function verCartao(id) {
    const resumo = await api(`/api/cartoes/${id}`);
    abrirModal(`${resumo.nome} - Gastos`, `
        <div style="margin-bottom:16px">
            <div style="display:flex;justify-content:space-between">
                <span>Limite usado:</span><strong>${formatMoney(resumo.limiteUsado)}</strong>
            </div>
            <div style="display:flex;justify-content:space-between">
                <span>Disponível:</span><strong class="text-success">${formatMoney(resumo.limiteDisponivel)}</strong>
            </div>
        </div>
        ${resumo.gastos.length === 0 ? '<p class="text-muted">Nenhum gasto neste cartão</p>' : `
        <table>
            <thead><tr><th>Descrição</th><th>Valor</th><th>Parcela</th></tr></thead>
            <tbody>
                ${resumo.gastos.map(g => `
                    <tr>
                        <td>${g.descricao}</td>
                        <td>${formatMoney(g.valor)}</td>
                        <td>${g.parcelas > 1 ? g.parcelaAtual + '/' + g.parcelas : 'À vista'}</td>
                    </tr>
                `).join('')}
            </tbody>
        </table>`}
    `);
}

function modalGastoCartao(cartaoId) {
    abrirModal('Novo Gasto no Cartão', `
        <div class="form-group"><label>Descrição</label><input id="fGCDesc"></div>
        <div class="form-group"><label>Valor (R$)</label><input type="number" id="fGCValor" step="0.01"></div>
        <div class="form-group"><label>Categoria</label><select id="fGCCat"></select></div>
        <div class="form-row">
            <div class="form-group"><label>Parcelas</label><input type="number" id="fGCParcelas" value="1" min="1"></div>
            <div class="form-group"><label>Data</label><input type="date" id="fGCData"></div>
        </div>
        <button class="btn btn-primary btn-block" onclick="salvarGastoCartao(${cartaoId})">Salvar</button>
    `);
    loadCategoriaSelect('fGCCat');
}

async function salvarGastoCartao(cartaoId) {
    const descricao = document.getElementById('fGCDesc').value.trim();
    const valorTotal = parseFloat(document.getElementById('fGCValor').value) || 0;

    if (!descricao) return toast('Preencha a descrição', 'error');
    if (valorTotal <= 0) return toast('Informe um valor válido', 'error');

    const parc = parseInt(document.getElementById('fGCParcelas').value) || 1;
    const valorParcela = valorTotal / parc;
    const data = {
        descricao,
        valor: Math.round(valorParcela * 100) / 100,
        categoria: document.getElementById('fGCCat').value || null,
        tipo: 'VARIAVEL',
        parcelas: parc,
        parcelaAtual: 1,
        dataGasto: document.getElementById('fGCData').value || null
    };
    try {
        await api(`/api/cartoes/${cartaoId}/gastos`, 'POST', data);
        fecharModal();
        toast('Gasto adicionado ao cartão!');
        loadCartoes(document.getElementById('content'));
    } catch (e) {
        toast('Erro ao salvar gasto: ' + e.message, 'error');
    }
}

// ==================== GASTOS ====================
async function loadGastos(c) {
    c.innerHTML = '<p>Carregando...</p>';
    const gastos = await api('/api/gastos');
    const fixos = gastos.filter(g => g.tipo === 'FIXO');
    const variaveis = gastos.filter(g => g.tipo === 'VARIAVEL');
    c.innerHTML = `
        <div style="margin-bottom:20px">
            <button class="btn btn-primary" onclick="modalNovoGasto('FIXO')">+ Gasto Fixo</button>
            <button class="btn btn-success" onclick="modalNovoGasto('VARIAVEL')" style="margin-left:8px">+ Gasto Variável</button>
        </div>
        <div class="grid grid-2">
            <div class="card">
                <div class="card-header"><h3>Gastos Fixos</h3></div>
                ${fixos.length === 0 ? '<div class="empty-state"><p>Nenhum gasto fixo</p></div>' : `
                <table>
                    <thead><tr><th>Descrição</th><th>Valor</th><th>Categoria</th><th></th></tr></thead>
                    <tbody>
                        ${fixos.map(g => `
                            <tr>
                                <td>${g.descricao}</td>
                                <td class="text-danger">${formatMoney(g.valor)}</td>
                                <td>${g.categoria || '-'}</td>
                                <td><button class="btn btn-sm btn-danger" onclick="deletarGasto(${g.id})">X</button></td>
                            </tr>
                        `).join('')}
                        <tr style="font-weight:700">
                            <td>TOTAL</td>
                            <td class="text-danger">${formatMoney(fixos.reduce((a, g) => a + g.valor, 0))}</td>
                            <td></td><td></td>
                        </tr>
                    </tbody>
                </table>`}
            </div>
            <div class="card">
                <div class="card-header"><h3>Gastos Variáveis</h3></div>
                ${variaveis.length === 0 ? '<div class="empty-state"><p>Nenhum gasto variável</p></div>' : `
                <table>
                    <thead><tr><th>Descrição</th><th>Valor</th><th>Categoria</th><th></th></tr></thead>
                    <tbody>
                        ${variaveis.map(g => `
                            <tr>
                                <td>${g.descricao}</td>
                                <td class="text-danger">${formatMoney(g.valor)}</td>
                                <td>${g.categoria || '-'}</td>
                                <td><button class="btn btn-sm btn-danger" onclick="deletarGasto(${g.id})">X</button></td>
                            </tr>
                        `).join('')}
                        <tr style="font-weight:700">
                            <td>TOTAL</td>
                            <td class="text-danger">${formatMoney(variaveis.reduce((a, g) => a + g.valor, 0))}</td>
                            <td></td><td></td>
                        </tr>
                    </tbody>
                </table>`}
            </div>
        </div>
    `;
}

function modalNovoGasto(tipo) {
    const tipoLabel = tipo === 'FIXO' ? 'Fixo' : 'Variável';
    abrirModal(`Novo Gasto ${tipoLabel}`, `
        <div class="form-group"><label>Descrição</label><input id="fGDesc" placeholder="Ex: Aluguel, Mercado"></div>
        <div class="form-group"><label>Valor (R$)</label><input type="number" id="fGValor" step="0.01"></div>
        <div class="form-group"><label>Categoria</label><select id="fGCat"></select></div>
        <div class="form-group"><label>Data</label><input type="date" id="fGData"></div>
        <input type="hidden" id="fGTipo" value="${tipo}">
        <button class="btn btn-primary btn-block" onclick="salvarGasto()">Salvar</button>
    `);
    loadCategoriaSelect('fGCat');
}

async function salvarGasto() {
    const descricao = document.getElementById('fGDesc').value.trim();
    const valor = parseFloat(document.getElementById('fGValor').value) || 0;

    if (!descricao) return toast('Preencha a descrição', 'error');
    if (valor <= 0) return toast('Informe um valor válido', 'error');

    const data = {
        descricao,
        valor,
        categoria: document.getElementById('fGCat').value || null,
        tipo: document.getElementById('fGTipo').value,
        dataGasto: document.getElementById('fGData').value || null
    };
    try {
        await api('/api/gastos', 'POST', data);
        fecharModal();
        toast('Gasto adicionado!');
        loadGastos(document.getElementById('content'));
    } catch (e) {
        toast('Erro ao salvar gasto: ' + e.message, 'error');
    }
}

async function deletarGasto(id) {
    if (!confirm('Excluir este gasto?')) return;
    await api(`/api/gastos/${id}`, 'DELETE');
    toast('Gasto excluído!');
    loadGastos(document.getElementById('content'));
}

// ==================== CONTAS A PAGAR ====================
async function loadContas(c) {
    c.innerHTML = '<p>Carregando...</p>';
    let contas;
    try {
        contas = await api('/api/contas');
    } catch (e) {
        c.innerHTML = '<div class="empty-state"><p>Erro ao carregar contas: ' + e.message + '</p></div>';
        return;
    }
    const pendentes = contas.filter(ct => !ct.paga).sort((a, b) => a.dataVencimento.localeCompare(b.dataVencimento));
    const pagas = contas.filter(ct => ct.paga);
    const hoje = new Date().toISOString().split('T')[0];

    c.innerHTML = `
        <div style="margin-bottom:20px;display:flex;gap:8px;flex-wrap:wrap">
            <button class="btn btn-primary" onclick="modalNovaConta()">+ Nova Conta</button>
            <button class="btn btn-warning" onclick="enviarAlertas()">Enviar Alertas por Email</button>
        </div>
        <div class="card">
            <div class="card-header"><h3>Contas Pendentes (${pendentes.length})</h3></div>
            ${pendentes.length === 0 ? '<div class="empty-state"><p>Nenhuma conta pendente</p></div>' : `
            <div class="table-container">
                <table>
                    <thead><tr><th>Descrição</th><th>Valor</th><th>Vencimento</th><th>Status</th><th>Ações</th></tr></thead>
                    <tbody>
                        ${pendentes.map(ct => {
                            let status = 'normal', statusT = 'Normal';
                            if (ct.dataVencimento < hoje) { status = 'atrasada'; statusT = 'Atrasada'; }
                            else if (ct.dataVencimento === hoje) { status = 'hoje'; statusT = 'Vence Hoje'; }
                            return `<tr>
                                <td>${ct.descricao} ${ct.recorrente ? '🔄' : ''}</td>
                                <td>${formatMoney(ct.valor)}</td>
                                <td>${formatDate(ct.dataVencimento)}</td>
                                <td><span class="status status-${status}">${statusT}</span></td>
                                <td class="actions">
                                    <button class="btn btn-sm btn-success" onclick="pagarConta(${ct.id})">Pagar</button>
                                    <button class="btn btn-sm btn-danger" onclick="deletarConta(${ct.id})">X</button>
                                </td>
                            </tr>`;
                        }).join('')}
                    </tbody>
                </table>
            </div>`}
        </div>
        ${pagas.length > 0 ? `
        <div class="card" style="margin-top:20px">
            <div class="card-header"><h3>Contas Pagas (${pagas.length})</h3></div>
            <div class="table-container">
                <table>
                    <thead><tr><th>Descrição</th><th>Valor</th><th>Pago em</th></tr></thead>
                    <tbody>
                        ${pagas.slice(-10).reverse().map(ct => `
                            <tr style="opacity:0.6">
                                <td>${ct.descricao}</td>
                                <td>${formatMoney(ct.valor)}</td>
                                <td>${ct.dataPagamento ? formatDate(ct.dataPagamento) : '-'}</td>
                            </tr>
                        `).join('')}
                    </tbody>
                </table>
            </div>
        </div>` : ''}
    `;
}

function formatDate(d) {
    if (!d) return '-';
    const [y, m, day] = d.split('-');
    return `${day}/${m}/${y}`;
}

function modalNovaConta() {
    abrirModal('Nova Conta a Pagar', `
        <div class="form-group"><label>Descrição</label><input id="fCtDesc" placeholder="Ex: Conta de Luz"></div>
        <div class="form-group"><label>Valor (R$)</label><input type="number" id="fCtValor" step="0.01"></div>
        <div class="form-group"><label>Data Vencimento</label><input type="date" id="fCtData"></div>
        <div class="form-group"><label>Categoria</label><select id="fCtCat"></select></div>
        <div class="form-group">
            <label><input type="checkbox" id="fCtRecorrente"> Conta recorrente (repete todo mês)</label>
        </div>
        <div class="form-group"><label>Observação</label><textarea id="fCtObs" rows="2"></textarea></div>
        <button class="btn btn-primary btn-block" onclick="salvarConta()">Salvar</button>
    `);
    loadCategoriaSelect('fCtCat');
}

async function salvarConta() {
    const descricao = document.getElementById('fCtDesc').value.trim();
    const valor = parseFloat(document.getElementById('fCtValor').value) || 0;
    const dataVencimento = document.getElementById('fCtData').value;

    if (!descricao) return toast('Preencha a descrição', 'error');
    if (valor <= 0) return toast('Informe um valor válido', 'error');
    if (!dataVencimento) return toast('Selecione a data de vencimento', 'error');

    const data = {
        descricao,
        valor,
        dataVencimento,
        categoria: document.getElementById('fCtCat').value || null,
        recorrente: document.getElementById('fCtRecorrente').checked,
        observacao: document.getElementById('fCtObs').value
    };
    try {
        await api('/api/contas', 'POST', data);
        fecharModal();
        toast('Conta adicionada!');
        loadContas(document.getElementById('content'));
    } catch (e) {
        toast('Erro ao salvar conta: ' + e.message, 'error');
    }
}

async function pagarConta(id) {
    try {
        await api(`/api/contas/${id}/pagar`, 'PATCH');
        toast('Conta marcada como paga!');
        loadContas(document.getElementById('content'));
    } catch (e) {
        toast('Erro ao pagar conta: ' + e.message, 'error');
    }
}

async function deletarConta(id) {
    if (!confirm('Excluir esta conta?')) return;
    try {
        await api(`/api/contas/${id}`, 'DELETE');
        toast('Conta excluída!');
        loadContas(document.getElementById('content'));
    } catch (e) {
        toast('Erro ao excluir conta: ' + e.message, 'error');
    }
}

async function enviarAlertas() {
    try {
        const res = await api('/api/alertas/enviar', 'POST');
        toast(res.status || 'Alertas enviados!');
    } catch (e) {
        toast('Erro ao enviar alertas. Verifique a configuração de email.', 'error');
    }
}

// ==================== INVESTIMENTOS ====================
async function loadInvestimentos(c) {
    c.innerHTML = '<p>Carregando...</p>';
    const inv = await api('/api/investimentos');
    const total = inv.reduce((a, i) => a + (i.valorAplicado || 0), 0);
    const rendTotal = inv.reduce((a, i) => a + (i.valorAplicado * (i.taxaRendimentoMensal || 0.009)), 0);

    c.innerHTML = `
        <div style="margin-bottom:20px">
            <button class="btn btn-primary" onclick="modalNovoInvestimento()">+ Novo Investimento</button>
        </div>
        <div class="grid grid-3" style="margin-bottom:20px">
            <div class="summary-card">
                <div class="label">Total Investido</div>
                <div class="value text-info">${formatMoney(total)}</div>
            </div>
            <div class="summary-card">
                <div class="label">Rendimento Mensal</div>
                <div class="value text-success">${formatMoney(rendTotal)}</div>
            </div>
            <div class="summary-card">
                <div class="label">Rendimento Anual (est.)</div>
                <div class="value text-success">${formatMoney(rendTotal * 12)}</div>
            </div>
        </div>
        <div class="card">
            ${inv.length === 0 ? '<div class="empty-state"><p>Nenhum investimento cadastrado</p></div>' : `
            <table>
                <thead><tr><th>Nome</th><th>Tipo</th><th>Valor</th><th>Taxa Mensal</th><th>Rend. Mensal</th><th></th></tr></thead>
                <tbody>
                    ${inv.map(i => `
                        <tr>
                            <td>${i.nome}</td>
                            <td>${i.tipo || '-'}</td>
                            <td>${formatMoney(i.valorAplicado)}</td>
                            <td>${((i.taxaRendimentoMensal || 0.009) * 100).toFixed(2)}%</td>
                            <td class="text-success">${formatMoney(i.valorAplicado * (i.taxaRendimentoMensal || 0.009))}</td>
                            <td><button class="btn btn-sm btn-danger" onclick="deletarInvestimento(${i.id})">X</button></td>
                        </tr>
                    `).join('')}
                </tbody>
            </table>`}
        </div>
    `;
}

function modalNovoInvestimento() {
    abrirModal('Novo Investimento', `
        <div class="form-group"><label>Nome</label><input id="fInvNome" placeholder="Ex: CDB, Tesouro Direto"></div>
        <div class="form-group"><label>Tipo</label><input id="fInvTipo" placeholder="Ex: Renda Fixa, Ações"></div>
        <div class="form-group"><label>Valor Aplicado (R$)</label><input type="number" id="fInvValor" step="0.01"></div>
        <div class="form-group"><label>Taxa Rend. Mensal (%)</label><input type="number" id="fInvTaxa" step="0.01" value="0.9"></div>
        <div class="form-group"><label>Data Aplicação</label><input type="date" id="fInvData"></div>
        <button class="btn btn-primary btn-block" onclick="salvarInvestimento()">Salvar</button>
    `);
}

async function salvarInvestimento() {
    const nome = document.getElementById('fInvNome').value.trim();
    const valor = parseFloat(document.getElementById('fInvValor').value) || 0;

    if (!nome) return toast('Preencha o nome', 'error');
    if (valor <= 0) return toast('Informe um valor válido', 'error');

    const data = {
        nome,
        tipo: document.getElementById('fInvTipo').value,
        valorAplicado: valor,
        taxaRendimentoMensal: (parseFloat(document.getElementById('fInvTaxa').value) || 0.9) / 100,
        dataAplicacao: document.getElementById('fInvData').value || null
    };
    try {
        await api('/api/investimentos', 'POST', data);
        fecharModal();
        toast('Investimento adicionado!');
        loadInvestimentos(document.getElementById('content'));
    } catch (e) {
        toast('Erro ao salvar investimento: ' + e.message, 'error');
    }
}

async function deletarInvestimento(id) {
    if (!confirm('Excluir este investimento?')) return;
    await api(`/api/investimentos/${id}`, 'DELETE');
    toast('Investimento excluído!');
    loadInvestimentos(document.getElementById('content'));
}

// ==================== OBJETIVOS ====================
async function loadObjetivos(c) {
    c.innerHTML = '<p>Carregando...</p>';
    const objs = await api('/api/objetivos');
    c.innerHTML = `
        <div style="margin-bottom:20px">
            <button class="btn btn-primary" onclick="modalNovoObjetivo()">+ Novo Objetivo</button>
        </div>
        <div class="grid grid-2">
            ${objs.length === 0 ? '<div class="empty-state" style="grid-column:1/-1"><p>Nenhum objetivo cadastrado</p></div>' :
            objs.map(o => {
                const pct = o.valorMeta > 0 ? Math.min((o.valorAtual / o.valorMeta * 100), 100).toFixed(1) : 0;
                const falta = o.valorMeta - o.valorAtual;
                const meses = o.economiaMensal > 0 && falta > 0 ? Math.ceil(falta / o.economiaMensal) : 0;
                return `
                <div class="card">
                    <div class="card-header">
                        <h3>${o.icone || '🎯'} ${o.nome}</h3>
                        <button class="btn btn-sm btn-danger" onclick="deletarObjetivo(${o.id})">X</button>
                    </div>
                    <div style="display:flex;justify-content:space-between;margin-bottom:8px">
                        <span>${formatMoney(o.valorAtual)}</span>
                        <span>${formatMoney(o.valorMeta)}</span>
                    </div>
                    <div class="progress-bar" style="height:14px;margin-bottom:12px">
                        <div class="progress-fill" style="width:${pct}%"></div>
                    </div>
                    <div style="display:flex;justify-content:space-between;font-size:0.85rem;color:var(--text-muted)">
                        <span>${pct}% concluído</span>
                        <span>${meses > 0 ? meses + ' meses restantes' : (falta <= 0 ? 'Concluído!' : '-')}</span>
                    </div>
                    <div style="margin-top:12px;display:flex;gap:8px">
                        <input type="number" id="dep_${o.id}" placeholder="Valor" step="0.01" style="flex:1;padding:8px;background:var(--bg-input);border:1px solid var(--border);border-radius:6px;color:var(--text)">
                        <button class="btn btn-sm btn-success" onclick="depositarObjetivo(${o.id})">Depositar</button>
                    </div>
                </div>`;
            }).join('')}
        </div>
    `;
}

function modalNovoObjetivo() {
    abrirModal('Novo Objetivo', `
        <div class="form-group"><label>Nome</label><input id="fObjNome" placeholder="Ex: Viagem, Carro"></div>
        <div class="form-group"><label>Valor Meta (R$)</label><input type="number" id="fObjMeta" step="0.01"></div>
        <div class="form-group"><label>Economia Mensal (R$)</label><input type="number" id="fObjEco" step="0.01"></div>
        <div class="form-group"><label>Ícone (emoji)</label><input id="fObjIcone" value="🎯" maxlength="4"></div>
        <button class="btn btn-primary btn-block" onclick="salvarObjetivo()">Salvar</button>
    `);
}

async function salvarObjetivo() {
    const nome = document.getElementById('fObjNome').value.trim();
    const valorMeta = parseFloat(document.getElementById('fObjMeta').value) || 0;

    if (!nome) return toast('Preencha o nome do objetivo', 'error');
    if (valorMeta <= 0) return toast('Informe o valor da meta', 'error');

    const data = {
        nome,
        valorMeta,
        economiaMensal: parseFloat(document.getElementById('fObjEco').value) || 0,
        icone: document.getElementById('fObjIcone').value
    };
    try {
        await api('/api/objetivos', 'POST', data);
        fecharModal();
        toast('Objetivo criado!');
        loadObjetivos(document.getElementById('content'));
    } catch (e) {
        toast('Erro ao salvar objetivo: ' + e.message, 'error');
    }
}

async function depositarObjetivo(id) {
    const valor = parseFloat(document.getElementById(`dep_${id}`).value) || 0;
    if (valor <= 0) return toast('Informe um valor válido', 'error');
    await api(`/api/objetivos/${id}/depositar?valor=${valor}`, 'PATCH');
    toast('Depósito realizado!');
    loadObjetivos(document.getElementById('content'));
}

async function deletarObjetivo(id) {
    if (!confirm('Excluir este objetivo?')) return;
    await api(`/api/objetivos/${id}`, 'DELETE');
    toast('Objetivo excluído!');
    loadObjetivos(document.getElementById('content'));
}

// ==================== PROJEÇÃO ====================
async function loadProjecao(c) {
    c.innerHTML = '<p>Carregando...</p>';
    const proj = await api('/api/projecao?meses=24');
    if (!proj) { c.innerHTML = '<div class="empty-state"><p>Erro ao carregar projeção</p></div>'; return; }

    const meses = proj.projecaoMeses || [];
    const maxVal = Math.max(...meses.map(m => Math.max(Math.abs(m.saldoFim), 1)));

    c.innerHTML = `
        <div class="grid grid-4" style="margin-bottom:20px">
            <div class="summary-card">
                <div class="label">Saldo Mensal</div>
                <div class="value ${(proj.saldoMensal||0)>=0?'text-success':'text-danger'}">${formatMoney(proj.saldoMensal)}</div>
            </div>
            <div class="summary-card">
                <div class="label">Total Gastos</div>
                <div class="value text-danger">${formatMoney(proj.totalGastos)}</div>
            </div>
            <div class="summary-card">
                <div class="label">Rendimento/mês</div>
                <div class="value text-success">${formatMoney(proj.rendimentoMensal)}</div>
            </div>
            <div class="summary-card">
                <div class="label">Previsão</div>
                <div class="value text-warning" style="font-size:1.1rem">${proj.tempoRestante}</div>
            </div>
        </div>

        <div class="card">
            <div class="card-header"><h3>Projeção de Saldo (24 meses)</h3></div>
            <div class="chart-bar-container">
                ${meses.map(m => {
                    const h = Math.max(Math.abs(m.saldoFim) / maxVal * 100, 2);
                    const color = m.saldoFim >= 0 ? 'var(--success)' : 'var(--danger)';
                    return `<div class="chart-bar" style="height:${h}%;background:${color}">
                        <div class="tooltip">
                            <strong>${m.label}</strong><br>
                            Saldo: ${formatMoney(m.saldoFim)}<br>
                            Rend: ${formatMoney(m.rendimento)}
                        </div>
                    </div>`;
                }).join('')}
            </div>
            <div class="chart-labels">
                ${meses.map(m => `<span>${m.label}</span>`).join('')}
            </div>
        </div>

        <div class="card" style="margin-top:20px">
            <div class="card-header"><h3>Detalhamento Mês a Mês</h3></div>
            <div class="table-container">
                <table>
                    <thead><tr><th>Mês</th><th>Saldo Início</th><th>Rendimento</th><th>Entrada</th><th>Saída</th><th>Saldo Final</th></tr></thead>
                    <tbody>
                        ${meses.map(m => `
                            <tr>
                                <td>${m.label}</td>
                                <td>${formatMoney(m.saldoInicio)}</td>
                                <td class="text-success">${formatMoney(m.rendimento)}</td>
                                <td class="text-success">${formatMoney(m.entrada)}</td>
                                <td class="text-danger">${formatMoney(m.saida)}</td>
                                <td style="font-weight:700" class="${m.saldoFim>=0?'text-success':'text-danger'}">${formatMoney(m.saldoFim)}</td>
                            </tr>
                        `).join('')}
                    </tbody>
                </table>
            </div>
        </div>
    `;
}

// ==================== CONFIGURAÇÕES ====================
async function loadConfiguracoes(c) {
    c.innerHTML = '<p>Carregando...</p>';
    const config = await api('/api/configuracao');
    c.innerHTML = `
        <div class="card" style="max-width:600px">
            <div class="card-header"><h3>Configurações Gerais</h3></div>
            <div class="form-group">
                <label>Salário Mensal (R$)</label>
                <input type="number" id="cfgSalario" step="0.01" value="${config.salario || ''}">
            </div>
            <div class="form-group">
                <label>Email para Alertas</label>
                <input type="email" id="cfgEmail" value="${config.emailAlertas || ''}" placeholder="seu@email.com">
            </div>
            <div class="form-group">
                <label>Telefone (WhatsApp)</label>
                <input type="text" id="cfgTelefone" value="${config.telefoneAlertas || ''}" placeholder="(11) 99999-9999">
            </div>
            <div class="form-group">
                <label>Dias antes do vencimento para alertar</label>
                <input type="number" id="cfgDias" value="${config.diasAntesAlerta || 3}" min="1" max="30">
            </div>
            <div class="form-group">
                <label>
                    <input type="checkbox" id="cfgEmailAtivo" ${config.alertasEmailAtivos ? 'checked' : ''}>
                    Ativar alertas por email
                </label>
            </div>
            <button class="btn btn-primary btn-block" onclick="salvarConfiguracoes()">Salvar Configurações</button>
        </div>

        <div class="card" style="max-width:600px;margin-top:20px">
            <div class="card-header"><h3>Configuração de Email (SMTP)</h3></div>
            <p style="color:var(--text-muted);font-size:0.9rem;margin-bottom:16px">
                Para receber alertas por email, configure o SMTP no arquivo <code>application.properties</code>:
            </p>
            <div style="background:var(--bg);padding:16px;border-radius:8px;font-family:monospace;font-size:0.85rem;color:var(--text-muted)">
                spring.mail.host=smtp.gmail.com<br>
                spring.mail.port=587<br>
                spring.mail.username=seu@gmail.com<br>
                spring.mail.password=sua_senha_de_app<br>
            </div>
            <p style="color:var(--text-muted);font-size:0.85rem;margin-top:12px">
                Para Gmail: ative a verificação em 2 etapas e gere uma "Senha de App" em
                <a href="https://myaccount.google.com/apppasswords" target="_blank" style="color:var(--primary)">myaccount.google.com/apppasswords</a>
            </p>
        </div>
    `;
}

async function salvarConfiguracoes() {
    const data = {
        salario: parseFloat(document.getElementById('cfgSalario').value) || 0,
        emailAlertas: document.getElementById('cfgEmail').value,
        telefoneAlertas: document.getElementById('cfgTelefone').value,
        diasAntesAlerta: parseInt(document.getElementById('cfgDias').value) || 3,
        alertasEmailAtivos: document.getElementById('cfgEmailAtivo').checked
    };
    await api('/api/configuracao', 'PUT', data);
    toast('Configurações salvas!');
}

// ==================== HELPERS ====================
async function loadCategoriaSelect(id) {
    const cats = await api('/api/categorias');
    const sel = document.getElementById(id);
    if (!sel) return;
    sel.innerHTML = '<option value="">Selecione...</option>' +
        cats.map(c => `<option value="${c}">${c.replace(/_/g, ' ')}</option>`).join('');
}

// Init
loadPage('dashboard');
