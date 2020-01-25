package com.github.cenkakin.mynewspaper.util;

import com.querydsl.core.types.dsl.BooleanExpression;

import java.util.Optional;
import java.util.function.Function;

public class OptionalBooleanExpressionBuilder {

  private BooleanExpression predicate;

  public OptionalBooleanExpressionBuilder(BooleanExpression predicate) {
    this.predicate = predicate;
  }

  public <T> OptionalBooleanExpressionBuilder notNullAnd(Function<T, BooleanExpression> expressionFunction, T value) {
    return Optional.ofNullable(value)
        .map(v -> new OptionalBooleanExpressionBuilder(predicate.and(expressionFunction.apply(v))))
        .orElse(this);
  }

  public BooleanExpression build() {
    return predicate;
  }
}