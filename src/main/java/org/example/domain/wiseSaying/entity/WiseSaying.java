package org.example.wiseSaying;

public class WiseSaying {
    private int id;
    private String content;
    private String author;

    public WiseSaying(String content, String author) {
        this.content = content;
        this.author = author;
    }

    public WiseSaying(int id, String content, String author) {
        this.id = id;
        this.content = content;
        this.author = author;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String toJson() {
        return String.format("{\"id\": %d, \"content\": \"%s\", \"author\": \"%s\"}",
                id, content.replace("\"", "\\\""), author.replace("\"", "\\\""));
    }

    public static WiseSaying fromJson(String json) {
        try {
            String[] lines = json.replace("{", "").replace("}", "").split(",");
            int id = Integer.parseInt(lines[0].split(":")[1].trim());
            String content = lines[1].split(":")[1].trim().replace("\"", "");
            String author = lines[2].split(":")[1].trim().replace("\"", "");
            return new WiseSaying(id, content, author);
        } catch (Exception e) {
            System.out.println("JSON 파싱 중 오류 발생: " + e.getMessage());
            return null;
        }
    }

    @Override
    public String toString() {
        return id + " / " + author + " / " + content;
    }
}