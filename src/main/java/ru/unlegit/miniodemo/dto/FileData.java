package ru.unlegit.miniodemo.dto;

import java.io.InputStream;

public record FileData(InputStream stream, String fileSize, String contentType) {}