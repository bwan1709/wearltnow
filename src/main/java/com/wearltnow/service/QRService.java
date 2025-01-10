package com.wearltnow.service;

import com.luciad.imageio.webp.WebPWriteParam;
import com.wearltnow.dto.request.payment.QRRequest;
import com.wearltnow.exception.AppException;
import com.wearltnow.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.cloudinary.json.JSONObject;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Iterator;


@Service
@RequiredArgsConstructor
public class QRService {

        private final RestTemplate restTemplate;

    public String createQrCode(QRRequest qrRequest) {
        // Gán giá trị cho addInfo từ orderId
        qrRequest.setAddInfo("" + qrRequest.getPaymentId());
        // Gán giá trị cho amount từ totalAmount và định dạng lại cho đúng
        DecimalFormat df = new DecimalFormat("#.##");
        qrRequest.setAmount(df.format(qrRequest.getTotalAmount())); // Định dạng số thập phân thành chuỗi

       // System.out.println("Request to VietQR: " + qrRequest);

        // Gửi yêu cầu đến API VietQR và nhận phản hồi
        ResponseEntity<String> response = restTemplate.postForEntity(
                "https://api.vietqr.io/v2/generate", qrRequest, String.class
        );

        // Kiểm tra nếu phản hồi thành công
        String desc = null;
        if (response.getStatusCode().is2xxSuccessful()) {
            String jsonResponse = response.getBody();
            // Phân tích phản hồi JSON
            JSONObject jsonObject = new JSONObject(jsonResponse);
            String code = jsonObject.getString("code");
            desc = jsonObject.getString("desc");

            if ("00".equals(code)) {
                // Lấy thông tin mã QR từ phản hồi
                String qrCodeDataUrl = jsonObject.getJSONObject("data").getString("qrDataURL");
                return qrCodeDataUrl;
            } else {
                throw new AppException(ErrorCode.QR_CODE_GENERATION_FAILED);
            }
        }
        throw new AppException(ErrorCode.QR_CODE_GENERATION_FAILED);
    }

    public String convertPngToWebp(String pngDataUrl){
        try{
            String base64Data = pngDataUrl.split(",")[1];//lay du lie
            byte[] imageBytes = java.util.Base64.getDecoder().decode(base64Data);

            //doc png
            BufferedImage bufferedImage = ImageIO.read(new ByteArrayInputStream(imageBytes));

            //tao output ghi hinh anh webp
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ImageOutputStream ios = ImageIO.createImageOutputStream(outputStream);
            Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName("webp");
            ImageWriter writer = writers.next();
            writer.setOutput(ios);

            //thiet lap chat luong anh
            WebPWriteParam writeParam = new WebPWriteParam(null);
            writeParam.setCompressionQuality(0.1f); // chat luong anh

            //ghi anh vao output stream
            writer.write(null, new IIOImage(bufferedImage, null, null), writeParam);
            writer.dispose();
            ios.close();

            //tra webP ve base 64
            return "data:image/webp;base64," +  java.util.Base64.getEncoder().encodeToString(outputStream.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
            throw new AppException(ErrorCode.QR_CODE_GENERATION_FAILED);
        }
    }

}
