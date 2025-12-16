/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.sql.Timestamp; // 날짜 시간용

public class BoardDTO {
    private int no;             // 글 번호
    private String title;       // 제목
    private String content;     // 내용
    private String writerId;    // 작성자 ID 
    private Timestamp regDate;  // 작성일

    public BoardDTO() {
    }

    public BoardDTO(String title, String content, String writerId) {
        this.title = title;
        this.content = content;
        this.writerId = writerId;
    }

    public BoardDTO(int no, String title, String content, String writerId, Timestamp regDate) {
        this.no = no;
        this.title = title;
        this.content = content;
        this.writerId = writerId;
        this.regDate = regDate;
    }

    public int getNo() { return no; }
    public void setNo(int no) { this.no = no; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getWriterId() { return writerId; }
    public void setWriterId(String writerId) { this.writerId = writerId; }
    public Timestamp getRegDate() { return regDate; }
    public void setRegDate(Timestamp regDate) { this.regDate = regDate; }
}