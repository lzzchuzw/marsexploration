package com.exploration.action;

import com.exploration.auth.JDLogin;
import com.exploration.gui.ImageIconUtils;
import com.exploration.request.HttpClientRequestHandler;
import com.exploration.shoppingrush.FetchUrlType;
import com.exploration.shoppingrush.JDPrecontract;
import com.exploration.shoppingrush.JDShoppingRush;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.cookie.Cookie;


import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

/**
 * @ClassName: QrCodeJDLoginAction
 * @Author: leisure
 * @CreateDate: 2021/2/23 16:48
 * @Description:
 */
public class QrCodeJDLoginAction {

    private JFrame frame;
    public static final String PRODUCT_ID = "1552946";//
    private String shoppingRushTime = "2020-05-03 18:40:00:220";

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                QrCodeJDLoginAction window = new QrCodeJDLoginAction();
                window.frame.setVisible(true);
            }
        });
    }

    /**
     * Create the application.
     */
    public QrCodeJDLoginAction(){
        initialize();
    }

    /**
     * Initialize the contents of the frame.
     */
    private void initialize() {
        frame = new JFrame();
        frame.setBounds(100, 100, 450, 300);
        frame.getContentPane().setLayout(null);

        JLabel qrCodeImg = new JLabel("QRCodeImg");
        qrCodeImg.setBounds(131, 10, 200, 200);

        // picture = new JLabel();
        qrCodeImg.setFont(qrCodeImg.getFont().deriveFont(Font.ITALIC));
        qrCodeImg.setHorizontalAlignment(JLabel.CENTER);
        HttpClientRequestHandler requestHandler = new HttpClientRequestHandler();
        JDLogin jdLogin = new JDLogin();
        Map<String, Object> visitJDLoginHomePageMap = jdLogin.visitJDLoginHomePage(requestHandler);
        // 下载并保存二维码 返回两个cookie QRCodeKey wlfstk_smdl
        String qrCodeImgUrl = String.valueOf(visitJDLoginHomePageMap.get("qrCodeImgUrl"));
        System.out.println("qrCodeImgUrl = " + qrCodeImgUrl);
        String qrCodePath = jdLogin.fetchJDLoginQrCodImg(requestHandler, qrCodeImgUrl);
        System.out.println("qrCodePath = " + qrCodePath);
        ImageIcon icon = ImageIconUtils.createImageIconByFilePath(qrCodePath);
        qrCodeImg.setIcon(icon);
        frame.getContentPane().add(qrCodeImg);

        JButton loginButton = new JButton("login");
        loginButton.setBounds(30, 220, 93, 23);
        loginButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                HttpClientContext tcontext = requestHandler.getContext();
                String appid = "133";
                String token = null;
                List<Cookie> cookieList = tcontext.getCookieStore().getCookies();
                for(Cookie cookie : cookieList){
                    if ("wlfstk_smdl".equals(cookie.getName())) {
                        token = cookie.getValue();
                        break;
                    }
                }
                String ticket = jdLogin.checkQrcode(requestHandler, appid, token);
                String skipUrl = jdLogin.validateQrCodeTicket(requestHandler, ticket);
                jdLogin.loginSuccessRedirectAfterValidateQrCode(requestHandler,skipUrl);
            }
        });
        frame.getContentPane().add(loginButton);
        JButton precontractButton = new JButton("预约");
        precontractButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                JDPrecontract jdPrecontract = new JDPrecontract();
                Map<String, Object> fetchPrecontractShoppingRushUrlMap = jdPrecontract
                        .fetchPrecontractShoppingRushUrl(requestHandler, PRODUCT_ID);
                boolean flag = jdPrecontract.precontractShoppingRush(requestHandler,
                        fetchPrecontractShoppingRushUrlMap);
                if (flag) {
                    System.out.println("预约成功！");
                } else {
                    System.out.println("预约失败~");
                }
            }
        });
        precontractButton.setBounds(162, 220, 93, 23);
        frame.getContentPane().add(precontractButton);

        JButton shoppingrushButton = new JButton("抢购");
        shoppingrushButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                JDShoppingRush jdsr = new JDShoppingRush(FetchUrlType.AFTER_SHOPPING_RUSH,PRODUCT_ID, shoppingRushTime);
				 /* jdsr.fetchUrlAndrushToPurchaseTask(requestHandler, false,
				 false);*/

                String rushToPurchaseUrl = jdsr.fetchRushToPurchaseUrl(requestHandler, PRODUCT_ID);

                Date startTime = null;
                try {
                    startTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS").parse(shoppingRushTime);
                } catch (ParseException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }

                // webDriver.get(rushToPurchaseUrl);

                Timer timer = new Timer();
                timer.schedule(new TimerTask() {

                    @Override
                    public void run() {
                        jdsr.rushToPurchaseTask(requestHandler, rushToPurchaseUrl, false, false);

                    }

                }, startTime);
            }
        });
        shoppingrushButton.setBounds(307, 220, 93, 23);
        frame.getContentPane().add(shoppingrushButton);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    }
}
