# 🎮 Bug Survivor — LibGDX Survival Game

> Một game sinh tồn top-down 2D xây dựng bằng [libGDX](https://libgdx.com/).  
> Bạn là một lập trình viên bị bao vây bởi các con Bug — hãy sống sót càng lâu càng tốt!

---

## 📋 Mục lục

- [Yêu cầu hệ thống](#-yêu-cầu-hệ-thống)
- [Cách chạy game](#-cách-chạy-game)
- [Điều khiển](#-điều-khiển)
- [Cơ chế gameplay](#-cơ-chế-gameplay)
- [Hệ thống nâng cấp](#-hệ-thống-nâng-cấp)
- [Kẻ địch](#-kẻ-địch)
- [Cấu trúc project](#-cấu-trúc-project)
- [Build & Gradle](#-build--gradle)

---

## 💻 Yêu cầu hệ thống

| Thành phần | Yêu cầu tối thiểu |
|---|---|
| Java | JDK 8 trở lên (khuyến nghị JDK 17) |
| OS | Windows / macOS / Linux |
| RAM | 512 MB trống |
| GPU | Hỗ trợ OpenGL 2.0 |

---

## 🚀 Cách chạy game

### Chạy trực tiếp qua Gradle

```bash
# Windows
gradlew.bat lwjgl3:run

# macOS / Linux
./gradlew lwjgl3:run
```

### Build file JAR chạy độc lập

```bash
gradlew.bat lwjgl3:jar
# File JAR xuất ra tại: lwjgl3/build/libs/MyFirstGame-1.0.0.jar
java -jar lwjgl3/build/libs/MyFirstGame-1.0.0.jar
```

---

## 🕹️ Điều khiển

| Phím / Thao tác | Hành động |
|---|---|
| `W` `A` `S` `D` | Di chuyển nhân vật |
| *(Tự động)* | Bắn đạn vào Bug gần nhất trong tầm nhìn |
| `ESC` | Tạm dừng / Tiếp tục game |
| Chuột (Menu) | Chọn nâng cấp kỹ năng, nút Restart / Thoát |

> **Lưu ý:** Nhân vật **chỉ bắn tự động** khi có Bug xuất hiện trong vùng sáng (tầm nhìn).  
> Nếu không có Bug nào trong tầm nhìn, đạn sẽ **không được bắn**.

---

## ⚙️ Cơ chế gameplay

### 🌫️ Sương mù chiến tranh (Fog of War)
- Xung quanh nhân vật có một **vùng sáng hình tròn** — bạn chỉ thấy và bắn được kẻ địch trong vùng này.
- Tầm nhìn **thu hẹp lại** khi bạn bị đánh nhiều (cơ chế Stress) hoặc máu xuống thấp.
- Tầm nhìn **mở rộng** theo thời gian và khi chọn nâng cấp *Clear Vision*.

### ❤️ Máu & Stress
- Máu tối đa: **100 HP**. Khi về 0 → **Game Over**.
- Mỗi lần bị Bug tấn công → thanh **Stress tăng** → tầm nhìn giảm tạm thời.
- Stress tự giảm dần khi không bị đánh.

### 🪤 Bẫy (Trap)
- Các ô màu **cam-đỏ nhấp nháy** trên bản đồ là bẫy.
- Đứng trên bẫy sẽ bị trừ **20 HP/giây** liên tục.

### 🧱 Tường (Wall)
- Nhân vật **không thể đi xuyên tường**.
- Tường biên ngăn bạn ra khỏi bản đồ.

### 🌊 Hệ thống Wave (Đợt quái)
- Mỗi **20 giây** là một wave mới — Bug sinh ra nhanh hơn và mạnh hơn.
- Tốc độ Bug tăng theo công thức: `speed = baseSpeed × (1 + wave × 0.1)`
- Đến **wave 5, 10, 15...** → **Boss xuất hiện** kèm thông báo đỏ trên màn hình.

### 💎 Kinh nghiệm & Lên cấp
- Giết Bug → rơi **Gem xanh** (10 XP mỗi viên).
- Giết Boss → rơi **3 Gem vàng** (30 XP mỗi viên = **90 XP tổng**).
- Đủ XP → **lên cấp** → bảng chọn nâng cấp xuất hiện.
- XP cần lên cấp tăng 20% mỗi cấp.

---

## ⬆️ Hệ thống nâng cấp

Mỗi lần lên cấp, bạn chọn **1 trong 4** nâng cấp:

| Nâng cấp | Hiệu ứng |
|---|---|
| 🍬 **Syntax Sugar** | Tốc độ bắn +30% |
| 🧹 **Clean Code** | Hồi **30 HP** ngay lập tức |
| 🏛️ **Legacy Support** | Tốc độ di chuyển +50 px/s |
| 👁️ **Clear Vision** | Tầm nhìn cơ bản +20% |

---

## 👾 Kẻ địch

### Bug thường 🔴
| Thuộc tính | Giá trị |
|---|---|
| Máu | 50 HP |
| Tấn công | Chạm người (10 damage/giây) |
| Tầm phát hiện | ~780 px (lớn hơn tầm nhìn player) |
| XP khi chết | 10 XP |

- **Không di chuyển** cho đến khi player bước vào tầm phát hiện → **đột ngột lao vào**.
- Một số Bug (từ wave 3) có khả năng **tàng hình** — chỉ hiện ra khi player đến gần.
- Bị bắn trúng → lập tức **aggro** dù đang ở xa.

### Boss 👾 *(xuất hiện mỗi 5 wave)*
| Thuộc tính | Giá trị |
|---|---|
| Máu | **3000 HP** |
| Tấn công từ xa | 15 damage / 1.5 giây |
| Tầm đánh xa | **600 px** (bằng tầm nhìn player) |
| Tầm phát hiện | ~900 px |
| XP khi chết | **90 XP** (3 gem × 30 XP) |

- Boss **đứng yên** khi player trong tầm đánh — tấn công từ xa theo chu kỳ.
- Có **vòng tròn đỏ nhấp nháy** hiển thị tầm nguy hiểm.
- Thanh máu màu **vàng** để phân biệt với bug thường.
- Drop **3 gem vàng** scatter ngẫu nhiên quanh xác khi chết.

---

## 📁 Cấu trúc project

```
EX1/
├── assets/                         # Tài nguyên game
│   └── arial.ttf                   # Font hỗ trợ tiếng Việt
├── core/                           # Logic game chính
│   └── src/main/java/com/tuan/game/
│       ├── MyGdxGame.java          # Entry point, khởi tạo font/assets
│       ├── domain/                 # Các thực thể game
│       │   ├── Player.java         # Nhân vật người chơi
│       │   ├── Bug.java            # Kẻ địch (bug thường + boss)
│       │   ├── Projectile.java     # Đạn (trail + explosion effect)
│       │   └── ExperienceGem.java  # Viên kinh nghiệm
│       ├── manager/
│       │   └── Assets.java         # Quản lý texture (tạo từ Pixmap)
│       ├── screen/
│       │   ├── PlayScreen.java     # Màn hình chơi chính
│       │   └── GameOverScreen.java # Màn hình kết thúc
│       └── system/
│           ├── GameConfig.java     # Hằng số cấu hình toàn game
│           └── EnvironmentState.java # Quản lý fog/stress động
└── lwjgl3/                         # Desktop launcher
    └── src/main/java/.../lwjgl3/
        └── Lwjgl3Launcher.java     # Main class chạy trên desktop
```

---

## 🔧 Build & Gradle

```bash
# Chạy game
gradlew lwjgl3:run

# Build JAR
gradlew lwjgl3:jar

# Compile kiểm tra lỗi
gradlew core:compileJava

# Dọn build cũ
gradlew clean
```

### Các flag hữu ích

| Flag | Tác dụng |
|---|---|
| `--rerun-tasks` | Bắt buộc chạy lại task dù đã up-to-date |
| `--offline` | Dùng cache, không tải dependency mới |
| `--info` | Hiện log chi tiết |
| `--daemon` | Dùng Gradle daemon (build nhanh hơn) |

---

## 🛠️ Công nghệ sử dụng

- **[libGDX](https://libgdx.com/) 1.14.0** — game framework
- **gdx-freetype** — render font tiếng Việt sắc nét
- **Pixmap** — tạo texture procedural, không cần file ảnh ngoài
- **ShapeRenderer** — vẽ fog of war (donut technique), health bar, boss ring
- **Scene2D Stage** — UI pause menu, upgrade menu, game over screen
- **OrthographicCamera** — camera theo dõi nhân vật với clamp biên

---


