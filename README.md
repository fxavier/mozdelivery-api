# Sistema de Delivery — Especificação Detalhada

> Plataforma completa de encomendas e entregas para Android, iOS e Web (gestão e dashboards), cobrindo múltiplas verticais: restaurantes, mercearias (grocery), farmácias, bombas de combustível, lojas de conveniência, eletrónicos, floristas, bebidas, etc. Inclui transporte/expedição, rastreio em tempo real, cancelamentos/devoluções, pagamentos, faturação e relatórios.
>

---

## 1) Visão Geral & Objetivos

- **Objetivo**: Permitir que clientes façam pedidos de qualquer tipo de produto e recebam em casa, no trabalho, ou levantem em loja; que parceiros (lojas/restaurantes/farmácias/bombas) gerem catálogo, stock, preços e promoções; e que **entregadores** façam o last-mile com **rastreio em tempo real**.
- **Mercados alvo**: Moçambique, suportando métodos de pagamento locais (Multibanco, M-Pesa, e-wallets), e moedas múltiplas (USD, MZN, etc.).
- **Modelo**: Multi-tenant (várias marcas/operadores na mesma plataforma), multi-cidade, multi-moeda, multi-impostos, multi-catálogo.
- **Plataformas**:
    - **Cliente (Android/iOS)** em **Flutter** (BLoC/MobX), PWA opcional.
    - **Parceiro/Backoffice Web** (React/Next.js + Tailwind) para gestão e dashboards.
    - **Motor de backend** (Java 21 + **Spring Modulith**/Hexagonal + Postgres + Redis + Kafka + Keycloak).

### Documentação de uso

Consulte o guia detalhado de utilização da API em [`docs/USAGE.md`](docs/USAGE.md).

---

## 2) Personas & Papéis

- **Cliente**: pesquisa produtos, cria carrinho, escolhe entrega/levantamento, paga, acompanha pedido, avalia.
- **Parceiro (Loja/Restaurante/Farmácia/Bomba)**: gere catálogo/menus, stock, horários, janelas de entrega, preços, promoções, aceitação automática/manual de pedidos.
- **Operador Logístico / Entregador**: recebe atribuições, aceita/recusa, navega, recolhe, entrega, recolhe prova (foto/assinatura/OTP), reconcilia pagamentos em dinheiro.
- **Admin/Operações**: governa parâmetros, SLAs, zonas, tarifas, taxas, auditoria, suporte e resolução de incidentes.
- **Financeiro**: conciliação, comissões, faturação, payouts a parceiros e couriers.

---

## 3) Principais Funcionalidades (End-to-End)

### 3.1 Catálogo & Descoberta

- Categorias por vertical (restaurantes, mercearias, farmácias, combustíveis, etc.) e **subcategorias**.
- Pesquisa full-text com filtros (preço, distância, tempo de entrega, avaliação, aberto/agora, promoções, dietas: vegan/sem glúten, etc.).
- **Lojas destacadas**, ofertas relâmpago (flash), cupões, combos/menus e **substituições** inteligentes (grocery) em caso de rutura.

### 3.2 Carrinho, Checkout & Agendamento

- Carrinho com múltiplas lojas (opcional) ou por loja (recomendado para logística).
- **Entrega imediata** ou **agendada** por janela horária; **Levantamento em loja** (click & collect) e **Curbside**.
- Endereços guardados, instruções de entrega/portaria, preferência de contacto.
- Cálculo de **taxas**: entrega, embalagem, serviço, distância, pedágio, taxa noturna/chuva, taxa de alta demanda.
- **Promoções**: cupões (% ou valor fixo), primeira compra, fidelização, níveis VIP.
- **Impostos** por categoria (IVA, taxas municipais), faturação com NUIT, **recibos** e notas de crédito.

### 3.3 Pagamentos

- Métodos: **Cartão** (Visa/Mastercard), **Multibanco Ref.**, **Apple/Google Pay**, **M-Pesa** (Moçambique), **Carteira interna**, **CoD** (cash on delivery, opcional).
- **3DS/SCA**, tokenização, **reembolsos** total/parcial, split de pagamentos (marketplace) para parceiros e comissões da plataforma.
- Anti-fraude: limites, verificação de identidade, device fingerprint, score de risco.

### 3.4 Preparação & Fulfillment do Parceiro

- Painel de **Pedidos** (Novos / Em preparação / Prontos / Entregues / Cancelados).
- Impressão de talões/etiquetas, gestão de fila de cozinha (KDS), tempos estimados.
- Gestão de **stock** (SKU/variações), validade (perecíveis), lotes, farmacêuticos (anexos de receita, verificação obrigatória), combustíveis (litros, tipo, restrições).

### 3.5 Logística & Dispatching

- **Motor de despacho** (assignação automática/inteligente): por proximidade, carga de trabalho, rating, veículo (moto/carro/bicicleta), capacidade, **tempo real de trânsito**.
- **Zonas de entrega** (polígonos/raios), **preços por zona** e horários de pico.
- **Roteamento**: uma recolha/uma entrega, **multi-drop** (rota com várias paragens), **batching**.
- **Fallback manual**: operador pode reatribuir, agrupar, ou chamar frota terceirizada.

### 3.6 App do Entregador (Rider)

- Ir **Online/Offline**, ver mapa de **hotzones**, aceitar/recusar (com taxa de aceitação medível), penalizações anti-cherry-picking.
- Fluxos: **Pickup** (prova no balcão, OTP/QR), **Entrega** (foto/assinatura/OTP), recolha de **pagamento em dinheiro** se ativo.
- Integração **navegação** (Maps/Apple/Here), chat/VOIP com cliente/loja, **SOS** e suporte.
- **Carteira do courier**: ganhos por entrega/hora, bónus, gorjetas, levantamentos (payouts) e reconciliação de CoD.

### 3.7 Rastreio e Comunicação

- **Live tracking** com atualizações (Pedido recebido → Em preparação → A caminho → Entregue).
- ETA dinâmico (machine learning sobre históricos, clima, tráfego).
- **Notificações**: push, SMS, email, in-app; chat triádico (cliente ⇄ courier ⇄ loja) com media (foto/áudio).

### 3.8 Cancelamentos, Devoluções & Suporte

- Políticas por estado: antes da preparação (sem custo), após início da preparação (custo parcial), a caminho (taxa), entregue (RMA/devolução conforme categoria).
- Fluxos de **incidente**: item em falta, pedido danificado, atraso, **estorno parcial** por item, cupão compensatório.
- **Centro de ajuda** com FAQs, tickets, SLAs, playbooks, e **resolução assistida por IA** (classificação e sugestões).

### 3.9 Dashboards & Relatórios

- **Operações**: pedidos por hora/zona, tempos (aceitação, preparação, pickup, entrega), taxa de sucesso/cancelamento, Heatmaps.
- **Financeiro**: GMV, take-rate, comissões, custos logísticos, margem por vertical, reconciliações, reembolsos, chargebacks.
- **Parceiros**: vendas por SKU, rutura, taxa de preparação, avaliações, ranking.
- **Couriers**: produtividade, aceitação, on-time, incidentes, ganhos, quilometragem.
- **Clientes**: LTV, retenção/coorte, NPS, churn, funil de checkout.

---

## 4) Requisitos Não Funcionais

- **Segurança**: Keycloak (OAuth2/OIDC), RBAC/ABAC, encriptação (em trânsito/repouso), mascaramento de contacto (número virtual), conformidade **GDPR** e receita médica (pharma).
- **Escalabilidade**: arquitetura **modulith** com fronteiras claras (DDD), preparando migração a microserviços; cache em **Redis**, mensageria **Kafka**.
- **Disponibilidade**: SLO 99.9%+, zonas múltiplas, tolerância a falhas, **rate limiting** e **circuit breakers**.
- **Observabilidade**: logs estruturados, tracing distribuído (OpenTelemetry), métricas (Prometheus/Grafana), alertas.
- **Performance**: latência p95 < 300ms API core, geocálculo otimizado, pré-cálculo de tarifas.

---

## 5) Arquitetura Lógica (Hexagonal + Modulith)

**Módulos (domínios)**:

1. **Identity & Access**: utilizadores, papéis, tenants, sessões, KYC (courier), verificação de número/email.
2. **Partner**: lojas, horários, zonas, tax rules, contratos, comissionamento.
3. **Catalog & Inventory**: categorias, produtos/SKU, variações, unidades, lotes, stock, substituições, etiquetas nutricionais.
4. **Pricing & Promotions**: regras de preço, taxas, cupões, campanhas, dynamic pricing (surge/chuva/pico).
5. **Orders**: carrinho, checkout, orquestração de pedido, estados, itens, notas, anexos (receita), faturas.
6. **Payments**: gateways, split, carteira, reembolsos, conciliações.
7. **Dispatch & Routing**: matching, otimização, zonas, rotas, SLAs.
8. **Delivery**: pickups, POD (proof of delivery), incidentes de rota, reconciliação CoD.
9. **Messaging**: notificações, SMS, email, push, chat, VOIP.
10. **Analytics**: eventos, ETL/ELT, cubos, dashboards, exports.
11. **Support**: tickets, macros, playbooks, resoluções, auditoria.

**Tecnologias sugeridas**:

- **Backend**: Java 21, Spring Boot 3.5+, Spring Modulith, Spring Data JPA, Flyway, MapStruct, OpenAPI;
- **DB**: PostgreSQL (particionamento por tenant/cidade), PostGIS (geoespacial), Redis (cache/sessões), Kafka (eventos).
- **Infra**: Docker/Kubernetes, GitHub Actions CI/CD, S3/MinIO (media), CDN, feature flags.
- **Front Web**: Next.js/React + Tailwind + shadcn/ui; **Mobile**: Flutter (BLoC/MobX), Firebase Cloud Messaging.

---

## 6) Fluxos Críticos (Happy Path)

### 6.1 Pedido de Mercearia (Grocery)

1. Cliente pesquisa → adiciona itens com substituições preferidas;
2. Escolhe entrega imediata/agendada, endereço, instruções;
3. Checkout: cupão + método de pagamento;
4. Loja recebe → prepara → marca "Pronto";
5. Dispatch atribui courier → pickup → entrega com OTP/foto;
6. Cliente avalia → fatura enviada → dados para analytics.

### 6.2 Pedido com Receita (Farmácia)

1. Upload de **receita** (PDF/foto) e validação manual/assistida;
2. Itens controlados liberados; possíveis alternativas genéricas;
3. Courier com verificação adicional de idade/identidade na entrega;
4. Registo de conformidade e termos aceites.

### 6.3 Combustível/Convenience (Bombas)

1. Cliente seleciona bomba parceira, escolhe itens de loja/serviços;
2. Entrega: loja envia encomenda; combustível em si **não** é transportado (regra de segurança);
3. Itens de conveniência entregues normalmente.

---

## 7) Políticas de Cancelamento & Reembolso (Exemplos)

- **Antes da preparação**: reembolso total.
- **Durante a preparação**: taxa X% ou custo dos itens já preparados.
- **A caminho**: taxa de deslocação + custos irrecuperáveis.
- **Pós-entrega**: apenas por defeito/dano/qualidade; processo RMA.
- **No-show cliente**: tentativa de contacto 2x + espera 10 min + devolução/descartar conforme categoria.

---

## 8) Modelos de Dados (Resumo)

- **User**(id, nome, email, telefone, roles, tenant_id)
- **Address**(id, user_id, geohash, lat/lng, instruções)
- **Partner**(id, tipo, nome, NIF, IBAN, horário, zonas)
- **Category/Product/SKU**(id, attrs, allergens, prescrições)
- **Inventory**(sku_id, qty, lote, validade)
- **Order**(id, user_id, partner_id, estado, total, impostos, taxas, janela)
- **OrderItem**(order_id, sku_id, qty, preço, substituição)
- **Payment**(order_id, método, status, split, refund)
- **DispatchJob**(order_id, courier_id, estado, rota, ETA)
- **DeliveryEvent**(job_id, tipo, timestamp, payload)
- **Invoice/CreditNote**(refs fiscais)
- **Ticket**(id, order_id, tipo, estado)

---

## 9) APIs (Esboço REST)

- `POST /auth/login`, `POST /auth/register`
- `GET /catalog/categories`, `GET /catalog/partners?near=lat,lng`, `GET /catalog/products?query=`
- `POST /cart/items`, `GET /cart`, `POST /checkout`
- `POST /orders`, `GET /orders/{id}`, `POST /orders/{id}/cancel`
- `POST /payments/{orderId}/confirm`, `POST /payments/{orderId}/refund`
- `POST /dispatch/assign`, `POST /delivery/{jobId}/pickup`, `POST /delivery/{jobId}/pod`
- `GET /tracking/{orderId}` (SSE/WebSocket)
- `POST /support/tickets`

(OpenAPI + versionamento; webhooks para eventos a parceiros.)

---

## 10) Integrações & Locais

- **Mapas/Geocoding/ETA**: Google/Here/OSM; PostGIS para zonas/raios.
- **Pagamentos**: Stripe/Adyen + **MB Way/Multibanco**; **M-Pesa** via gateway local.
- **Comunicações**: Twilio/MessageBird, Firebase Push, máscara de chamadas.
- **Fiscal/Contabilidade**: SAF-T/PT, ATCUD, numeração, export para ERP.

---

## 11) Conformidade & Governança

- **GDPR**: consentimentos, DPO, portabilidade, esquecimento, retenção por categoria de dado.
- **Farmácia**: validação de receita, restrições por substância, controlo de idade.
- **KYC Couriers**: carta, registo criminal, seguro, inspeção do veículo.
- **Segurança Alimentar**: gestão de temperaturas (campo opcional), alergénios.

---

## 12) Operação & SLA

- **Tempo alvo**: aceite < 2 min; preparação 10–20 min; pickup < 5 min; entrega < 30 min urbano.
- **On-call** 24/7, runbooks, rollback/feature flags, Chaos tests.
- **KPIs**: On-time rate, cancel rate, AOV, GMV, take-rate, LTV, NPS, First Contact Resolution, Courier Utilization.

---

## 13) Roadmap de MVP → V1.0

**MVP (8–12 semanas)**:

- Catálogo básico, pedidos, pagamentos (MB Way + Cartão), dispatch simples por proximidade, tracking, app courier básica, backoffice parceiro + dashboards essenciais.

**V1.0**:

- Multi-drop, surge pricing, substituições grocery, farmácia com receita, split de pagamento a parceiros, faturação fiscal PT, analytics avançado, suporte com macros/IA.

---

## 14) UX & Design (Mobile)

- **Cliente**: home com carrosséis, busca com sugestões, filtros rápidos, carrinho sticky, ETA na loja, tracking mapa em tempo real.
- **Courier**: toggle online, fila de pedidos, mapas claros, passos explícitos (pickup → on-route → delivered), botão SOS, resumo de ganhos.
- **Acessibilidade**: contraste AA, tamanhos dinâmicos, suportes de idioma (pt-PT, pt-MZ, en), RTL ready.

---

## 15) Riscos & Mitigações

- **Fraude**: 3DS, limites, device binding, análise de padrões (ML), bloqueios por IP.
- **Picos de procura**: surge pricing, filas, espera informada, parcerias com frota.
- **Rutura de stock**: substituições e comunicação proativa, SLAs de atualização de stock.
- **Clima/Tráfego**: ETAs ajustados, taxa clima, incentivo a couriers.

---

## 16) Extensões Futuras

- **Assinaturas** (grocery semanal), **dark stores**, **loyalty** por níveis, **publicidade** no app (sponsored listings),
- **Roteamento multi-parceiro** (shopper + driver), **armários inteligentes** (lockers), **drones**/AGVs piloto.

---

## 17) Matriz de Permissões (exemplos)

- **Cliente**: criar/editar endereço, pagar, cancelar conforme política, avaliar.
- **Parceiro**: gerir catálogo/stock, aceitar pedidos, emitir faturas, ver relatórios da própria loja.
- **Courier**: ver/aceitar jobs, POD, gerir disponibilidade, ver ganhos próprios.
- **Admin**: tudo + auditoria, zonas, taxas globais, gestão de usuários/tenants.

---

## 18) Métricas de Qualidade & Testes

- **Testes**: unit (>=85% core), integração (Testcontainers), e2e (Cypress/Appium), carga (k6), contrato (OpenAPI), segurança (SAST/DAST).
- **Qualidade**: SonarQube, lint, pré-commit, revisão obrigatória, trunk-based.

---

## 19) Entregáveis do Projeto

- Código fonte (Backend + Apps + Web), Infra-as-Code (Terraform), pipelines CI/CD.
- Documentação arquitetural (ADR, C4), OpenAPI, playbooks de operação, manuais de utilizador.
- Catálogo de APIs e Postman Collection, dados seed (tenants/demo), scripts de migração.

---
