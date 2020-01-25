package com.github.cenkakin.mynewspaper.exception;

/**
 * Created by cenkakin
 */
public class OutdatedUpdateArticleException extends RuntimeException {

  public OutdatedUpdateArticleException(Long version) {
    super("You need to increase version number or article has already been changed! Current version: " + version);
  }
}
