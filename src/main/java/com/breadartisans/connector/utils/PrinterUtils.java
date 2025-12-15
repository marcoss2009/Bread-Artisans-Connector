package com.breadartisans.connector.utils;

public class PrinterUtils {
    public static final int LINE_WIDTH = 48;
    public static final int LINE_WIDTH_BOLD = 24;
    public enum Align {LEFT, CENTER, RIGHT}
    public record Column(String text, int width, Align align) {}

    public static String buildRow(Column... columns) {
        StringBuilder row = new StringBuilder();

        for (Column col : columns) {
            String text = col.text != null ? col.text : "";
            int width = col.width;

            if (text.length() > width)
                text = text.substring(0, width);

            // Calculate padding
            int padding = width - text.length();

            // Alignment
            switch (col.align) {
                case LEFT -> row.append(text).append(" ".repeat(padding));
                case RIGHT -> row.append(" ".repeat(padding)).append(text);
                case CENTER -> {
                    int leftPad = padding / 2;
                    int rightPad = padding - leftPad;
                    row.append(" ".repeat(leftPad)).append(text).append(" ".repeat(rightPad));
                }
            }
        }

        return row.toString();
    }

    public static String twoColumns(String left, String right) {
        int leftWidth = LINE_WIDTH / 2;
        int rightWidth = LINE_WIDTH - leftWidth;

        int space = LINE_WIDTH - left.length() - right.length();
        if (space < 1) space = 1;

        return left + " ".repeat(space) + right;
    }

    public static String twoColumnsBold(String left, String right) {
        int leftWidth = LINE_WIDTH_BOLD / 2;
        int rightWidth = LINE_WIDTH_BOLD - leftWidth;

        int space = LINE_WIDTH_BOLD - left.length() - right.length();
        if (space < 1) space = 1;

        return left + " ".repeat(space) + right;
    }

    public static String lineBreaker() {
        return "-".repeat(LINE_WIDTH);
    }
}
