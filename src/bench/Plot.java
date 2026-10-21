package bench;

import javax.imageio.ImageIO;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class Plot {
    public static class Series {
        public final String name;
        public final double[] x;
        public final double[] y;
        public final Color color;

        public Series(String name, double[] x, double[] y, Color color) {
            this.name = name;
            this.x = x;
            this.y = y;
            this.color = color;
        }
    }

    private static final int WIDTH = 900;
    private static final int HEIGHT = 520;
    private static final int LEFT = 95;
    private static final int RIGHT = 180;
    private static final int TOP = 55;
    private static final int BOTTOM = 65;

    public static void lineChart(String path, String title, String xLabel, String yLabel, List<Series> series)
            throws IOException {
        BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D canvas = image.createGraphics();
        canvas.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        canvas.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        canvas.setColor(Color.WHITE);
        canvas.fillRect(0, 0, WIDTH, HEIGHT);

        double maxX = 0;
        double maxY = 0;
        for (Series line : series) {
            for (double value : line.x) {
                maxX = Math.max(maxX, value);
            }
            for (double value : line.y) {
                maxY = Math.max(maxY, value);
            }
        }
        if (maxY == 0) {
            maxY = 1;
        }
        maxY *= 1.1;

        int plotWidth = WIDTH - LEFT - RIGHT;
        int plotHeight = HEIGHT - TOP - BOTTOM;

        canvas.setFont(new Font("SansSerif", Font.BOLD, 16));
        canvas.setColor(Color.BLACK);
        canvas.drawString(title, LEFT, 30);

        canvas.setFont(new Font("SansSerif", Font.PLAIN, 11));
        for (int step = 0; step <= 5; step++) {
            int y = TOP + plotHeight - step * plotHeight / 5;
            canvas.setColor(new Color(0xDDDDDD));
            canvas.drawLine(LEFT, y, LEFT + plotWidth, y);
            canvas.setColor(Color.DARK_GRAY);
            canvas.drawString(format(maxY * step / 5), 12, y + 4);
        }
        for (int step = 0; step <= 5; step++) {
            int x = LEFT + step * plotWidth / 5;
            canvas.setColor(new Color(0xEEEEEE));
            canvas.drawLine(x, TOP, x, TOP + plotHeight);
            canvas.setColor(Color.DARK_GRAY);
            String label = format(maxX * step / 5);
            canvas.drawString(label, x - label.length() * 3, TOP + plotHeight + 18);
        }

        canvas.setColor(Color.BLACK);
        canvas.drawLine(LEFT, TOP, LEFT, TOP + plotHeight);
        canvas.drawLine(LEFT, TOP + plotHeight, LEFT + plotWidth, TOP + plotHeight);
        canvas.setFont(new Font("SansSerif", Font.PLAIN, 12));
        canvas.drawString(xLabel, LEFT + plotWidth / 2 - 40, HEIGHT - 20);
        canvas.drawString(yLabel, 12, TOP - 18);

        int legendY = TOP + 10;
        for (Series line : series) {
            canvas.setColor(line.color);
            canvas.setStroke(new BasicStroke(2f));
            int previousX = 0;
            int previousY = 0;
            for (int i = 0; i < line.x.length; i++) {
                int x = LEFT + (int) (line.x[i] / maxX * plotWidth);
                int y = TOP + plotHeight - (int) (line.y[i] / maxY * plotHeight);
                if (i > 0) {
                    canvas.drawLine(previousX, previousY, x, y);
                }
                canvas.fillOval(x - 4, y - 4, 8, 8);
                previousX = x;
                previousY = y;
            }
            canvas.fillRect(LEFT + plotWidth + 20, legendY - 9, 12, 12);
            canvas.setColor(Color.BLACK);
            canvas.drawString(line.name, LEFT + plotWidth + 38, legendY + 2);
            legendY += 22;
        }

        canvas.dispose();
        File file = new File(path);
        file.getParentFile().mkdirs();
        ImageIO.write(image, "png", file);
    }

    private static String format(double value) {
        if (value >= 100) {
            return String.valueOf(Math.round(value));
        }
        return String.format("%.2f", value);
    }
}
