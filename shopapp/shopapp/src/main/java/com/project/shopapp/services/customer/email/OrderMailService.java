package com.project.shopapp.services.customer.email;

import com.project.shopapp.models.Order;
import com.project.shopapp.models.OrderDetail;
import com.project.shopapp.repositories.OrderDetailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.List;

@Service
public class OrderMailService {
    @Autowired
    private OrderDetailRepository orderDetailRepository;
    @Autowired
    private EmailService emailService;

    public void sendMailOrder(Order order) {
        SimpleDateFormat dt = new SimpleDateFormat("dd-MM-yyyy");
        List<OrderDetail> listOrderDetails = orderDetailRepository.findByOrder(order);
        StringBuilder content = new StringBuilder();
        content.append(HEADER);
        for (OrderDetail oderDetail : listOrderDetails) {
            content.append("<tr>\r\n"
                    + "                                                    <td width=\"25%\" align=\"left\" style=\"font-family: Open sans-serif; font-size: 18px; font-weight: 400; line-height: 24px; padding: 15px 10px 5px 10px;text-align: center;\">\r\n"
                    + "                                                        <img style=\"width: 85%;\" src="
                    + oderDetail.getProduct().getUrlImage() + ">\r\n"
                    + "                                                    </td>\r\n"
                    + "                                                    <td width=\"25%\" align=\"left\" style=\"font-family: Open sans-serif; font-size: 18px; font-weight: 400; line-height: 24px; padding: 15px 10px 5px 10px;\"> "
                    + oderDetail.getProduct().getName() + " </td>\r\n"
                    + "                                                    <td width=\"25%\" align=\"left\" style=\"font-family: Open sans-serif; font-size: 18px; font-weight: 400; line-height: 24px; padding: 15px 10px 5px 10px;\"> "
                    + oderDetail.getQuantity() + " </td>\r\n"
                    + "                                                    <td width=\"25%\" align=\"left\" style=\"font-family: Open sans-serif; font-size: 18px; font-weight: 400; line-height: 24px; padding: 15px 10px 5px 10px;\"> "
                    + format(String.valueOf(oderDetail.getUnitPrice())) + " </td>\r\n"
                    + "                                                </tr>");
        }
        content.append(BODY2);
        content.append(
                "<td width=\"55%\" align=\"left\" style=\"font-family: Open sans-serif; font-size: 20px; font-weight: 800; line-height: 24px; padding: 10px; border-top: 3px solid #eeeeee; border-bottom: 3px solid #eeeeee;\"> Tổng tiền: </td>\r\n"
                        + "                                                    <td width=\"25%\" align=\"left\" style=\"font-family: Open sans-serif; font-size: 20px; font-weight: 800; line-height: 24px; padding: 10px; border-top: 3px solid #eeeeee; border-bottom: 3px solid #eeeeee; color: red;\"> "
                        + format(String.valueOf(order.getTotalPrice())) + " </td>");
        content.append(BODY3);
        content.append(
                "<td align=\"center\" valign=\"top\" style=\"font-family: Open sans-serif; font-size: 20px; font-weight: 400; line-height: 24px;\">\r\n"
                        + "                                                            <p style=\"font-weight: 800;\">Địa chỉ giao hàng</p>\r\n"
                        + "                                                            <p>" + order.getAddress()
                        + "</p>\r\n" + "                                                        </td>\r\n"
                        + "                                                    </tr>\r\n"
                        + "                                                </table>\r\n"
                        + "                                            </div>\r\n"
                        + "                                            <div style=\"display:inline-block; max-width:50%; min-width:240px; vertical-align:top; width:100%;\">\r\n"
                        + "                                                <table align=\"left\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" style=\"max-width:300px;\">\r\n"
                        + "                                                    <tr>\r\n"
                        + "                                                        <td align=\"center\" valign=\"top\" style=\"font-family: Open sans-serif; font-size: 20px; font-weight: 400; line-height: 24px;\">\r\n"
                        + "                                                            <p style=\"font-weight: 800;\">Ngày đặt hàng</p>\r\n"
                        + "                                                            <p>"
                        + dt.format(order.getOrderDate()) + "</p>\r\n"
                        + "                                                        </td>\r\n"
                        + "                                                    </tr>\r\n"
                        + "                                                </table>\r\n"
                        + "                                            </div>\r\n"
                        + "                                            <div style=\"display:inline-block; max-width:50%; min-width:240px; vertical-align:top; width:100%;\">\r\n"
                        + "                                                <table align=\"left\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" style=\"max-width:300px;\">\r\n"
                        + "                                                    <tr>\r\n"
                        + "                                                        <td align=\"center\" valign=\"top\" style=\"font-family: Open sans-serif; font-size: 20px; font-weight: 400; line-height: 24px;\">\r\n"
                        + "                                                            <p style=\"font-weight: 800;\">Tên người nhận</p>\r\n"
                        + "                                                            <p>" + order.getFullName()
                        + "</p>\r\n" + "                                                        </td>\r\n"
                        + "                                                    </tr>\r\n"
                        + "                                                </table>\r\n"
                        + "                                            </div>\r\n"
                        + "                                            <div style=\"display:inline-block; max-width:50%; min-width:240px; vertical-align:top; width:100%;\">\r\n"
                        + "                                                <table align=\"left\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" style=\"max-width:300px;\">\r\n"
                        + "                                                    <tr>\r\n"
                        + "                                                        <td align=\"center\" valign=\"top\" style=\"font-family: Open sans-serif; font-size: 20px; font-weight: 400; line-height: 24px;\">\r\n"
                        + "                                                            <p style=\"font-weight: 800;\">Số điện thoại</p>\r\n"
                        + "                                                            <p>" + order.getPhoneNumber()
                        + "</p>\r\n" + "                                                        </td>");
        content.append(FOOTER);
        emailService.queue(order.getEmail(), "Đặt hàng thành công", content.toString());
    }

    public String format(String number) {
        DecimalFormat formatter = new DecimalFormat("###,###,###.##");
        return formatter.format(Double.valueOf(number)) + " VNĐ";
    }

    static String HEADERDELIVER = "<body style=\"margin: 0 !important; padding: 0 !important; background-color: #fcb800;\" bgcolor=\"#fcb800\">\r\n"
            + "    <div style=\"display: none; font-size: 1px; color: #fefefe; line-height: 1px; font-family: Open Sans, Helvetica, Arial, sans-serif; max-height: 0px; max-width: 0px; opacity: 0; overflow: hidden;\">\r\n"
            + "        Vegana Store chuyên bán thực phẩm sạch\r\n" + "    </div>\r\n"
            + "    <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\">\r\n" + "        <tr>\r\n"
            + "            <td align=\"center\" style=\"background-color: #eeeeee;\" bgcolor=\"#eeeeee\">\r\n"
            + "                <table align=\"center\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" style=\"max-width:600px;\">\r\n"
            + "                    <tr>\r\n"
            + "                        <td align=\"center\" valign=\"top\" style=\"font-size:0; padding: 35px;\" bgcolor=\"#fcb800\">\r\n"
            + "                            <div style=\"display:inline-block; max-width:50%; min-width:100px; vertical-align:top; width:100%;\">\r\n"
            + "                                <table align=\"left\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" style=\"max-width:300px;\">\r\n"
            + "                                    <tr>\r\n"
            + "                                        <td align=\"left\" valign=\"top\" style=\"font-family: Open Sans, Helvetica, Arial, sans-serif; font-size: 36px; font-weight: 800; line-height: 48px;\" class=\"mobile-center\">\r\n"
            + "                                            <img src=\"https://res.cloudinary.com/martfury/image/upload/v1647788678/users/j53qiooahnkiq2jmz9lk.png\" width=\"220px\"/>\r\n"
            + "                                        </td>\r\n" + "                                    </tr>\r\n"
            + "                                </table>\r\n" + "                            </div>\r\n"
            + "                            \r\n" + "                        </td>\r\n" + "                    </tr>\r\n"
            + "                    <tr>\r\n"
            + "                        <td align=\"center\" style=\"padding: 35px 35px 20px 35px; background-color: #ffffff;\" bgcolor=\"#ffffff\">\r\n"
            + "                            <table align=\"center\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" style=\"max-width:600px;\">\r\n"
            + "                                <tr>\r\n"
            + "                                    <td align=\"center\" style=\"font-family: Open sans-serif; font-size: 16px; font-weight: 400; line-height: 24px; padding-top: 25px;\">\r\n"
            + "                                        <img src=\"https://res.cloudinary.com/veggie-shop/image/upload/v1634045009/assets/checked_pudgic.png?fbclid=IwAR2aTBpMU1Gbj8pVwuU6sH1lUAUEeK2U8df1mrI4zCyMT97OnjkEIbgBSQw\" width=\"115\" height=\"110\" style=\"display: block; border: 0px;\" /><br>\r\n"
            + "                                        <h2 style=\"font-size: 30px; font-weight: 800; line-height: 36px; color: #333333; margin: 0;\"> Giao hàng thành công! </h2>\r\n"
            + "                                        <p style=\"font-family: Open sans-serif; font-size: 18px;\"><em>Cảm ơn bạn đã tin tưởng! Chúc bạn một ngày vui vẻ!</em></p>\r\n"
            + "                                        <p style=\"font-family: Open sans-serif; font-size: 18px;\"><em>Hẹn gặp lại quý khách!</em></p>\r\n"
            + "                                    </td>\r\n" + "                                </tr>\r\n"
            + "                                \r\n" + "                                <tr>\r\n"
            + "                                    <td align=\"left\" style=\"padding-top: 20px;\">\r\n"
            + "                                        <table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">\r\n"
            + "                                            <p style=\"font-size: 20px;font-family: Open sans-serif; text-decoration: underline; width: 200px;\">Đơn hàng đã giao:</p>\r\n"
            + "                                            <tr>\r\n"
            + "                                                <td width=\"25%\" align=\"left\" bgcolor=\"#eeeeee\" style=\"font-family: Open sans-serif; font-size: 18px; font-weight: 800; line-height: 24px; padding: 10px;\">#</td>\r\n"
            + "                                                <td width=\"25%\" align=\"left\" bgcolor=\"#eeeeee\" style=\"font-family: Open sans-serif; font-size: 18px; font-weight: 800; line-height: 24px; padding: 10px;\">Tên</td>\r\n"
            + "                                                <td width=\"25%\" align=\"left\" bgcolor=\"#eeeeee\" style=\"font-family: Open sans-serif; font-size: 18px; font-weight: 800; line-height: 24px; padding: 10px;\">Số lượng</td>\r\n"
            + "                                                <td width=\"25%\" align=\"left\" bgcolor=\"#eeeeee\" style=\"font-family: Open sans-serif; font-size: 18px; font-weight: 800; line-height: 24px; padding: 10px;\">Thành tiền</td>\r\n"
            + "                                            </tr>";

    static String HEADERSUCCESS = "<body style=\"margin: 0 !important; padding: 0 !important; background-color: #fcb800;\" bgcolor=\"#fcb800\">\r\n"
            + "    <div style=\"display: none; font-size: 1px; color: #fefefe; line-height: 1px; font-family: Open Sans, Helvetica, Arial, sans-serif; max-height: 0px; max-width: 0px; opacity: 0; overflow: hidden;\">\r\n"
            + "        Vegana Store chuyên bán thực phẩm sạch\r\n" + "    </div>\r\n"
            + "    <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\">\r\n" + "        <tr>\r\n"
            + "            <td align=\"center\" style=\"background-color: #eeeeee;\" bgcolor=\"#eeeeee\">\r\n"
            + "                <table align=\"center\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" style=\"max-width:600px;\">\r\n"
            + "                    <tr>\r\n"
            + "                        <td align=\"center\" valign=\"top\" style=\"font-size:0; padding: 35px;\" bgcolor=\"#fcb800\">\r\n"
            + "                            <div style=\"display:inline-block; max-width:50%; min-width:100px; vertical-align:top; width:100%;\">\r\n"
            + "                                <table align=\"left\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" style=\"max-width:300px;\">\r\n"
            + "                                    <tr>\r\n"
            + "                                        <td align=\"left\" valign=\"top\" style=\"font-family: Open Sans, Helvetica, Arial, sans-serif; font-size: 36px; font-weight: 800; line-height: 48px;\" class=\"mobile-center\">\r\n"
            + "                                            <img src=\"https://res.cloudinary.com/martfury/image/upload/v1647788678/users/j53qiooahnkiq2jmz9lk.png\" width=\"220px\"/>\r\n"
            + "                                        </td>\r\n" + "                                    </tr>\r\n"
            + "                                </table>\r\n" + "                            </div>\r\n"
            + "                            \r\n" + "                        </td>\r\n" + "                    </tr>\r\n"
            + "                    <tr>\r\n"
            + "                        <td align=\"center\" style=\"padding: 35px 35px 20px 35px; background-color: #ffffff;\" bgcolor=\"#ffffff\">\r\n"
            + "                            <table align=\"center\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" style=\"max-width:600px;\">\r\n"
            + "                                <tr>\r\n"
            + "                                    <td align=\"center\" style=\"font-family: Open sans-serif; font-size: 16px; font-weight: 400; line-height: 24px; padding-top: 25px;\">\r\n"
            + "                                        <img src=\"https://res.cloudinary.com/veggie-shop/image/upload/v1634045009/assets/checked_pudgic.png?fbclid=IwAR2aTBpMU1Gbj8pVwuU6sH1lUAUEeK2U8df1mrI4zCyMT97OnjkEIbgBSQw\" width=\"115\" height=\"110\" style=\"display: block; border: 0px;\" /><br>\r\n"
            + "                                        <h2 style=\"font-size: 30px; font-weight: 800; line-height: 36px; color: #333333; margin: 0;\"> Xác nhận đã thanh toán thành công! </h2>\r\n"
            + "                                        <p style=\"font-family: Open sans-serif; font-size: 18px;\"><em>Cảm ơn bạn đã tin tưởng! Chúng tôi sẽ nhanh chóng giao hàng cho bạn!</em></p>\r\n"
            + "                                    </td>\r\n" + "                                </tr>\r\n"
            + "                                \r\n" + "                                <tr>\r\n"
            + "                                    <td align=\"left\" style=\"padding-top: 20px;\">\r\n"
            + "                                        <table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">\r\n"
            + "                                            <p style=\"font-size: 20px;font-family: Open sans-serif; text-decoration: underline; width: 200px;\">Đơn hàng đã thanh toán:</p>\r\n"
            + "                                            <tr>\r\n"
            + "                                                <td width=\"25%\" align=\"left\" bgcolor=\"#eeeeee\" style=\"font-family: Open sans-serif; font-size: 18px; font-weight: 800; line-height: 24px; padding: 10px;\">#</td>\r\n"
            + "                                                <td width=\"25%\" align=\"left\" bgcolor=\"#eeeeee\" style=\"font-family: Open sans-serif; font-size: 18px; font-weight: 800; line-height: 24px; padding: 10px;\">Tên</td>\r\n"
            + "                                                <td width=\"25%\" align=\"left\" bgcolor=\"#eeeeee\" style=\"font-family: Open sans-serif; font-size: 18px; font-weight: 800; line-height: 24px; padding: 10px;\">Số lượng</td>\r\n"
            + "                                                <td width=\"25%\" align=\"left\" bgcolor=\"#eeeeee\" style=\"font-family: Open sans-serif; font-size: 18px; font-weight: 800; line-height: 24px; padding: 10px;\">Thành tiền</td>\r\n"
            + "                                            </tr>";

    static String HEADERCANCEL = "<body style=\"margin: 0 !important; padding: 0 !important; background-color: #fcb800;\" bgcolor=\"#fcb800\">\r\n"
            + "    <div style=\"display: none; font-size: 1px; color: #fefefe; line-height: 1px; font-family: Open Sans, Helvetica, Arial, sans-serif; max-height: 0px; max-width: 0px; opacity: 0; overflow: hidden;\">\r\n"
            + "        Vegana Store chuyên bán thực phẩm sạch\r\n" + "    </div>\r\n"
            + "    <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\">\r\n" + "        <tr>\r\n"
            + "            <td align=\"center\" style=\"background-color: #eeeeee;\" bgcolor=\"#eeeeee\">\r\n"
            + "                <table align=\"center\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" style=\"max-width:600px;\">\r\n"
            + "                    <tr>\r\n"
            + "                        <td align=\"center\" valign=\"top\" style=\"font-size:0; padding: 35px;\" bgcolor=\"#fcb800\">\r\n"
            + "                            <div style=\"display:inline-block; max-width:50%; min-width:100px; vertical-align:top; width:100%;\">\r\n"
            + "                                <table align=\"left\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" style=\"max-width:300px;\">\r\n"
            + "                                    <tr>\r\n"
            + "                                        <td align=\"left\" valign=\"top\" style=\"font-family: Open Sans, Helvetica, Arial, sans-serif; font-size: 36px; font-weight: 800; line-height: 48px;\" class=\"mobile-center\">\r\n"
            + "                                            <img src=\"https://res.cloudinary.com/martfury/image/upload/v1647788678/users/j53qiooahnkiq2jmz9lk.png\" width=\"220px\"/>\r\n"
            + "                                        </td>\r\n" + "                                    </tr>\r\n"
            + "                                </table>\r\n" + "                            </div>\r\n"
            + "                            \r\n" + "                        </td>\r\n" + "                    </tr>\r\n"
            + "                    <tr>\r\n"
            + "                        <td align=\"center\" style=\"padding: 35px 35px 20px 35px; background-color: #ffffff;\" bgcolor=\"#ffffff\">\r\n"
            + "                            <table align=\"center\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" style=\"max-width:600px;\">\r\n"
            + "                                <tr>\r\n"
            + "                                    <td align=\"center\" style=\"font-family: Open sans-serif; font-size: 16px; font-weight: 400; line-height: 24px; padding-top: 25px;\">\r\n"
            + "                                        <img src=\"https://res.cloudinary.com/veggie-shop/image/upload/v1634046654/assets/cancellation_xhljqh.png\" width=\"115\" height=\"110\" style=\"display: block; border: 0px;\" /><br>\r\n"
            + "                                        <h2 style=\"font-family: Open sans-serif;font-size: 30px; font-weight: 800; line-height: 36px; color: #333333; margin: 0;\"> Hủy đơn hàng thành công! </h2>\r\n"
            + "                                        <p style=\"font-family: Open sans-serif; font-size: 18px;\"><em>Chúng tôi rất tiếc về vấn đề này, hẹn gặp lại quý khách!</em></p>\r\n"
            + "                                    </td>\r\n" + "                                </tr>\r\n"
            + "                                \r\n" + "                                <tr>\r\n"
            + "                                    <td align=\"left\" style=\"padding-top: 20px;\">\r\n"
            + "                                        <table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">\r\n"
            + "                                            <p style=\"font-size: 20px;font-family: Open sans-serif; text-decoration: underline; width: 200px;\">Đơn hàng đã hủy:</p>\r\n"
            + "                                            <tr>\r\n"
            + "                                                <td width=\"25%\" align=\"left\" bgcolor=\"#eeeeee\" style=\"font-family: Open sans-serif; font-size: 18px; font-weight: 800; line-height: 24px; padding: 10px;\">#</td>\r\n"
            + "                                                <td width=\"25%\" align=\"left\" bgcolor=\"#eeeeee\" style=\"font-family: Open sans-serif; font-size: 18px; font-weight: 800; line-height: 24px; padding: 10px;\">Tên</td>\r\n"
            + "                                                <td width=\"25%\" align=\"left\" bgcolor=\"#eeeeee\" style=\"font-family: Open sans-serif; font-size: 18px; font-weight: 800; line-height: 24px; padding: 10px;\">Số lượng</td>\r\n"
            + "                                                <td width=\"25%\" align=\"left\" bgcolor=\"#eeeeee\" style=\"font-family: Open sans-serif; font-size: 18px; font-weight: 800; line-height: 24px; padding: 10px;\">Thành tiền</td>\r\n"
            + "                                            </tr>";

    static String HEADER = "<body bgcolor=\"#2196F3\" style=\"margin: 0 !important; padding: 0 !important; background-color: #2196F3;\">\r\n"
            + "<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\">\r\n"
            + "            <tr>\r\n"
            + "                <td align=\"center\" style=\"background-color: #eeeeee;\" bgcolor=\"#eeeeee\">\r\n"
            + "                    <table align=\"center\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" style=\"max-width:600px;\">\r\n"
            + "                        <tr>\r\n"
            + "                            <td align=\"center\" valign=\"top\" style=\"font-size:0; padding: 35px;\" bgcolor=\"##2196F3\">\r\n"
            + "                                <div style=\"display:inline-block; max-width:50%; min-width:100px; vertical-align:top; width:100%;\">\r\n"
            + "                                    <table align=\"left\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" style=\"max-width:300px;\">\r\n"
            + "                                        \r\n" + "                                    </table>\r\n"
            + "                                </div>\r\n" + "\r\n" + "                            </td>\r\n"
            + "                        </tr>\r\n" + "                        <tr>\r\n"
            + "                            <td align=\"center\" style=\"padding: 35px 35px 20px 35px; background-color: #ffffff;\" bgcolor=\"#ffffff\">\r\n"
            + "                                <table align=\"center\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" style=\"max-width:600px;\">\r\n"
            + "                                    <tr>\r\n"
            + "                                        <td align=\"center\" style=\"font-family: Open sans-serif; font-size: 16px; font-weight: 400; line-height: 24px; padding-top: 25px;\">\r\n"
            + "                                            <img src=\"https://res.cloudinary.com/websitemart/image/upload/v1745459605/wqij789tbxbhbjehthqd.webp\" width=\"115\" height=\"110\" style=\"display: block; border: 0px;\" /><br>\r\n"
            + "                                            <h2 style=\"font-size: 30px; font-weight: 800; line-height: 36px; color: #333333; margin: 0;\"> Chúc mừng đã đặt hàng thành công! </h2>\r\n"
            + "                                            <p style=\"font-family: Open sans-serif; font-size: 18px;\"><em>Chúng tôi sẽ tiến hành xử lý đơn hàng sớm nhất, bạn vui lòng chờ nhé!</em></p>\r\n"
            + "                                        </td>\r\n" + "                                    </tr>\r\n"
            + "\r\n" + "                                    <tr>\r\n"
            + "                                        <td align=\"left\" style=\"padding-top: 20px;\">\r\n"
            + "                                            <table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">\r\n"
            + "                                                <p style=\"font-size: 20px;font-family: Open sans-serif; text-decoration: underline; width: 200px;\">Đơn hàng của bạn:</p>\r\n"
            + "                                                <tr>\r\n"
            + "                                                    <td width=\"25%\" align=\"left\" bgcolor=\"#eeeeee\" style=\"font-family: Open sans-serif; font-size: 18px; font-weight: 800; line-height: 24px; padding: 10px;\">#</td>\r\n"
            + "                                                    <td width=\"25%\" align=\"left\" bgcolor=\"#eeeeee\" style=\"font-family: Open sans-serif; font-size: 18px; font-weight: 800; line-height: 24px; padding: 10px;\">Tên</td>\r\n"
            + "                                                    <td width=\"25%\" align=\"left\" bgcolor=\"#eeeeee\" style=\"font-family: Open sans-serif; font-size: 18px; font-weight: 800; line-height: 24px; padding: 10px;\">Số lượng</td>\r\n"
            + "                                                    <td width=\"25%\" align=\"left\" bgcolor=\"#eeeeee\" style=\"font-family: Open sans-serif; font-size: 18px; font-weight: 800; line-height: 24px; padding: 10px;\">Thành tiền</td>\r\n"
            + "                                                </tr>";
    static String BODY2 = "</table>\r\n" + "                                        </td>\r\n"
            + "                                    </tr>\r\n" + "                                    <tr>\r\n"
            + "                                        <td align=\"left\" style=\"padding-top: 20px;\">\r\n"
            + "                                            <table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">\r\n"
            + "                                                <tr>";
    static String BODY3 = "</tr>\r\n" + "                                            </table>\r\n"
            + "                                        </td>\r\n" + "                                    </tr>\r\n"
            + "                                </table>\r\n" + "                            </td>\r\n"
            + "                        </tr>\r\n" + "                        <tr>\r\n"
            + "                            <td align=\"center\" height=\"100%\" valign=\"top\" width=\"100%\" style=\"padding: 0 35px 35px 35px; background-color: #ffffff;\" bgcolor=\"#ffffff\">\r\n"
            + "                                <table align=\"center\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" style=\"max-width:660px;\">\r\n"
            + "                                    <tr>\r\n"
            + "                                        <td align=\"center\" valign=\"top\" style=\"font-size:0;\">\r\n"
            + "                                            <div style=\"display:inline-block; max-width:50%; min-width:240px; vertical-align:top; width:100%;\">\r\n"
            + "                                                <table align=\"left\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" style=\"max-width:300px;\">\r\n"
            + "                                                    <tr>\r\n"
            + "                                                        <td align=\"left\" valign=\"top\" style=\"font-family: Open sans-serif; font-size: 20px; font-weight: 400; line-height: 24px;\">";
    static String BODY4 = "</td>\r\n" + "                                                    </tr>\r\n"
            + "                                                </table>\r\n"
            + "                                            </div>\r\n"
            + "                                            <div style=\"display:inline-block; max-width:50%; min-width:240px; vertical-align:top; width:100%;\">\r\n"
            + "                                                <table align=\"left\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" style=\"max-width:300px;\">\r\n"
            + "                                                    <tr>\r\n"
            + "                                                        <td align=\"left\" valign=\"top\" style=\"font-family: Open sans-serif; font-size: 20px; font-weight: 400; line-height: 24px;\">";
    static String FOOTER = "</tr>\r\n" + "                                                </table>\r\n"
            + "                                            </div>\r\n"
            + "                                        </td>\r\n" + "                                    </tr>\r\n"
            + "                                </table>\r\n" + "                            </td>\r\n"
            + "                        </tr>\r\n" + "\r\n" + "                        <tr>\r\n"
            + "                            <td align=\"center\" style=\"padding: 35px; background-color: #ffffff;\" bgcolor=\"#ffffff\">\r\n"
            + "                                <table align=\"center\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" style=\"max-width:600px;\">\r\n"
            + "                                    <tr>\r\n"
            + "                                        <td align=\"center\" style=\"font-family: Open sans-serif; font-size: 18px; font-weight: 400; line-height: 24px; padding: 5px 0 10px 0;\">\r\n"
            + "                                            <p style=\"font-size: 18px; font-weight: 800; line-height: 18px; color: #2196F3;\"> Thuanle Shop </p>\r\n"
            + "                                            <p style=\"font-family: Open sans-serif;\">Cảm ơn bạn đã tin tưởng chúng tôi - Chúc bạn một ngày vui vẻ!</p>\r\n"
            + "                                        </td>\r\n" + "                                    </tr>\r\n"
            + "\r\n" + "                                </table>\r\n" + "                            </td>\r\n"
            + "                        </tr>\r\n" + "                    </table>\r\n" + "                </td>\r\n"
            + "            </tr>\r\n" + "        </table>\r\n" + "    </body>";
}
