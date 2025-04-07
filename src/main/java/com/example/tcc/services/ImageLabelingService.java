package com.example.tcc.services;

import org.springframework.stereotype.Service;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Service
public class ImageLabelingService {
    public byte[] drawString(byte[] imageBytes, String inputString) {
        try {
            if (imageBytes == null) {
                System.err.println("Erro ao obter imagem do S3.");
                return null;
            }

            final BufferedImage image = ImageIO.read(new ByteArrayInputStream(imageBytes));

            Graphics g = image.getGraphics();
            g.setColor(Color.LIGHT_GRAY);
            g.fillRect(0, 0, 122, 40);
            g.setFont(g.getFont().deriveFont(30f));
            g.setColor(Color.BLACK);
            g.drawString(inputString, 5, 30);
            g.dispose();

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, "jpg", baos);
            return baos.toByteArray();
        } catch (IOException e) {
            // Trata a exceção de IO, por exemplo, se o arquivo não puder ser criado
            System.err.println("ImageLabelingService: Erro ao processar imagem: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
}
