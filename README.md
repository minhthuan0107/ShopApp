# 🛒 ShopApp

![Java](https://img.shields.io/badge/Java-11-red?style=for-the-badge&logo=java)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-2.x-brightgreen?style=for-the-badge&logo=springboot)
![Angular](https://img.shields.io/badge/Angular-12-DD0031?style=for-the-badge&logo=angular)
![MySQL](https://img.shields.io/badge/MySQL-8.0-blue?style=for-the-badge&logo=mysql)
![RabbitMQ](https://img.shields.io/badge/RabbitMQ-3.x-FF6600?style=for-the-badge&logo=rabbitmq)
![Docker](https://img.shields.io/badge/Docker-20.x-2496ED?style=for-the-badge&logo=docker)
---
## 📌 Giới thiệu
ShopApp là một hệ thống thương mại điện tử **full-stack** được phát triển với **Spring Boot** (Backend) và **Angular** (Frontend), hỗ trợ đầy đủ tính năng mua sắm trực tuyến cho khách hàng và quản trị viên.  
Ứng dụng tích hợp **thanh toán trực tuyến VNPAY**, **RabbitMQ** cho hệ thống gửi coupon, và **WebSocket** để nhận thông báo real-time.
---
## ⚙️ Công nghệ sử dụng
### Backend
- **Spring Boot** – Xây dựng REST API.
- **Spring Security** – Xác thực và phân quyền người dùng.
- **Spring Data JPA (Hibernate)** – Làm việc với MySQL.
- **MySQL** – Cơ sở dữ liệu quan hệ.
- **RabbitMQ** – Gửi coupon và thông báo, chạy bằng Docker.
- **WebSocket** – Thông báo real-time.
- **VNPAY** – Thanh toán trực tuyến.
- **Docker** – Deploy RabbitMQ và các dịch vụ.
### Frontend
- **Angular 12** – Xây dựng giao diện SPA.
- **TypeScript** – Ngôn ngữ chính cho frontend.
- **Bootstrap** – Giao diện responsive.
- **SCSS** – Tùy chỉnh style.
---
## 🧩 Chức năng

### 👤 Người dùng
- Đăng ký / Đăng nhập (JWT).
- Xem danh mục & sản phẩm.
- Tìm kiếm & lọc sản phẩm.
- Giỏ hàng – Thêm, xóa, cập nhật số lượng.
- Nhập mã Coupon khi thanh toán.
- Thay đổi mật khẩu.
- Theo dõi / Hủy đơn hàng.
- Thanh toán:
  - COD
  - **VNPAY**
- Yêu thích sản phẩm.
- Bình luận, đánh giá sản phẩm.
- Phản hồi bình luận của người khác.
- Thông báo real-time khi:
  - Đặt hàng thành công
  - Hủy đơn hàng
  - Nhận coupon mới
- Xem & Xóa thông báo.
- Nhận email khi đặt/hủy đơn.

### 🛠️ Quản trị viên (Admin)
- Thống kê doanh thu theo tháng, năm.
- Biểu đồ doanh thu theo từng năm.
- Quản lý người dùng (thêm, sửa, khóa/mở khóa).
- Quản lý sản phẩm (thêm, sửa, xóa mềm).
- Quản lý đơn hàng.
- Quản lý bình luận & đánh giá.
- Quản lý coupon (gửi qua RabbitMQ).
- Quản lý danh mục & thương hiệu.
- Quản lý hàng tồn kho.
- Danh sách sản phẩm bán chạy nhất.
- Thông báo khi có đơn đặt hoặc hủy.

---

## 📊 Kiến trúc hệ thống

```mermaid
graph TD
    A[Angular (Frontend)] -->|HTTP/HTTPS| B[Spring Boot REST API]
    B --> C[MySQL Database]
    B --> D[RabbitMQ (Docker)]
    B --> E[WebSocket Server]
    B --> F[VNPAY Payment Gateway]

🚀 Cách chạy dự án
1. Yêu cầu
Java 11+
Maven 4+
Node.js 16+
MySQL
Docker
2. Backend
bash
Sao chép
Chỉnh sửa
# Clone repo
git clone https://github.com/minhthuan0107/ShopApp.git
cd ShopApp/backend
# Chạy RabbitMQ bằng Docker
docker run -d --hostname rabbit-host --name rabbitmq-container \
  -p 5672:5672 -p 15672:15672 rabbitmq:3-management
# Import database
mysql -u root -p < database.sql
# Build & run Spring Boot
mvn clean install
mvn spring-boot:run
3. Frontend
npm install
ng serve
Truy cập user: http://localhost:4200
Truy cập admin: http://localhost:4300
📬 Liên hệ
Tác giả: Lê Thuận
Email: thuanminhle0107@gmail.com

