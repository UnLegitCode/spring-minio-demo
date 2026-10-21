package ru.unlegit.miniodemo.dto;

public record PresignedUrlResponse(String presignedUrl, String lifetime) {}