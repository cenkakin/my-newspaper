package com.github.cenkakin.mynewspaper.exception;

/**
 * Created by cenkakin
 */
public class ArticleNotFoundException extends RuntimeException {

    public ArticleNotFoundException(String id) {
        super("Article id is not found! id: " + id);
    }
}
