package com.example.springChat.global.utils;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.AllArgsConstructor;

@Converter
@AllArgsConstructor
public class MessageCryptoConverter implements AttributeConverter<String, String> {

    private final CryptoByAes cryptoByAes;

    @Override
    public String convertToDatabaseColumn(String attribute) {
        try {
            return cryptoByAes.encrypt(attribute);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String convertToEntityAttribute(String dbData) {
        try {
            return cryptoByAes.decrypt(dbData);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
