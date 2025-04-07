package com.example.tcc.services;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import nu.pattern.OpenCV;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Comparator;

@Service
@RequiredArgsConstructor
public class BarcodeDetectionService {
    private final AWSS3Service s3Service;

    @PostConstruct
    public void init() {
        // Carregar biblioteca do OpenCV
        OpenCV.loadShared();
    }

    private static BufferedImage matToBufferedImage(Mat mat) {
        // Converta o Mat para um formato de imagem padrão
        MatOfByte matOfByte = new MatOfByte();
        Imgcodecs.imencode(".jpg", mat, matOfByte); // Encode as JPEG

        byte[] byteArray = matOfByte.toArray();
        BufferedImage bufferedImage = null;

        try (ByteArrayInputStream bis = new ByteArrayInputStream(byteArray)) {
            bufferedImage = ImageIO.read(bis); // Read the bytes into BufferedImage
        } catch (IOException e) {
            e.printStackTrace();
        }

        return bufferedImage;
    }

    // Recortar a imagem mantendo todos os pixels na largura e mantendo apenas 25% da altura
    private Mat cropInitialImage(Mat image) {
        int height = image.rows();
        int width = image.cols();
        int newHeight = (int) (height * 0.25);
        int top = (height - newHeight) / 2;
        int bottom = top + newHeight;
        return image.submat(top, bottom, 0, width);
    }

    private Mat convertToGrayScale(Mat image) {
        Mat gray = new Mat();
        Imgproc.cvtColor(image, gray, Imgproc.COLOR_BGR2GRAY);

        return gray;
    }

    private Mat computeScharrGradient(Mat gray) {
        Mat gradX = new Mat();
        Mat gradY = new Mat();
        Imgproc.Scharr(gray, gradX, CvType.CV_32F, 1, 0);
        Imgproc.Scharr(gray, gradY, CvType.CV_32F, 0, 1);
        Mat gradient = new Mat();
        Core.subtract(gradX, gradY, gradient);
        Core.convertScaleAbs(gradient, gradient);

        return gradient;
    }

    private Mat computeThreshold(Mat gradient) {
        Mat blurred = new Mat();
        Imgproc.blur(gradient, blurred, new Size(9, 9));
        Mat thresh = new Mat();
        Imgproc.threshold(blurred, thresh, 200, 255, Imgproc.THRESH_BINARY);

        return thresh;
    }

    private Mat computeClose(Mat thresh) {
        Mat kernel = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(21, 7));
        Mat closed = new Mat();
        Imgproc.morphologyEx(thresh, closed, Imgproc.MORPH_CLOSE, kernel);

        return closed;
    }

    private Mat computeErosionAndDilatation(Mat closed) {
        Imgproc.erode(closed, closed, new Mat(), new Point(-1, -1), 1);
        Imgproc.dilate(closed, closed, new Mat(), new Point(-1, -1), 1);

        return closed;
    }

    private MatOfPoint findLargestContour(Mat closed) {
        // Encontrar contornos e identificar o maior
        java.util.List<MatOfPoint> contours = new java.util.ArrayList<>();
        Imgproc.findContours(closed.clone(), contours, new Mat(), Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
        return contours.stream().max(Comparator.comparingDouble(Imgproc::contourArea)).orElse(null);
    }

    private boolean contourIsValid(Mat image, MatOfPoint largestContour) {
        if (largestContour == null) {
            return false;
        }

        double contourArea = Imgproc.contourArea(largestContour);
        double minArea = 0.03 * image.cols() * image.rows(); // 3% da área da imagem original
        return !(contourArea < minArea);
    }

    private Mat extractLargestContour(Mat image, MatOfPoint largestContour) {
        if (!contourIsValid(image, largestContour)) {
            return null;
        }

        // Encontrar o retângulo delimitador ao redor do código de barras detectado
        RotatedRect rect = Imgproc.minAreaRect(new MatOfPoint2f(largestContour.toArray()));
        Point[] box = new Point[4];
        rect.points(box);

        // Extrair as coordenadas do retângulo delimitador
        Rect boundingRect = Imgproc.boundingRect(new MatOfPoint(box));

        // Calcular a margem de 20%
        double tolerance = 0.2;
        int marginX = (int) (boundingRect.width * tolerance);
        int marginY = (int) (boundingRect.height * tolerance);

        // Definir os limites do recorte
        int x1 = Math.max(boundingRect.x - marginX, 0);
        int y1 = Math.max(boundingRect.y - marginY, 0);
        int x2 = Math.min(boundingRect.x + boundingRect.width + marginX, image.cols());
        int y2 = Math.min(boundingRect.y + boundingRect.height + marginY, image.rows());

        // Recortar a região delimitada com a margem de 20%
        return new Mat(image, new Rect(x1, y1, x2 - x1, y2 - y1));
    }

    public BufferedImage detectBarcode(String filename) {
        // Baixar a imagem do S3 e obter os bytes
        byte[] imageBytes = s3Service.downloadImage(filename);

        if (imageBytes == null) {
            System.err.println("Erro ao obter imagem do S3.");
            return null;
        }

        // Converter byte array para Mat
        Mat image = Imgcodecs.imdecode(new MatOfByte(imageBytes), Imgcodecs.IMREAD_COLOR);

        if (image.empty()) {
            System.err.println("Imagem não encontrada ou inválida");
            return null;
        }
        // Processar a imagem
        Mat croppedImage = cropInitialImage(image);
        Mat gray = convertToGrayScale(croppedImage);
        Mat gradient = computeScharrGradient(gray);
        Mat thresh = computeThreshold(gradient);
        Mat closed = computeClose(thresh);
        Mat eroded = computeErosionAndDilatation(closed);

        MatOfPoint largestContour = findLargestContour(eroded);
        Mat finalImage = extractLargestContour(croppedImage, largestContour);

        if(finalImage == null) {
            System.err.println("Código de barras não encontrado. Retornando imagem completa");
            finalImage = croppedImage;
        }

        return matToBufferedImage(finalImage);
    }
}