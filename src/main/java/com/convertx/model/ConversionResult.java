package com.convertx.model;
import java.time.LocalDateTime;
public record ConversionResult(String input,String output,String path,boolean success,long durationMs,LocalDateTime timestamp,String message) {}
