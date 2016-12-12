package com.example.master.ahlalhdeeth;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by imac on 11/12/2016.
 */

public class Post {

    private List<String> quotes;
    private String text;
    private String textWithoutHtml;
    private String author;
    private boolean isConnected;
    private String lastModified;
    private String numPost;
    private String link;

    public List<String> getQuotes() {
        return quotes;
    }

    public void setQuotes(List<String> quotes) {
        this.quotes = quotes;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getTextWithoutHtml() {
        return textWithoutHtml;
    }

    public void setTextWithoutHtml(String textWithoutHtml) {
        this.textWithoutHtml = textWithoutHtml;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public boolean isConnected() {
        return isConnected;
    }

    public void setConnected(boolean connected) {
        isConnected = connected;
    }

    public String getLastModified() {
        return lastModified;
    }

    public void setLastModified(String lastModified) {
        this.lastModified = lastModified;
    }

    public String getNumPost() {
        return numPost;
    }

    public void setNumPost(String numPost) {
        this.numPost = numPost;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
}
