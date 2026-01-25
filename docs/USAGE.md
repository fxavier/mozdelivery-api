# Guia de Uso do Sistema MozDelivery API

Este guia descreve o fluxo completo de utilização da API, desde o onboarding de tenant/lojas (merchants) e registo de couriers, até autenticação, catálogo, pedidos, pagamentos, despacho, tracking e confirmação de entrega.

## Índice

1. [Base URL e documentação interativa](#base-url-e-documentação-interativa)
2. [Autenticação e autorização](#autenticação-e-autorização)
3. [Fluxo de onboarding (tenant → merchant → API key)](#fluxo-de-onboarding-tenant--merchant--api-key)
4. [Gestão de catálogo (catálogos → categorias → produtos)](#gestão-de-catálogo-catálogos--categorias--produtos)
5. [Fluxo do cliente registado (pedido end-to-end)](#fluxo-do-cliente-registado-pedido-end-to-end)
6. [Fluxo de guest checkout](#fluxo-de-guest-checkout)
7. [Fluxo de courier (registo, aprovação, tracking, entrega)](#fluxo-de-courier-registo-aprovação-tracking-entrega)
8. [Pagamentos e reembolsos](#pagamentos-e-reembolsos)
9. [Tracking e estado da entrega](#tracking-e-estado-da-entrega)

---

## Base URL e documentação interativa

- **Base URL (exemplo)**: `https://api.seu-dominio.tld`
- **Swagger UI**: `GET /swagger-ui/index.html`
- **OpenAPI**: `GET /v3/api-docs`

---

## Autenticação e autorização

A API suporta **JWT via OAuth2/OIDC** e **API Keys** para operações autenticadas.

### JWT (OAuth2/OIDC)

- Os tokens devem incluir `role` (ou `roles`) e `sub` como identificador do utilizador.
- Para merchants, o `merchant_id` é obrigatório no token.

**Exemplo de claims esperados**:

```json
{
  "sub": "user-123",
  "role": "MERCHANT",
  "merchant_id": "b1c34e12-9a4f-4f8d-9d3c-0c75fb2b6f73",
  "iat": 1733146852,
  "exp": 1733150452
}
```

**Header com JWT**:

```bash
curl -H "Authorization: Bearer <jwt>" \
  https://api.seu-dominio.tld/api/v1/orders
```

### API Key (para integrações de merchant)

- Pode ser enviada via `X-API-Key` ou `Authorization: ApiKey <chave>`.
- Ao autenticar com API key, o sistema assume o **role MERCHANT** e **scopes** associados.

**Criar API key**:

```bash
curl -X POST https://api.seu-dominio.tld/api/v1/api-keys \
  -H "Authorization: Bearer <jwt>" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Integração POS",
    "scopes": ["catalog:read", "catalog:write", "order:read", "order:write"],
    "expiresAt": "2026-12-31T23:59:59Z"
  }'
```

**Usar API key**:

```bash
curl -H "X-API-Key: <api-key>" \
  https://api.seu-dominio.tld/api/v1/catalogs/merchant/<merchantId>
```

### Scopes mais comuns

- `catalog:read`, `catalog:write`
- `order:read`, `order:write`
- `payment:read`, `payment:write`
- `dispatch:read`, `dispatch:write`
- `tracking:read`, `tracking:write`

---

## Fluxo de onboarding (tenant → merchant → API key)

### 1) Criar tenant

**Endpoint**: `POST /api/v1/tenants/onboard`

```bash
curl -X POST https://api.seu-dominio.tld/api/v1/tenants/onboard \
  -H "Content-Type: application/json" \
  -d '{
    "tenantName": "MozDelivery Maputo",
    "vertical": "RESTAURANT",
    "contactEmail": "ops@mozdelivery.co.mz",
    "contactPhone": "+258841234567",
    "businessAddress": "Av. 24 de Julho, Maputo"
  }'
```

### 2) Registar merchant

**Endpoint**: `POST /api/v1/merchants/register`

```bash
curl -X POST https://api.seu-dominio.tld/api/v1/merchants/register \
  -H "Content-Type: application/json" \
  -d '{
    "businessName": "Pizzaria Central",
    "displayName": "Pizzaria Central",
    "businessRegistrationNumber": "12345/2025",
    "taxId": "NUIT-987654",
    "contactEmail": "contacto@pizzaria.co.mz",
    "contactPhone": "+258843334444",
    "businessAddress": "Rua de Tchamba, Maputo",
    "city": "Maputo",
    "country": "MZ",
    "vertical": "RESTAURANT"
  }'
```

### 3) Aprovar merchant (admin)

**Endpoint**: `POST /api/v1/merchants/{merchantId}/approval`

```bash
curl -X POST https://api.seu-dominio.tld/api/v1/merchants/<merchantId>/approval \
  -H "Authorization: Bearer <jwt-admin>" \
  -H "Content-Type: application/json" \
  -d '{
    "merchantId": "<merchantId>",
    "approved": true,
    "reason": "Documentação validada",
    "reviewedBy": "admin@mozdelivery.co.mz"
  }'
```

### 4) Criar API key (merchant)

Ver exemplo na secção de [autenticação e autorização](#autenticação-e-autorização).

---

## Gestão de catálogo (catálogos → categorias → produtos)

> Requer autenticação de **MERCHANT/ADMIN** com scope `catalog:write`.

### 1) Criar catálogo

**Endpoint**: `POST /api/v1/catalogs`

```bash
curl -X POST https://api.seu-dominio.tld/api/v1/catalogs \
  -H "Authorization: Bearer <jwt>" \
  -H "Content-Type: application/json" \
  -d '{
    "merchantId": "<merchantId>",
    "name": "Menu Principal",
    "description": "Pizzas e bebidas",
    "displayOrder": 1
  }'
```

### 2) Criar categoria

**Endpoint**: `POST /api/v1/categories`

```bash
curl -X POST https://api.seu-dominio.tld/api/v1/categories \
  -H "Authorization: Bearer <jwt>" \
  -H "Content-Type: application/json" \
  -d '{
    "merchantId": "<merchantId>",
    "catalogId": "<catalogId>",
    "name": "Pizzas",
    "description": "Pizzas tradicionais e especiais",
    "imageUrl": "https://cdn.exemplo/pizzas.png",
    "displayOrder": 1
  }'
```

### 3) Criar produto

**Endpoint**: `POST /api/v1/products`

```bash
curl -X POST https://api.seu-dominio.tld/api/v1/products \
  -H "Authorization: Bearer <jwt>" \
  -H "Content-Type: application/json" \
  -d '{
    "merchantId": "<merchantId>",
    "categoryId": "<categoryId>",
    "name": "Pizza Margherita",
    "description": "Tomate, mozzarella e manjericão",
    "imageUrls": ["https://cdn.exemplo/pizza-margherita.jpg"],
    "price": 650.00,
    "currency": "MZN",
    "trackStock": true,
    "currentStock": 25,
    "lowStockThreshold": 5,
    "maxStock": 50
  }'
```

### 4) Ativar catálogo

**Endpoint**: `PUT /api/v1/catalogs/{catalogId}/activate`

```bash
curl -X PUT https://api.seu-dominio.tld/api/v1/catalogs/<catalogId>/activate \
  -H "Authorization: Bearer <jwt>"
```

---

## Fluxo do cliente registado (pedido end-to-end)

### 1) Descobrir merchants e produtos (público)

```bash
curl https://api.seu-dominio.tld/api/public/merchants?city=Maputo&vertical=RESTAURANT
curl https://api.seu-dominio.tld/api/public/merchants/<merchantId>/catalogs
curl https://api.seu-dominio.tld/api/public/catalogs/<catalogId>/categories
curl https://api.seu-dominio.tld/api/public/categories/<categoryId>/products
```

### 2) Criar pedido

**Endpoint**: `POST /api/v1/orders`

```bash
curl -X POST https://api.seu-dominio.tld/api/v1/orders \
  -H "Authorization: Bearer <jwt-client>" \
  -H "Content-Type: application/json" \
  -d '{
    "merchantId": "<merchantId>",
    "customerId": "<customerId>",
    "items": [
      {
        "productId": "<productId>",
        "productName": "Pizza Margherita",
        "quantity": 2,
        "unitPrice": 650.00
      }
    ],
    "deliveryAddress": {
      "street": "Av. Julius Nyerere",
      "city": "Maputo",
      "district": "Sommerschield",
      "postalCode": "1100",
      "country": "MZ",
      "latitude": -25.965530,
      "longitude": 32.589200,
      "deliveryInstructions": "Portão azul"
    },
    "paymentMethod": "MPESA",
    "currency": "MZN"
  }'
```

### 3) Confirmar pagamento do pedido

**Endpoint**: `POST /api/v1/orders/{orderId}/payment/confirm`

```bash
curl -X POST https://api.seu-dominio.tld/api/v1/orders/<orderId>/payment/confirm \
  -H "Authorization: Bearer <jwt-client>" \
  -H "Content-Type: application/json" \
  -d '{
    "paymentReference": "MPESA-REF-12345",
    "gatewayTransactionId": "gw-98765"
  }'
```

### 4) Acompanhar tracking do pedido

**Endpoint**: `GET /api/v1/tracking/orders/{orderId}`

```bash
curl -H "Authorization: Bearer <jwt-client>" \
  https://api.seu-dominio.tld/api/v1/tracking/orders/<orderId>
```

---

## Fluxo de guest checkout

### 1) Criar pedido guest

**Endpoint**: `POST /api/public/orders/guest`

```bash
curl -X POST https://api.seu-dominio.tld/api/public/orders/guest \
  -H "Content-Type: application/json" \
  -d '{
    "merchantId": "<merchantId>",
    "guestInfo": {
      "contactPhone": "+258841234567",
      "contactEmail": "cliente@exemplo.com",
      "contactName": "Ana Torres"
    },
    "items": [
      {
        "productId": "<productId>",
        "productName": "Pizza Margherita",
        "quantity": 1,
        "unitPrice": 650.00
      }
    ],
    "deliveryAddress": {
      "street": "Av. 25 de Setembro",
      "city": "Maputo",
      "district": "Baixa",
      "postalCode": "1100",
      "country": "MZ",
      "latitude": -25.968280,
      "longitude": 32.573500,
      "deliveryInstructions": "Chamar no intercom"
    },
    "paymentMethod": "CASH_ON_DELIVERY",
    "currency": "MZN"
  }'
```

### 2) Tracking guest

**Endpoint**: `GET /api/public/orders/guest/track?token=<token>`

```bash
curl "https://api.seu-dominio.tld/api/public/orders/guest/track?token=<trackingToken>"
```

### 3) Reenviar código de confirmação

**Endpoint**: `POST /api/public/orders/guest/resend-code?token=<token>`

```bash
curl -X POST "https://api.seu-dominio.tld/api/public/orders/guest/resend-code?token=<trackingToken>"
```

### 4) Converter guest em cliente registado

**Endpoint**: `POST /api/public/orders/guest/convert-to-customer?token=<token>&customerId=<customerId>`

```bash
curl -X POST "https://api.seu-dominio.tld/api/public/orders/guest/convert-to-customer?token=<trackingToken>&customerId=<customerId>"
```

---

## Fluxo de courier (registo, aprovação, tracking, entrega)

### 1) Registo de courier

**Endpoint**: `POST /api/v1/couriers/register`

```bash
curl -X POST https://api.seu-dominio.tld/api/v1/couriers/register \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "Carlos",
    "lastName": "Mabote",
    "email": "carlos@courier.co.mz",
    "phoneNumber": "+258847777777",
    "vehicleInfo": {
      "type": "MOTORCYCLE",
      "make": "Honda",
      "model": "CB125",
      "licensePlate": "ABC-123",
      "color": "Vermelha",
      "year": 2022
    },
    "deliveryCapacity": {
      "maxOrders": 4,
      "maxWeight": 12000,
      "maxVolume": 40000
    },
    "initialLocation": {
      "latitude": -25.969000,
      "longitude": 32.573000
    },
    "availabilitySchedule": {
      "weeklySchedule": {
        "MONDAY": {"startTime": "09:00:00", "endTime": "18:00:00"},
        "TUESDAY": {"startTime": "09:00:00", "endTime": "18:00:00"},
        "WEDNESDAY": {"startTime": "09:00:00", "endTime": "18:00:00"}
      },
      "availableDays": ["MONDAY", "TUESDAY", "WEDNESDAY"]
    },
    "city": "Maputo",
    "drivingLicenseNumber": "DL-998877",
    "emergencyContactName": "Maria Mabote",
    "emergencyContactPhone": "+258849999999",
    "notes": "Disponível para turnos noturnos"
  }'
```

### 2) Aprovação de courier (admin)

**Endpoint**: `POST /api/v1/admin/couriers/{deliveryPersonId}/approve`

```bash
curl -X POST https://api.seu-dominio.tld/api/v1/admin/couriers/<deliveryPersonId>/approve \
  -H "Authorization: Bearer <jwt-admin>" \
  -H "Content-Type: application/json" \
  -d '{
    "deliveryPersonId": "<deliveryPersonId>",
    "approvalStatus": "APPROVED",
    "reviewNotes": "Documentação validada",
    "reviewerComments": "admin@mozdelivery.co.mz"
  }'
```

### 3) Atualização de localização (tracking)

**Endpoint**: `PUT /api/v1/tracking/deliveries/{deliveryId}/location`

```bash
curl -X PUT "https://api.seu-dominio.tld/api/v1/tracking/deliveries/<deliveryId>/location?latitude=-25.9691&longitude=32.5742&accuracy=12.5&speed=18" \
  -H "Authorization: Bearer <jwt-courier>"
```

### 4) Confirmar entrega via código (DCC)

**Endpoint**: `POST /api/v1/delivery-confirmation/complete`

```bash
curl -X POST https://api.seu-dominio.tld/api/v1/delivery-confirmation/complete \
  -H "Authorization: Bearer <jwt-courier>" \
  -H "Content-Type: application/json" \
  -d '{
    "orderId": "<orderId>",
    "confirmationCode": "123456",
    "courierId": "<courierId>",
    "deliveryNotes": "Entregue ao porteiro"
  }'
```

---

## Pagamentos e reembolsos

### Criar pagamento

**Endpoint**: `POST /api/v1/payments`

```bash
curl -X POST https://api.seu-dominio.tld/api/v1/payments \
  -H "Authorization: Bearer <jwt>" \
  -H "Content-Type: application/json" \
  -d '{
    "orderId": "<orderId>",
    "amount": {
      "amount": 1300.00,
      "currency": "MZN"
    },
    "currency": "MZN",
    "paymentMethod": "MPESA",
    "description": "Pagamento do pedido"
  }'
```

### Processar pagamento

**Endpoint**: `POST /api/v1/payments/{paymentId}/process`

```bash
curl -X POST https://api.seu-dominio.tld/api/v1/payments/<paymentId>/process \
  -H "Authorization: Bearer <jwt>"
```

### Criar reembolso

**Endpoint**: `POST /api/v1/payments/refunds`

```bash
curl -X POST https://api.seu-dominio.tld/api/v1/payments/refunds \
  -H "Authorization: Bearer <jwt>" \
  -H "Content-Type: application/json" \
  -d '{
    "paymentId": "<paymentId>",
    "amount": {
      "amount": 650.00,
      "currency": "MZN"
    },
    "reason": "CUSTOMER_REQUEST",
    "description": "Cliente cancelou antes de preparo"
  }'
```

---

## Tracking e estado da entrega

Principais endpoints de tracking:

- `GET /api/v1/tracking/orders/{orderId}` → tracking por pedido
- `GET /api/v1/tracking/deliveries/{deliveryId}` → tracking por delivery
- `GET /api/v1/tracking/deliveries/{deliveryId}/eta` → ETA
- `GET /api/v1/tracking/deliveries/{deliveryId}/progress` → progresso (%)
- `GET /api/v1/tracking/deliveries/{deliveryId}/time-remaining` → tempo restante

---

## Notas finais

- Todos os **IDs** são UUIDs representados como strings nas chamadas HTTP.
- Para ambientes locais, combine este guia com a configuração do `docker-compose.yml` e os endpoints públicos disponíveis em `/api/public/**`.
- Em caso de dúvidas sobre permissões, utilize a matriz de permissões e roles do sistema.
