# 仓库管理系统

一个基于 Spring Boot 的自包含式仓库管理 Web 应用，面向中小型企业仓库业务，提供客户、供应商、商品、销售订单、采购订单、收货单、上架任务、拣货任务、发票和缺货订单等功能，并通过完整的身份认证机制保护系统访问，同时支持可选的基于邮件的双因素认证。

> **二次开发说明**
>
> 本项目基于开源项目 `rock-and-code/warehouse-management-system` 进行二次开发与中文化。在保留原项目核心架构、业务逻辑及开源许可的基础上，对系统页面、界面文本和项目文档进行了中文本地化，并将在后续继续进行功能完善。

![系统首页](docs/screenshots/03-dashboard-hero.png)

---

## 主要功能

- **运营数据仪表盘** —— 提供 KPI 数据卡片（未完成销售订单、未完成采购订单、待处理收货单、缺货订单），以及库存概览（库存价值、库存数量、缺货数量、低库存数量）。同时通过 Chart.js 展示每日销售趋势、年度供应商采购金额排名和采购订单状态，并提供需要补货的商品、超过 30 天未完成的采购订单等数据列表，以及销量前 5 的商品信息。

- **完整的身份认证机制** —— 使用 Spring Security 6 实现表单登录、BCrypt 密码加密，可选的**邮件双因素认证（2FA）**、基于邮件链接的密码重置，以及仅管理员可访问的 `/admin/users` 用户管理区域。

- **左侧导航界面** —— 使用深色固定侧边栏、Bootstrap Icons 和可折叠菜单组织各业务模块；移动端使用 Bootstrap Offcanvas 抽屉式导航。顶部导航栏显示当前页面标题、登录用户名以及退出登录入口。

- **真实的初始化测试数据** —— 首次启动时，系统会自动生成约 200 个销售订单、150 个采购订单、1100 个仓库库位、400 条库存记录，以及发票和缺货订单等数据。数据均位于当前年份内，因此系统启动后即可直接查看仪表盘统计图表。

- **技术栈** —— Java 17 · Spring Boot 3.2 · Spring Security 6 · Spring Data JPA · Thymeleaf · Bootstrap 5 · Chart.js 4 · H2（内存数据库）· Lombok · Maven。

---

## 系统截图

### 系统首页（仪表盘）

登录后进入系统首页。页面顶部展示 KPI 数据，中间展示库存概览、近 30 天每日销售趋势、供应商采购金额、采购订单状态等图表，同时提供补货列表、热销商品和采购订单逾期信息。

![系统首页](docs/screenshots/02-dashboard.png)

### 侧边栏导航

各业务模块通过侧边栏进行分类，并可以展开查看具体功能。顶部导航栏显示当前页面标题和当前登录用户。

![展开的侧边栏](docs/screenshots/04-sidebar-expanded.png)

### 登录

系统通过 `/login` 提供表单登录功能，同时提供忘记密码入口，可发送密码重置邮件。

![登录页面](docs/screenshots/01-login.png)

### 双因素认证

启用 2FA 的用户在正确输入密码后会进入 `/verify-2fa` 页面。

系统会生成 6 位验证码，并使用 BCrypt 对验证码进行哈希处理后发送邮件。如果没有配置 SMTP，验证码会作为开发环境备用方案输出到应用控制台。

![双因素认证](docs/screenshots/06-verify-2fa.png)

### 忘记密码

无论输入的邮箱是否对应系统账户，页面都会显示相同的提示信息，从而避免通过页面反馈判断账户是否存在。

![忘记密码](docs/screenshots/07-forgot-password.png)

### 管理员用户管理

管理员可以通过 `/admin/users` 管理系统用户，包括用户列表、新增、编辑、启用/禁用、切换 2FA 和删除用户等操作。

该功能仅允许 `ROLE_ADMIN` 用户访问。

![管理员用户管理](docs/screenshots/05-admin-users.png)

---

## 项目运行

项目运行需要：

- Java 17
- 项目自带的 Maven Wrapper

启动项目：

```bash
./mvnw spring-boot:run
```

启动成功后访问：

```text
http://localhost:8080
```

系统预置以下测试账户：

| 用户名 | 密码 | 角色 | 2FA |
|---|---|---|---|
| `admin` | `Password123!` | ADMIN | 关闭 |
| `manager` | `Password123!` | USER | 开启 |

使用 `manager` 登录时会进入双因素认证流程。

默认情况下系统没有配置 SMTP，因此 6 位验证码会输出到应用日志中，可以搜索：

```text
Your verification code is:
```

如果需要真正发送邮件，请继续阅读下面的邮件配置说明。

---

## 配置邮件服务

系统使用标准 Spring Mail 发送以下邮件：

- 双因素认证验证码
- 密码重置链接

如果没有配置邮件服务，`EmailService` 会将邮件正文输出到控制台，这种方式适合开发和测试环境。

### 1. 配置环境变量

项目根目录提供了 `.env.example` 文件。

复制为 `.env`：

```bash
cp .env.example .env
```

然后根据实际 SMTP 服务修改：

```bash
# .env
MAIL_HOST=smtp.gmail.com
MAIL_PORT=587
MAIL_USERNAME=you@gmail.com
MAIL_PASSWORD=your-app-password
MAIL_FROM=no-reply@yourdomain.com
APP_BASE_URL=http://localhost:8080
```

使用 Gmail 时，`MAIL_PASSWORD` 必须使用 **App Password（应用专用密码）**，而不是 Gmail 账户登录密码。

### 2. 启动前加载环境变量

Spring Boot 默认不会自动加载项目根目录下的 `.env` 文件，因此需要在启动前先将环境变量加载到当前 Shell：

```bash
set -a
source .env
set +a

./mvnw spring-boot:run
```

其中：

- `set -a` 会让 `source .env` 加载的变量自动对子进程可见。
- `set +a` 会关闭这一行为。
- Maven 启动 Spring Boot 后，`application.properties` 会通过 `${MAIL_HOST}` 等配置读取这些环境变量。

### 3. 为 `manager` 配置真实邮箱

默认的：

```text
manager@example.com
```

不会真正收到邮件。

可以使用：

```text
admin / Password123!
```

登录管理员账户，然后进入：

```text
用户管理 → manager → 更新 → 设置邮箱 → 保存
```

之后再次使用 `manager` 登录时，2FA 验证码就会发送到该邮箱。

如果不想通过页面操作，也可以直接进入 H2 Console：

```text
http://localhost:8080/h2-console
```

执行：

```sql
UPDATE app_user
SET email = 'you@yourdomain.com'
WHERE username = 'manager';
```

---

## 其他邮件配置方式

如果不希望使用 `.env` 加载方式，也可以选择以下方案。

### IntelliJ IDEA / VS Code 运行配置

可以直接将环境变量填写到运行配置的 **Environment variables** 中，无需在项目目录中保存 `.env` 文件。

### `application-local.properties`

可以创建：

```text
src/main/resources/application-local.properties
```

然后通过：

```text
--spring.profiles.active=local
```

启用本地配置。

如果该文件包含个人邮箱、密码等敏感信息，应将其加入 Git 忽略列表。

### spring-dotenv

也可以使用 `spring-dotenv`：

```text
https://github.com/paulschwarz/spring-dotenv
```

添加 Maven 依赖后，可以让 Spring 自动加载项目根目录下的 `.env` 文件。如果经常使用环境变量配置，这种方式会更加方便。

---

## 常用命令

启动项目：

```bash
./mvnw spring-boot:run
```

构建项目：

```bash
./mvnw clean package
```

运行全部测试：

```bash
./mvnw test
```

运行指定测试类：

```bash
./mvnw -Dtest=TrieTest test
```

运行指定测试方法：

```bash
./mvnw -Dtest=TrieTest#getWordList test
```

---

## H2 数据库控制台

项目提供 H2 Console：

```text
http://localhost:8080/h2-console
```

默认连接信息：

| 配置项 | 值 |
|---|---|
| JDBC URL | `jdbc:h2:mem:testdb` |
| 用户名 | `SA` |
| 密码 | 无 |

可以通过 H2 Console 对运行中的数据库进行临时 SQL 查询和调试。

---

## 核心业务流程

系统的入库和出库流程共享一套核心业务实体。

### 入库流程

```text
采购订单
    ↓
收货
    ↓
收货单（GRN）
    ↓
生成库存
    ↓
库存位于地面临时库位
    ↓
上架
    ↓
移动到实际仓库库位
```

具体流程：

1. **采购订单（Purchase Order）** → 收货时创建对应的**收货单（GRN）**。
2. 完成收货单后，在地面库位 `00-00-0-0` 创建库存记录。
3. **上架（Put-away）**将库存从地面临时库位移动到实际仓库库位。

### 出库流程

```text
销售订单
    ↓
生成拣货任务
    ↓
完成拣货
    ↓
扣减库存
    ↓
生成发票
    ↓
生成缺货订单
```

具体流程：

1. 保存**销售订单（Sales Order）**后，系统会自动创建对应的**拣货任务（Picking Job）**。
2. 完成拣货任务后，系统会扣减库存并生成**发票（Invoice）**。
3. 如果库存无法满足订单需求，则会创建对应的**缺货订单（Backorder）**。

仪表盘中的统计数据会展示这条业务链路中的各个环节。

---

## 项目结构

```text
src/main/java/com/example/warehouseManagement/
├── Bootstrap/           # CommandLineRunner，负责初始化内存数据库
├── Controllers/         # Spring MVC 控制器及 Thymeleaf 页面控制
├── Domains/             # JPA 实体及嵌套状态枚举
│   ├── DTOs/            # Native Query 返回的投影 DTO
│   └── Exceptions/      # Controller 捕获的领域异常
├── Repositories/        # Spring Data CrudRepository 接口及 Native Query
├── Security/            # SecurityConfig 和 2FA AuthenticationSuccessHandler
├── Services/            # 各业务能力对应的接口与实现类
├── Util/                # 独立工具类
└── DSA/                 # Trie 和 BinarySearchTree 数据结构
                         # （测试中使用）

src/main/resources/
├── application.properties
└── templates/
    ├── admin/               # 用户管理页面（ROLE_ADMIN）
    ├── auth/                # 登录、2FA、忘记/重置密码
    ├── customers/           # 客户管理
    ├── dashboard/           # 运营数据仪表盘
    ├── fragments/           # 公共 Header / Navbar / Footer
    ├── goodsReceiptNotes/   # 收货单
    ├── items/               # 商品
    ├── itemPrices/          # 商品价格
    ├── pickingJobs/         # 拣货任务
    ├── purchaseOrders/      # 采购订单
    ├── putAwayTasks/        # 上架任务
    ├── reports/             # 报表
    ├── salesOrders/         # 销售订单
    └── vendors/             # 供应商
```

---

## 注意事项

### 内存数据库

系统当前使用内存 H2 数据库。

每次重新启动应用时，数据库中的数据都会被清空，然后由 `Bootstrap` 重新生成初始化数据。

持久化存储目前属于后续规划内容，原项目规划采用基于文件的 H2 数据库以及 Flyway 数据库迁移。

### H2 特定 SQL

项目中的部分 Native Query 使用 H2 数据库方言，例如：

```text
TO_CHAR
DATEDIFF('DAY', …)
EXTRACT(WEEK FROM CURRENT_DATE())
NVL
```

如果后续更换数据库，需要针对报表和仪表盘相关 SQL 进行调整。

### 默认账户仅用于开发环境

项目中的默认账户和密码仅用于开发与测试。

如果将系统部署到非本地环境，应修改默认账户信息，或者删除初始化测试数据。

---

## 二次开发与中文化

本项目基于开源仓库管理系统进行二次开发。

目前主要完成：

- 系统界面中文本地化
- 公共导航栏中文化
- 登录、认证相关页面中文化
- 商品、客户、供应商等基础业务页面中文化
- 销售订单、采购订单、收货等业务页面中文化
- 拣货、上架、库存调整等仓储业务页面中文化
- 报表和搜索页面中文化
- 错误页面及公共组件中文化
- 项目 README 文档中文化

后续将在保留原项目核心架构的基础上继续进行功能完善和二次开发。

---

## License

本项目遵循 **MIT License**。

本项目是在原开源项目基础上的二次开发与中文化版本，请保留原项目作者及许可证信息。