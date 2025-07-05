package com.bkap.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.bkap.entity.Order;
import com.bkap.entity.OrderDetail;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendHtmlEmail(String to, String subject, String htmlContent) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true); // true để gửi HTML

            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Lỗi khi gửi email: " + e.getMessage());
        }
    }
    
    public String generateOrderEmailHTML(Order order, boolean includeConfirmLink) {
        StringBuilder sb = new StringBuilder();
        sb.append("<h2>🛒 Cảm ơn bạn đã đặt hàng tại Decor!</h2>");
        sb.append("<p>Đơn hàng #" + order.getId() + " của bạn đã được tạo.</p>");

        if (includeConfirmLink) {
            sb.append("<p>Vui lòng xác nhận đơn hàng bằng cách bấm vào nút bên dưới:</p>");
            sb.append("<a href='http://localhost:8080/api/order/confirm?id=" + order.getId() + "' " +
                      "style='padding: 10px 20px; background-color: #28a745; color: white; text-decoration: none;'>Xác nhận đơn hàng</a>");
        } else {
            sb.append("<p>Đơn hàng của bạn đã được thanh toán thành công qua VNPAY.</p>");
        }

        sb.append("<h3>📦 Chi tiết đơn hàng:</h3>");
        sb.append("<table border='1' cellpadding='10' cellspacing='0' style='border-collapse: collapse;'>");
        sb.append("<tr><th>Sản phẩm</th><th>Số lượng</th><th>Giá</th></tr>");

        double total = 0;

        for (OrderDetail detail : order.getOrderDetails()) {
            double itemTotal = detail.getPrice() * detail.getQuantity();
            total += itemTotal;

            sb.append("<tr>")
              .append("<td>").append(detail.getProduct().getName()).append("</td>")
              .append("<td>").append(detail.getQuantity()).append("</td>")
              .append("<td>").append(String.format("%,.0f", itemTotal)).append("đ</td>")
              .append("</tr>");
        }

        sb.append("<tr style='font-weight: bold;'>")
          .append("<td colspan='2' style='text-align: right;'>Tổng cộng:</td>")
          .append("<td>").append(String.format("%,.0f", total)).append("đ</td>")
          .append("</tr>");

        sb.append("</table>");
        return sb.toString();
    }


}

